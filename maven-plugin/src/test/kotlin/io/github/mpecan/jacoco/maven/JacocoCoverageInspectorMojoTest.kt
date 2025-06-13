package io.github.mpecan.jacoco.maven

import io.github.mpecan.jacoco.model.*
import org.apache.maven.plugin.MojoExecutionException
import org.apache.maven.plugin.logging.Log
import org.apache.maven.project.MavenProject
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path

class JacocoCoverageInspectorMojoTest {

    private lateinit var testMojo: TestMojo
    private lateinit var mockLog: TestLog
    private lateinit var mockProject: MavenProject

    @TempDir
    lateinit var tempDir: Path

    @BeforeEach
    fun setUp() {
        testMojo = TestMojo()
        mockLog = TestLog()
        mockProject = MavenProject()
        
        testMojo.log = mockLog
        testMojo.project = mockProject
        testMojo.format = "TABLE"
        testMojo.colorOutput = true
    }

    @Test
    fun `should warn and return when jacoco report file does not exist`() {
        val nonExistentFile = tempDir.resolve("nonexistent.xml").toFile()
        testMojo.jacocoReportFile = nonExistentFile

        testMojo.execute()

        assertThat(mockLog.warnings).contains("JaCoCo report file not found: ${nonExistentFile.absolutePath}")
        assertThat(mockLog.infos).contains("No coverage data to display")
    }

    @Test
    fun `should execute successfully with valid report file`() {
        // Create a minimal JaCoCo XML report file
        val reportFile = tempDir.resolve("jacoco.xml").toFile()
        reportFile.writeText("""
            <?xml version="1.0" encoding="UTF-8"?>
            <report name="test-project">
                <package name="com/example">
                    <class name="com/example/TestClass" sourcefilename="TestClass.java">
                        <counter type="INSTRUCTION" missed="5" covered="10"/>
                        <counter type="LINE" missed="2" covered="8"/>
                        <counter type="BRANCH" missed="1" covered="3"/>
                        <counter type="COMPLEXITY" missed="1" covered="2"/>
                        <counter type="METHOD" missed="1" covered="3"/>
                        <counter type="CLASS" missed="0" covered="1"/>
                    </class>
                </package>
            </report>
        """.trimIndent())

        testMojo.jacocoReportFile = reportFile
        testMojo.testOutput = "Test output"

        testMojo.execute()

        assertThat(mockLog.infos).contains("\nTest output")
        assertThat(testMojo.receivedCoverageData).isNotNull
        assertThat(testMojo.receivedFilter).isNotNull
    }

    @Test
    fun `should apply all filter parameters correctly`() {
        // Create a minimal JaCoCo XML report file
        val reportFile = tempDir.resolve("jacoco.xml").toFile()
        reportFile.writeText("""
            <?xml version="1.0" encoding="UTF-8"?>
            <report name="test-project">
                <package name="com/example">
                    <class name="com/example/TestClass" sourcefilename="TestClass.java">
                        <counter type="LINE" missed="2" covered="8"/>
                    </class>
                </package>
            </report>
        """.trimIndent())

        testMojo.jacocoReportFile = reportFile
        testMojo.minCoverage = 80.0
        testMojo.coverageType = "LINE"
        testMojo.minClassCoverage = 70.0
        testMojo.maxMethodCoverage = 95.0
        testMojo.includePatterns = listOf("com.example.**")
        testMojo.excludePatterns = listOf("**.test.**")
        testMojo.packageFilter = "com.example"

        testMojo.execute()

        val filter = testMojo.receivedFilter!!
        assertThat(filter.minThresholds).containsEntry(CoverageType.LINE, 0.8)
        assertThat(filter.minThresholds).containsEntry(CoverageType.CLASS, 0.7)
        assertThat(filter.maxThresholds).containsEntry(CoverageType.METHOD, 0.95)
        assertThat(filter.includePatterns).contains("com.example.**", "com.example*")
        assertThat(filter.excludePatterns).contains("**.test.**")
    }

