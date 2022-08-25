package com.apollographql.apollo.internal

import com.google.common.truth.Truth.assertThat
import com.apollographql.apollo.api.Operation
import com.apollographql.apollo.api.ResponseField
import com.apollographql.apollo.api.ScalarTypeAdapters
import com.apollographql.apollo.internal.response.RealResponseWriter
import org.junit.Test

class RealResponseWriterTest {

  @Test
  fun name() {
    val writer = RealResponseWriter(Operation.Variables(), ScalarTypeAdapters.DEFAULT)

    val field = ResponseField.forObject("user", "user", emptyMap(), false, emptyList())
    val oldValue =
          mapOf(
              "appProps" to RealResponseWriter.FieldDescriptor(ResponseField.forObject("appProps", "appProps", emptyMap(), false, emptyList()),
                  mapOf(
                      "f1" to RealResponseWriter.FieldDescriptor(ResponseField.forBoolean("f1", "f1", emptyMap(), false, emptyList()), true),
                      "f2" to RealResponseWriter.FieldDescriptor(ResponseField.forObject("f2", "f2", emptyMap(), false, emptyList()),
                        mapOf("a" to RealResponseWriter.FieldDescriptor(ResponseField.forBoolean("a", "a", emptyMap(), false, emptyList()), true)),
                      ),
                  )
              ),
              "mobile" to RealResponseWriter.FieldDescriptor(ResponseField.forString("mobile", "mobile", emptyMap(), false, emptyList()), "+225"),
              "int" to RealResponseWriter.FieldDescriptor(ResponseField.forInt("int", "int", emptyMap(), false, emptyList()), 1),
          )
    val newValue: Map<String, RealResponseWriter.FieldDescriptor> =
        mapOf(
            "appProps" to RealResponseWriter.FieldDescriptor(ResponseField.forObject("appProps", "appProps", emptyMap(), false, emptyList()),
                mapOf(
                    "f2" to RealResponseWriter.FieldDescriptor(ResponseField.forObject("f2", "f2", emptyMap(), false, emptyList()),
                        mapOf("b" to RealResponseWriter.FieldDescriptor(ResponseField.forBoolean("b", "b", emptyMap(), false, emptyList()), true)),
                    ),
                    "f3" to RealResponseWriter.FieldDescriptor(ResponseField.forBoolean("f3", "f3", emptyMap(), false, emptyList()), true),
                )
            ),
            "float" to RealResponseWriter.FieldDescriptor(ResponseField.forDouble("float", "float", emptyMap(), false, emptyList()), 1.1),
        )
    val actual = writer.deepMergeObjects(field, oldValue, newValue)

    val expected =
        mapOf(
            "appProps" to RealResponseWriter.FieldDescriptor(ResponseField.forObject("appProps", "appProps", emptyMap(), false, emptyList()),
                mapOf(
                    "f1" to RealResponseWriter.FieldDescriptor(ResponseField.forBoolean("f1", "f1", emptyMap(), false, emptyList()), true),
                    "f2" to RealResponseWriter.FieldDescriptor(ResponseField.forObject("f2", "f2", emptyMap(), false, emptyList()),
                        mapOf(
                            "a" to RealResponseWriter.FieldDescriptor(ResponseField.forBoolean("a", "a", emptyMap(), false, emptyList()), true),
                            "b" to RealResponseWriter.FieldDescriptor(ResponseField.forBoolean("b", "b", emptyMap(), false, emptyList()), true)
                        ),
                    ),
                    "f3" to RealResponseWriter.FieldDescriptor(ResponseField.forBoolean("f3", "f3", emptyMap(), false, emptyList()), true),
                )
            ),
            "mobile" to RealResponseWriter.FieldDescriptor(ResponseField.forString("mobile", "mobile", emptyMap(), false, emptyList()), "+225"),
            "int" to RealResponseWriter.FieldDescriptor(ResponseField.forInt("int", "int", emptyMap(), false, emptyList()), 1),
            "float" to RealResponseWriter.FieldDescriptor(ResponseField.forDouble("float", "float", emptyMap(), false, emptyList()), 1.1)
        )

    assertThat(writer.rawFieldValues(actual.value as Map<String, RealResponseWriter.FieldDescriptor>)).isEqualTo(writer.rawFieldValues(expected))
  }
}
