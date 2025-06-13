# JaCoCo Coverage Inspector Gradle Plugin - Development Plan

## Project Overview

This Gradle plugin will provide a command-line interface for inspecting and parsing JaCoCo coverage reports with both human-readable and machine-parsable outputs. It will support filtering and aggregation at multiple levels (project, package, file, class, method).

## Core Features

### 1. Coverage Listing Commands
- **Project Coverage**: Display overall coverage for the main project and all subprojects
- **File Coverage**: List file-level coverage with filtering capabilities
- **Package Coverage**: Aggregate and display package-level coverage statistics
- **Multi-format Output**: Support for human-readable tables and machine-parsable JSON/CSV

### 2. Filtering Capabilities
- **Coverage Type Filters**: Apply thresholds to any JaCoCo coverage metric
  - Class coverage
  - Method coverage
  - Line coverage
  - Branch coverage (conditions)
  - Instruction coverage
  - Complexity coverage
- **Threshold Operators**:
  - `--min-[type]-coverage`: Show only items with coverage >= specified value
  - `--max-[type]-coverage`: Show only items with coverage <= specified value
  - Combine multiple thresholds (e.g., high class coverage but low branch coverage)
- **Path Filters**:
  - Include/exclude patterns for packages and files
  - Regular expression support
- **Filter Logic**:
  - Multiple filters combined with AND logic by default
  - Option for OR logic between filter groups

### 3. Output Formats
- **Human-readable**: Formatted tables with color-coding (terminal support)
- **JSON**: Structured data for programmatic access
- **CSV**: For spreadsheet analysis
- **Markdown**: For documentation and reports

## Technical Architecture

### Multi-Module Project Structure ✅ IMPLEMENTED
```
jacoco-coverage-inspector/
├── core/                                     # Shared core library
│   ├── src/main/kotlin/io/github/mpecan/jacoco/
│   │   ├── model/
│   │   │   ├── CoverageData.kt             # Coverage data models
│   │   │   ├── CoverageFilter.kt           # Filter specifications
│   │   │   ├── CoverageType.kt             # Coverage counter types
│   │   │   └── OutputFormat.kt             # Output format enums
│   │   ├── parser/
│   │   │   └── JacocoXmlParser.kt          # XML report parsing
│   │   ├── formatter/
│   │   │   ├── TableFormatter.kt           # Human-readable tables
│   │   │   ├── JsonFormatter.kt            # JSON output
│   │   │   ├── CsvFormatter.kt             # CSV output
│   │   │   └── MarkdownFormatter.kt        # Markdown output
│   │   └── aggregator/
│   │       ├── PackageCoverageAggregator.kt # Package aggregation logic
│   │       └── ProjectCoverageAggregator.kt # Project aggregation logic
│   └── src/test/kotlin/                    # Comprehensive unit tests (80%+ coverage)
├── gradle-plugin/                           # Gradle plugin module
│   ├── src/main/kotlin/io/github/mpecan/jacoco/
│   │   ├── JacocoCoverageInspectorPlugin.kt # Main plugin class
│   │   ├── JacocoInspectorExtension.kt     # Configuration DSL
│   │   ├── tasks/
│   │   │   ├── ListProjectCoverageTask.kt  # Project coverage listing
│   │   │   ├── ListFileCoverageTask.kt     # File coverage listing
│   │   │   ├── ListPackageCoverageTask.kt  # Package coverage listing
│   │   │   └── BaseInspectorTask.kt        # Common task functionality
│   │   └── util/                           # Task utilities
│   └── src/test/kotlin/                    # Integration tests with fixtures
└── maven-plugin/                           # Maven plugin module
    ├── src/main/kotlin/io/github/mpecan/jacoco/maven/
    │   ├── JacocoCoverageInspectorMojo.kt  # Base Mojo class
    │   ├── ListProjectCoverageMojo.kt      # Maven goal for project coverage
    │   ├── ListFileCoverageMojo.kt         # Maven goal for file coverage
    │   ├── ListPackageCoverageMojo.kt      # Maven goal for package coverage
    │   └── util/
    │       └── MavenParameterMapper.kt     # Parameter mapping utilities
    └── src/test/kotlin/                    # Comprehensive unit tests (83%+ coverage)
```

