plugins {
    kotlin("jvm")
}

dependencies {
    api(project.projects.core)
    api(libs.tgbotapi)
    implementation(libs.log4j.api)
    implementation(libs.skija.linux)

    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.mockk)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.log4j.core)
}
