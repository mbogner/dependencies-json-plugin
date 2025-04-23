package dev.mbo.djp

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Path
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DependenciesJsonPluginFunctionalTest {

    @Test
    fun `plugin writes dependencies to JSON file`() {
        // Setup a temporary test project
        val testProjectPath: Path = kotlin.io.path.createTempDirectory(prefix = "test-plugin")
        val testProjectDir = testProjectPath.toFile()
        File(testProjectDir, "settings.gradle.kts").writeText("rootProject.name = \"test-project\"")

        File(testProjectDir, "settings.gradle.kts").writeText(
            """
            rootProject.name = "test-project"
            """.trimIndent()
        )
        val pluginJarPath = File(
            DependenciesJsonPlugin::class.java.protectionDomain.codeSource.location.toURI()
        ).absolutePath.replace("\\", "\\\\")

        File(testProjectDir, "build.gradle.kts").writeText(
            """
            buildscript {
                dependencies {
                    classpath(files("$pluginJarPath"))
                }
            }
            
            apply {
                plugin("java")
            }
            
            project.plugins.apply(dev.mbo.djp.DependenciesJsonPlugin::class.java)
            
            repositories {
                mavenCentral()
            }
            
            dependencies {
                add("implementation", "com.google.guava:guava:31.1-jre")
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
}