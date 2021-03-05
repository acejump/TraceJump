import org.apache.tools.ant.taskdefs.optional.jlink.jlink
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm") version "1.5.0-M1"
    id("com.github.ben-manes.versions") version "0.38.0"

    // TODO: migrate from JFX to https://github.com/JetBrains/skiko
    id("org.openjfx.javafxplugin") version "0.0.9"
    // Cannot update
    id("org.beryx.jlink") version "2.23.1"
    id("com.gluonhq.client-gradle-plugin") version "0.1.35"
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

val entrypoint = "org.acejump.tracejump.MainKt"

application {
    mainClassName = entrypoint
}

val javaVersion = JavaVersion.VERSION_11.toString()

javafx {
    version = javaVersion
    modules("javafx.controls")
}

group = "org.tracejump"
version = "0.2-SNAPSHOT"

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.bytedeco:javacv-platform:1.5.4")
    implementation("org.bytedeco:tesseract-platform:4.1.1-1.5.4")
    implementation("org.bytedeco:leptonica-platform:1.80.0-1.5.4")
    implementation("org.openjfx:javafx:11.0.2")
    implementation("com.google.guava:guava:30.1-jre")
    implementation("org.openjfx:javafx-swing:11.0.2")
    implementation("com.1stleg:jnativehook:2.1.0")
//    implementation("org.apache.lucene:lucene-core:_")
//    implementation("org.apache.lucene:lucene-queryparser:_")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = javaVersion
}

jlink {
    launcher {
        name = "hello"
    }
    jpackage {
        outputDir = "my-packaging"
        imageName = "TraceJump"
        installerName = "TraceJumpInstaller"
        installerType = "dmg"
        appVersion = project.version.toString()
        description = "Mixed reality trace link navigator"
    }
    addExtraDependencies("javafx")
}