plugins {
    kotlin("jvm")
}

dependencies {
    api(project.projects.core)
    api(project.projects.votes)
    api(project.projects.votes.votingProcessor)
    api(libs.tgbotapi)
    implementation(project.projects.votes.tgbotapiExtensions)
    implementation(libs.log4j.api)
    implementation(libs.jsoup)

    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.mockk)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.log4j.core)
}
