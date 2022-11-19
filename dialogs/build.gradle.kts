plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    api(libs.tgbotapi)
    implementation(libs.kotlinx.serialization.core)
}
