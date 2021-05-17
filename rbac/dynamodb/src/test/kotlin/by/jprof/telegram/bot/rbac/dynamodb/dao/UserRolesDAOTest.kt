package by.jprof.telegram.bot.rbac.dynamodb.dao

import by.jprof.telegram.bot.utils.aws_junit5.Endpoint
import me.madhead.aws_junit5.common.AWSClient
import me.madhead.aws_junit5.dynamo.v2.DynamoDB
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient

@Tag("db")
@ExtendWith(DynamoDB::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class UserRolesDAOTest {
    @AWSClient(endpoint = Endpoint::class)
    private lateinit var dynamoDB: DynamoDbAsyncClient
    private lateinit var sut: UserRolesDAO

    @BeforeAll
    internal fun setup() {
        sut = UserRolesDAO(dynamoDB, "user-roles")
    }

    @Test
    fun save() {
        TODO()
    }

    @Test
    fun get() {
        TODO()
    }
}
