package by.jprof.telegram.bot.utils.dynamodb

import software.amazon.awssdk.services.dynamodb.model.AttributeValue

fun String.toAttributeValue(): AttributeValue = AttributeValue.builder().s(this).build()

fun List<AttributeValue>.toAttributeValue() = AttributeValue.builder().l(this).build()

fun Map<String, AttributeValue>.toAttributeValue() = AttributeValue.builder().m(this).build()
