plugins {
    kotlin("jvm")
}

dependencies {
    implementation(libs.rome)

    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.mockk)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.log4j.core)
}
