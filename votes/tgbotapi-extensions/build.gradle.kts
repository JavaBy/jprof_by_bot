plugins {
    kotlin("jvm")
}

dependencies {
    api(project(":votes"))
    api(libs.tgbotapi.core)

    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)
    testRuntimeOnly(libs.junit.jupiter.engine)
}
