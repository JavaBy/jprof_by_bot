plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("com.github.johnrengelman.shadow")
}

dependencies {
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.tgbotapi)
    implementation(projects.english.urbanDictionary)
    implementation(projects.english.urbanWordOfTheDay.dynamodb)
    implementation(projects.english.languageRooms.dynamodb)
    implementation(projects.english.urbanWordOfTheDayFormatter)
    implementation(projects.pins.sfn)

    implementation(libs.bundles.aws.lambda)
    implementation(libs.koin.core)
    implementation(libs.kotlinx.coroutines.jdk8)
    implementation(libs.bundles.log4j)
}
