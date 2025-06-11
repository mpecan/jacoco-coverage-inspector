package io.github.mpecan.jacoco.parser

import io.github.mpecan.jacoco.model.*
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Parser for JaCoCo XML reports
 */
class JacocoXmlParser {
    
    fun parseReport(reportFile: File): ProjectCoverageData {
        if (!reportFile.exists()) {
            throw IllegalArgumentException("JaCoCo report file does not exist: ${reportFile.absolutePath}")
        }
        
        val document = parseXmlFile(reportFile)
        val reportElement = document.documentElement
        
        if (reportElement.tagName != "report") {
            throw IllegalArgumentException("Invalid JaCoCo report: root element should be 'report'")
        }
        
        // Parse session info to get project name
        val projectName = extractProjectName(reportElement)
        
        // Parse counters at report level
        val reportCounters = parseCounters(reportElement)
        
        // Parse packages
        val packages = parsePackages(reportElement)
        
        return ProjectCoverageData(
            projectName = projectName,
            projectCounters = reportCounters,
            packages = packages
        )
    }
    
    private fun parseXmlFile(file: File): Document {
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        return builder.parse(file)
    }
    
    private fun extractProjectName(reportElement: Element): String {
        // Try to get from sessioninfo first
        val sessionInfos = reportElement.getElementsByTagName("sessioninfo")
        if (sessionInfos.length > 0) {
            val sessionInfo = sessionInfos.item(0) as Element
            val id = sessionInfo.getAttribute("id")
            if (id.isNotEmpty()) {
                return id
            }
        }
        
        // Fallback to "Unknown Project"
        return "Unknown Project"
    }
    
    private fun parseCounters(element: Element): Map<CoverageType, CoverageCounter> {
        val counters = mutableMapOf<CoverageType, CoverageCounter>()
        
        // Only get direct counter children, not all descendants
        val childNodes = element.childNodes
        for (i in 0 until childNodes.length) {
            val node = childNodes.item(i)
            if (node is Element && node.tagName == "counter") {
                val type = node.getAttribute("type")
                val missed = node.getAttribute("missed").toIntOrNull() ?: 0
                val covered = node.getAttribute("covered").toIntOrNull() ?: 0
                
                val coverageType = when (type.uppercase()) {
                    "INSTRUCTION" -> CoverageType.INSTRUCTION
                    "BRANCH" -> CoverageType.BRANCH
                    "LINE" -> CoverageType.LINE
                    "COMPLEXITY" -> CoverageType.COMPLEXITY
                    "METHOD" -> CoverageType.METHOD
                    "CLASS" -> CoverageType.CLASS
                    else -> continue // Skip unknown types
                }
                
                counters[coverageType] = CoverageCounter(coverageType, missed, covered)
            }
        }
        
        return counters
    }
    
    private fun parsePackages(reportElement: Element): List<PackageCoverageData> {
        val packages = mutableListOf<PackageCoverageData>()
        
        // Only get direct package children
        val childNodes = reportElement.childNodes
        for (i in 0 until childNodes.length) {
            val node = childNodes.item(i)
            if (node is Element && node.tagName == "package") {
                val packageName = node.getAttribute("name").replace("/", ".")
                val packageCounters = parseCounters(node)
                val classes = parseClasses(node)
                
                packages.add(
                    PackageCoverageData(
                        packageName = packageName,
                        packageCounters = packageCounters,
                        classes = classes
                    )
                )
            }
        }
        
        return packages
    }
    
    private fun parseClasses(packageElement: Element): List<ClassCoverageData> {
        val classes = mutableListOf<ClassCoverageData>()
        
        // Only get direct class children
        val childNodes = packageElement.childNodes
        for (i in 0 until childNodes.length) {
            val node = childNodes.item(i)
            if (node is Element && node.tagName == "class") {
                val className = node.getAttribute("name").replace("/", ".")
                val sourceFileName = node.getAttribute("sourcefilename").takeIf { it.isNotEmpty() }
                val classCounters = parseCounters(node)
                val methods = parseMethods(node)
                
                classes.add(
                    ClassCoverageData(
                        className = className,
                        classCounters = classCounters,
                        sourceFileName = sourceFileName,
                        methods = methods
                    )
                )
            }
        }
        
        return classes
    }
    
    private fun parseMethods(classElement: Element): List<MethodCoverageData> {
        val methods = mutableListOf<MethodCoverageData>()
        
        // Only get direct method children
        val childNodes = classElement.childNodes
        for (i in 0 until childNodes.length) {
            val node = childNodes.item(i)
            if (node is Element && node.tagName == "method") {
                val methodName = node.getAttribute("name")
                val descriptor = node.getAttribute("desc").takeIf { it.isNotEmpty() }
                val line = node.getAttribute("line").toIntOrNull()
                val methodCounters = parseCounters(node)
                
                methods.add(
                    MethodCoverageData(
                        methodName = methodName,
                        methodCounters = methodCounters,
                        descriptor = descriptor,
                        line = line
                    )
                )
            }
        }
        
        return methods
    }
}