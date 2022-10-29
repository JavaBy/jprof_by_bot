package by.jprof.telegram.bot.launchers.lambda.config

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.YouTubeRequestInitializer
import org.koin.core.qualifier.named
import org.koin.dsl.module

val youtubeModule = module {
    single {
        YouTube
            .Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                null
            )
            .setApplicationName("jprof-by-bot")
            .setYouTubeRequestInitializer(YouTubeRequestInitializer(get(named(TOKEN_YOUTUBE_API))))
            .build()
    }
}
