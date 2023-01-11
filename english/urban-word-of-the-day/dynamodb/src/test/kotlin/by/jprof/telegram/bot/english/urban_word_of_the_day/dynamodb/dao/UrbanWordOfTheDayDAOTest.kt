package by.jprof.telegram.bot.english.urban_word_of_the_day.dynamodb.dao

import by.jprof.telegram.bot.english.urban_word_of_the_day.model.UrbanWordOfTheDay
import by.jprof.telegram.bot.utils.aws_junit5.Endpoint
import java.time.LocalDate
import kotlinx.coroutines.runBlocking
import me.madhead.aws_junit5.common.AWSClient
import me.madhead.aws_junit5.dynamo.v2.DynamoDB
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient

@Tag("db")
@ExtendWith(DynamoDB::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class UrbanWordOfTheDayDAOTest {
    @AWSClient(endpoint = Endpoint::class)
    private lateinit var dynamoDB: DynamoDbAsyncClient
    private lateinit var sut: UrbanWordOfTheDayDAO

    @BeforeAll
    internal fun setup() {
        sut = UrbanWordOfTheDayDAO(dynamoDB, "urbanWordsOfTheDay")
    }

    @Test
    fun save() = runBlocking {
        sut.save(urbanWordOfTheDay.copy(date = LocalDate.MAX))
    }

    @Test
    fun get() = runBlocking {
        assertEquals(urbanWordOfTheDay, sut.get(now))
    }

    @Test
    fun getUnexisting() = runBlocking {
        assertNull(sut.get(LocalDate.MIN))
    }

    private val now = LocalDate.parse("2022-12-05")
    private val urbanWordOfTheDay
        get() = UrbanWordOfTheDay(
            date = now,
            word = "word",
            definition = "definition",
            example = "example",
            permalink = "https://word.urbanup.com",
        )
}