### Key Components

#### 1. Plugin Registration
- Register custom tasks for each coverage inspection type
- Integrate with existing JaCoCo plugin infrastructure
- Support both single and multi-project builds

#### 2. XML Parsing
- Parse JaCoCo XML reports using Kotlin XML libraries
- Extract coverage metrics at all levels (counter types)
- Handle missing or malformed reports gracefully

#### 3. Data Model
- Hierarchical coverage data structure
- Support for all JaCoCo counter types
- Efficient aggregation and filtering

#### 4. Command-Line Interface
- Gradle task properties for all options
- Support for both task configuration and command-line parameters
- Sensible defaults with override capabilities

## Development Phases

### Phase 1: Foundation ✅ COMPLETED
- [x] Set up Gradle plugin project structure
- [x] Implement basic plugin registration
- [x] Create data models for coverage information
- [x] Implement JaCoCo XML parser
- [x] Write unit tests for parser

### Phase 2: Core Tasks ✅ COMPLETED
- [x] Implement ListProjectCoverageTask
- [x] Implement ListFileCoverageTask
- [x] Implement ListPackageCoverageTask
- [x] Add basic table formatter for human output
- [x] Write integration tests

### Phase 3: Filtering & Formatting ✅ COMPLETED
- [x] Implement coverage filtering system
- [x] Add JSON formatter
- [x] Add CSV formatter
- [x] Add Markdown formatter
- [x] Implement color-coding for terminal output

### Phase 4: Advanced Features ✅ COMPLETED
- [x] Add support for multi-project aggregation
- [x] Add configuration DSL for default settings
- [x] Performance optimization for large projects
- [ ] Implement coverage trend analysis (Future enhancement)

### Phase 5: Documentation & Publishing 🔄 IN PROGRESS
- [x] Write comprehensive user documentation
- [x] Set up CI/CD pipeline
- [x] Update documentation for dual-plugin architecture
- [ ] Create example projects
- [ ] Prepare for Maven Central publishing
- [ ] Write migration guide from other tools

### Phase 6: Maven Plugin Development ✅ COMPLETED
- [x] Extract core components into shared library module
- [x] Create Maven plugin module structure
- [x] Implement Maven Mojo classes wrapping core functionality
- [x] Adapt command-line interface for Maven goals
- [x] Write Maven-specific integration tests
- [x] Achieve 80%+ test coverage requirement
- [x] Create Maven plugin documentation
- [x] Set up Maven plugin deployment configuration

## Usage Examples

### Applied to Project
```kotlin
// build.gradle.kts
plugins {
    id("io.github.mpecan.jacoco-inspector") version "1.0.0"
}

jacocoInspector {
    defaultFormat = OutputFormat.TABLE
    colorOutput = true
    
    // Default filters for all tasks
    filters {
        minClassCoverage = 80.0
        minMethodCoverage = 70.0
        minBranchCoverage = 60.0
        excludePatterns = listOf("**/*Generated*", "**/*Test*")
    }
    
    // Task-specific overrides
    listFileCoverage {
        showOnlyBelowThresholds = true  // Focus on problematic files
        maxBranchCoverage = 50.0
    }
}
```

### Command-Line Usage
```bash
# List project coverage
./gradlew listProjectCoverage

# List files with low branch coverage
./gradlew listFileCoverage --max-branch-coverage=50 --format=json

# List files with high class coverage but low method coverage
./gradlew listFileCoverage --min-class-coverage=90 --max-method-coverage=60

# List packages with any coverage type below 80%
./gradlew listPackageCoverage --min-line-coverage=80 --min-branch-coverage=80 --min-method-coverage=80

# Complex filtering example
./gradlew listFileCoverage \
  --include="com.example.core.*" \
  --exclude="**/*Test*" \
  --min-class-coverage=95 \
  --max-branch-coverage=70 \
  --format=csv

# Use without adding to build.gradle
./gradlew -I jacoco-inspector-init.gradle listProjectCoverage --min-instruction-coverage=85
```

