package by.jprof.telegram.bot.times.timezones.dynamodb.dao

import by.jprof.telegram.bot.times.timezones.model.TimeZone
import by.jprof.telegram.bot.utils.aws_junit5.Endpoint
import kotlinx.coroutines.runBlocking
import me.madhead.aws_junit5.common.AWSClient
import me.madhead.aws_junit5.dynamo.v2.DynamoDB
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient

@Tag("db")
@ExtendWith(DynamoDB::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class TimeZoneDAOTest {
    @AWSClient(endpoint = Endpoint::class)
    private lateinit var dynamoDB: DynamoDbAsyncClient
    private lateinit var sut: TimeZoneDAO

    @BeforeAll
    internal fun setup() {
        sut = TimeZoneDAO(dynamoDB, "timezones")
    }

    @Test
    fun save() = runBlocking {
        sut.save(timeZone.copy(user = 2))
    }

    @Test
    fun get() = runBlocking {
        assertEquals(timeZone, sut.get(1, 1))
    }

    @Test
    fun getByUsername() = runBlocking {
        assertEquals(timeZone.copy(chat = 3), sut.getByUsername("test", 3))
    }

    @Test
    fun getUnexisting() = runBlocking {
        assertNull(sut.get(-1, -2))
        assertNull(sut.getByUsername("unexisting", -2))
    }

    private val timeZone
        get() = TimeZone(
            user = 1L,
            username = "test",
            chat = 1L,
            zoneId = "UTC",
        )
}
