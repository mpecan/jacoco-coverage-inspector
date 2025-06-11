package io.github.mpecan.jacoco

import org.gradle.api.Plugin
import org.gradle.api.Project

class JacocoCoverageInspectorPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // Register the plugin extension for configuration
        val extension = project.extensions.create(
            "jacocoInspector",
            JacocoInspectorExtension::class.java
        )
        
        // TODO: Register tasks once they are implemented
        // - ListProjectCoverageTask
        // - ListFileCoverageTask  
        // - ListPackageCoverageTask
        
        project.logger.info("JaCoCo Coverage Inspector plugin applied to project ${project.name}")
    }
}