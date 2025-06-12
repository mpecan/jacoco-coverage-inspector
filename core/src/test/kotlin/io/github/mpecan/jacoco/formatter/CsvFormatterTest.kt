package io.github.mpecan.jacoco.formatter

import io.github.mpecan.jacoco.model.*
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertContains
import kotlin.test.assertEquals

class CsvFormatterTest {
    
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
    fun `should format project coverage as CSV`() {
        val formatter = CsvFormatter()
        val project = createTestProject()
        
        val output = formatter.format(project)
        
        // Check header
        assertContains(output, "Type,Name,ClassCovered,ClassMissed,ClassTotal,ClassPercentage")
        assertContains(output, "MethodCovered,MethodMissed,MethodTotal,MethodPercentage")
        assertContains(output, "LineCovered,LineMissed,LineTotal,LinePercentage")
        assertContains(output, "BranchCovered,BranchMissed,BranchTotal,BranchPercentage")
        assertContains(output, "InstructionCovered,InstructionMissed,InstructionTotal,InstructionPercentage")
        assertContains(output, "ComplexityCovered,ComplexityMissed,ComplexityTotal,ComplexityPercentage")
        
        // Check data row
        assertContains(output, "PROJECT,TestProject,8,2,10,80.00,15,5,20,75.00,90,10,100,90.00,80,40,120,66.67,100,20,120,83.33,50,15,65,76.92")
    }
    
    @Test
    fun `should format empty list as empty string`() {
        val formatter = CsvFormatter()
        val emptyList = emptyList<PackageCoverageData>()
        
        val output = formatter.format(emptyList)
        
        assertEquals("", output)
    }
    
    @Test
    fun `should format package list as CSV`() {
        val formatter = CsvFormatter()
        
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
        
        // Check header
        assertContains(output, "Type,Name,ClassCovered,ClassMissed,ClassTotal,ClassPercentage")
        
        // Check data rows
        assertContains(output, "PACKAGE,com.example.service,5,1,6,83.33,20,5,25,80.00,100,20,120,83.33,40,10,50,80.00,0,0,0,0.00,0,0,0,0.00")
        assertContains(output, "PACKAGE,com.example.controller,5,1,6,83.33,20,5,25,80.00,100,20,120,83.33,40,10,50,80.00,0,0,0,0.00,0,0,0,0.00")
    }
    
    @Test
    fun `should format class list as CSV`() {
        val formatter = CsvFormatter()
        
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
        
        // Check header - includes SourceFile column for classes
        assertContains(output, "Type,Name,SourceFile,ClassCovered,ClassMissed,ClassTotal,ClassPercentage")
        
        // Check data rows
        assertContains(output, "CLASS,com.example.UserService,UserService.java,1,0,1,100.00,10,2,12,83.33,50,10,60,83.33,20,5,25,80.00,0,0,0,0.00,0,0,0,0.00")
        assertContains(output, "CLASS,com.example.ProductController,ProductController.java,1,0,1,100.00,10,2,12,83.33,50,10,60,83.33,20,5,25,80.00,0,0,0,0.00,0,0,0,0.00")
    }
    
    @Test
    fun `should handle null source file name`() {
        val formatter = CsvFormatter()
        
        val counters = mapOf(
            CoverageType.CLASS to createTestCounter(CoverageType.CLASS, 1, 0)
        )
        
        val classes = listOf(
            ClassCoverageData("com.example.UserService", counters, null)
        )
        
        val output = formatter.format(classes)
        
        assertContains(output, "CLASS,com.example.UserService,,1,0,1,100.00")
    }
    
    @Test
    fun `should escape CSV special characters`() {
        val formatter = CsvFormatter()
        
        val counters = mapOf(
            CoverageType.CLASS to createTestCounter(CoverageType.CLASS, 1, 0)
        )
        
        val classes = listOf(
            ClassCoverageData("com.example,Test\"Service", counters, "Test,File\".java")
        )
        
        val output = formatter.format(classes)
        
        assertContains(output, "\"com.example,Test\"\"Service\"")
        assertContains(output, "\"Test,File\"\".java\"")
    }
    
    @Test
    fun `should handle missing counters with zeros`() {
        val formatter = CsvFormatter()
        
        // Only LINE coverage available
        val counters = mapOf(
            CoverageType.LINE to createTestCounter(CoverageType.LINE, 50, 10)
        )
        
        val packages = listOf(
            PackageCoverageData("com.example", counters)
        )
        
        val output = formatter.format(packages)
        
        // Missing coverage types should show as 0,0,0,0.00
        assertContains(output, "PACKAGE,com.example,0,0,0,0.00,0,0,0,0.00,50,10,60,83.33,0,0,0,0.00,0,0,0,0.00,0,0,0,0.00")
    }
    
    @Test
    fun `should format project with nested packages and classes`() {
        val formatter = CsvFormatter()
        
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
        
        // Should contain project, package, and class rows
        assertContains(output, "PROJECT,TestProject")
        assertContains(output, "PACKAGE,com.example.service")
        assertContains(output, "CLASS,UserService,UserService.java")
    }
    
    @Test
    fun `should format simple string`() {
        val formatter = CsvFormatter()
        
        val output = formatter.format("Hello World")
        
        assertEquals("Hello World", output)
    }
    
    @Test
    fun `should format string with CSV special characters`() {
        val formatter = CsvFormatter()
        
        val output = formatter.format("Hello, \"World\"")
        
        assertEquals("\"Hello, \"\"World\"\"\"", output)
    }
    
    @Test
    fun `should handle string with newlines`() {
        val formatter = CsvFormatter()
        
        val output = formatter.format("Hello\nWorld")
        
        assertEquals("\"Hello\nWorld\"", output)
    }
}