    @Test
    fun `should handle different output formats`() {
        val reportFile = tempDir.resolve("jacoco.xml").toFile()
        reportFile.writeText("""
            <?xml version="1.0" encoding="UTF-8"?>
            <report name="test-project">
                <package name="com/example">
                    <class name="com/example/TestClass" sourcefilename="TestClass.java">
                        <counter type="LINE" missed="2" covered="8"/>
                    </class>
                </package>
            </report>
        """.trimIndent())

        testMojo.jacocoReportFile = reportFile
        
        // Test JSON format
        testMojo.format = "json"
        testMojo.execute()
        assertThat(testMojo.usedFormat).isEqualTo(OutputFormat.JSON)
        
        // Test CSV format
        testMojo.format = "CSV"
        testMojo.execute()
        assertThat(testMojo.usedFormat).isEqualTo(OutputFormat.CSV)
        
        // Test MARKDOWN format
        testMojo.format = "markdown"
        testMojo.execute()
        assertThat(testMojo.usedFormat).isEqualTo(OutputFormat.MARKDOWN)
    }

    @Test
    fun `should throw MojoExecutionException on parsing error`() {
        val reportFile = tempDir.resolve("invalid.xml").toFile()
        reportFile.writeText("invalid xml content")

        testMojo.jacocoReportFile = reportFile

        assertThatThrownBy { testMojo.execute() }
            .isInstanceOf(MojoExecutionException::class.java)
            .hasMessageContaining("Error processing JaCoCo report")
    }

    @Test
    fun `should handle null optional parameters`() {
        val reportFile = tempDir.resolve("jacoco.xml").toFile()
        reportFile.writeText("""
            <?xml version="1.0" encoding="UTF-8"?>
            <report name="test-project">
                <package name="com/example">
                    <class name="com/example/TestClass" sourcefilename="TestClass.java">
                        <counter type="LINE" missed="2" covered="8"/>
                    </class>
                </package>
            </report>
        """.trimIndent())

        testMojo.jacocoReportFile = reportFile
        // All optional parameters are null by default

        testMojo.execute()

        val filter = testMojo.receivedFilter!!
        assertThat(filter.minThresholds).isEmpty()
        assertThat(filter.maxThresholds).isEmpty()
        assertThat(filter.includePatterns).isEmpty()
        assertThat(filter.excludePatterns).isEmpty()
    }

    /**
     * Test implementation of the abstract mojo for testing purposes
     */
    private class TestMojo : JacocoCoverageInspectorMojo() {
        var testOutput: String = "default output"
        var receivedCoverageData: ProjectCoverageData? = null
        var receivedFilter: CoverageFilter? = null
        var usedFormat: OutputFormat? = null

        override fun generateOutput(coverageData: ProjectCoverageData, filter: CoverageFilter): Any {
            this.receivedCoverageData = coverageData
            this.receivedFilter = filter
            this.usedFormat = OutputFormat.valueOf(format.uppercase())
            return testOutput
        }
    }

    /**
     * Simple test log implementation
     */
    private class TestLog : Log {
        val debugs = mutableListOf<String>()
        val infos = mutableListOf<String>()
        val warnings = mutableListOf<String>()
        val errors = mutableListOf<String>()

        override fun isDebugEnabled() = true
        override fun debug(content: CharSequence?) { content?.let { debugs.add(it.toString()) } }
        override fun debug(content: CharSequence?, error: Throwable?) { content?.let { debugs.add(it.toString()) } }
        override fun debug(error: Throwable?) { error?.let { debugs.add(it.toString()) } }

        override fun isInfoEnabled() = true
        override fun info(content: CharSequence?) { content?.let { infos.add(it.toString()) } }
        override fun info(content: CharSequence?, error: Throwable?) { content?.let { infos.add(it.toString()) } }
        override fun info(error: Throwable?) { error?.let { infos.add(it.toString()) } }

        override fun isWarnEnabled() = true
        override fun warn(content: CharSequence?) { content?.let { warnings.add(it.toString()) } }
        override fun warn(content: CharSequence?, error: Throwable?) { content?.let { warnings.add(it.toString()) } }
        override fun warn(error: Throwable?) { error?.let { warnings.add(it.toString()) } }

        override fun isErrorEnabled() = true
        override fun error(content: CharSequence?) { content?.let { errors.add(it.toString()) } }
        override fun error(content: CharSequence?, error: Throwable?) { content?.let { errors.add(it.toString()) } }
        override fun error(error: Throwable?) { error?.let { errors.add(it.toString()) } }
    }
}