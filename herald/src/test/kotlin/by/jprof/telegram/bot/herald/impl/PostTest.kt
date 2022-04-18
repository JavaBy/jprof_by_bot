package by.jprof.telegram.bot.herald.impl

import by.jprof.telegram.bot.herald.model.Frontmatter
import by.jprof.telegram.bot.herald.model.Post
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Named
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.nio.file.Path
import java.util.stream.Stream

internal class PostTest {
    @ParameterizedTest(name = "{0}")
    @MethodSource
    fun post(path: Path, post: Post) {
        assertEquals(
            post,
            post(path)
        )
    }

    companion object {
        @JvmStatic
        fun post() = Stream.of(
            Arguments.of(
                Named.of("001", "001".testResourcePath),
                Post(
                    Frontmatter(chats = listOf(-1001146107319, -1001585354456)),
                    "This is content.\n"
                )
            ),
            Arguments.of(
                Named.of("002", "002".testResourcePath),
                Post(
                    Frontmatter(chats = listOf(-1001146107319, -1001585354456), image = "002.png"),
                    "This is a\nmultiline content!\n"
                )
            ),
        )

        private val String.testResourcePath: Path
            get() = Path.of(this@Companion::class.java.classLoader.getResource("posts/$this.md").toURI())
    }
}
