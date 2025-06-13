# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a multi-module project that provides command-line inspection and parsing capabilities for JaCoCo coverage reports. It includes both **Gradle plugin** and **Maven plugin** implementations, sharing a common core library. It allows users and LLM agents to easily query coverage data at project, package, and file levels with both human-readable and machine-parsable outputs.

## Development Commands

### Gradle Build and Test
```bash
# Build the entire project (all modules)
./gradlew build

# Run all tests
./gradlew test

# Run a specific test class
./gradlew test --tests "TestClassName"

# Run with more detailed output
./gradlew test --info

# Clean and rebuild
./gradlew clean build

# Publish core module to local Maven repository (required for Maven plugin development)
./gradlew :core:publishToMavenLocal
```

### Maven Plugin Build and Test
```bash
# Build and test Maven plugin (requires core module in local Maven repository)
cd maven-plugin
./mvnw clean verify
cd ..

# Run Maven plugin tests only
cd maven-plugin
./mvnw test
cd ..

# Clean Maven plugin
cd maven-plugin
./mvnw clean
cd ..
```

### Development Workflow
```bash
# Check for dependency updates
./gradlew dependencyUpdates

# Run continuous build during development
./gradlew build --continuous

# Full CI workflow test
./gradlew build
./gradlew :core:publishToMavenLocal
cd maven-plugin && ./mvnw clean verify && cd ..
```

## Architecture Notes

### Current State
- Multi-module Kotlin JVM project using Gradle 8.10.2
- Kotlin 2.1.20 with JVM target 17 (aligned across both plugins)
- JUnit platform for testing
- Dual build system support: Gradle + Maven

### Project Architecture

1. **Core Module** (`core/`):
   - Shared library containing parsing, formatting, and data models
   - Published to Maven repository for use by both plugins
   - 80%+ test coverage requirement

2. **Gradle Plugin Module** (`gradle-plugin/`):
   - Gradle-specific task implementations
   - Extension-based configuration (`jacocoInspector`)
   - Integration tests with test fixtures

3. **Maven Plugin Module** (`maven-plugin/`):
   - Maven Mojo implementations in Kotlin
   - Parameter-based configuration
   - 83%+ test coverage achieved

### Key Components

1. **Gradle Plugin**:
   - `JacocoCoverageInspectorPlugin`: Main plugin class that registers tasks
   - `ListProjectCoverageTask`: Shows coverage for all projects
   - `ListFileCoverageTask`: Shows file-level coverage with filtering
   - `ListPackageCoverageTask`: Shows package-level coverage aggregation

2. **Maven Plugin**:
   - `JacocoCoverageInspectorMojo`: Base abstract mojo class
   - `ListProjectCoverageMojo`: Maven goal for project coverage
   - `ListFileCoverageMojo`: Maven goal for file-level coverage
   - `ListPackageCoverageMojo`: Maven goal for package-level coverage

3. **Shared Features**:
   - Multiple output formats: Table (human), JSON, CSV, Markdown
   - Flexible filtering by coverage type and thresholds
   - Support for multi-project/multi-module builds
   - Identical command-line parameter support

4. **Package Structure**:
   - `io.github.mpecan.jacoco.tasks/`: Coverage inspection tasks (Gradle)
   - `io.github.mpecan.jacoco.maven/`: Maven Mojo implementations
   - `io.github.mpecan.jacoco.parser/`: JaCoCo XML parsing (shared)
   - `io.github.mpecan.jacoco.formatter/`: Output formatting (shared)
   - `io.github.mpecan.jacoco.model/`: Data models (shared)

4. **Git strategy**:
  - This project requires the use of conventional commits
  - All commits should use the correct format and so should PRs
  - PRs are squashed

### Key Development Considerations
- The project uses Foojay resolver for JVM toolchain management
- Group ID is `io.github.mpecan` - maintain this for consistency
- **Version alignment**: Both plugins use same version (1.0-SNAPSHOT)
- **Dependency management**: Maven plugin depends on core module from local repository
- **CI workflow**: Gradle build → core publish → Maven build
- Follow plugin best practices for backwards compatibility
- Test coverage requirement: 80% minimum across all modules
- Follow Gradle plugin best practices for backwards compatibility
- Test against multiple Gradle versions when implementing the plugin

## Code Quality Guidelines

### Testing Requirements
- **ALWAYS run tests before moving on**: 
  - For single test additions: Execute `./gradlew :module:test --tests "TestClassName"` to run only the specific test class in the specific module
  - For broader changes: Execute `./gradlew test` to run the full test suite
- Write comprehensive tests for all new functionality
- Aim for high test coverage while focusing on meaningful test scenarios
- Test both happy path and error conditions

### Code Organization
- **File size limit**: Keep files under 500 lines maximum for maintainability
- Break large files into smaller, focused components when approaching this limit
- Each class should have a single, well-defined responsibility

### Design Principles
- **Follow SOLID principles** when appropriate:
  - Single Responsibility: Each class should have one reason to change
  - Open/Closed: Open for extension, closed for modification
  - Liskov Substitution: Subtypes should be substitutable for base types
  - Interface Segregation: Prefer specific interfaces over large general ones
  - Dependency Inversion: Depend on abstractions, not concretions

### Testability Guidelines
- **Abstract external dependencies**: File I/O, network calls, system dependencies
- **Use constructor injection** when it improves testability:
  - Provide default implementations for production use
  - Allow dependency override for testing scenarios
  - Only apply when it genuinely improves code design
- Design for testability from the start rather than retrofitting tes