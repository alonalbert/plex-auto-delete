package com.alonalbert.pad.server.deluge.model.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.serializer
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

open class ListToPojoSerializer<T : Any>(
  private val kClass: KClass<T>,
) : KSerializer<T> {

  private val constructor = kClass.primaryConstructor
    ?: kClass.constructors.firstOrNull()
    ?: error("No constructor found for ${kClass.qualifiedName}")

  private val parameters = constructor.parameters

  private val properties: List<KProperty1<T, *>> = run {
    val memberProps = kClass.memberProperties.associateBy { it.name }
    parameters.map { param ->
      memberProps[param.name]
        ?: error("Property '${param.name}' not found in ${kClass.qualifiedName}")
    }
  }

  private val requiredCount: Int

  init {
    var seenNullable = false
    var required = 0
    for (param in parameters) {
      val isNullable = param.type.isMarkedNullable
      if (isNullable) {
        seenNullable = true
      } else if (seenNullable) {
        throw IllegalArgumentException(
          "Nullable fields must be the last fields by order in ${kClass.qualifiedName ?: kClass.simpleName}: " +
            "field '${param.name}' is non-nullable but follows nullable field(s)",
        )
      } else {
        required++
      }
    }
    requiredCount = required
  }

  override val descriptor: SerialDescriptor =
    buildClassSerialDescriptor(kClass.simpleName ?: "ListToPojo") {
      parameters.forEach { param ->
        val paramSerializer = serializer(param.type)
        element(
          elementName = param.name ?: "element",
          descriptor = paramSerializer.descriptor,
          isOptional = param.type.isMarkedNullable,
        )
      }
    }

  override fun deserialize(decoder: Decoder): T {
    val jsonDecoder = (decoder as? JsonDecoder)
      ?: error("ListToPojoSerializer can only be used with JSON decoding")
    val jsonArray = (jsonDecoder.decodeJsonElement() as? JsonArray)
      ?: error("Expected JsonArray for ${kClass.simpleName}")

    require(jsonArray.size >= requiredCount) {
      "${kClass.simpleName} JSON array must contain at least $requiredCount elements, got ${jsonArray.size}"
    }

    val args = arrayOfNulls<Any>(parameters.size)
    for (i in parameters.indices) {
      val param = parameters[i]
      if (i < jsonArray.size) {
        val element = jsonArray[i]
        if (element is JsonNull) {
          require(param.type.isMarkedNullable) {
            "Element at index $i for parameter '${param.name}' in ${kClass.simpleName} is null, but parameter is non-nullable"
          }
          args[i] = null
        } else {
          val paramSerializer = jsonDecoder.json.serializersModule.serializer(param.type)
          args[i] = jsonDecoder.json.decodeFromJsonElement(paramSerializer, element)
        }
      } else {
        args[i] = null
      }
    }

    return constructor.call(*args)
  }

  override fun serialize(encoder: Encoder, value: T) {
    val jsonEncoder = (encoder as? JsonEncoder)
      ?: error("ListToPojoSerializer can only be used with JSON encoding")

    val jsonArray = buildJsonArray {
      for (i in parameters.indices) {
        val param = parameters[i]
        val prop = properties[i]
        val propValue = prop.get(value)
        if (propValue == null) {
          add(JsonNull)
        } else {
          val paramSerializer = jsonEncoder.json.serializersModule.serializer(param.type)
          add(jsonEncoder.json.encodeToJsonElement(paramSerializer, propValue))
        }
      }
    }

    jsonEncoder.encodeJsonElement(jsonArray)
  }
}
