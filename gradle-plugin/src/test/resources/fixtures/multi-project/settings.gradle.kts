plugins {
    id("jacoco-testkit-coverage") // this will dump coverage data
}

rootProject.name = "multi-project"

include("module-a", "module-b")