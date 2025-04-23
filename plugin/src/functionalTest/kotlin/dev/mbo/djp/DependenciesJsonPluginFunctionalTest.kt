package dev.mbo.djp

import com.sun.net.httpserver.HttpServer
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test
import java.io.File
import java.net.InetSocketAddress
import java.nio.file.Path
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DependenciesJsonPluginFunctionalTest {

    companion object {
        private const val SETTINGS_GRADLE_KTS = """
pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
    }
}
rootProject.name = "test-project"
            """
    }

    @Test
    fun `plugin writes dependencies to JSON file`() {
        val testProjectPath: Path = kotlin.io.path.createTempDirectory(prefix = "test-plugin")
        val testProjectDir = testProjectPath.toFile()
        File(testProjectDir, "settings.gradle.kts").writeText(SETTINGS_GRADLE_KTS)
        File(testProjectDir, "build.gradle.kts").writeText(
            """
                plugins {
                    java
                    id("dev.mbo.djp.dependencies-json") version "1.0.0-SNAPSHOT"
                }
                
                repositories {
                    mavenCentral()
                }
                
                dependencies {
                    implementation("com.google.guava:guava:31.1-jre")
                }
                """.trimIndent()
        )

        // Run the plugin task
        val result = GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withPluginClasspath()
            .withArguments("dependencies-json", "--info")
            .forwardOutput()
            .build()

        assertTrue(result.output.contains("Wrote dependency JSON"), "Expected log message not found!")

        val outputFile = File(testProjectDir, "build/dependencies.json")

        // Assertions
        assertEquals(TaskOutcome.SUCCESS, result.task(":dependencies-json")?.outcome)
        assertTrue(outputFile.exists(), "Expected file ${outputFile.path} to exist")
        val outContent = outputFile.readText()
        assertTrue(outContent.contains("guava"), "Expected output to contain 'guava'")
    }

    @Test
    fun `plugin uploads JSON file to http server`() {
        val server = HttpServer.create(InetSocketAddress("localhost", 8089), 0).apply {
            createContext("/upload") { exchange ->
                exchange.sendResponseHeaders(200, 0)
                exchange.responseBody.close()
            }
            executor = null
            start()
        }
        try {
            val testProjectPath: Path = kotlin.io.path.createTempDirectory(prefix = "test-plugin2")
            val testProjectDir = testProjectPath.toFile()
            File(testProjectDir, "settings.gradle.kts").writeText(SETTINGS_GRADLE_KTS)
            File(testProjectDir, "build.gradle.kts").writeText(
                """
                plugins {
                    java
                    id("dev.mbo.djp.dependencies-json") version "1.0.0-SNAPSHOT"
                }
                
                repositories {
                    mavenCentral()
                }
                
                dependencies {
                    implementation("com.google.guava:guava:31.1-jre")
                }
                
                dependenciesJson {
                    postUrl.set("http://localhost:8089/upload")
                }
                """.trimIndent()
            )

            // Run the plugin task
            val result = GradleRunner.create()
                .withProjectDir(testProjectDir)
                .withPluginClasspath()
                .withArguments("dependencies-json-upload", "--info")
                .forwardOutput()
                .build()

            assertEquals(TaskOutcome.SUCCESS, result.task(":dependencies-json-upload")?.outcome)
        } finally {
            server.stop(0)
        }
    }
}