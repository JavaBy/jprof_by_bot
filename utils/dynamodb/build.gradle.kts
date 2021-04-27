plugins {
    kotlin("jvm")
}

dependencies {
    api(libs.dynamodb)

    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.aws.junit5.dynamo.v2)
    testRuntimeOnly(libs.junit.jupiter.engine)
}
