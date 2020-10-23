// AUTO-GENERATED FILE. DO NOT MODIFY.
//
// This class was automatically generated by Apollo GraphQL plugin from the GraphQL queries it found.
// It should not be modified by hand.
//
package com.example.named_fragment_delegate.fragment

import com.apollographql.apollo.api.ResponseField
import com.apollographql.apollo.api.internal.ResponseAdapter
import com.apollographql.apollo.api.internal.ResponseReader
import com.apollographql.apollo.api.internal.ResponseWriter
import com.example.named_fragment_delegate.type.CustomType
import kotlin.Any
import kotlin.Array
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List

@Suppress("NAME_SHADOWING", "UNUSED_ANONYMOUS_PARAMETER", "LocalVariableName",
    "RemoveExplicitTypeArguments", "NestedLambdaShadowedImplicitParameter", "PropertyName",
    "RemoveRedundantQualifierName")
object HumanDetails_ResponseAdapter : ResponseAdapter<HumanDetails.DefaultImpl> {
  private val RESPONSE_FIELDS: Array<ResponseField> = arrayOf(
    ResponseField.forString("__typename", "__typename", null, false, null),
    ResponseField.forString("name", "name", null, false, null),
    ResponseField.forCustomType("profileLink", "profileLink", null, false, CustomType.URL, null),
    ResponseField.forObject("friendsConnection", "friendsConnection", null, false, null)
  )

  override fun fromResponse(reader: ResponseReader, __typename: String?): HumanDetails.DefaultImpl {
    return reader.run {
      var __typename: String? = __typename
      var name: String? = null
      var profileLink: Any? = null
      var friendsConnection: HumanDetails.FriendsConnection1? = null
      while(true) {
        when (selectField(RESPONSE_FIELDS)) {
          0 -> __typename = readString(RESPONSE_FIELDS[0])
          1 -> name = readString(RESPONSE_FIELDS[1])
          2 -> profileLink = readCustomType<Any>(RESPONSE_FIELDS[2] as ResponseField.CustomTypeField)
          3 -> friendsConnection = readObject<HumanDetails.FriendsConnection1>(RESPONSE_FIELDS[3]) { reader ->
            FriendsConnection1_ResponseAdapter.fromResponse(reader)
          }
          else -> break
        }
      }
      HumanDetails.DefaultImpl(
        __typename = __typename!!,
        name = name!!,
        profileLink = profileLink!!,
        friendsConnection = friendsConnection!!
      )
    }
  }

  override fun toResponse(writer: ResponseWriter, value: HumanDetails.DefaultImpl) {
    writer.writeString(RESPONSE_FIELDS[0], value.__typename)
    writer.writeString(RESPONSE_FIELDS[1], value.name)
    writer.writeCustom(RESPONSE_FIELDS[2] as ResponseField.CustomTypeField, value.profileLink)
    writer.writeObject(RESPONSE_FIELDS[3]) { writer ->
      FriendsConnection1_ResponseAdapter.toResponse(writer, value.friendsConnection)
    }
  }

  object Node1_ResponseAdapter : ResponseAdapter<HumanDetails.Node1> {
    private val RESPONSE_FIELDS: Array<ResponseField> = arrayOf(
      ResponseField.forString("__typename", "__typename", null, false, null),
      ResponseField.forString("name", "name", null, false, null)
    )

    override fun fromResponse(reader: ResponseReader, __typename: String?): HumanDetails.Node1 {
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
        HumanDetails.Node1(
          __typename = __typename!!,
          name = name!!
        )
      }
    }

    override fun toResponse(writer: ResponseWriter, value: HumanDetails.Node1) {
      writer.writeString(RESPONSE_FIELDS[0], value.__typename)
      writer.writeString(RESPONSE_FIELDS[1], value.name)
    }
  }

  object Edge1_ResponseAdapter : ResponseAdapter<HumanDetails.Edge1> {
    private val RESPONSE_FIELDS: Array<ResponseField> = arrayOf(
      ResponseField.forString("__typename", "__typename", null, false, null),
      ResponseField.forObject("node", "node", null, true, null)
    )

    override fun fromResponse(reader: ResponseReader, __typename: String?): HumanDetails.Edge1 {
      return reader.run {
        var __typename: String? = __typename
        var node: HumanDetails.Node1? = null
        while(true) {
          when (selectField(RESPONSE_FIELDS)) {
            0 -> __typename = readString(RESPONSE_FIELDS[0])
            1 -> node = readObject<HumanDetails.Node1>(RESPONSE_FIELDS[1]) { reader ->
              Node1_ResponseAdapter.fromResponse(reader)
            }
            else -> break
          }
        }
        HumanDetails.Edge1(
          __typename = __typename!!,
          node = node
        )
      }
    }

    override fun toResponse(writer: ResponseWriter, value: HumanDetails.Edge1) {
      writer.writeString(RESPONSE_FIELDS[0], value.__typename)
      if(value.node == null) {
        writer.writeObject(RESPONSE_FIELDS[1], null)
      } else {
        writer.writeObject(RESPONSE_FIELDS[1]) { writer ->
          Node1_ResponseAdapter.toResponse(writer, value.node)
        }
      }
    }
  }

  object FriendsConnection1_ResponseAdapter : ResponseAdapter<HumanDetails.FriendsConnection1> {
    private val RESPONSE_FIELDS: Array<ResponseField> = arrayOf(
      ResponseField.forString("__typename", "__typename", null, false, null),
      ResponseField.forList("edges", "edges", null, true, null)
    )

    override fun fromResponse(reader: ResponseReader, __typename: String?):
        HumanDetails.FriendsConnection1 {
      return reader.run {
        var __typename: String? = __typename
        var edges: List<HumanDetails.Edge1?>? = null
        while(true) {
          when (selectField(RESPONSE_FIELDS)) {
            0 -> __typename = readString(RESPONSE_FIELDS[0])
            1 -> edges = readList<HumanDetails.Edge1>(RESPONSE_FIELDS[1]) { reader ->
              reader.readObject<HumanDetails.Edge1> { reader ->
                Edge1_ResponseAdapter.fromResponse(reader)
              }
            }
            else -> break
          }
        }
        HumanDetails.FriendsConnection1(
          __typename = __typename!!,
          edges = edges
        )
      }
    }

    override fun toResponse(writer: ResponseWriter, value: HumanDetails.FriendsConnection1) {
      writer.writeString(RESPONSE_FIELDS[0], value.__typename)
      writer.writeList(RESPONSE_FIELDS[1], value.edges) { values, listItemWriter ->
        values?.forEach { value ->
          if(value == null) {
            listItemWriter.writeObject(null)
          } else {
            listItemWriter.writeObject { writer ->
              Edge1_ResponseAdapter.toResponse(writer, value)
            }
          }
        }
      }
    }
  }
}