### Standalone Usage (without modifying build.gradle)
```bash
# Download init script
curl -O https://raw.githubusercontent.com/mpecan/jacoco-coverage-inspector/main/init-scripts/jacoco-inspector-init.gradle

# Run with init script
./gradlew -I jacoco-inspector-init.gradle listProjectCoverage
```

### Maven Usage
```xml
<!-- pom.xml -->
<plugin>
    <groupId>io.github.mpecan</groupId>
    <artifactId>jacoco-inspector-maven-plugin</artifactId>
    <version>1.0.0</version>
</plugin>
```

```bash
# Maven command-line usage
mvn jacoco-inspector:list-project-coverage

# List files with branch coverage issues
mvn jacoco-inspector:list-file-coverage -Dformat=json -DmaxBranchCoverage=50

# List files with specific coverage patterns
mvn jacoco-inspector:list-file-coverage -DminClassCoverage=90 -DmaxMethodCoverage=60

# List packages below thresholds
mvn jacoco-inspector:list-package-coverage \
  -DminLineCoverage=80 \
  -DminBranchCoverage=80 \
  -DminMethodCoverage=80 \
  -Dinclude="com.example.*"
```

## Testing Strategy

### Unit Tests
- Parser correctness with various XML structures
- Filter logic validation
- Formatter output verification
- Coverage calculation accuracy

### Integration Tests
- Multi-project build scenarios
- Various JaCoCo report configurations
- Command-line parameter handling
- Error scenarios (missing reports, etc.)

### Functional Tests
- End-to-end workflow validation
- Performance with large codebases
- Compatibility with different Gradle versions

## Documentation Plan

### User Documentation
1. **Getting Started Guide**: Quick setup and basic usage
2. **Configuration Reference**: All options and their defaults
3. **Command Reference**: All tasks and their parameters
4. **Output Format Guide**: Examples of each format
5. **Filtering Guide**: Complex filtering examples
6. **Troubleshooting**: Common issues and solutions

### Developer Documentation
1. **Architecture Overview**: Component relationships
2. **Extension Guide**: Adding new formatters or filters
3. **Contributing Guide**: Development setup and guidelines
4. **API Reference**: Public classes and methods

## Success Metrics
- Easy integration (< 5 minutes setup)
- Fast execution (< 1s for medium projects)
- Clear, actionable output
- Reliable parsing of all valid JaCoCo reports
- Positive user feedback and adoption

## Inspiration from jacoco-markdown-gradle-plugin
- Task naming convention (`<jacoco-task>Inspector`)
- Extension-based configuration
- Flexible output options
- Class filtering capabilities
- Integration with existing JaCoCo infrastructure

## ✅ Shared Core Library Benefits - ACHIEVED
The multi-module approach with a shared core library provides:
- **Code Reuse**: ✅ Common parsing, filtering, and formatting logic shared between Gradle and Maven plugins
- **Consistent Behavior**: ✅ Same coverage calculations and output formats across build tools
- **Easier Maintenance**: ✅ Bug fixes and improvements benefit both plugins
- **Future Extensibility**: ✅ Easy to add CLI tool, IDE plugins, or other integrations
- **Independent Testing**: ✅ Core logic can be tested independently of build tool specifics

## Project Status Summary

### ✅ COMPLETED FEATURES
- **Multi-Build System Support**: Both Gradle and Maven plugins fully implemented
- **Core Functionality**: Complete coverage parsing, filtering, and formatting
- **Output Formats**: Table, JSON, CSV, and Markdown formatters
- **Advanced Filtering**: All coverage types with min/max thresholds
- **Pattern Matching**: Include/exclude patterns for packages and files
- **High Test Coverage**: 80%+ coverage across all modules
- **Production Ready**: Comprehensive error handling and edge cases

### 🔄 IN PROGRESS
- Documentation updates for dual-plugin architecture
- Example projects for both build systems

### 📋 FUTURE ENHANCEMENTS
- Coverage trend analysis
- Maven Central publishing
- Migration guides from other tools
- CLI standalone tool