package io.github.mpecan.jacoco.formatter

import io.github.mpecan.jacoco.model.*
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertContains
import kotlin.test.assertEquals

class JsonFormatterTest {
    
    private fun createTestCounter(type: CoverageType, covered: Int, missed: Int): CoverageCounter {
        return CoverageCounter(type, missed, covered)
    }
    
    private fun createTestProject(): ProjectCoverageData {
        val counters = mapOf(
            CoverageType.INSTRUCTION to createTestCounter(CoverageType.INSTRUCTION, 100, 20),
            CoverageType.BRANCH to createTestCounter(CoverageType.BRANCH, 80, 40),
            CoverageType.LINE to createTestCounter(CoverageType.LINE, 90, 10),
            CoverageType.METHOD to createTestCounter(CoverageType.METHOD, 15, 5),
            CoverageType.CLASS to createTestCounter(CoverageType.CLASS, 8, 2)
        )
        
        return ProjectCoverageData("TestProject", counters)
    }
    
    @Test
    fun `should format project coverage as JSON`() {
        val formatter = JsonFormatter()
        val project = createTestProject()
        
        val output = formatter.format(project)
        
        assertContains(output, "\"projectName\": \"TestProject\"")
        assertContains(output, "\"counters\":")
        assertContains(output, "\"INSTRUCTION\":")
        assertContains(output, "\"covered\": 100")
        assertContains(output, "\"missed\": 20")
        assertContains(output, "\"total\": 120")
        assertContains(output, "\"percentage\": 83.33")
    }
    
    @Test
    fun `should format empty list as JSON array`() {
        val formatter = JsonFormatter()
        val emptyList = emptyList<PackageCoverageData>()
        
        val output = formatter.format(emptyList)
        
        assertEquals("[]", output)
    }
    
    @Test
    fun `should format package list as JSON array`() {
        val formatter = JsonFormatter()
        
        val counters = mapOf(
            CoverageType.CLASS to createTestCounter(CoverageType.CLASS, 5, 1),
            CoverageType.METHOD to createTestCounter(CoverageType.METHOD, 20, 5),
            CoverageType.LINE to createTestCounter(CoverageType.LINE, 100, 20),
            CoverageType.BRANCH to createTestCounter(CoverageType.BRANCH, 40, 10)
        )
        
        val packages = listOf(
            PackageCoverageData("com.example.service", counters),
            PackageCoverageData("com.example.controller", counters)
        )
        
        val output = formatter.format(packages)
        
        assertContains(output, "\"packageName\": \"com.example.service\"")
        assertContains(output, "\"packageName\": \"com.example.controller\"")
        assertContains(output, "\"CLASS\":")
        assertContains(output, "\"covered\": 5")
        assertContains(output, "\"percentage\": 83.33")
        assertTrue(output.startsWith("["))
        assertTrue(output.endsWith("]"))
    }
    
    @Test
    fun `should format class list as JSON`() {
        val formatter = JsonFormatter()
        
        val counters = mapOf(
            CoverageType.CLASS to createTestCounter(CoverageType.CLASS, 1, 0),
            CoverageType.METHOD to createTestCounter(CoverageType.METHOD, 10, 2),
            CoverageType.LINE to createTestCounter(CoverageType.LINE, 50, 10),
            CoverageType.BRANCH to createTestCounter(CoverageType.BRANCH, 20, 5)
        )
        
        val classes = listOf(
            ClassCoverageData("com.example.UserService", counters, "UserService.java"),
            ClassCoverageData("com.example.ProductController", counters, "ProductController.java")
        )
        
        val output = formatter.format(classes)
        
        assertContains(output, "\"className\": \"com.example.UserService\"")
        assertContains(output, "\"sourceFileName\": \"UserService.java\"")
        assertContains(output, "\"className\": \"com.example.ProductController\"")
        assertContains(output, "\"sourceFileName\": \"ProductController.java\"")
        assertContains(output, "\"covered\": 1")
        assertContains(output, "\"percentage\": 100.0")
        assertTrue(output.startsWith("["))
        assertTrue(output.endsWith("]"))
    }
    
    @Test
    fun `should handle null source file name`() {
        val formatter = JsonFormatter()
        
        val counters = mapOf(
            CoverageType.CLASS to createTestCounter(CoverageType.CLASS, 1, 0)
        )
        
        val classes = listOf(
            ClassCoverageData("com.example.UserService", counters, null)
        )
        
        val output = formatter.format(classes)
        
        assertContains(output, "\"className\": \"com.example.UserService\"")
        assertContains(output, "\"sourceFileName\": null")
    }
    
    @Test
    fun `should escape JSON special characters`() {
        val formatter = JsonFormatter()
        
        val counters = mapOf(
            CoverageType.CLASS to createTestCounter(CoverageType.CLASS, 1, 0)
        )
        
        val classes = listOf(
            ClassCoverageData("com.example.\"Test\\Service\"", counters, "Test\"File.java")
        )
        
        val output = formatter.format(classes)
        
        assertContains(output, "\"className\": \"com.example.\\\"Test\\\\Service\\\"\"")
        assertContains(output, "\"sourceFileName\": \"Test\\\"File.java\"")
    }
    
    @Test
    fun `should format project with nested packages and classes`() {
        val formatter = JsonFormatter()
        
        val classCounters = mapOf(
            CoverageType.CLASS to createTestCounter(CoverageType.CLASS, 1, 0),
            CoverageType.LINE to createTestCounter(CoverageType.LINE, 50, 10)
        )
        
        val packageCounters = mapOf(
            CoverageType.CLASS to createTestCounter(CoverageType.CLASS, 2, 1),
            CoverageType.LINE to createTestCounter(CoverageType.LINE, 100, 20)
        )
        
        val projectCounters = mapOf(
            CoverageType.CLASS to createTestCounter(CoverageType.CLASS, 5, 2),
            CoverageType.LINE to createTestCounter(CoverageType.LINE, 200, 40)
        )
        
        val classes = listOf(
            ClassCoverageData("UserService", classCounters, "UserService.java")
        )
        
        val packages = listOf(
            PackageCoverageData("com.example.service", packageCounters, classes)
        )
        
        val project = ProjectCoverageData("TestProject", projectCounters, packages)
        
        val output = formatter.format(project)
        
        assertContains(output, "\"projectName\": \"TestProject\"")
        assertContains(output, "\"packages\":")
        assertContains(output, "\"packageName\": \"com.example.service\"")
        assertContains(output, "\"classes\":")
        assertContains(output, "\"className\": \"UserService\"")
        assertContains(output, "\"sourceFileName\": \"UserService.java\"")
    }
    
    @Test
    fun `should handle empty counters map`() {
        val formatter = JsonFormatter()
        
        val project = ProjectCoverageData("EmptyProject", emptyMap())
        
        val output = formatter.format(project)
        
        assertContains(output, "\"projectName\": \"EmptyProject\"")
        assertContains(output, "\"counters\": {\n    }")
    }
    
    @Test
    fun `should format simple string`() {
        val formatter = JsonFormatter()
        
        val output = formatter.format("Hello World")
        
        assertEquals("\"Hello World\"", output)
    }
    
    @Test
    fun `should format string with special characters`() {
        val formatter = JsonFormatter()
        
        val output = formatter.format("Hello \"World\"\nNew Line\tTab\\Backslash")
        
        assertEquals("\"Hello \\\"World\\\"\\nNew Line\\tTab\\\\Backslash\"", output)
    }
}