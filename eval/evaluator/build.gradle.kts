plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}

dependencies {
    api(project.projects.eval.dto)
    implementation(libs.bundles.aws.lambda)
    implementation(libs.koin.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.bundles.log4j)

    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.log4j.core)
}
