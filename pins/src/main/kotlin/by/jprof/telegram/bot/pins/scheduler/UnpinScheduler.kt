package by.jprof.telegram.bot.pins.scheduler

import by.jprof.telegram.bot.pins.dto.Unpin

interface UnpinScheduler {
    suspend fun scheduleUnpin(unpin: Unpin)
}
