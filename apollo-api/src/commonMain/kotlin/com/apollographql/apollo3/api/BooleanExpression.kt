package com.apollographql.apollo3.api

/**
 * A boolean expression
 *
 * @param T the type of the variable elements. This allows representing BooleanExpression that only contain variables and
 * other that may also contain possibleTypes
 */
sealed class BooleanExpression<out T : Any> {
  /**
   * This is not super well defined but works well enough for our simple use cases
   */
  abstract fun simplify(): BooleanExpression<T>

  object True : BooleanExpression<Nothing>() {
    override fun simplify() = this
  }

  object False : BooleanExpression<Nothing>() {
    override fun simplify() = this
  }

  data class Not<out T : Any>(val operand: BooleanExpression<T>) : BooleanExpression<T>() {
    override fun simplify() = when (this.operand) {
      is True -> False
      is False -> True
      else -> this
    }
  }

  data class Or<T : Any>(val operands: Set<BooleanExpression<T>>) : BooleanExpression<T>() {
    init {
      check(operands.isNotEmpty()) {
        "ApolloGraphQL: cannot create a 'Or' condition from an empty list"
      }
    }

    override fun simplify() = operands.filter {
      it != False
    }.map { it.simplify() }
        .let {
          when {
            it.contains(True) -> True
            it.isEmpty() -> False
            it.size == 1 -> it.first()
            else -> {
              Or(it.toSet())
            }
          }
        }

    override fun toString() = operands.joinToString(" | ")
  }

  data class And<T : Any>(val operands: Set<BooleanExpression<T>>) : BooleanExpression<T>() {
    init {
      check(operands.isNotEmpty()) {
        "ApolloGraphQL: cannot create a 'And' condition from an empty list"
      }
    }

    override fun simplify() = operands.filter {
      it != True
    }.map { it.simplify() }
        .let {
          when {
            it.contains(False) -> False
            it.isEmpty() -> True
            it.size == 1 -> it.first()
            else -> {
              And(it.toSet())
            }
          }
        }
  }

  data class Element<out T : Any>(
      val value: T,
  ) : BooleanExpression<T>() {
    override fun simplify() = this
  }
}

fun <T : Any> BooleanExpression<T>.or(vararg other: BooleanExpression<T>): BooleanExpression<T> = BooleanExpression.Or((other.toList() + this).toSet())
fun <T : Any> BooleanExpression<T>.and(vararg other: BooleanExpression<T>): BooleanExpression<T> = BooleanExpression.And((other.toList() + this).toSet())

fun <T : Any> or(vararg other: BooleanExpression<T>): BooleanExpression<T> = BooleanExpression.Or((other.toList()).toSet())
fun <T : Any> and(vararg other: BooleanExpression<T>): BooleanExpression<T> = BooleanExpression.And((other.toList()).toSet())
fun <T : Any> not(other: BooleanExpression<T>): BooleanExpression<T> = BooleanExpression.Not(other)
fun variable(name: String): BooleanExpression<BVariable> = BooleanExpression.Element(BVariable(name))
fun possibleTypes(vararg typenames: String): BooleanExpression<BPossibleTypes> = BooleanExpression.Element(BPossibleTypes(typenames.toSet()))

fun <T : Any> BooleanExpression<T>.evaluate(block: (T) -> Boolean): Boolean {
  return when (this) {
    BooleanExpression.True -> true
    BooleanExpression.False -> false
    is BooleanExpression.Not -> !operand.evaluate(block)
    is BooleanExpression.Or -> operands.any { it.evaluate(block) }
    is BooleanExpression.And -> operands.all { it.evaluate(block) }
    is BooleanExpression.Element -> block(value)
  }
}

fun BooleanExpression<BTerm>.evaluate(variables: Set<String>, typename: String): Boolean {
  return evaluate {
    when(it) {
      is BVariable -> variables.contains(it.name)
      is BPossibleTypes -> it.possibleTypes.contains(typename)
    }
  }
}

sealed class BTerm
data class BVariable(val name: String) : BTerm()
data class BPossibleTypes(val possibleTypes: Set<String>) : BTerm()
