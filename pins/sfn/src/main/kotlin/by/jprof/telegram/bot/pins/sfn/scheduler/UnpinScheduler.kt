package by.jprof.telegram.bot.pins.sfn.scheduler

import by.jprof.telegram.bot.pins.dto.Unpin
import by.jprof.telegram.bot.pins.scheduler.UnpinScheduler
import kotlinx.coroutines.future.await
import software.amazon.awssdk.services.sfn.SfnAsyncClient

class UnpinScheduler(
    val sfn: SfnAsyncClient,
    val stateMachine: String,
) : UnpinScheduler {
    override suspend fun scheduleUnpin(unpin: Unpin) {
        sfn.startExecution {
            it.stateMachineArn(stateMachine)
            it.name("${unpin.chatId}_${unpin.messageId}_${unpin.ttl}")
            it.input("""{
                |  "messageId": ${unpin.messageId},
                |  "chatId": ${unpin.chatId},
                |  "userId": ${unpin.userId},
                |  "ttl": ${unpin.ttl}
                |}""".trimMargin())
        }.await()
    }
}
