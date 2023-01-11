plugins {
    kotlin("jvm")
}

dependencies {
    api(project.projects.english.languageRooms)
    api(libs.dynamodb)
    implementation(project.projects.utils.dynamodb)
    implementation(libs.kotlinx.coroutines.jdk8)

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
