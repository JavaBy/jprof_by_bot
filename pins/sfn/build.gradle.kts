plugins {
    kotlin("jvm")
}

dependencies {
    api(project.projects.pins)
    api(project.projects.pins.dto)
    api(project.projects.pins.scheduler)
    api(libs.sfn)
    implementation(libs.kotlinx.coroutines.jdk8)

    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.aws.junit5.dynamo.v2)
    testImplementation(project.projects.utils.awsJunit5)
    testRuntimeOnly(libs.junit.jupiter.engine)
}
