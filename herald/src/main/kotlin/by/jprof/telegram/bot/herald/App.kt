package by.jprof.telegram.bot.herald

import by.jprof.telegram.bot.herald.impl.post
import by.jprof.telegram.bot.herald.impl.postFile

suspend fun main(args: Array<String>) {
    val postFile = postFile() ?: run { println("No post for today"); return }

    println("Today's post: $postFile")

    val post = post(postFile) ?: run { println("Cannot parse the file"); return }

    println("Parsed the post: $post")
}
