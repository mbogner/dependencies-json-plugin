plugins {
    // https://plugins.gradle.org/plugin/net.researchgate.release
    id("net.researchgate.release") version "3.1.0"
}

allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
    }
}

// Hook into plugin publishing inside plugin module
tasks.named("afterReleaseBuild") {
    dependsOn(":plugin:publishPlugins")
}