package by.jprof.telegram.bot.herald

import by.jprof.telegram.bot.herald.impl.post
import by.jprof.telegram.bot.herald.impl.postFile
import by.jprof.telegram.bot.herald.impl.send

suspend fun main() {
    val postFile = postFile() ?: run { println("No post for today"); return }

    println("Today's post: $postFile")

    val post = post(postFile) ?: run { println("Cannot parse the file"); return }

    println("Parsed the post: $post")

    send(post)

    println("Done")
}
