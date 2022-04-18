package by.jprof.telegram.bot.herald.impl

import by.jprof.telegram.bot.herald.model.Post
import com.charleskorn.kaml.Yaml
import kotlinx.serialization.decodeFromString
import java.nio.file.Path
import kotlin.io.path.readText
import kotlin.text.RegexOption.DOT_MATCHES_ALL
import kotlin.text.RegexOption.MULTILINE

fun post(path: Path): Post? {
    val text = path.readText()
    val regex = Regex("-{3,}\\R(?<frontmatter>.*)\\R-{3,}\\s+(?<content>.*)", setOf(MULTILINE, DOT_MATCHES_ALL))

    return regex.find(text)?.let {
        val groups = it.groups as MatchNamedGroupCollection

        val frontmatter = groups["frontmatter"]?.value ?: return null
        val content = groups["content"]?.value ?: return null

        Post(Yaml.default.decodeFromString(frontmatter), content)
    }
}
