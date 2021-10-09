package by.jprof.telegram.bot.kotlin.dynamodb.dao

import by.jprof.telegram.bot.kotlin.model.KotlinMentions
import by.jprof.telegram.bot.kotlin.model.UserStatistics
import by.jprof.telegram.bot.utils.aws_junit5.Endpoint
import kotlinx.coroutines.runBlocking
import me.madhead.aws_junit5.common.AWSClient
import me.madhead.aws_junit5.dynamo.v2.DynamoDB
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import java.time.Instant

@Tag("db")
@ExtendWith(DynamoDB::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class KotlinMentionsDAOTest {
    @AWSClient(endpoint = Endpoint::class)
    private lateinit var dynamoDB: DynamoDbAsyncClient
    private lateinit var sut: KotlinMentionsDAO

    @BeforeAll
    internal fun setup() {
        sut = KotlinMentionsDAO(dynamoDB, "kotlin-mentions")
    }

    @Test
    fun save() = runBlocking {
        sut.save(kotlinMentions)
    }

    @Test
    fun get() = runBlocking {
        Assertions.assertEquals(kotlinMentions, sut.get(42))
    }

    @Test
    fun getUnexisting() = runBlocking {
        Assertions.assertNull(sut.get(-42))
    }

    private val kotlinMentions
        get() = KotlinMentions(
            chat = 42L,
            lastMention = Instant.ofEpochMilli(1227376823000),
            usersStatistics = mapOf(
                1L to UserStatistics(2, Instant.ofEpochMilli(1227376823000)),
                2L to UserStatistics(1, Instant.ofEpochMilli(1214996810000)),
            )
        )
}
