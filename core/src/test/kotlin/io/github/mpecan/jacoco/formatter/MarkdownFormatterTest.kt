package io.github.mpecan.jacoco.formatter

import io.github.mpecan.jacoco.model.*
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertContains
import kotlin.test.assertEquals

class MarkdownFormatterTest {
    
    private fun createTestCounter(type: CoverageType, covered: Int, missed: Int): CoverageCounter {
        return CoverageCounter(type, missed, covered)
    }
    
    private fun createTestProject(): ProjectCoverageData {
        val counters = mapOf(
            CoverageType.INSTRUCTION to createTestCounter(CoverageType.INSTRUCTION, 100, 20),
            CoverageType.BRANCH to createTestCounter(CoverageType.BRANCH, 80, 40),
            CoverageType.LINE to createTestCounter(CoverageType.LINE, 90, 10),
            CoverageType.METHOD to createTestCounter(CoverageType.METHOD, 15, 5),
            CoverageType.CLASS to createTestCounter(CoverageType.CLASS, 8, 2),
            CoverageType.COMPLEXITY to createTestCounter(CoverageType.COMPLEXITY, 50, 15)
        )
        
        return ProjectCoverageData("TestProject", counters)
    }
    
    @Test
    fun `should format project coverage as Markdown`() {
        val formatter = MarkdownFormatter()
        val project = createTestProject()
        
        val output = formatter.format(project)
        
        assertContains(output, "# Project Coverage Summary")
        assertContains(output, "**Project:** TestProject")
        assertContains(output, "## Coverage Overview")
        assertContains(output, "| Type | Covered | Missed | Total | Coverage |")
        assertContains(output, "|------|---------|--------|-------|----------|")
        assertContains(output, "| Instruction | 100 | 20 | 120 | 🟢 83.3% |")
        assertContains(output, "| Line | 90 | 10 | 100 | 🟢 90.0% |")
        assertContains(output, "| Method | 15 | 5 | 20 | 🟡 75.0% |")
        assertContains(output, "| Branch | 80 | 40 | 120 | 🟡 66.7% |")
    }
    
    @Test
    fun `should format empty list with message`() {
        val formatter = MarkdownFormatter()
        val emptyList = emptyList<PackageCoverageData>()
        
        val output = formatter.format(emptyList)
        
        assertEquals("*No items match the specified filters.*\n", output)
    }
    
    @Test
    fun `should format package list as Markdown table`() {
        val formatter = MarkdownFormatter()
        
        val highCoverage = mapOf(
            CoverageType.CLASS to createTestCounter(CoverageType.CLASS, 5, 1),
            CoverageType.METHOD to createTestCounter(CoverageType.METHOD, 90, 10),
            CoverageType.LINE to createTestCounter(CoverageType.LINE, 100, 20),
            CoverageType.BRANCH to createTestCounter(CoverageType.BRANCH, 40, 10)
        )
        
        val lowCoverage = mapOf(
            CoverageType.CLASS to createTestCounter(CoverageType.CLASS, 1, 2),
            CoverageType.METHOD to createTestCounter(CoverageType.METHOD, 20, 30),
            CoverageType.LINE to createTestCounter(CoverageType.LINE, 30, 70),
            CoverageType.BRANCH to createTestCounter(CoverageType.BRANCH, 10, 40)
        )
        
        val packages = listOf(
            PackageCoverageData("com.example.service", highCoverage),
            PackageCoverageData("com.example.controller", lowCoverage)
        )
        
        val output = formatter.format(packages)
        
        assertContains(output, "## Package Coverage")
        assertContains(output, "| Package | Class | Method | Line | Branch |")
        assertContains(output, "|---------|-------|--------|------|--------|")
        assertContains(output, "| com\\.example\\.service | 🟢 83.3% | 🟢 90.0% | 🟢 83.3% | 🟢 80.0% |")
        assertContains(output, "| com\\.example\\.controller | 🔴 33.3% | 🔴 40.0% | 🔴 30.0% | 🔴 20.0% |")
    }
    
