package by.jprof.telegram.bot.eval.evaluator.config

import by.jprof.telegram.bot.eval.evaluator.middleware.Eval
import by.jprof.telegram.bot.eval.evaluator.middleware.EvalPipeline
import by.jprof.telegram.bot.eval.evaluator.middleware.JavaScriptEval
import org.koin.core.qualifier.named
import org.koin.dsl.module

val pipelineModule = module {
    single {
        EvalPipeline(getAll())
    }

    single<Eval>(named("JavaScriptEval")) {
        JavaScriptEval()
    }
}
