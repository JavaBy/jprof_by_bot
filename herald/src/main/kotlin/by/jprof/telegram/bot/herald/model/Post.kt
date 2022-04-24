package by.jprof.telegram.bot.herald.model

data class Post(
    val id: String,
    val frontmatter: Frontmatter,
    val content: String,
)
