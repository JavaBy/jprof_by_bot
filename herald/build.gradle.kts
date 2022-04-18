plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    application
}

application {
    mainClass.set("by.jprof.telegram.bot.herald.AppKt")
}

dependencies {
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.kaml)

    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.log4j.core)
}
