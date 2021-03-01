package com.apollographql.apollo3.api

import com.apollographql.apollo3.api.ExecutionContext.Element
import com.apollographql.apollo3.api.ExecutionContext.Key
import kotlin.jvm.JvmField

@ApolloExperimental
abstract class ClientContext(override val key: Key<*>): Element
@ApolloExperimental
abstract class RequestContext(override val key: Key<*>): Element
@ApolloExperimental
abstract class ResponseContext(override val key: Key<*>): Element

/**
 * A context of GraphQL operation execution, represented as a set of [Key] keys and corresponding [Element] values.
 */
@ApolloExperimental
interface ExecutionContext {

  /**
   * Returns the element with the given [key] from this context or `null`.
   */
  operator fun <E : Element> get(key: Key<E>): E?

  /**
   * Accumulates entries of this context starting with [initial] value and applying [operation] from left to right to current accumulator
   * value and each element of this context.
   */
  fun <R> fold(initial: R, operation: (R, Element) -> R): R

  /**
   * Returns a context containing elements from this context and elements from  other [context].
   * The elements from this context with the same key as in the other one are dropped.
   */
  operator fun plus(context: ExecutionContext): ExecutionContext {
    return if (context === EmptyExecutionContext) this else {
      context.fold(this) { acc, element ->
        val removed = acc.minusKey(element.key)
        if (removed === EmptyExecutionContext) element else {
          CombinedExecutionContext(removed, element)
        }
      }
    }
  }

  /**
   * Returns a context containing elements from this context, but without an element with the specified [key].
   */
  fun minusKey(key: Key<*>): ExecutionContext

  /**
   * Key for the elements of [ExecutionContext]. [E] is a type of element with this key.
   */
  interface Key<E : Element>

  /**
   * An element of the [ExecutionContext]. An element of the execution context is a singleton context by itself.
   */
  interface Element : ExecutionContext {
    /**
     * A key of this execution context element.
     */
    val key: Key<*>

    override operator fun <E : Element> get(key: Key<E>): E? {
      @Suppress("UNCHECKED_CAST")
      return if (this.key == key) this as E else null
    }

    override fun <R> fold(initial: R, operation: (R, Element) -> R): R {
      return operation(initial, this)
    }

    override fun minusKey(key: Key<*>): ExecutionContext {
      return if (this.key == key) EmptyExecutionContext else this
    }
  }

  companion object {
    @JvmField
    val Empty: ExecutionContext = EmptyExecutionContext
  }
}

@ApolloExperimental
internal object EmptyExecutionContext : ExecutionContext {
  override fun <E : Element> get(key: Key<E>): E? = null
  override fun <R> fold(initial: R, operation: (R, Element) -> R): R = initial
  override fun plus(context: ExecutionContext): ExecutionContext = context
  override fun minusKey(key: Key<*>): ExecutionContext = this
}

@ApolloExperimental
internal class CombinedExecutionContext(
    private val left: ExecutionContext,
    private val element: Element
) : ExecutionContext {

  override fun <E : Element> get(key: Key<E>): E? {
    var cur = this
    while (true) {
      cur.element[key]?.let { return it }
      val next = cur.left
      if (next is CombinedExecutionContext) {
        cur = next
      } else {
        return next[key]
      }
    }
  }

  override fun <R> fold(initial: R, operation: (R, Element) -> R): R =
      operation(left.fold(initial, operation), element)

  override fun minusKey(key: Key<*>): ExecutionContext {
    element[key]?.let { return left }
    val newLeft = left.minusKey(key)
    return when {
      newLeft === left -> this
      newLeft === EmptyExecutionContext -> element
      else -> CombinedExecutionContext(newLeft, element)
    }
  }
}
