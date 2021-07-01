plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}

dependencies {
    implementation(libs.bundles.aws.lambda)
    implementation(libs.koin.core)
    implementation(libs.bundles.tgbotapi)
    implementation(libs.bundles.log4j)
    implementation(project.projects.core)
    implementation(project.projects.votes.dynamodb)
    implementation(project.projects.jep)
    implementation(project.projects.youtube.dynamodb)
    implementation(project.projects.kotlin.dynamodb)
    implementation(project.projects.dialogs.dynamodb)
    implementation(project.projects.quizoji.dynamodb)
}
