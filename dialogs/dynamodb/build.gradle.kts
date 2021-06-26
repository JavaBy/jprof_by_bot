plugins {
    kotlin("jvm")
}

dependencies {
    api(project.projects.dialogs)
    api(libs.dynamodb)
    implementation(project.projects.utils.dynamodb)
    implementation(project.projects.utils.tgbotapiSerialization)
    implementation(libs.kotlinx.coroutines.jdk8)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.aws.junit5.dynamo.v2)
    testImplementation(project.projects.utils.awsJunit5)
    testRuntimeOnly(libs.junit.jupiter.engine)
}

tasks {
    val dbTest by registering(Test::class) {
        group = LifecycleBasePlugin.VERIFICATION_GROUP
        description = "Runs the DB tests."
        shouldRunAfter("test")
        outputs.upToDateWhen { false }
        useJUnitPlatform {
            includeTags("db")
        }
    }
}