    @Test
    fun `should format class list as Markdown table`() {
        val formatter = MarkdownFormatter()
        
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
        
        assertContains(output, "## File Coverage")
        assertContains(output, "| Class | Source File | Class | Method | Line | Branch |")
        assertContains(output, "|-------|-------------|-------|--------|------|--------|")
        assertContains(output, "| com\\.example\\.UserService | UserService\\.java | 🟢 100.0% | 🟢 83.3% | 🟢 83.3% | 🟢 80.0% |")
        assertContains(output, "| com\\.example\\.ProductController | ProductController\\.java | 🟢 100.0% | 🟢 83.3% | 🟢 83.3% | 🟢 80.0% |")
    }
    
    @Test
    fun `should handle null source file name`() {
        val formatter = MarkdownFormatter()
        
        val counters = mapOf(
            CoverageType.CLASS to createTestCounter(CoverageType.CLASS, 1, 0)
        )
        
        val classes = listOf(
            ClassCoverageData("com.example.UserService", counters, null)
        )
        
        val output = formatter.format(classes)
        
        assertContains(output, "| com\\.example\\.UserService | N/A | 🟢 100.0% | N/A | N/A | N/A |")
    }
    
    @Test
    fun `should escape Markdown special characters`() {
        val formatter = MarkdownFormatter()
        
        val counters = mapOf(
            CoverageType.CLASS to createTestCounter(CoverageType.CLASS, 1, 0)
        )
        
        val classes = listOf(
            ClassCoverageData("com.example.Test*Service#1", counters, "Test[File].java")
        )
        
        val output = formatter.format(classes)
        
        assertContains(output, "| com\\.example\\.Test\\*Service\\#1 | Test\\[File\\]\\.java | 🟢 100.0% | N/A | N/A | N/A |")
    }
    
    @Test
    fun `should handle missing counters`() {
        val formatter = MarkdownFormatter()
        
        // Only LINE coverage available
        val counters = mapOf(
            CoverageType.LINE to createTestCounter(CoverageType.LINE, 50, 10)
        )
        
        val packages = listOf(
            PackageCoverageData("com.example", counters)
        )
        
        val output = formatter.format(packages)
        
        assertContains(output, "| com\\.example | N/A | N/A | 🟢 83.3% | N/A |")
    }
    
    @Test
    fun `should use correct emoji indicators for coverage levels`() {
        val formatter = MarkdownFormatter()
        
        val highCoverage = mapOf(
            CoverageType.LINE to createTestCounter(CoverageType.LINE, 90, 10) // 90%
        )
        
        val mediumCoverage = mapOf(
            CoverageType.LINE to createTestCounter(CoverageType.LINE, 70, 30) // 70%
        )
        
        val lowCoverage = mapOf(
            CoverageType.LINE to createTestCounter(CoverageType.LINE, 30, 70) // 30%
        )
        
        val packages = listOf(
            PackageCoverageData("high.coverage", highCoverage),
            PackageCoverageData("medium.coverage", mediumCoverage),
            PackageCoverageData("low.coverage", lowCoverage)
        )
        
        val output = formatter.format(packages)
        
        assertContains(output, "🟢 90.0%") // High coverage (>= 80%)
        assertContains(output, "🟡 70.0%") // Medium coverage (>= 60%)
        assertContains(output, "🔴 30.0%") // Low coverage (< 60%)
    }
    
    @Test
    fun `should format project with nested packages and classes`() {
        val formatter = MarkdownFormatter()
        
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
        
        assertContains(output, "# Project Coverage Summary")
        assertContains(output, "**Project:** TestProject")
        assertContains(output, "## Package Details")
        assertContains(output, "| com\\.example\\.service | 🟡 66.7% | N/A | 🟢 83.3% | N/A |")
    }
    
    @Test
    fun `should format simple string`() {
        val formatter = MarkdownFormatter()
        
        val output = formatter.format("Hello World")
        
        assertEquals("Hello World", output)
    }
    
    @Test
    fun `should format string with markdown special characters`() {
        val formatter = MarkdownFormatter()
        
        val output = formatter.format("Hello *World* with **bold** and [link](url)")
        
        assertEquals("Hello \\*World\\* with \\*\\*bold\\*\\* and \\[link\\]\\(url\\)", output)
    }
    
    @Test
    fun `should format string list as bulleted list`() {
        val formatter = MarkdownFormatter()
        
        val output = formatter.format(listOf("Item 1", "Item 2", "Item 3"))
        
        assertEquals("- Item 1\n- Item 2\n- Item 3", output)
    }
}