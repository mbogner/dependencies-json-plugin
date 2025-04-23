package dev.mbo.djp.model

import java.time.Instant
import java.time.format.DateTimeFormatter

data class ProjectDto(
    val name: String,
    val version: String,
    val dependencies: Map<String, List<Map<String, String>>>,
    val timestamp: String = DateTimeFormatter.ISO_INSTANT.format(Instant.now())
)