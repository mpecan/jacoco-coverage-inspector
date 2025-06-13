# JaCoCo Coverage Inspector Gradle Plugin

A powerful Gradle plugin that provides comprehensive command-line inspection and parsing capabilities for JaCoCo coverage reports. Designed for both human readability and machine parsing, making it ideal for CI/CD pipelines, automated analysis tools, and development workflows.

## 🚀 Features

- 📊 **Multi-level Coverage Analysis**: Project, package, and file-level coverage reporting
- 🎯 **Flexible Filtering**: Filter by any coverage metric (class, method, line, branch, instruction, complexity)
- 📄 **Multiple Output Formats**: Human-readable tables, JSON, CSV, and Markdown
- 🔍 **Advanced Threshold Filtering**: Find code with coverage above or below specified thresholds
- 🎨 **Rich Console Output**: Color-coded coverage indicators and emoji support
- 🔧 **Extensive Configuration**: Extension-based configuration with command-line overrides
- 🏗️ **Multi-module Support**: Works seamlessly with multi-project Gradle builds
- ⚡ **High Performance**: Efficient XML parsing with kotlinx.serialization
- 🧪 **Production Ready**: 80%+ test coverage with comprehensive integration tests

## 🏃 Quick Start

```bash
# List overall project coverage
./gradlew listProjectCoverage

# Get JSON output for CI integration
./gradlew listProjectCoverage --format=json

# Find files with low branch coverage
./gradlew listFileCoverage --minBranchCoverage=50

# Find well-tested classes but with poor method coverage
./gradlew listFileCoverage --minClassCoverage=90 --maxMethodCoverage=60

# Generate Markdown report for documentation
./gradlew listPackageCoverage --format=markdown
```

## 📦 Installation

### Using the plugins DSL (Gradle 2.1+)

```kotlin
plugins {
    id("io.github.mpecan.jacoco-inspector") version "1.0.0"
}
```

### Using legacy plugin application

```kotlin
buildscript {
    repositories {
        gradlePluginPortal()
    }
    dependencies {
        classpath("io.github.mpecan:jacoco-coverage-inspector:1.0.0")
    }
}

apply(plugin = "io.github.mpecan.jacoco-inspector")
```

## 🎯 Available Tasks

| Task | Description | Usage |
|------|-------------|-------|
| `listProjectCoverage` | Shows overall project coverage summary | `./gradlew listProjectCoverage` |
| `listPackageCoverage` | Lists coverage for all packages | `./gradlew listPackageCoverage` |
| `listFileCoverage` | Lists coverage for individual files/classes | `./gradlew listFileCoverage` |

## 📄 Output Formats

### Table (Default)
Human-readable console output with optional color coding:
```bash
./gradlew listProjectCoverage --format=table --color
```

### JSON
Structured data perfect for CI/CD integration:
```bash
./gradlew listProjectCoverage --format=json
```

### CSV
Spreadsheet-compatible format for data analysis:
```bash
./gradlew listProjectCoverage --format=csv
```

### Markdown
Documentation-ready format with emoji indicators:
```bash
./gradlew listProjectCoverage --format=markdown
```

## 🔧 Configuration

### Extension Configuration

```kotlin
jacocoInspector {
    // Default output format for all tasks
    defaultFormat = io.github.mpecan.jacoco.model.OutputFormat.JSON
    
    // Enable/disable color output
    colorOutput = true
    
    // Global coverage thresholds
    minLineCoverage = 80.0
    minBranchCoverage = 70.0
    minMethodCoverage = 75.0
    minClassCoverage = 90.0
    minInstructionCoverage = 85.0
    minComplexityCoverage = 80.0
    
    // Maximum thresholds (useful for finding over-tested code)
    maxLineCoverage = 100.0
    maxBranchCoverage = 100.0
    
    // Pattern-based filtering
    includePatterns = listOf("com.mycompany.*", "*.service.*")
    excludePatterns = listOf("*.test.*", "*.mock.*")
}
```

### Command Line Options

All tasks support extensive command-line customization:

#### Output Control
- `--format=<table|json|csv|markdown>` - Output format
- `--color` / `--no-color` - Enable/disable color output

#### Coverage Filtering
- `--minCoverage=<percentage>` - Generic minimum coverage threshold
- `--coverageType=<INSTRUCTION|BRANCH|LINE|COMPLEXITY|METHOD|CLASS>` - Coverage type for generic threshold
- `--minLineCoverage=<percentage>` - Minimum line coverage
- `--minBranchCoverage=<percentage>` - Minimum branch coverage
- `--minMethodCoverage=<percentage>` - Minimum method coverage
- `--minClassCoverage=<percentage>` - Minimum class coverage
- `--minInstructionCoverage=<percentage>` - Minimum instruction coverage
- `--minComplexityCoverage=<percentage>` - Minimum complexity coverage

#### Maximum Thresholds
- `--maxLineCoverage=<percentage>` - Maximum line coverage
- `--maxBranchCoverage=<percentage>` - Maximum branch coverage
- `--maxMethodCoverage=<percentage>` - Maximum method coverage
- `--maxClassCoverage=<percentage>` - Maximum class coverage
- `--maxInstructionCoverage=<percentage>` - Maximum instruction coverage
- `--maxComplexityCoverage=<percentage>` - Maximum complexity coverage

#### Pattern Filtering
- `--packageFilter=<pattern>` - Filter by package name pattern
- `--includePatterns=<pattern1,pattern2>` - Include only matching patterns
- `--excludePatterns=<pattern1,pattern2>` - Exclude matching patterns

