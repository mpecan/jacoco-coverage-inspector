package io.github.mpecan.jacoco

import io.github.mpecan.jacoco.tasks.ListFileCoverageTask
import io.github.mpecan.jacoco.tasks.ListPackageCoverageTask
import io.github.mpecan.jacoco.tasks.ListProjectCoverageTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.*

class JacocoCoverageInspectorPluginTest {
    
    private lateinit var project: Project
    
    @BeforeEach
    fun setup() {
        project = ProjectBuilder.builder().build()
    }
    
    @Test
    fun `plugin can be applied to project`() {
        project.plugins.apply("java")
        project.plugins.apply("jacoco")
        project.plugins.apply(JacocoCoverageInspectorPlugin::class.java)
        
        assertTrue(project.plugins.hasPlugin(JacocoCoverageInspectorPlugin::class.java))
    }
    
    @Test
    fun `plugin creates jacocoInspector extension`() {
        project.plugins.apply("java")
        project.plugins.apply("jacoco")
        project.plugins.apply(JacocoCoverageInspectorPlugin::class.java)
        
        val extension = project.extensions.findByName("jacocoInspector")
        assertNotNull(extension, "Plugin should create jacocoInspector extension")
        assertTrue(extension is JacocoInspectorExtension, "Extension should be of type JacocoInspectorExtension")
    }
    
    @Test
    fun `plugin registers all coverage tasks after evaluation`() {
        project.plugins.apply("java")
        project.plugins.apply("jacoco")
        project.plugins.apply(JacocoCoverageInspectorPlugin::class.java)
        
        // Tasks are registered after evaluation
        assertNull(project.tasks.findByName("listProjectCoverage"), "Tasks should not exist before evaluation")
        assertNull(project.tasks.findByName("listPackageCoverage"), "Tasks should not exist before evaluation")
        assertNull(project.tasks.findByName("listFileCoverage"), "Tasks should not exist before evaluation")
        
        // Force project evaluation
        (project as org.gradle.api.internal.project.ProjectInternal).evaluate()
        
        // Check tasks are registered
        val projectTask = project.tasks.findByName("listProjectCoverage")
        assertNotNull(projectTask, "listProjectCoverage task should be registered")
        assertTrue(projectTask is ListProjectCoverageTask, "Task should be of correct type")
        
        val packageTask = project.tasks.findByName("listPackageCoverage")
        assertNotNull(packageTask, "listPackageCoverage task should be registered")
        assertTrue(packageTask is ListPackageCoverageTask, "Task should be of correct type")
        
        val fileTask = project.tasks.findByName("listFileCoverage")
        assertNotNull(fileTask, "listFileCoverage task should be registered")
        assertTrue(fileTask is ListFileCoverageTask, "Task should be of correct type")
    }
    
    @Test
    fun `plugin tasks belong to verification group`() {
        project.plugins.apply("java")
        project.plugins.apply("jacoco")
        project.plugins.apply(JacocoCoverageInspectorPlugin::class.java)
        (project as org.gradle.api.internal.project.ProjectInternal).evaluate()
        
        val tasks = listOf("listProjectCoverage", "listPackageCoverage", "listFileCoverage")
        
        tasks.forEach { taskName ->
            val task = project.tasks.findByName(taskName)
            assertNotNull(task)
            assertEquals("verification", task.group, "$taskName should belong to verification group")
        }
    }
    
    @Test
    fun `plugin tasks have correct descriptions`() {
        project.plugins.apply("java")
        project.plugins.apply("jacoco")
        project.plugins.apply(JacocoCoverageInspectorPlugin::class.java)
        (project as org.gradle.api.internal.project.ProjectInternal).evaluate()
        
        val projectTask = project.tasks.findByName("listProjectCoverage")
        assertEquals("Lists overall project coverage from JaCoCo report", projectTask?.description)
        
        val packageTask = project.tasks.findByName("listPackageCoverage")
        assertEquals("Lists package-level coverage from JaCoCo report with filtering capabilities", packageTask?.description)
        
        val fileTask = project.tasks.findByName("listFileCoverage")
        assertEquals("Lists file-level coverage from JaCoCo report with filtering capabilities", fileTask?.description)
    }
    
