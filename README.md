# JaCoCo Coverage Inspector Gradle Plugin

A Gradle plugin that provides command-line inspection and parsing capabilities for JaCoCo coverage reports. Designed for both human readability and machine parsing, making it ideal for CI/CD pipelines and automated analysis tools.

## Features

- 📊 **Multi-level Coverage Analysis**: Project, package, and file-level coverage reporting
- 🎯 **Flexible Filtering**: Filter by any coverage metric (class, method, line, branch, instruction, complexity)
- 📄 **Multiple Output Formats**: Human-readable tables, JSON, CSV, and Markdown
- 🔍 **Threshold-based Queries**: Find code with coverage above or below specified thresholds
- 🚀 **Standalone Usage**: Use without modifying your build files
- 🔧 **Maven Support**: Coming soon - Maven plugin with identical functionality

## Quick Start

```bash
# List overall project coverage
./gradlew listProjectCoverage

# Find files with low branch coverage
./gradlew listFileCoverage --max-branch-coverage=50

# Find well-tested classes with poor method coverage
./gradlew listFileCoverage --min-class-coverage=90 --max-method-coverage=60
```

## Installation

### Adding to your build

```kotlin
plugins {
    id("io.github.mpecan.jacoco-inspector") version "1.0.0"
}
```

### Standalone usage (without modifying build.gradle)

```bash
curl -O https://raw.githubusercontent.com/mpecan/gradle-plugin-jacoco-agent/main/init-scripts/jacoco-inspector-init.gradle
./gradlew -I jacoco-inspector-init.gradle listProjectCoverage
```

## Documentation

See [PLAN.md](PLAN.md) for the development roadmap and detailed feature specifications.

## License

MIT License - see [LICENSE](LICENSE) for details

## Contributing

Contributions are welcome! Please read our contributing guidelines before submitting PRs.