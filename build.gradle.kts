import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.github.jengelman.gradle.plugins.shadow.transformers.Log4j2PluginsCacheFileTransformer
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm").version("1.4.32").apply(false)
    id("com.github.johnrengelman.shadow").version("6.1.0").apply(false)
}

subprojects {
    repositories {
        mavenCentral()
        maven("https://packages.jetbrains.team/maven/p/skija/maven")
    }

    tasks {
        withType<KotlinCompile> {
            kotlinOptions.jvmTarget = "11"
        }
        withType<Jar> {
            // Workaround for https://stackoverflow.com/q/42174572/750510
            archiveBaseName.set(rootProject.name + "-" + this.project.path.removePrefix(":").replace(":", "-"))
        }
        withType<Test> {
            useJUnitPlatform {
                excludeTags("db")
            }
            testLogging {
                showStandardStreams = true
            }
        }
        withType<ShadowJar> {
            transform(Log4j2PluginsCacheFileTransformer::class.java)
        }
    }
}
