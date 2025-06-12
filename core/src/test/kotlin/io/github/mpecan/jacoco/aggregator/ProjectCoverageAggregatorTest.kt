package io.github.mpecan.jacoco.aggregator

import io.github.mpecan.jacoco.model.*
import kotlin.test.*

class ProjectCoverageAggregatorTest {
    
    private val aggregator = ProjectCoverageAggregator()
    
    @Test
    fun `should aggregate single project correctly`() {
        val projectData = createSampleProject()
        
        val result = aggregator.aggregateProject(projectData)
        
        assertEquals("TestProject", result.projectName)
        assertEquals(2, result.packageCount)
        assertEquals(3, result.classCount)
        assertEquals(6, result.methodCount)
        
        // Verify counters
        val lineCounter = result.totalCounters[CoverageType.LINE]
        assertNotNull(lineCounter)
        assertEquals(100, lineCounter.covered)
        assertEquals(50, lineCounter.missed)
    }
    
    @Test
    fun `should handle project with no packages`() {
        val projectData = ProjectCoverageData(
            projectName = "EmptyProject",
            projectCounters = mapOf(
                CoverageType.LINE to CoverageCounter(CoverageType.LINE, missed = 0, covered = 0)
            ),
            packages = emptyList()
        )
        
        val result = aggregator.aggregateProject(projectData)
        
        assertEquals("EmptyProject", result.projectName)
        assertEquals(0, result.packageCount)
        assertEquals(0, result.classCount)
        assertEquals(0, result.methodCount)
    }
    
    @Test
    fun `should aggregate multiple projects correctly`() {
        val project1 = createSampleProject("Project1", 
            linesCovered = 80, linesMissed = 20,
            branchesCovered = 60, branchesMissed = 40
        )
        val project2 = createSampleProject("Project2",
            linesCovered = 120, linesMissed = 30,
            branchesCovered = 90, branchesMissed = 10
        )
        
        val result = aggregator.aggregateMultipleProjects(listOf(project1, project2))
        
        assertEquals("Multi-Project (2 projects)", result.projectName)
        assertEquals(4, result.packageCount) // 2 packages per project
        assertEquals(6, result.classCount) // 3 classes per project
        assertEquals(12, result.methodCount) // 6 methods per project
        
        // Verify aggregated counters
        val lineCounter = result.totalCounters[CoverageType.LINE]
        assertNotNull(lineCounter)
        assertEquals(200, lineCounter.covered) // 80 + 120
        assertEquals(50, lineCounter.missed) // 20 + 30
        
        val branchCounter = result.totalCounters[CoverageType.BRANCH]
        assertNotNull(branchCounter)
        assertEquals(150, branchCounter.covered) // 60 + 90
        assertEquals(50, branchCounter.missed) // 40 + 10
    }
    
    @Test
    fun `should handle empty project list`() {
        val result = aggregator.aggregateMultipleProjects(emptyList())
        
        assertEquals("No Projects", result.projectName)
        assertEquals(0, result.packageCount)
        assertEquals(0, result.classCount)
        assertEquals(0, result.methodCount)
        assertTrue(result.totalCounters.isEmpty())
    }
    
    @Test
    fun `should handle single project in list`() {
        val project = createSampleProject()
        val resultFromList = aggregator.aggregateMultipleProjects(listOf(project))
        val resultDirect = aggregator.aggregateProject(project)
        
        assertEquals(resultDirect.projectName, resultFromList.projectName)
        assertEquals(resultDirect.packageCount, resultFromList.packageCount)
        assertEquals(resultDirect.classCount, resultFromList.classCount)
        assertEquals(resultDirect.methodCount, resultFromList.methodCount)
    }
    
    @Test
    fun `should preserve all coverage types`() {
        val project = ProjectCoverageData(
            projectName = "FullCoverageProject",
            projectCounters = mapOf(
                CoverageType.INSTRUCTION to CoverageCounter(CoverageType.INSTRUCTION, missed = 10, covered = 90),
                CoverageType.BRANCH to CoverageCounter(CoverageType.BRANCH, missed = 5, covered = 15),
                CoverageType.LINE to CoverageCounter(CoverageType.LINE, missed = 20, covered = 80),
                CoverageType.COMPLEXITY to CoverageCounter(CoverageType.COMPLEXITY, missed = 3, covered = 7),
                CoverageType.METHOD to CoverageCounter(CoverageType.METHOD, missed = 2, covered = 8),
                CoverageType.CLASS to CoverageCounter(CoverageType.CLASS, missed = 1, covered = 4)
            ),
            packages = emptyList()
        )
        
        val result = aggregator.aggregateProject(project)
        
        assertEquals(6, result.totalCounters.size)
        CoverageType.entries.forEach { type ->
            assertNotNull(result.totalCounters[type], "Counter for $type should be present")
        }
    }
    
    private fun createSampleProject(
        name: String = "TestProject",
        linesCovered: Int = 100,
        linesMissed: Int = 50,
        branchesCovered: Int = 80,
        branchesMissed: Int = 20
    ): ProjectCoverageData {
        val method1 = MethodCoverageData(
            methodName = "method1",
            methodCounters = mapOf(
                CoverageType.LINE to CoverageCounter(CoverageType.LINE, missed = 5, covered = 10)
            )
        )
        val method2 = MethodCoverageData(
            methodName = "method2",
            methodCounters = mapOf(
                CoverageType.LINE to CoverageCounter(CoverageType.LINE, missed = 10, covered = 20)
            )
        )
        
        val class1 = ClassCoverageData(
            className = "Class1",
            classCounters = mapOf(
                CoverageType.LINE to CoverageCounter(CoverageType.LINE, missed = 15, covered = 30)
            ),
            methods = listOf(method1, method2)
        )
        
        val class2 = ClassCoverageData(
            className = "Class2",
            classCounters = mapOf(
                CoverageType.LINE to CoverageCounter(CoverageType.LINE, missed = 20, covered = 40)
            ),
            methods = listOf(method1, method2)
        )
        
        val class3 = ClassCoverageData(
            className = "Class3",
            classCounters = mapOf(
                CoverageType.LINE to CoverageCounter(CoverageType.LINE, missed = 15, covered = 30)
            ),
            methods = listOf(method1, method2)
        )
        
        val package1 = PackageCoverageData(
            packageName = "com.example.package1",
            packageCounters = mapOf(
                CoverageType.LINE to CoverageCounter(CoverageType.LINE, missed = 35, covered = 70)
            ),
            classes = listOf(class1, class2)
        )
        
        val package2 = PackageCoverageData(
            packageName = "com.example.package2",
            packageCounters = mapOf(
                CoverageType.LINE to CoverageCounter(CoverageType.LINE, missed = 15, covered = 30)
            ),
            classes = listOf(class3)
        )
        
        return ProjectCoverageData(
            projectName = name,
            projectCounters = mapOf(
                CoverageType.LINE to CoverageCounter(CoverageType.LINE, missed = linesMissed, covered = linesCovered),
                CoverageType.BRANCH to CoverageCounter(CoverageType.BRANCH, missed = branchesMissed, covered = branchesCovered)
            ),
            packages = listOf(package1, package2)
        )
    }
}