package io.github.mpecan.jacoco.formatter

import io.github.mpecan.jacoco.model.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Formats coverage data as JSON output using kotlinx.serialization
 */
class JsonFormatter : CoverageFormatter {
    
    private val json = Json {
        prettyPrint = true
        encodeDefaults = true
    }
    
    override fun format(data: Any): String {
        return when (data) {
            is ProjectCoverageData -> json.encodeToString(data)
            is List<*> -> formatList(data)
            else -> json.encodeToString(data.toString())
        }
    }
    
    private fun formatList(list: List<*>): String {
        return when {
            list.isEmpty() -> "[]"
            list.all { it is PackageCoverageData } -> {
                json.encodeToString(list.filterIsInstance<PackageCoverageData>())
            }
            list.all { it is ClassCoverageData } -> {
                json.encodeToString(list.filterIsInstance<ClassCoverageData>())
            }
            list.all { it is MethodCoverageData } -> {
                json.encodeToString(list.filterIsInstance<MethodCoverageData>())
            }
            else -> {
                // Fallback for mixed types or unknown types
                json.encodeToString(list.map { it.toString() })
            }
        }
    }
}