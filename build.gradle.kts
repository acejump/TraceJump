import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  application
  kotlin("jvm") version "1.6.0"
  id("com.github.ben-manes.versions") version "0.39.0"

  // Cannot update
  id("org.beryx.jlink") version "2.24.0"
  id("org.bytedeco.gradle-javacpp-platform") version "1.5.6"
}

ext {
  set("javacppPlatform", "macosx-arm64")
}

repositories {
  mavenCentral()
  maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

application.mainClass.set("org.acejump.tracejump.MainKt")

group = "org.tracejump"
version = "0.2-SNAPSHOT"

val osArch = System.getProperty("os.arch")
var targetArch = when (osArch) {
  "x86_64", "amd64" -> "x64"
  "aarch64" -> "arm64"
  else -> error("Unsupported arch: $osArch")
}

val os = System.getProperty("os.name")
val targetOs = when {
  os == "Mac OS X" -> "macos"
  os.startsWith("Win") -> "windows"
  os.startsWith("Linux") -> "linux"
  else -> throw Error("Unsupported OS: $os")
}

dependencies {
  implementation(kotlin("bom"))
  implementation(kotlin("stdlib"))
//  implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
//  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

  implementation("org.bytedeco:javacv-platform:1.5.6")
  implementation("org.bytedeco:leptonica-platform:1.80.0-1.5.6")
  implementation("org.bytedeco:tesseract-platform:4.1.1-1.5.6") // OCR

  implementation("com.github.kwhat:jnativehook:2.2.1") // Mouse / keyboard hook

  val target = "${targetOs}-${targetArch}"
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.5.2")
  // UI does not does not seem to work past 0.2.26+
  implementation("org.jetbrains.skiko:skiko-jvm-runtime-$target:0.2.25")
}

tasks.compileKotlin {
  kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
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
}