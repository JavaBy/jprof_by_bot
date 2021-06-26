plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    api(libs.tgbotapi.core)
    implementation(libs.kotlinx.serialization.core)
}
