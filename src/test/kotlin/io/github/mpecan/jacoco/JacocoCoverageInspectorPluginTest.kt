package io.github.mpecan.jacoco

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class JacocoCoverageInspectorPluginTest {
    
    @Test
    fun `should apply plugin successfully`() {
        val project: Project = ProjectBuilder.builder().build()
        project.plugins.apply("io.github.mpecan.jacoco-inspector")
        
        assertTrue(project.plugins.hasPlugin(JacocoCoverageInspectorPlugin::class.java))
    }
    
    @Test
    fun `should create extension when applied`() {
        val project: Project = ProjectBuilder.builder().build()
        project.plugins.apply(JacocoCoverageInspectorPlugin::class.java)
        
        val extension = project.extensions.getByName("jacocoInspector")
        assertNotNull(extension)
        assertTrue(extension is JacocoInspectorExtension)
    }
}