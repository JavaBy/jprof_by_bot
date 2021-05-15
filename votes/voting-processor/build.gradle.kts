plugins {
    kotlin("jvm")
}

dependencies {
    api(libs.tgbotapi.core)
    implementation(project.projects.votes)
    implementation(project.projects.votes.tgbotapiExtensions)
    implementation(libs.tgbotapi.extensions.api)
    implementation(libs.log4j.api)

    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.mockk)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.log4j.core)
}
