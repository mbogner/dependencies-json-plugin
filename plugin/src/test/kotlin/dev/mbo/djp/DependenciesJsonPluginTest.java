package dev.mbo.djp;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class DependenciesJsonPluginTest {
    @Test
    void pluginRegistersATask() {
        Project project = ProjectBuilder.builder().build();
        project.getPlugins().apply("dev.mbo.djp.dependencies-json");
        assertNotNull(project.getTasks().findByName("dependencies-json"));
    }
}