## 💡 Usage Examples

### Development Workflow

```bash
# Quick coverage check during development
./gradlew test jacocoTestReport listProjectCoverage

# Find files that need more testing
./gradlew listFileCoverage --minLineCoverage=80 --format=table

# Generate report for code review
./gradlew listPackageCoverage --format=markdown > coverage-report.md
```

### CI/CD Integration

```bash
# Generate machine-readable coverage data
./gradlew listProjectCoverage --format=json > coverage.json

# Fail build if any file has less than 80% line coverage
./gradlew listFileCoverage --minLineCoverage=80 --format=json | jq '. | length' | grep -q "^0$"

# Export coverage data for analysis tools
./gradlew listFileCoverage --format=csv > coverage-data.csv
```

### Code Quality Analysis

```bash
# Find files with high complexity but low coverage
./gradlew listFileCoverage --maxComplexityCoverage=60 --minLineCoverage=90

# Identify over-tested simple code
./gradlew listFileCoverage --maxLineCoverage=95 --minComplexityCoverage=20

# Focus on specific packages
./gradlew listFileCoverage --packageFilter=com.mycompany.core --minBranchCoverage=75
```

## 🏗️ Multi-module Projects

The plugin automatically works with multi-module Gradle projects:

```bash
# Run on specific subproject
./gradlew :my-subproject:listProjectCoverage

# Run on all subprojects
./gradlew listProjectCoverage

# Generate consolidated report
./gradlew listPackageCoverage --format=json > consolidated-coverage.json
```

## 🎨 Output Examples

### Table Format
```
PROJECT COVERAGE SUMMARY
============================================================

Project: my-awesome-project

Type            Covered     Missed      Total       Coverage
------------------------------------------------------------
Instruction        1250        150       1400       🟢 89.3%
Branch              245         55        300       🟢 81.7%
Line                890        110       1000       🟢 89.0%
Method              156         24        180       🟢 86.7%
Class                18          2         20       🟢 90.0%
```

### JSON Format
```json
{
  "projectName": "my-awesome-project",
  "counters": {
    "INSTRUCTION": {
      "type": "INSTRUCTION",
      "missed": 150,
      "covered": 1250,
      "total": 1400,
      "ratio": 0.8928571428571429,
      "percentage": 89.29
    }
  }
}
```

### Markdown Format
```markdown
# Project Coverage Summary

**Project:** my-awesome-project

## Coverage Overview

| Type | Covered | Missed | Total | Coverage |
|------|---------|--------|-------|----------|
| Instruction | 1250 | 150 | 1400 | 🟢 89.3% |
| Line | 890 | 110 | 1000 | 🟢 89.0% |
```

## 🔍 Prerequisites

- Gradle 7.0+ (recommended: 8.0+)
- Kotlin JVM 1.8+ (recommended: 2.1+)
- JaCoCo plugin configured in your project
- Java/Kotlin 17+ for plugin execution

## 🏭 Architecture

The plugin is built with a clean multi-module architecture:

- **Core Module**: Pure Kotlin library with coverage parsing and formatting
- **Gradle Plugin Module**: Gradle-specific task implementations and extensions
- **Formatters**: Pluggable output formatters with common interface
- **Models**: Comprehensive data model with kotlinx.serialization support

## 🧪 Development

### Building from Source

```bash
git clone https://github.com/mpecan/jacoco-coverage-inspector.git
cd jacoco-coverage-inspector
./gradlew build
```

### Running Tests

```bash
# Run all tests
./gradlew test

# Run with coverage
./gradlew test jacocoTestReport

# Check coverage compliance
./gradlew jacocoTestCoverageVerification
```

### Project Structure

```
├── core/                           # Core parsing and formatting library
│   ├── src/main/kotlin/
│   │   ├── formatter/             # Output formatters (JSON, CSV, Markdown, Table)
│   │   ├── model/                 # Data models with serialization
│   │   └── parser/                # JaCoCo XML parsing
│   └── src/test/kotlin/           # Comprehensive unit tests
├── gradle-plugin/                  # Gradle plugin implementation
│   ├── src/main/kotlin/
│   │   └── tasks/                 # Gradle task implementations
│   └── src/test/kotlin/           # Integration tests with test fixtures
└── README.md                      # This file
```

## 📋 Requirements

The plugin maintains high quality standards:

- ✅ 80%+ test coverage on all modules
- ✅ Comprehensive integration tests
- ✅ Multi-platform compatibility
- ✅ Backward compatibility with existing JaCoCo reports
- ✅ Performance optimized for large codebases

## 🤝 Contributing

We welcome contributions! Please:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development Guidelines

- Maintain 80%+ test coverage
- Follow Kotlin coding conventions
- Add integration tests for new features
- Update documentation for user-facing changes

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- [JaCoCo](https://www.jacoco.org/) for the excellent coverage analysis tool
- [Kotlin](https://kotlinlang.org/) and [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization) for the robust foundation
- [Gradle](https://gradle.org/) for the powerful build platform

## 📞 Support

- 🐛 **Issues**: [GitHub Issues](https://github.com/mpecan/jacoco-coverage-inspector/issues)
- 💬 **Discussions**: [GitHub Discussions](https://github.com/mpecan/jacoco-coverage-inspector/discussions)
- 📖 **Documentation**: [Wiki](https://github.com/mpecan/jacoco-coverage-inspector/wiki)

---

**Made with ❤️ for the Kotlin and Gradle community**