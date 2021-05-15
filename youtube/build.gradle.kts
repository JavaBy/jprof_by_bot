plugins {
    kotlin("jvm")
}

dependencies {
    api(project.projects.core)
    api(project.projects.votes)
    api(libs.tgbotapi.core)
    api(libs.google.api.services.youtube)
    implementation(project.projects.votes.votingProcessor)
    implementation(project.projects.votes.tgbotapiExtensions)
    implementation(libs.tgbotapi.extensions.api)
    implementation(libs.tgbotapi.extensions.utils)
    implementation(libs.log4j.api)

    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.mockk)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.log4j.core)
}
