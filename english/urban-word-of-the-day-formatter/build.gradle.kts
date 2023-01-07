plugins {
    kotlin("jvm")
}

dependencies {
    implementation(projects.english.urbanWordOfTheDay)
    implementation(libs.tgbotapi)
}
