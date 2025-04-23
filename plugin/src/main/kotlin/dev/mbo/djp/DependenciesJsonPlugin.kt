package dev.mbo.djp

import groovy.json.JsonOutput
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File
import java.net.HttpURLConnection
import java.net.URI
import java.util.*

@Suppress("unused")
class DependenciesJsonPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // Register the extension with injected layout + objects
        val extension = project.extensions.create(
            "dependenciesJson",
            DependenciesJsonPluginExtension::class.java,
            project.objects,
            project.layout
        )

        val generateTask = project.tasks.register("dependencies-json") {
            group = "reporting"
            description = "Outputs resolved dependencies as JSON grouped by scope"

            doLast {
                val result = mutableMapOf<String, MutableList<Map<String, String>>>()

                project.configurations.forEach { config ->
                    if (config.isCanBeResolved) {
                        val deps = mutableListOf<Map<String, String>>()
                        try {
                            config.resolvedConfiguration.resolvedArtifacts.forEach { artifact ->
                                deps += mapOf(
                                    "group" to artifact.moduleVersion.id.group,
                                    "name" to artifact.name,
                                    "version" to artifact.moduleVersion.id.version,
                                    "file" to artifact.file.name
                                )
                            }
                        } catch (_: Exception) {
                        }
                        if (deps.isNotEmpty()) {
                            result[config.name] = deps
                        }
                    }
                }

                val outputFile: File = extension.outputFile.get().asFile
                outputFile.parentFile.mkdirs()
                outputFile.writeText(JsonOutput.prettyPrint(JsonOutput.toJson(result)))
                project.logger.lifecycle("Wrote dependency JSON to ${outputFile.absolutePath}")
            }
        }

        project.tasks.register("dependencies-json-upload") {
            group = "reporting"
            description = "HTTP POSTs the generated dependencies JSON file to a server"
            dependsOn(generateTask)

            doLast {
                val outputFile: File = extension.outputFile.get().asFile
                val serverUrl: String = extension.postUrl.orNull
                    ?: throw IllegalArgumentException("postUrl is not configured.")

                project.logger.lifecycle("Posting dependency JSON to $serverUrl")

                val connection = URI.create(serverUrl).toURL().openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.doOutput = true
                connection.setRequestProperty("Content-Type", "application/json")

                val httpUser = System.getenv("DJP_HTTP_USER")
                val httpPass = System.getenv("DJP_HTTP_PASS")

                if (!httpUser.isNullOrEmpty() && !httpPass.isNullOrEmpty()) {
                    val authString = Base64.getEncoder().encodeToString("$httpUser:$httpPass".toByteArray())
                    connection.setRequestProperty("Authorization", "Basic $authString")
                }

                outputFile.inputStream().use { input ->
                    connection.outputStream.use { output ->
                        input.copyTo(output)
                    }
                }

                val responseCode = connection.responseCode
                if (responseCode in 200..299) {
                    project.logger.lifecycle("Successfully posted dependencies JSON.")
                } else {
                    throw RuntimeException("Failed to post dependencies JSON. Server responded with status code: $responseCode")
                }
            }
        }
    }
}