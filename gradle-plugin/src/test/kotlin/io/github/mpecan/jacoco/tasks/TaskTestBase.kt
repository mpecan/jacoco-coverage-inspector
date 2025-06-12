package io.github.mpecan.jacoco.tasks

import io.github.mpecan.jacoco.PluginFixtureTest
import java.io.File

abstract class TaskTestBase : PluginFixtureTest() {
    
    protected fun configureBuildFile(taskName: String, configuration: String) {
        val buildFile = testProjectDir.resolve("build.gradle.kts")
        val originalContent = buildFile.readText()
        buildFile.writeText(originalContent + """
            
            tasks.named<io.github.mpecan.jacoco.tasks.${taskName.replaceFirstChar { it.uppercase() }}>("$taskName") {
                $configuration
            }
        """.trimIndent())
    }
    
    protected fun addTaskConfiguration(taskName: String, vararg configs: Pair<String, Any>) {
        val configString = configs.joinToString("\n                ") { (key, value) ->
            when (value) {
                is String -> "$key.set(\"$value\")"
                is Boolean -> "$key.set($value)"
                is Number -> "$key.set($value)"
                is List<*> -> "$key.set(listOf(${value.joinToString { "\"$it\"" }}))"
                else -> "$key.set($value)"
            }
        }
        configureBuildFile(taskName.replaceFirstChar { it.uppercase() }, configString)
    }
}