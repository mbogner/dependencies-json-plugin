@file:Suppress("UnstableApiUsage")

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    // https://plugins.gradle.org/plugin/com.gradle.plugin-publish
    id("com.gradle.plugin-publish") version "1.3.1"
}

group = "dev.mbo"

repositories {
    mavenLocal()
    mavenCentral()
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            // https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter
            useJUnitJupiter("5.12.2")
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test")
                implementation("org.jetbrains.kotlin:kotlin-test-junit5")
            }
        }
        val functionalTest by registering(JvmTestSuite::class) {
            dependencies {
                implementation(project())
                dependencies {
                    implementation("org.jetbrains.kotlin:kotlin-test")
                    implementation("org.jetbrains.kotlin:kotlin-test-junit5")
                }
            }
            targets {
                all {
                    testTask.configure { shouldRunAfter(test) }
                }
            }
        }
    }
}

gradlePlugin {
    val dependenciesJson by plugins.creating {
        id = "dev.mbo.djp.dependencies-json"
        displayName = "Dependencies JSON Plugin"
        description = "Outputs resolved dependencies as JSON grouped by configuration"
        implementationClass = "dev.mbo.djp.DependenciesJsonPlugin"

        // ✅ NEW: Metadata goes here
        tags.set(listOf("dependencies", "json", "reporting"))
        website.set("https://github.com/mbogner/dependencies-json-plugin")
        vcsUrl.set("https://github.com/mbogner/dependencies-json-plugin.git")
    }
}

gradlePlugin.testSourceSets.add(sourceSets["functionalTest"])

tasks.named<Task>("check") {
    dependsOn(testing.suites.named("functionalTest"))
}
