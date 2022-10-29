package by.jprof.telegram.bot.launchers.lambda.config

import by.jprof.telegram.bot.pins.scheduler.UnpinScheduler
import org.koin.core.qualifier.named
import org.koin.dsl.module
import software.amazon.awssdk.services.sfn.SfnAsyncClient
import by.jprof.telegram.bot.pins.sfn.scheduler.UnpinScheduler as SfnUnpinScheduler

val sfnModule = module {
    single {
        SfnAsyncClient.create()
    }

    single<UnpinScheduler> {
        SfnUnpinScheduler(
            sfn = get(),
            stateMachine = get(named(STATE_MACHINE_UNPINS))
        )
    }
}
