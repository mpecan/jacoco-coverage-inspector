# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Gradle plugin that provides command-line inspection and parsing capabilities for JaCoCo coverage reports. It allows users and LLM agents to easily query coverage data at project, package, and file levels with both human-readable and machine-parsable outputs.

## Development Commands

### Build and Test
```bash
# Build the project
./gradlew build

# Run all tests
./gradlew test

# Run a specific test class
./gradlew test --tests "TestClassName"

# Run with more detailed output
./gradlew test --info

# Clean and rebuild
./gradlew clean build
```

### Development Workflow
```bash
# Check for dependency updates
./gradlew dependencyUpdates

# Run continuous build during development
./gradlew build --continuous
```

## Architecture Notes

### Current State
- Basic Kotlin JVM project using Gradle 8.10.2
- Kotlin 2.1.20 with JVM target 21
- JUnit platform for testing

### Plugin Architecture

1. **Core Components**:
   - `JacocoCoverageInspectorPlugin`: Main plugin class that registers tasks
   - `ListProjectCoverageTask`: Shows coverage for all projects
   - `ListFileCoverageTask`: Shows file-level coverage with filtering
   - `ListPackageCoverageTask`: Shows package-level coverage aggregation

2. **Key Features**:
   - Multiple output formats: Table (human), JSON, CSV, Markdown
   - Flexible filtering by coverage type and thresholds
   - Support for multi-project builds
   - Standalone usage without build file modification

3. **Package Structure**:
   - `io.github.mpecan.jacoco.tasks/`: Coverage inspection tasks
   - `io.github.mpecan.jacoco.parser/`: JaCoCo XML parsing
   - `io.github.mpecan.jacoco.formatter/`: Output formatting
   - `io.github.mpecan.jacoco.model/`: Data models

### Key Development Considerations
- The project uses Foojay resolver for JVM toolchain management
- Group ID is `io.github.mpecan` - maintain this for consistency
- Follow Gradle plugin best practices for backwards compatibility
- Test against multiple Gradle versions when implementing the plugin