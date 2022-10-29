plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    application
}

application {
    mainClass.set("by.jprof.telegram.bot.herald.AppKt")
}

dependencies {
    implementation(platform(libs.ktor.bom))

    implementation(libs.kotlinx.serialization.core)
    implementation(libs.kaml)
    implementation(libs.tgbotapi.extensions.api)
    implementation(project.projects.votes.dynamodb)
    implementation(project.projects.votes.tgbotapiExtensions)
    implementation(project.projects.votes.dynamodb)

    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.log4j.core)
}