    @Test
    fun `plugin works without JaCoCo plugin but tasks are not configured`() {
        project.plugins.apply("java")
        project.plugins.apply(JacocoCoverageInspectorPlugin::class.java)
        (project as org.gradle.api.internal.project.ProjectInternal).evaluate()
        
        // Tasks should still be created
        assertNotNull(project.tasks.findByName("listProjectCoverage"))
        assertNotNull(project.tasks.findByName("listPackageCoverage"))
        assertNotNull(project.tasks.findByName("listFileCoverage"))
        
        // But they won't have default report file configured
        val task = project.tasks.findByName("listProjectCoverage") as ListProjectCoverageTask
        assertFalse(task.jacocoReportFile.isPresent, "Report file should not be configured without JaCoCo plugin")
    }
    
    @Test
    fun `plugin configures tasks to depend on jacocoTestReport when available`() {
        project.plugins.apply("java")
        project.plugins.apply("jacoco")
        project.plugins.apply(JacocoCoverageInspectorPlugin::class.java)
        
        // The java plugin already creates a test task
        (project as org.gradle.api.internal.project.ProjectInternal).evaluate()
        
        val tasks = listOf("listProjectCoverage", "listPackageCoverage", "listFileCoverage")
        
        tasks.forEach { taskName ->
            val task = project.tasks.findByName(taskName)
            assertNotNull(task)
            // The plugin should find and depend on the jacocoTestReport task if it exists
            val jacocoTestReport = project.tasks.findByName("jacocoTestReport")
            if (jacocoTestReport != null) {
                assertTrue(
                    task.dependsOn.any { dep -> 
                        when (dep) {
                            is Task -> dep.name == "jacocoTestReport"
                            is String -> dep == "jacocoTestReport"
                            else -> false
                        }
                    },
                    "$taskName should depend on jacocoTestReport"
                )
            }
        }
    }
    
    @Test
    fun `extension can be configured in build script`() {
        project.plugins.apply("java")
        project.plugins.apply("jacoco")
        project.plugins.apply(JacocoCoverageInspectorPlugin::class.java)
        
        // Configure extension
        val extension = project.extensions.getByType(JacocoInspectorExtension::class.java)
        extension.defaultFormat = io.github.mpecan.jacoco.model.OutputFormat.JSON
        extension.colorOutput = false
        extension.minLineCoverage = 80.0
        extension.includePatterns = listOf("**/*.java")
        
        // Verify configuration
        assertEquals(io.github.mpecan.jacoco.model.OutputFormat.JSON, extension.defaultFormat)
        assertEquals(false, extension.colorOutput)
        assertEquals(80.0, extension.minLineCoverage)
        assertEquals(listOf("**/*.java"), extension.includePatterns)
    }
    
    @Test
    fun `plugin applies extension defaults to tasks`() {
        project.plugins.apply("java")
        project.plugins.apply("jacoco")
        project.plugins.apply(JacocoCoverageInspectorPlugin::class.java)
        
        // Configure extension before evaluation
        val extension = project.extensions.getByType(JacocoInspectorExtension::class.java)
        extension.defaultFormat = io.github.mpecan.jacoco.model.OutputFormat.CSV
        extension.colorOutput = false
        extension.minLineCoverage = 75.0
        
        (project as org.gradle.api.internal.project.ProjectInternal).evaluate()
        
        // Check that tasks have extension defaults applied
        val task = project.tasks.findByName("listProjectCoverage") as ListProjectCoverageTask
        assertEquals(io.github.mpecan.jacoco.model.OutputFormat.CSV, task.format.get())
        assertEquals(false, task.colorOutput.get())
        assertEquals(75.0, task.minLineCoverage.get())
    }
    
    @Test
    fun `plugin can be applied to multi-project builds`() {
        val rootProject = ProjectBuilder.builder().withName("root").build()
        val subProject = ProjectBuilder.builder().withName("sub").withParent(rootProject).build()
        
        subProject.plugins.apply("java")
        subProject.plugins.apply("jacoco")
        subProject.plugins.apply(JacocoCoverageInspectorPlugin::class.java)
        
        assertTrue(subProject.plugins.hasPlugin(JacocoCoverageInspectorPlugin::class.java))
        assertNotNull(subProject.extensions.findByName("jacocoInspector"))
    }
}