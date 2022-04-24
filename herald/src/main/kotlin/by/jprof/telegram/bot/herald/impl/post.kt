package by.jprof.telegram.bot.herald.impl

import by.jprof.telegram.bot.herald.model.Frontmatter
import by.jprof.telegram.bot.herald.model.Post
import com.charleskorn.kaml.Yaml
import kotlinx.serialization.decodeFromString
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.readText
import kotlin.io.path.relativeTo
import kotlin.text.RegexOption.DOT_MATCHES_ALL
import kotlin.text.RegexOption.MULTILINE

fun post(path: Path): Post? {
    val cwd = Path("")
    val text = path.readText()
    val regex = Regex("-{3,}\\R(?<frontmatter>.*)\\R-{3,}\\s+(?<content>.*)", setOf(MULTILINE, DOT_MATCHES_ALL))

    return regex.find(text)?.let {
        val groups = it.groups as MatchNamedGroupCollection

        val frontmatter = groups["frontmatter"]?.value ?: return null
        val content = groups["content"]?.value ?: return null
        val relativePath = path.relativeTo(cwd.toAbsolutePath())

        Post(
            relativePath.parent.toString() + "/" + relativePath.nameWithoutExtension,
            Yaml.default.decodeFromString<Frontmatter>(frontmatter).run {
                if (this.image == null) {
                    this
                } else {
                    this.copy(
                        image = path.resolveSibling(this.image).absolutePathString()
                    )
                }
            },
            content,
        )
    }
}
