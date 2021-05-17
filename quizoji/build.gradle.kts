plugins {
    kotlin("jvm")
}

dependencies {
    api(project.projects.core)
    api(libs.tgbotapi.core)
    implementation(libs.log4j.api)
    implementation(libs.tgbotapi.extensions.api)
    implementation(libs.tgbotapi.extensions.utils)

    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.mockk)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.log4j.core)
}
