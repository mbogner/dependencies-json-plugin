plugins {
    // https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-gradle-plugin
    // Downgraded:
    // The `embedded-kotlin` and `kotlin-dsl` plugins rely on features of Kotlin `2.0.21`
    // that might work differently than in the requested version `2.1.20`.
    kotlin("jvm") version "2.0.21"

    // https://plugins.gradle.org/plugin/net.researchgate.release
    id("net.researchgate.release") version "3.1.0"
}

allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
    }

    plugins.withType<JavaPlugin>().configureEach {
        extensions.configure<JavaPluginExtension> {
            toolchain.languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        compilerOptions.jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

tasks.named("afterReleaseBuild") {
    dependsOn(":plugin:publishPlugins")
}