package by.jprof.telegram.bot.rbac.dao

import by.jprof.telegram.bot.rbac.model.UserRoles

interface UserRolesDAO {
    suspend fun save(userRoles: UserRoles)

    suspend fun get(user: Long, chat: Long): UserRoles?
}
