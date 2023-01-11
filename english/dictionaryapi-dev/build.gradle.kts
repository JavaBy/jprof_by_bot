plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    implementation(platform(libs.ktor.bom))

    implementation(libs.ktor.client.apache)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.log4j.api)

    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.mockk)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.log4j.core)
}

tasks {
    val integrationTest by registering(Test::class) {
        group = LifecycleBasePlugin.VERIFICATION_GROUP
        description = "Runs the integration tests."
        shouldRunAfter("test")
        outputs.upToDateWhen { false }
        useJUnitPlatform {
            includeTags("it")
        }
    }
}
