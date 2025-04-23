package dev.mbo.djp

import com.sun.net.httpserver.HttpServer
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test
import java.io.File
import java.net.InetSocketAddress
import java.util.*
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
        private const val BUILD_GRADLE_KTS = """
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
"""
    }

    @Test
    fun `plugin writes dependencies to JSON file`() {
        val testProjectDir = prepareProject()

        val result = runTask("dependencies-json", testProjectDir)

        // Assertions
        assertEquals(TaskOutcome.SUCCESS, result.task(":dependencies-json")?.outcome)
        val outputFile = File(testProjectDir, "build/dependencies.json")
        assertTrue(outputFile.exists(), "Expected file ${outputFile.path} to exist")
        val fileContent = outputFile.readText()

        assertTrue(fileContent.contains("\"name\":"), "Expected to include 'name:'")
        assertTrue(fileContent.contains("\"version\":"), "Expected to include 'version:'")
        assertTrue(fileContent.contains("\"dependencies\":"), "Expected to include 'dependencies:'")

        // guava needs to be inside because we defined it
        assertTrue(fileContent.contains("guava"), "Expected output to contain 'guava'")
        // also check log message to exist
        assertTrue(result.output.contains("Wrote dependency JSON"), "Expected log message not found!")
    }

    @Test
    fun `plugin uploads JSON file to http server`() {
        val server = startUploadServer()
        try {
            val testProjectDir = prepareProject(
                additionalConfig = """
                    dependenciesJson {
                        postUrl.set("http://localhost:${server.address.port}/upload")
                    }
                """.trimIndent()
            )

            val result = runTask("dependencies-json-upload", testProjectDir)
            assertEquals(TaskOutcome.SUCCESS, result.task(":dependencies-json-upload")?.outcome)
        } finally {
            server.stop(0)
        }
    }

    fun prepareProject(
        name: String = UUID.randomUUID().toString(),
        additionalConfig: String = ""
    ): File {
        val testProjectPath = kotlin.io.path.createTempDirectory(prefix = name)
        val testProjectDir = testProjectPath.toFile()
        File(testProjectDir, "settings.gradle.kts").writeText(SETTINGS_GRADLE_KTS)
        File(testProjectDir, "build.gradle.kts").writeText(
            """
            $BUILD_GRADLE_KTS
            
            $additionalConfig
        """.trimIndent()
        )
        return testProjectDir
    }

    private fun startUploadServer(port: Int = 0): HttpServer {
        return HttpServer.create(InetSocketAddress("localhost", port), 0).apply {
            createContext("/upload") { exchange ->
                exchange.sendResponseHeaders(200, 0)
                exchange.responseBody.close()
            }
            executor = null
            start()
        }
    }

    private fun runTask(task: String, projectDir: File): BuildResult {
        return GradleRunner.create()
            .withProjectDir(projectDir)
            .withPluginClasspath()
            .withArguments(task, "--info")
            .forwardOutput()
            .build()
    }
}