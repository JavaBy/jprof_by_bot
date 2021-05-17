package by.jprof.telegram.bot.rbac.dynamodb.dao

import by.jprof.telegram.bot.rbac.dao.UserRolesDAO
import by.jprof.telegram.bot.rbac.model.UserRoles
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient

class UserRolesDAO(
    private val dynamoDb: DynamoDbAsyncClient,
    private val table: String
) : UserRolesDAO {
    override suspend fun save(userRoles: UserRoles) {
        TODO()
    }

    override suspend fun get(user: Long, chat: Long): UserRoles? {
        TODO()
    }
}
