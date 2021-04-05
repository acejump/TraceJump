import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  application
  kotlin("jvm") version "1.5.0-M2"
  id("com.github.ben-manes.versions") version "0.38.0"

  // Cannot update
  id("org.beryx.jlink") version "2.23.1"
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

val target = "${targetOs}-${targetArch}"

dependencies {
  implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

  implementation("org.bytedeco:javacv-platform:1.5.5")
  implementation("org.bytedeco:leptonica-platform:1.80.0-1.5.5")
  implementation("org.bytedeco:tesseract-platform:4.1.1-1.5.5") // OCR

  implementation("com.1stleg:jnativehook:2.1.0") // Mouse / keyboard hook

  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.4.3")
  implementation("org.jetbrains.skiko:skiko-jvm-runtime-$target:0.2.21")
}

tasks.withType<KotlinCompile> {
  kotlinOptions.jvmTarget = JavaVersion.VERSION_11.toString()
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