package by.jprof.telegram.bot.inside_java.blog

import com.rometools.rome.io.SyndFeedInput
import org.junit.jupiter.api.Test

class Test {
    @Test
    fun test() {
        val resource = this::class.java.getResourceAsStream("/feed.xml")!!.bufferedReader()
        val input = SyndFeedInput()
        val feed = input.build(resource)

        println(feed)
    }
}
