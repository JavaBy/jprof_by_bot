plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}

dependencies {
    implementation(libs.bundles.aws.lambda)
    implementation(libs.bundles.tgbotapi)
    implementation(libs.bundles.log4j)
}
