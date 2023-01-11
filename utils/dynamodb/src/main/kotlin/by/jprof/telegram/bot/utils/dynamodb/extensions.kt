package by.jprof.telegram.bot.utils.dynamodb

import java.time.Instant
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

fun String.toAttributeValue(): AttributeValue = AttributeValue.builder().s(this).build()

fun Int.toAttributeValue(): AttributeValue = AttributeValue.builder().n(this.toString()).build()

fun Long.toAttributeValue(): AttributeValue = AttributeValue.builder().n(this.toString()).build()

fun Boolean.toAttributeValue(): AttributeValue = AttributeValue.builder().bool(this).build()

fun Instant.toAttributeValue(): AttributeValue = this.toEpochMilli().toAttributeValue()

fun List<AttributeValue>.toAttributeValue(): AttributeValue = AttributeValue.builder().l(this).build()

fun Map<String, AttributeValue>.toAttributeValue(): AttributeValue = AttributeValue.builder().m(this).build()

fun AttributeValue?.toString(name: String): String = this?.s() ?: throw IllegalStateException("Missing $name property")

fun AttributeValue?.toLong(name: String): Long =
    this?.n()?.toLong() ?: throw IllegalStateException("Missing $name property")

fun AttributeValue?.toBoolean(name: String): Boolean = this?.bool() ?: throw IllegalStateException("Missing $name property")

fun AttributeValue?.toInstant(name: String): Instant =
    Instant.ofEpochMilli(this?.n()?.toLong() ?: throw IllegalStateException("Missing $name property"))
