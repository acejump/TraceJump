import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    idea apply true
    kotlin("jvm") version "1.4.0"
    id("org.beryx.jlink") version "2.19.0"
    id("org.openjfx.javafxplugin") version "0.0.9"
    id("de.fayard.refreshVersions") version "0.8.6"
    id("com.gluonhq.client-gradle-plugin") version "0.1.28"
}

repositories {
    mavenCentral()
    jcenter()
    maven("https://jitpack.io")
}

val entrypoint = "tracejump/org.acejump.tracejump.MainKt"

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

repositories { mavenCentral() }

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.bytedeco:javacv-platform:_")
    implementation("org.bytedeco:tesseract-platform:_")
    implementation("org.bytedeco:leptonica-platform:_")
    implementation("org.openjfx:javafx:_")
    implementation("com.google.guava:guava:_")
    implementation("org.openjfx:javafx-swing:_")
    implementation("com.1stleg:jnativehook:_")
//    implementation("com.github.kwebio:kweb-core:0.7.20")
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