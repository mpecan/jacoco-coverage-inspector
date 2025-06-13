package io.github.mpecan.jacoco.tasks

import io.github.mpecan.jacoco.JacocoInspectorExtension
import io.github.mpecan.jacoco.model.OutputFormat
import org.gradle.api.provider.Property
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TaskOverrideTests {

    private data class Case<T>(
        val fieldName: String,
        val taskPropertyGetter: (BaseInspectorTask) -> Property<T>,
        val extension: JacocoInspectorExtension,
        val expectedValue: T,
        val setPropertyOnTask: (BaseInspectorTask) -> Unit = { task -> }
    )

    @TestFactory
    fun `should correctly manage the default for a field`(): List<DynamicTest> = listOf(
        // Format field tests
        Case(
            "outputFormat defined in extension",
            BaseInspectorTask::format,
            stubExtension(format = OutputFormat.TABLE),
            OutputFormat.TABLE
        ),
        Case(
            "outputFormat defined in task overrides extension",
            BaseInspectorTask::format,
            stubExtension(format = OutputFormat.CSV),
            OutputFormat.TABLE,
            { task -> task.format.set(OutputFormat.TABLE) }
        ),
        
        // Color output tests
        Case(
            "colorOutput defined in extension",
            BaseInspectorTask::colorOutput,
            stubExtension(colorOutput = true),
            true
        ),
        Case(
            "colorOutput defined in task overrides extension",
            BaseInspectorTask::colorOutput,
            stubExtension(colorOutput = false),
            true,
            { task -> task.colorOutput.set(true) }
        ),
        
        // Minimum coverage threshold tests
        Case(
            "minClassCoverage defined in extension",
            BaseInspectorTask::minClassCoverage,
            stubExtension(minClassCoverage = 85.0),
            85.0
        ),
        Case(
            "minClassCoverage defined in task overrides extension",
            BaseInspectorTask::minClassCoverage,
            stubExtension(minClassCoverage = 75.0),
            90.0,
            { task -> task.minClassCoverage.set(90.0) }
        ),
        Case(
            "minMethodCoverage defined in extension",
            BaseInspectorTask::minMethodCoverage,
            stubExtension(minMethodCoverage = 80.0),
            80.0
        ),
        Case(
            "minMethodCoverage defined in task overrides extension",
            BaseInspectorTask::minMethodCoverage,
            stubExtension(minMethodCoverage = 70.0),
            85.0,
            { task -> task.minMethodCoverage.set(85.0) }
        ),
        Case(
            "minLineCoverage defined in extension",
            BaseInspectorTask::minLineCoverage,
            stubExtension(minLineCoverage = 75.0),
            75.0
        ),
        Case(
            "minLineCoverage defined in task overrides extension",
            BaseInspectorTask::minLineCoverage,
            stubExtension(minLineCoverage = 65.0),
            80.0,
            { task -> task.minLineCoverage.set(80.0) }
        ),
        Case(
            "minBranchCoverage defined in extension",
            BaseInspectorTask::minBranchCoverage,
            stubExtension(minBranchCoverage = 70.0),
            70.0
        ),
        Case(
            "minBranchCoverage defined in task overrides extension",
            BaseInspectorTask::minBranchCoverage,
            stubExtension(minBranchCoverage = 60.0),
            75.0,
            { task -> task.minBranchCoverage.set(75.0) }
        ),
        Case(
            "minInstructionCoverage defined in extension",
            BaseInspectorTask::minInstructionCoverage,
            stubExtension(minInstructionCoverage = 88.0),
            88.0
        ),
        Case(
            "minInstructionCoverage defined in task overrides extension",
            BaseInspectorTask::minInstructionCoverage,
            stubExtension(minInstructionCoverage = 78.0),
            92.0,
            { task -> task.minInstructionCoverage.set(92.0) }
        ),
        Case(
            "minComplexityCoverage defined in extension",
            BaseInspectorTask::minComplexityCoverage,
            stubExtension(minComplexityCoverage = 82.0),
            82.0
        ),
        Case(
            "minComplexityCoverage defined in task overrides extension",
            BaseInspectorTask::minComplexityCoverage,
            stubExtension(minComplexityCoverage = 72.0),
            87.0,
            { task -> task.minComplexityCoverage.set(87.0) }
        ),
        
        // Maximum coverage threshold tests
        Case(
            "maxClassCoverage defined in extension",
            BaseInspectorTask::maxClassCoverage,
            stubExtension(maxClassCoverage = 95.0),
            95.0
        ),
        Case(
            "maxClassCoverage defined in task overrides extension",
            BaseInspectorTask::maxClassCoverage,
            stubExtension(maxClassCoverage = 90.0),
            98.0,
            { task -> task.maxClassCoverage.set(98.0) }
        ),
        Case(
            "maxMethodCoverage defined in extension",
            BaseInspectorTask::maxMethodCoverage,
            stubExtension(maxMethodCoverage = 92.0),
            92.0
        ),
        Case(
            "maxMethodCoverage defined in task overrides extension",
            BaseInspectorTask::maxMethodCoverage,
            stubExtension(maxMethodCoverage = 88.0),
            95.0,
            { task -> task.maxMethodCoverage.set(95.0) }
        ),
        Case(
            "maxLineCoverage defined in extension",
            BaseInspectorTask::maxLineCoverage,
            stubExtension(maxLineCoverage = 90.0),
            90.0
        ),
        Case(
            "maxLineCoverage defined in task overrides extension",
            BaseInspectorTask::maxLineCoverage,
            stubExtension(maxLineCoverage = 85.0),
            93.0,
            { task -> task.maxLineCoverage.set(93.0) }
        ),
        Case(
            "maxBranchCoverage defined in extension",
            BaseInspectorTask::maxBranchCoverage,
            stubExtension(maxBranchCoverage = 88.0),
            88.0
        ),
        Case(
            "maxBranchCoverage defined in task overrides extension",
            BaseInspectorTask::maxBranchCoverage,
            stubExtension(maxBranchCoverage = 83.0),
            91.0,
            { task -> task.maxBranchCoverage.set(91.0) }
        ),
        Case(
            "maxInstructionCoverage defined in extension",
            BaseInspectorTask::maxInstructionCoverage,
            stubExtension(maxInstructionCoverage = 94.0),
            94.0
        ),
        Case(
            "maxInstructionCoverage defined in task overrides extension",
            BaseInspectorTask::maxInstructionCoverage,
            stubExtension(maxInstructionCoverage = 89.0),
            97.0,
            { task -> task.maxInstructionCoverage.set(97.0) }
        ),
        Case(
            "maxComplexityCoverage defined in extension",
            BaseInspectorTask::maxComplexityCoverage,
            stubExtension(maxComplexityCoverage = 86.0),
            86.0
        ),
        Case(
            "maxComplexityCoverage defined in task overrides extension",
            BaseInspectorTask::maxComplexityCoverage,
            stubExtension(maxComplexityCoverage = 81.0),
            89.0,
            { task -> task.maxComplexityCoverage.set(89.0) }
        )
    ).map { (description, getter, extension,expectedValue, setPropertyOnTask) ->
        dynamicTest(description) {
            val task = ProjectBuilder.builder().build().tasks.create(
                "inspector",
                BaseInspectorTask::class.java
            )
            setPropertyOnTask(task)
            task.applyDefaults(extension)
            assertEquals(expectedValue, getter(task).get())
        }
    }

    @Test
    fun `should correctly manage the default for includePatterns`() {
        val task = ProjectBuilder.builder().build().tasks.create(
            "inspector",
            BaseInspectorTask::class.java
        )
        task.applyDefaults(
            stubExtension(
                includePatterns = listOf("**/*IncludeTest.*", "com.example.*")
            )
        )
        assertEquals(
            listOf("**/*IncludeTest.*", "com.example.*"),
            task.includePatterns.get(),
            "includePatterns should be applied from extension"
        )
    }
    
    @Test
    fun `should correctly manage the default for excludePatterns`() {
        val task = ProjectBuilder.builder().build().tasks.create(
            "inspector",
            BaseInspectorTask::class.java
        )
        task.applyDefaults(
            stubExtension(
                excludePatterns = listOf("**/*ExcludeTest.*", "**/*Mock*")
            )
        )
        assertEquals(
            listOf("**/*ExcludeTest.*", "**/*Mock*"),
            task.excludePatterns.get(),
            "excludePatterns should be applied from extension"
        )
    }
    
    @Test
    fun `should not override task-level patterns with extension patterns`() {
        val task = ProjectBuilder.builder().build().tasks.create(
            "inspector",
            BaseInspectorTask::class.java
        )
        
        // Set task-level patterns first
        task.includePatterns.set(listOf("task.level.*"))
        task.excludePatterns.set(listOf("task.exclude.*"))
        
        // Apply extension with different patterns
        task.applyDefaults(
            stubExtension(
                includePatterns = listOf("extension.include.*"),
                excludePatterns = listOf("extension.exclude.*")
            )
        )
        
        // Task-level patterns should be preserved
        assertEquals(
            listOf("task.level.*"),
            task.includePatterns.get(),
            "Task-level includePatterns should not be overridden"
        )
        assertEquals(
            listOf("task.exclude.*"),
            task.excludePatterns.get(),
            "Task-level excludePatterns should not be overridden"
        )
    }
    
    @Test
    fun `should not apply empty extension patterns`() {
        val task = ProjectBuilder.builder().build().tasks.create(
            "inspector",
            BaseInspectorTask::class.java
        )
        
        // Apply extension with empty patterns (default behavior)
        task.applyDefaults(stubExtension(
            includePatterns = emptyList(),
            excludePatterns = emptyList()
        ))
        
        // Properties should not be set when extension has empty patterns
        // because the implementation checks extension.includePatterns.isNotEmpty()
        assertTrue(
            task.includePatterns.get().isEmpty(),
            "includePatterns should be empty when extension has empty patterns"
        )
        assertTrue(
            task.excludePatterns.get().isEmpty(),
            "excludePatterns should be empty when extension has empty patterns"
        )
    }


}

