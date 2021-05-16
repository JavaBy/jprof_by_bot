package by.jprof.telegram.bot.votes.dynamodb.dao

import by.jprof.telegram.bot.utils.dynamodb.toAttributeValue
import by.jprof.telegram.bot.utils.dynamodb.toString
import by.jprof.telegram.bot.votes.dao.VotesDAO
import by.jprof.telegram.bot.votes.model.Votes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

class VotesDAO(
    private val dynamoDb: DynamoDbAsyncClient,
    private val table: String
) : VotesDAO {
    override suspend fun save(votes: Votes) {
        withContext(Dispatchers.IO) {
            dynamoDb.putItem {
                it.tableName(table)
                it.item(votes.toAttributes())
            }.await()
        }
    }

    override suspend fun get(id: String): Votes? {
        return withContext(Dispatchers.IO) {
            dynamoDb.getItem {
                it.tableName(table)
                it.key(mapOf("id" to id.toAttributeValue()))
            }.await()?.item()?.takeUnless { it.isEmpty() }?.toVotes()
        }
    }
}

fun Votes.toAttributes(): Map<String, AttributeValue> = mapOf(
    "id" to this.id.toAttributeValue(),
    "options" to this.options.map { it.toAttributeValue() }.toAttributeValue(),
    "votes" to this.votes.mapValues { (_, value) -> value.toAttributeValue() }.toAttributeValue()
)

fun Map<String, AttributeValue>.toVotes(): Votes = Votes(
    id = this["id"].toString("id"),
    options = this["options"]?.l()
        ?.mapNotNull { it.s() }
        ?: emptyList(),
    votes = this["votes"]?.m()
        ?.mapValues { (_, value) -> value.s() ?: "" }
        ?: emptyMap()
)
