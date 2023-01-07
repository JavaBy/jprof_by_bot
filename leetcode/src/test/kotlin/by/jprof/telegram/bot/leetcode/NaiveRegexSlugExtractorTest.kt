package by.jprof.telegram.bot.leetcode

import java.util.stream.Stream
import kotlin.streams.asStream
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class NaiveRegexSlugExtractorTest {
    @ParameterizedTest
    @MethodSource
    fun extract(text: String, slug: String?) {
        assertEquals(slug, NaiveRegexSlugExtractor(text))
    }

    private fun extract(): Stream<Arguments> = sequence {
        yield(Arguments.of("", null))
        yield(Arguments.of("  ", null))
        yield(Arguments.of("test", null))
        yield(Arguments.of("https://google.com", null))
        yield(Arguments.of("https://leetcode.com/problems/two-sum/", "two-sum"))
        yield(Arguments.of("https://leetcode.com/problems/two-sum", "two-sum"))
        yield(Arguments.of("https://leetcode.com/problems/3sum/", "3sum"))
        yield(Arguments.of("https://leetcode.com/problems/3sum", "3sum"))
        yield(Arguments.of(
            "https://leetcode.com/problems/minimum-number-of-arrows-to-burst-balloons/description/",
            "minimum-number-of-arrows-to-burst-balloons"
        ))
        yield(Arguments.of(
            "https://leetcode.com/problems/minimum-number-of-arrows-to-burst-balloons/discussion",
            "minimum-number-of-arrows-to-burst-balloons"
        ))
        yield(Arguments.of(
            "https://leetcode.com/problems/minimum-number-of-arrows-to-burst-balloons/solutions",
            "minimum-number-of-arrows-to-burst-balloons"
        ))
        yield(Arguments.of(
            "https://leetcode.com/problems/minimum-number-of-arrows-to-burst-balloons/submissions/",
            "minimum-number-of-arrows-to-burst-balloons"
        ))
    }.asStream()
}
