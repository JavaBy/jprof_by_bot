plugins {
    kotlin("jvm")
}

dependencies {
    api(project.projects.core)
    api(libs.tgbotapi)
    api(project.projects.monies)
    implementation(project.projects.pins.dto)
    implementation(project.projects.pins.scheduler)
    implementation(libs.log4j.api)

    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.mockk)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.log4j.core)
}
