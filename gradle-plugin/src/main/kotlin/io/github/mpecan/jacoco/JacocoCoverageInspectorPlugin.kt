package io.github.mpecan.jacoco

import io.github.mpecan.jacoco.tasks.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.testing.jacoco.plugins.JacocoPlugin
import org.gradle.testing.jacoco.tasks.JacocoReport
import java.io.File

class JacocoCoverageInspectorPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // Register the plugin extension for configuration
        val extension = project.extensions.create(
            "jacocoInspector",
            JacocoInspectorExtension::class.java
        )
        
        // Register tasks after project evaluation to ensure proper configuration
        project.afterEvaluate {
            // Find the default JaCoCo test report task
            val jacocoTestReport = project.tasks.findByName("jacocoTestReport") as? JacocoReport
            val defaultReportFile = jacocoTestReport?.reports?.xml?.outputLocation?.asFile?.get()
            
            // Register ListProjectCoverageTask
            project.tasks.register("listProjectCoverage", ListProjectCoverageTask::class.java) { task ->
                task.applyDefaults(extension)
                defaultReportFile?.let {
                    task.jacocoReportFile.convention(project.layout.file(project.provider { it }))
                }
                jacocoTestReport?.let {
                    task.dependsOn(it)
                }
            }
            
            // Register ListFileCoverageTask
            project.tasks.register("listFileCoverage", ListFileCoverageTask::class.java) { task ->
                task.applyDefaults(extension)
                defaultReportFile?.let {
                    task.jacocoReportFile.convention(project.layout.file(project.provider { it }))
                }
                jacocoTestReport?.let {
                    task.dependsOn(it)
                }
            }
            
            // Register ListPackageCoverageTask
            project.tasks.register("listPackageCoverage", ListPackageCoverageTask::class.java) { task ->
                task.applyDefaults(extension)
                defaultReportFile?.let {
                    task.jacocoReportFile.convention(project.layout.file(project.provider { it }))
                }
                jacocoTestReport?.let {
                    task.dependsOn(it)
                }
            }
        }
        
        project.logger.info("JaCoCo Coverage Inspector plugin applied to project ${project.name}")
    }
}