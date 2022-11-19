plugins {
    kotlin("jvm")
}

dependencies {
    api(project.projects.core)
    api(libs.tgbotapi)
    implementation(project.projects.dialogs)
    implementation(project.projects.votes)
    implementation(project.projects.votes.tgbotapiExtensions)
    implementation(project.projects.votes.votingProcessor)
    implementation(libs.log4j.api)

    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.mockk)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.log4j.core)
}
