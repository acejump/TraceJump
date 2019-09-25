import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.50"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
      compile("org.bytedeco:javacv-platform:1.5.1")
  compile("org.bytedeco:tesseract-platform:1.5.1")
  compile("org.bytedeco:leptonica-platform:1.5.1")
  implementation("com.1stleg:jnativehook:2.1.0")
  implementation("org.openjfx:javafx-swing:11")
  implementation("org.openjfx:javafx:11")
  implementation("com.google.guava:guava:28.1-jre")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

configure<ApplicationPluginConvention> {
    mainClassName = "MainKt"
}