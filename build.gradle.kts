import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    `maven-publish`
    idea apply true
    kotlin("jvm") version "1.3.61"
//    id("org.jetbrains.intellij") version "0.4.10"
    id("org.openjfx.javafxplugin") version "0.0.8"
    id("org.beryx.jlink") version "2.16.4"
}

val entrypoint = "tracejump/org.acejump.tracejump.MainKt"

application {
    mainClassName = entrypoint
}

javafx {
    modules("javafx.controls")
}

group = "org.tracejump"
version = "0.1-SNAPSHOT"

repositories { mavenCentral() }

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.bytedeco:javacv-platform:1.5.2")
    implementation("org.bytedeco:tesseract-platform:4.1.0-1.5.2")
    implementation("org.bytedeco:leptonica-platform:1.78.0-1.5.2")
    implementation("org.openjfx:javafx:11")
    implementation("com.google.guava:guava:28.1-jre")
    implementation("org.openjfx:javafx-swing:11")
    implementation("com.1stleg:jnativehook:2.1.0")
//    implementation("org.apache.lucene:lucene-core:8.2.0")
//    implementation("org.apache.lucene:lucene-queryparser:8.2.0")
}

//intellij {
//    pluginName = "AceJump"
//    updateSinceUntilBuild = false
//    setPlugins("java")
//}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

jlink {
    launcher {
        name = "hello"
    }
    jpackage {
        jpackageHome = "/Users/breandan/Downloads/jdk-14.jdk/Contents/Home/"
        outputDir = "my-packaging"
        imageName = "TraceJump"
        installerName = "TraceJumpInstaller"
        installerType = "dmg"
        appVersion = "0.1"
        description = "Mixed reality trace link navigator"
    }
    addExtraDependencies("javafx")
}

publishing {
    publications.create<MavenPublication>("default") {
        pom {
            description.set("Mixed reality trace link navigator")
            name.set("TraceJump")
            url.set("https://github.com/acejump/tracejump")
            developers {
                developer {
                    id.set("Breandan Considine")
                    name.set("Breandan Considine")
                    email.set("bre@ndan.co")
                    organization.set("Université de Montréal")
                }
            }
            scm {
                url.set("https://github.com/acejump/tracejump")
            }
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            setUrl("https://maven.pkg.github.com/acejump/tracejump")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("GPR_USER")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("GPR_API_KEY")
            }
        }
    }
    publications {
        register("gpr", MavenPublication::class) {
            from(components["java"])
        }
    }
}