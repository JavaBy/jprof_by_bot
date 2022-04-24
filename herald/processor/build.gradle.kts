plugins {
    kotlin("jvm")
}

dependencies {
    api(project.projects.core)
    api(project.projects.votes)
    api(project.projects.votes.votingProcessor)
    implementation(libs.log4j.api)
}
