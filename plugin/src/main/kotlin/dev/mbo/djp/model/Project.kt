package dev.mbo.djp.model

import java.time.Instant

data class ProjectDto(
    val name: String,
    val version: String,
    val dependencies: Map<String, List<Map<String, String>>>,
    val timestamp: Instant = Instant.now(),
)