plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}

dependencies {
    api(project.projects.pins.dto)
    implementation(libs.bundles.aws.lambda)
    implementation(libs.koin.core)
    implementation(libs.bundles.tgbotapi)
    implementation(libs.bundles.log4j)
    implementation(project.projects.pins.dynamodb)
}
