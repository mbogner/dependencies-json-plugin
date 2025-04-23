package dev.mbo.djp

import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

open class DependenciesJsonPluginExtension @Inject constructor(
    objects: ObjectFactory,
    layout: ProjectLayout
) {
    val outputFile: RegularFileProperty = objects.fileProperty().convention(
        layout.buildDirectory.file("dependencies.json")
    )
}