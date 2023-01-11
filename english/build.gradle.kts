plugins {
    kotlin("jvm")
}

dependencies {
    api(project.projects.core)
    implementation(projects.english.languageRooms)
    implementation(projects.english.urbanWordOfTheDay)
    implementation(projects.english.urbanWordOfTheDayFormatter)
    implementation(projects.english.urbanDictionary)
    implementation(projects.english.dictionaryapiDev)
    implementation(libs.log4j.api)

    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.mockk)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.log4j.core)
}
