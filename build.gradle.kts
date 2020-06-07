import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    `maven-publish`
    idea apply true
    kotlin("jvm") version "1.3.72"
    id("org.beryx.jlink") version "2.19.0"
    id("org.openjfx.javafxplugin") version "0.0.8"
    id("de.fayard.refreshVersions") version "0.8.6"
    id("com.gluonhq.client-gradle-plugin") version "0.1.27"
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