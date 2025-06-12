package io.github.mpecan.jacoco.model

import kotlinx.serialization.Serializable

/**
 * JaCoCo counter types for coverage measurement
 */
@Serializable
enum class CoverageType {
    INSTRUCTION,
    BRANCH,
    LINE,
    COMPLEXITY,
    METHOD,
    CLASS
}