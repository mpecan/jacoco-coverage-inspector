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

4. **Git strategy**:
  - This project requires the use of conventional commits
  - All commits should use the correct format and so should PRs
  - PRs are squashed

### Key Development Considerations
- The project uses Foojay resolver for JVM toolchain management
- Group ID is `io.github.mpecan` - maintain this for consistency
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
- Design for testability from the start rather than retrofitting tests