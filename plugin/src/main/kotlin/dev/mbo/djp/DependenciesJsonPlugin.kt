package dev.mbo.djp

import groovy.json.JsonOutput
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class DependenciesJsonPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // Register the extension with injected layout + objects
        val extension = project.extensions.create(
            "dependenciesJson",
            DependenciesJsonPluginExtension::class.java,
            project.objects,
            project.layout
        )

        project.tasks.register("dependencies-json") {
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
    }
}