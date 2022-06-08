package by.jprof.telegram.bot.eval.evaluator.middleware

import by.jprof.telegram.bot.eval.dto.EvalEvent
import by.jprof.telegram.bot.eval.dto.EvalResponse
import by.jprof.telegram.bot.eval.dto.Language
import org.apache.logging.log4j.LogManager
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.io.path.absolutePathString
import kotlin.io.path.writeText

class JavaScriptEval : Eval {
    companion object {
        private val logger = LogManager.getLogger(JavaScriptEval::class.java)
    }

    override suspend fun eval(payload: EvalEvent): EvalResponse? {
        val file = kotlin.io.path.createTempFile(prefix = "JavaScriptEval", suffix = ".js")

        logger.info("Created temp file: {}", file)

        file.writeText(payload.code)

        try {
            val proc = ProcessBuilder("node", file.absolutePathString())
                .directory(file.parent.toFile())
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE)
                .start()

            proc.waitFor(30, TimeUnit.SECONDS)

            val result = proc.exitValue()
            val stdout = proc.inputStream.bufferedReader().use { it.readText() }
            val stderr = proc.errorStream.bufferedReader().use { it.readText() }

            logger.info("Process finished with status: {}. stdout: {}, stderr: {}", result, stdout, stderr)

            return EvalResponse.Successful(Language.JAVASCRIPT, stdout, stderr)
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }
}
