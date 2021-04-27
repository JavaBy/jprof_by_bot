plugins {
    kotlin("jvm")
}

dependencies {
    api(libs.bundles.tgbotapi)
    implementation(libs.log4j.api)

    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.log4j.core)
}
