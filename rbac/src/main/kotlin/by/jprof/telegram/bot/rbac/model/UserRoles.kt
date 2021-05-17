package by.jprof.telegram.bot.rbac.model

data class UserRoles(
    val user: Long,
    val chat: Long,
    val roles: List<Role> = emptyList(),
)
