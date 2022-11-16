plugins {
    kotlin("jvm")
}

dependencies {
    api(project.projects.votes)
    api(libs.tgbotapi)

    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)
    testRuntimeOnly(libs.junit.jupiter.engine)
}
