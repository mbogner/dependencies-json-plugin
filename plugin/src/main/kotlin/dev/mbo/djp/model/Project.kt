package dev.mbo.djp.model

data class ProjectDto(
    val name: String,
    val version: String,
    val dependencies: Map<String, List<Map<String, String>>>
)