import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    idea apply true
    kotlin("jvm") version "1.3.60"
    id("org.jetbrains.intellij") version "0.4.13"
    id("org.openjfx.javafxplugin") version "0.0.8"
}

group = "org.tracejump"
version = "0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
        kotlinOptions.freeCompilerArgs += "-progressive"
    }

    named<Zip>("buildPlugin") {
        dependsOn("test")
        archiveFileName.set("TraceJump.zip")
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    compile("org.bytedeco:javacv-platform:1.5.2")
    compile("org.bytedeco:tesseract-platform:1.5.2")
    compile("org.bytedeco:leptonica-platform:1.5.2")
    implementation("com.1stleg:jnativehook:2.1.0")
    implementation("org.openjfx:javafx-swing:11")
    implementation("org.openjfx:javafx:11")
    implementation("com.google.guava:guava:28.1-jre")
    implementation("org.apache.lucene:lucene-core:8.2.0")
    implementation("org.apache.lucene:lucene-queryparser:8.2.0")
}

intellij {
    pluginName = "AceJump"
    updateSinceUntilBuild = false
    setPlugins("java")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

javafx {
    modules("javafx.controls")
}

configure<ApplicationPluginConvention> {
    mainClassName = "MainKt"
}