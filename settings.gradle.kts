rootProject.name = "tracejump"

pluginManagement {
    repositories {
        maven("https://nexus.gluonhq.com/nexus/content/repositories/releases")
        gradlePluginPortal()
    }
}

pluginManagement.repositories {
    mavenCentral()
    gradlePluginPortal()
    maven ("https://dl.bintray.com/kotlin/kotlin-eap")
}