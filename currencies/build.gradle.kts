plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    implementation(platform(libs.ktor.bom))

    api(project.projects.core)
    api(libs.tgbotapi.core)
    implementation(libs.tgbotapi.extensions.api)
    implementation(libs.log4j.api)
    implementation(libs.ktor.client.apache)
    implementation(libs.ktor.client.serialization)

    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.mockk)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.log4j.core)
}
