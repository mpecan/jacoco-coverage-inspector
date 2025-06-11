package io.github.mpecan.jacoco.parser

import io.github.mpecan.jacoco.model.CoverageType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import java.io.File

class JacocoXmlParserTest {
    
    private val parser = JacocoXmlParser()
    
    @Test
    fun `should parse simple JaCoCo report`() {
        val xmlContent = """
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <report name="TestProject">
                <sessioninfo id="test-session" start="1234567890" dump="1234567891"/>
                <counter type="INSTRUCTION" missed="10" covered="90"/>
                <counter type="BRANCH" missed="5" covered="15"/>
                <counter type="LINE" missed="2" covered="18"/>
                <counter type="COMPLEXITY" missed="3" covered="7"/>
                <counter type="METHOD" missed="1" covered="9"/>
                <counter type="CLASS" missed="0" covered="5"/>
                <package name="com/example">
                    <counter type="INSTRUCTION" missed="10" covered="90"/>
                    <counter type="BRANCH" missed="5" covered="15"/>
                    <counter type="LINE" missed="2" covered="18"/>
                    <class name="com/example/TestClass" sourcefilename="TestClass.java">
                        <counter type="INSTRUCTION" missed="5" covered="45"/>
                        <counter type="BRANCH" missed="2" covered="8"/>
                        <counter type="LINE" missed="1" covered="9"/>
                        <method name="testMethod" desc="()V" line="10">
                            <counter type="INSTRUCTION" missed="2" covered="20"/>
                            <counter type="BRANCH" missed="1" covered="4"/>
                            <counter type="LINE" missed="0" covered="5"/>
                        </method>
                    </class>
                </package>
            </report>
        """.trimIndent()
        
        val tempFile = File.createTempFile("jacoco-test", ".xml")
        tempFile.writeText(xmlContent)
        
        try {
            val result = parser.parseReport(tempFile)
            
            // Verify project data
            assertEquals("test-session", result.projectName)
            assertNotNull(result.getCounter(CoverageType.INSTRUCTION))
            assertEquals(90, result.getCounter(CoverageType.INSTRUCTION)!!.covered)
            assertEquals(10, result.getCounter(CoverageType.INSTRUCTION)!!.missed)
            assertEquals(0.9, result.getCoverageRatio(CoverageType.INSTRUCTION)!!, 0.001)
            
            // Verify package data
            assertEquals(1, result.packages.size)
            val packageData = result.packages[0]
            assertEquals("com.example", packageData.packageName)
            assertNotNull(packageData.getCounter(CoverageType.LINE))
            assertEquals(18, packageData.getCounter(CoverageType.LINE)!!.covered)
            
            // Verify class data
            assertEquals(1, packageData.classes.size)
            val classData = packageData.classes[0]
            assertEquals("com.example.TestClass", classData.className)
            assertEquals("TestClass.java", classData.sourceFileName)
            assertNotNull(classData.getCounter(CoverageType.INSTRUCTION))
            assertEquals(45, classData.getCounter(CoverageType.INSTRUCTION)!!.covered)
            
            // Verify method data
            assertEquals(1, classData.methods.size)
            val methodData = classData.methods[0]
            assertEquals("testMethod", methodData.methodName)
            assertEquals("()V", methodData.descriptor)
            assertEquals(10, methodData.line)
            assertNotNull(methodData.getCounter(CoverageType.LINE))
            assertEquals(5, methodData.getCounter(CoverageType.LINE)!!.covered)
            
        } finally {
            tempFile.delete()
        }
    }
    
    @Test
    fun `should handle missing counters gracefully`() {
        val xmlContent = """
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <report name="MinimalProject">
                <sessioninfo id="minimal-session"/>
                <counter type="LINE" missed="5" covered="10"/>
                <package name="com/example">
                    <counter type="LINE" missed="5" covered="10"/>
                    <class name="com/example/SimpleClass">
                        <counter type="LINE" missed="5" covered="10"/>
                    </class>
                </package>
            </report>
        """.trimIndent()
        
        val tempFile = File.createTempFile("jacoco-minimal", ".xml")
        tempFile.writeText(xmlContent)
        
        try {
            val result = parser.parseReport(tempFile)
            
            assertEquals("minimal-session", result.projectName)
            assertNotNull(result.getCounter(CoverageType.LINE))
            assertEquals(null, result.getCounter(CoverageType.BRANCH)) // Not present in XML
            
        } finally {
            tempFile.delete()
        }
    }
    
    @Test
    fun `should handle project name fallback`() {
        val xmlContent = """
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <report name="TestProject">
                <counter type="LINE" missed="5" covered="10"/>
            </report>
        """.trimIndent()
        
        val tempFile = File.createTempFile("jacoco-no-session", ".xml")
        tempFile.writeText(xmlContent)
        
        try {
            val result = parser.parseReport(tempFile)
            assertEquals("Unknown Project", result.projectName)
            
        } finally {
            tempFile.delete()
        }
    }
    
    @Test
    fun `should throw exception for missing file`() {
        val nonExistentFile = File("non-existent-file.xml")
        
        try {
            parser.parseReport(nonExistentFile)
            assertTrue(false, "Should have thrown exception")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message!!.contains("does not exist"))
        }
    }
    
    @Test
    fun `should throw exception for invalid XML structure`() {
        val xmlContent = """
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <invalid-root>
                <counter type="LINE" missed="5" covered="10"/>
            </invalid-root>
        """.trimIndent()
        
        val tempFile = File.createTempFile("jacoco-invalid", ".xml")
        tempFile.writeText(xmlContent)
        
        try {
            parser.parseReport(tempFile)
            assertTrue(false, "Should have thrown exception")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message!!.contains("Invalid JaCoCo report"))
        } finally {
            tempFile.delete()
        }
    }
}