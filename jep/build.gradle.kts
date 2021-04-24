plugins {
    kotlin("jvm")
}

dependencies {
    api(project(":core"))
    api(project(":votes"))
    implementation(project(":votes:tgbotapi-extensions"))
    implementation(libs.log4j.api)
    implementation(libs.jsoup)

    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.mockk)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.log4j.core)
}
