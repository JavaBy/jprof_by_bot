package by.jprof.telegram.bot.runners.lambda.config

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.YouTubeRequestInitializer
import org.koin.core.qualifier.named
import org.koin.dsl.module

val youtubeModule = module {
    single {
        YouTube
            .Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                null
            )
            .setApplicationName("jprof-by-bot")
            .setYouTubeRequestInitializer(YouTubeRequestInitializer(get(named(TOKEN_YOUTUBE_API))))
            .build()
    }
}
