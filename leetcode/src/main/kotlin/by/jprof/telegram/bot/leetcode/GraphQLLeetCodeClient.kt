package by.jprof.telegram.bot.leetcode

import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import kotlinx.serialization.Required
import kotlinx.serialization.Serializable
import org.apache.logging.log4j.LogManager
import java.io.Closeable
import java.net.URL
import kotlin.reflect.KClass

class GraphQLLeetCodeClient : LeetCodeClient, Closeable {
    companion object {
        private val logger = LogManager.getLogger(GraphQLLeetCodeClient::class.java)!!
    }

    private val client = GraphQLKtorClient(
        url = URL("https://leetcode.com/graphql"),
    )

    override suspend fun questionData(slug: String): Question? {
        val request = QuestionDataQuery(QuestionDataQuery.Variables(slug))
        val response = client.execute(request)

        response.errors?.let { errors ->
            errors.forEach { error -> logger.error(error.message) }
            return null
        }

        return response.data?.question
    }

    override fun close() {
        client.close()
    }
}

@Serializable
private class QuestionDataQuery(
    override val variables: Variables,
) : GraphQLClientRequest<QuestionData> {
    @Required
    override val query: String = """
        query questionData(${"$"}slug: String!) {
          question(titleSlug: ${"$"}slug) {
            title
            titleSlug
            content
            isPaidOnly
            difficulty
            likes
            dislikes
            categoryTitle
          }
        }
    """.trimIndent()

    @Required
    override val operationName: String = "questionData"

    override fun responseType(): KClass<QuestionData> = QuestionData::class

    @Serializable
    data class Variables(
        val slug: String
    )
}

@Serializable
private data class QuestionData(
    val question: Question?,
)
