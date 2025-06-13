package io.github.mpecan.jacoco.model

import kotlinx.serialization.Serializable

/**
 * Base class for all coverage data (projects, packages, classes, methods)
 */
@Serializable
abstract class CoverageData(
    val name: String,
    val counters: Map<CoverageType, CoverageCounter>
) {
    fun getCounter(type: CoverageType): CoverageCounter? = counters[type]
    
    fun getCoverageRatio(type: CoverageType): Double? = getCounter(type)?.ratio
    
    fun getCoveragePercentage(type: CoverageType): Double? = getCounter(type)?.percentage
}

/**
 * Coverage data for a project
 */
@Serializable
data class ProjectCoverageData(
    val projectName: String,
    val projectCounters: Map<CoverageType, CoverageCounter>,
    val packages: List<PackageCoverageData> = emptyList()
) : CoverageData(projectName, projectCounters)

/**
 * Coverage data for a package
 */
@Serializable
data class PackageCoverageData(
    val packageName: String,
    val packageCounters: Map<CoverageType, CoverageCounter>,
    val classes: List<ClassCoverageData> = emptyList()
) : CoverageData(packageName, packageCounters)

/**
 * Coverage data for a class
 */
@Serializable
data class ClassCoverageData(
    val className: String,
    val classCounters: Map<CoverageType, CoverageCounter>,
    val sourceFileName: String? = null,
    val methods: List<MethodCoverageData> = emptyList()
) : CoverageData(className, classCounters)

/**
 * Coverage data for a method
 */
@Serializable
data class MethodCoverageData(
    val methodName: String,
    val methodCounters: Map<CoverageType, CoverageCounter>,
    val descriptor: String? = null,
    val line: Int? = null
) : CoverageData(methodName, methodCounters)