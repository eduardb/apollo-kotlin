// AUTO-GENERATED FILE. DO NOT MODIFY.
//
// This class was automatically generated by Apollo GraphQL plugin from the GraphQL queries it found.
// It should not be modified by hand.
//
package com.example.reserved_keywords

import com.apollographql.apollo.api.ResponseField
import com.apollographql.apollo.api.internal.ResponseAdapter
import com.apollographql.apollo.api.internal.ResponseReader
import com.apollographql.apollo.api.internal.ResponseWriter
import com.example.reserved_keywords.type.CustomType
import kotlin.Array
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List

@Suppress("NAME_SHADOWING", "UNUSED_ANONYMOUS_PARAMETER", "LocalVariableName",
    "RemoveExplicitTypeArguments", "NestedLambdaShadowedImplicitParameter", "PropertyName",
    "RemoveRedundantQualifierName")
object TestQuery_ResponseAdapter : ResponseAdapter<TestQuery.Data> {
  private val RESPONSE_FIELDS: Array<ResponseField> = arrayOf(
    ResponseField.forObject("yield", "hero", null, true, null),
    ResponseField.forList("objects", "search", mapOf<String, Any>(
      "text" to "abc"), true, null)
  )

  override fun fromResponse(reader: ResponseReader, __typename: String?): TestQuery.Data {
    return reader.run {
      var yield_: TestQuery.Yield_? = null
      var objects: List<TestQuery.Object?>? = null
      while(true) {
        when (selectField(RESPONSE_FIELDS)) {
          0 -> yield_ = readObject<TestQuery.Yield_>(RESPONSE_FIELDS[0]) { reader ->
            TestQuery_ResponseAdapter.Yield__ResponseAdapter.fromResponse(reader)
          }
          1 -> objects = readList<TestQuery.Object>(RESPONSE_FIELDS[1]) { reader ->
            reader.readObject<TestQuery.Object> { reader ->
              TestQuery_ResponseAdapter.Object_ResponseAdapter.fromResponse(reader)
            }
          }
          else -> break
        }
      }
      TestQuery.Data(
        yield_ = yield_,
        objects = objects
      )
    }
  }

  override fun toResponse(writer: ResponseWriter, value: TestQuery.Data) {
    if(value.yield_ == null) {
      writer.writeObject(RESPONSE_FIELDS[0], null)
    } else {
      writer.writeObject(RESPONSE_FIELDS[0]) { writer ->
        TestQuery_ResponseAdapter.Yield__ResponseAdapter.toResponse(writer, value.yield_)
      }
    }
    writer.writeList(RESPONSE_FIELDS[1], value.objects) { values, listItemWriter ->
      values?.forEach { value ->
        if(value == null) {
          listItemWriter.writeObject(null)
        } else {
          listItemWriter.writeObject { writer ->
            TestQuery_ResponseAdapter.Object_ResponseAdapter.toResponse(writer, value)
          }
        }
      }
    }
  }

  object Yield__ResponseAdapter : ResponseAdapter<TestQuery.Yield_> {
    private val RESPONSE_FIELDS: Array<ResponseField> = arrayOf(
      ResponseField.forString("__typename", "__typename", null, false, null),
      ResponseField.forCustomType("it", "id", null, false, CustomType.ID, null),
      ResponseField.forString("name", "name", null, false, null)
    )

    override fun fromResponse(reader: ResponseReader, __typename: String?): TestQuery.Yield_ {
      return reader.run {
        var __typename: String? = __typename
        var it_: String? = null
        var name: String? = null
        while(true) {
          when (selectField(RESPONSE_FIELDS)) {
            0 -> __typename = readString(RESPONSE_FIELDS[0])
            1 -> it_ = readCustomType<String>(RESPONSE_FIELDS[1] as ResponseField.CustomTypeField)
            2 -> name = readString(RESPONSE_FIELDS[2])
            else -> break
          }
        }
        TestQuery.Yield_(
          __typename = __typename!!,
          it_ = it_!!,
          name = name!!
        )
      }
    }

    override fun toResponse(writer: ResponseWriter, value: TestQuery.Yield_) {
      writer.writeString(RESPONSE_FIELDS[0], value.__typename)
      writer.writeCustom(RESPONSE_FIELDS[1] as ResponseField.CustomTypeField, value.it_)
      writer.writeString(RESPONSE_FIELDS[2], value.name)
    }
  }

  object Character_ResponseAdapter : ResponseAdapter<TestQuery.Character> {
    private val RESPONSE_FIELDS: Array<ResponseField> = arrayOf(
      ResponseField.forString("__typename", "__typename", null, false, null),
      ResponseField.forString("name", "name", null, false, null)
    )

    override fun fromResponse(reader: ResponseReader, __typename: String?): TestQuery.Character {
      return reader.run {
        var __typename: String? = __typename
        var name: String? = null
        while(true) {
          when (selectField(RESPONSE_FIELDS)) {
            0 -> __typename = readString(RESPONSE_FIELDS[0])
            1 -> name = readString(RESPONSE_FIELDS[1])
            else -> break
          }
        }
        TestQuery.Character(
          __typename = __typename!!,
          name = name!!
        )
      }
    }

    override fun toResponse(writer: ResponseWriter, value: TestQuery.Character) {
      writer.writeString(RESPONSE_FIELDS[0], value.__typename)
      writer.writeString(RESPONSE_FIELDS[1], value.name)
    }
  }

  object OtherObject_ResponseAdapter : ResponseAdapter<TestQuery.OtherObject> {
    private val RESPONSE_FIELDS: Array<ResponseField> = arrayOf(
      ResponseField.forString("__typename", "__typename", null, false, null)
    )

    override fun fromResponse(reader: ResponseReader, __typename: String?): TestQuery.OtherObject {
      return reader.run {
        var __typename: String? = __typename
        while(true) {
          when (selectField(RESPONSE_FIELDS)) {
            0 -> __typename = readString(RESPONSE_FIELDS[0])
            else -> break
          }
        }
        TestQuery.OtherObject(
          __typename = __typename!!
        )
      }
    }

    override fun toResponse(writer: ResponseWriter, value: TestQuery.OtherObject) {
      writer.writeString(RESPONSE_FIELDS[0], value.__typename)
    }
  }

  object Object_ResponseAdapter : ResponseAdapter<TestQuery.Object> {
    private val RESPONSE_FIELDS: Array<ResponseField> = arrayOf(
      ResponseField.forString("__typename", "__typename", null, false, null)
    )

    override fun fromResponse(reader: ResponseReader, __typename: String?): TestQuery.Object {
      val typename = __typename ?: reader.readString(RESPONSE_FIELDS[0])
      return when(typename) {
        "Droid" -> TestQuery_ResponseAdapter.Character_ResponseAdapter.fromResponse(reader, typename)
        "Human" -> TestQuery_ResponseAdapter.Character_ResponseAdapter.fromResponse(reader, typename)
        else -> TestQuery_ResponseAdapter.OtherObject_ResponseAdapter.fromResponse(reader, typename)
      }
    }

    override fun toResponse(writer: ResponseWriter, value: TestQuery.Object) {
      when(value) {
        is TestQuery.Character -> TestQuery_ResponseAdapter.Character_ResponseAdapter.toResponse(writer, value)
        is TestQuery.OtherObject -> TestQuery_ResponseAdapter.OtherObject_ResponseAdapter.toResponse(writer, value)
      }
    }
  }
}