fun stubExtension(
    format: OutputFormat = OutputFormat.TABLE,
    colorOutput: Boolean = true,

    /**
     * Default minimum coverage thresholds
     */
    minClassCoverage: Double? = null,
    minMethodCoverage: Double? = null,
    minLineCoverage: Double? = null,
    minBranchCoverage: Double? = null,
    minInstructionCoverage: Double? = null,
    minComplexityCoverage: Double? = null,

    /**
     * Default maximum coverage thresholds
     */
    maxClassCoverage: Double? = null,
    maxMethodCoverage: Double? = null,
    maxLineCoverage: Double? = null,
    maxBranchCoverage: Double? = null,
    maxInstructionCoverage: Double? = null,
    maxComplexityCoverage: Double? = null,

    /**
     * Default exclude patterns
     */
    excludePatterns: List<String> = emptyList(),

    /**
     * Default include patterns
     */
    includePatterns: List<String> = emptyList(),
) = JacocoInspectorExtension().apply {
    this.defaultFormat = format
    this.colorOutput = colorOutput
    this.minClassCoverage = minClassCoverage
    this.minMethodCoverage = minMethodCoverage
    this.minLineCoverage = minLineCoverage
    this.minBranchCoverage = minBranchCoverage
    this.minInstructionCoverage = minInstructionCoverage
    this.minComplexityCoverage = minComplexityCoverage
    this.maxClassCoverage = maxClassCoverage
    this.maxMethodCoverage = maxMethodCoverage
    this.maxLineCoverage = maxLineCoverage
    this.maxBranchCoverage = maxBranchCoverage
    this.maxInstructionCoverage = maxInstructionCoverage
    this.maxComplexityCoverage = maxComplexityCoverage
    this.excludePatterns = excludePatterns
    this.includePatterns = includePatterns
}