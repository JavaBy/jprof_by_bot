package by.jprof.telegram.bot.english.urban_word_of_the_day.dynamodb.dao

import by.jprof.telegram.bot.english.urban_word_of_the_day.model.UrbanWordOfTheDay
import java.time.LocalDate
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

internal class UrbanWordOfTheDayTest {
    @Test
    fun toAttributes() {
        assertEquals(
            attributes,
            urbanWordOfTheDay.toAttributes()
        )
    }

    @Test
    fun toUrbanWordOfTheDay() {
        assertEquals(
            urbanWordOfTheDay,
            attributes.toUrbanWordOfTheDay()
        )
    }

    private val now = LocalDate.now()
    private val urbanWordOfTheDay
        get() = UrbanWordOfTheDay(
            date = now,
            word = "word",
            definition = "definition",
            example = "example",
            permalink = "https://word.urbanup.com",
        )
    private val attributes
        get() = mapOf(
            "date" to AttributeValue.builder().s(now.toString()).build(),
            "word" to AttributeValue.builder().s("word").build(),
            "definition" to AttributeValue.builder().s("definition").build(),
            "example" to AttributeValue.builder().s("example").build(),
            "permalink" to AttributeValue.builder().s("https://word.urbanup.com").build(),
        )
}
