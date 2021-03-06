package by.jprof.telegram.bot.votes.model

data class Votes(
    val id: String,
    val options: List<String> = emptyList(),
    val votes: Map<String, String> = emptyMap()
) {
    fun count(option: String) = this.votes.count { (_, vote) -> vote == option }
}
