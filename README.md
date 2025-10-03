# Automated Testing Framework for DSP Solution

A comprehensive test automation framework for testing DSP (Demand-Side Platform) server functionality, including bid request/response testing and analysis.

## 🚀 Features

- **API Testing**: Custom OkHttp-based HTTP client for backend API testing
- **UI Testing**: Playwright for modern web application testing
- **TestNG Framework**: Advanced test execution and reporting
- **ReportPortal Integration**: Professional test reporting and analytics
- **Multiple Test Suites**: Backend, Frontend, and E2E testing
- **Built-in Telemetry**: Real-time metrics via WebSocket and HTTP servers
- **Thread-Safe Design**: Proper resource management and ThreadLocal cleanup
- **Configurable Architecture**: Externalized configuration with validation
- **Custom Exception Hierarchy**: Clear error reporting with semantic exceptions

## 🛠️ Prerequisites

- **Java 21** (LTS)
- **Gradle 8.6+**
- **Docker** (for local development)

## 📦 Quick Start

### 1. Clone and Setup
```bash
git clone <repository-url>
cd automated-testing
```

### 2. Run Tests
```bash
# Run all tests
./gradlew testAll

# Run specific test suites
./gradlew testBackend      # Backend regression tests
./gradlew testFrontend     # Frontend regression tests
./gradlew testMlm          # MLM-specific tests
./gradlew testBackendE2E   # Backend E2E tests
```

### 3. Build Project
```bash
./gradlew build
```

## 🔧 Available Tasks

### Test Execution Tasks
| Task | Description | Group |
|------|-------------|-------|
| `test` | Run all test suites | verification |
| `cleanTestResults` | Clean test results and reports | build |
| `configureReportPortalBE` | Configure ReportPortal for backend | - |
| `configureReportPortalFE` | Configure ReportPortal for frontend | - |

### Code Quality Tasks
| Task | Description | Group |
|------|-------------|-------|
| `check` | Run all checks (compile, test, quality) | verification |
| `checkstyleAll` | Run Checkstyle on all subprojects | verification |
| `pmdAll` | Run PMD on all subprojects | verification |
| `spotbugsAll` | Run SpotBugs on all subprojects | verification |
| `qualityCheckAll` | Run all quality checks (Checkstyle, PMD, SpotBugs) | verification |
| `qualityReports` | Print paths to all quality check reports | verification |

### Build Tasks
| Task | Description | Group |
|------|-------------|-------|
| `build` | Assemble and test the project | build |
| `clean` | Delete the build directory | build |
| `cleanAll` | Clean all subprojects | build |

## ⚡ Performance Optimizations

### Build Performance
- **Parallel Execution**: Enabled by default
- **Build Caching**: Gradle build cache enabled
- **Configuration Cache**: Speeds up configuration phase
- **Worker Optimization**: Configurable worker count

### Test Performance
- **Optimized JVM Settings**: Tuned for testing workloads
- **Test Filtering**: Efficient test selection and exclusion

### Memory Management
- **Heap Size**: 8GB default, 4GB for CI
- **Metaspace**: 512MB limit
- **GC Optimization**: Parallel garbage collection

## 🏗️ Project Structure

### Module Organization
```
xenoss-automation-framework/
├── core/                      # Core framework module
│   └── src/main/java/io/xenoss/
│       ├── config/            # Configuration management
│       ├── utils/             # Utility classes
│       ├── telemetry/         # Test reporting and monitoring
│       ├── listeners/         # TestNG listeners
│       └── exceptions/        # Custom exception hierarchy
│
├── be-core/                   # Backend testing foundation
│   └── src/main/java/io/xenoss/
│       ├── http/              # HTTP client and request/response handling
│       ├── backend/client/    # Base client classes
│       ├── telemetry/         # Backend-specific telemetry
│       └── exceptions/        # HTTP-related exceptions
│
├── fe-core/                   # Frontend testing foundation
│   └── src/main/java/io/xenoss/frontend/
│       ├── pages/             # Page Object Model base classes
│       ├── components/        # Reusable UI components
│       ├── elements/          # Element wrappers
│       └── sites/             # Site-level navigation
│
└── be-dsp/                    # DSP-specific implementations
    └── src/main/java/io/xenoss/backend/
        ├── client/            # DSP API clients (BidderClient, etc.)
        ├── model/             # Request/response models
        ├── e2e/               # End-to-end test base classes
        └── testdata/          # Test data builders
```

## 📏 Code Quality Standards

The framework enforces strict code quality standards:

### Checkstyle Rules
- **Line length**: Max 120 characters
- **Method length**: Max 60 lines (excluding empty lines)
- **File length**: Max 600 lines per class
- **Method count**: Max 14 methods per class
- **Code style**: Enforces consistent formatting and naming

### PMD Analysis
- Best practices enforcement
- Code style checks
- Performance optimizations
- Error-prone patterns detection

### SpotBugs
- Bug pattern detection
- Security vulnerability scanning
- Thread safety issues

### Running Quality Checks
```bash
# Run all quality checks
./gradlew qualityCheckAll

# Run individual checks
./gradlew checkstyleAll
./gradlew pmdAll
./gradlew spotbugsAll

# View report locations
./gradlew qualityReports
```

### Quality Check Reports
Reports are generated in `build/reports/` for each module:
- Checkstyle: `build/reports/checkstyle/*.html`
- PMD: `build/reports/pmd/*.html`
- SpotBugs: `build/reports/spotbugs/*.html`

## 🔒 Security Features

- **Secure Dependencies**: Latest stable versions with security patches
- **Environment Isolation**: Separate configurations for different environments
- **XPath Injection Protection**: Automatic escaping in frontend selectors
- **Input Validation**: Comprehensive validation on configuration and parameters

## ⚙️ Configuration

The framework uses `testConfig.yaml` for configuration. Key settings:

### Core Settings
```yaml
environment: dev              # Environment to use (dev, stage, etc.)
uiActionTimeoutSeconds: 15   # UI action timeout
isHeadless: false            # Run browser in headless mode
isSilent: false              # Suppress logging output
threadPoolSize: 15           # Thread pool size for parallel execution
```

### HTTP Client Settings (optional - defaults provided)
```yaml
httpConnectionPoolSize: 100  # Max connections in pool (default: 100)
httpTimeoutSeconds: 30       # Connection/read/write timeout (default: 30)
httpKeepAliveSeconds: 30     # Keep-alive duration (default: 30)
```

### Telemetry Settings
```yaml
startTelemetryServer: true      # Enable telemetry server
telemetryHttpPort: 8080         # HTTP server port
telemetryWsPort: 8090           # WebSocket server port
telemetryUpdateIntervalMs: 100  # Metrics update interval (default: 100)
```

### Environment-Specific Settings
```yaml
environments:
  dev:
    dspUrl: "https://dsp-api-dev.xenoss.io"
    bidderUrl: "http://bidders-asg-dev.xenoss.io"
    # ... other environment URLs
```

**Override via System Properties:**
```bash
./gradlew test -Denvironment=stage -DhttpTimeoutSeconds=60
```

**Specify Custom Config Path:**
```bash
./gradlew test -Dconfig.path=/path/to/custom/testConfig.yaml
```

## 📊 Test Reporting

### ReportPortal Integration
- **Real-time Reporting**: Live test execution monitoring
- **Advanced Analytics**: Test trends and performance metrics
- **Integration**: Seamless CI/CD integration

### Local Reports
- **HTML Reports**: Detailed test execution reports
- **JUnit XML**: CI/CD compatible test results
- **Test Results**: Comprehensive test outcome data

## 🚀 CI/CD Integration

### Local Development
```bash
# Run with specific properties
./gradlew testBackend -Dtest.environment=staging

# Run with custom JVM args
./gradlew testBackend -Dorg.gradle.jvmargs="-Xmx4g"

# Run specific test methods
./gradlew test --tests "*AppBundleTargetingTest*"
```

## 📈 Monitoring and Telemetry

- **Test Execution Metrics**: Performance and reliability tracking
- **WebSocket Server**: Real-time test monitoring
- **HTTP Server**: Test result reporting endpoints
- **Console Logging**: Structured logging for debugging

## 🛠️ Troubleshooting

### Common Issues

1. **Memory Issues**
   ```bash
   # Increase heap size
   ./gradlew test -Dorg.gradle.jvmargs="-Xmx12g"
   ```

2. **Test Timeouts**
   ```bash
   # Increase test timeout
   ./gradlew test -Dtest.timeout=3600
   ```

3. **Build Performance Issues**
   ```bash
   # Disable parallel execution
   ./gradlew test -Dorg.gradle.parallel=false
   ```

### Performance Tuning

- **Worker Count**: Adjust based on available CPU cores
- **Memory Settings**: Optimize for your test workload
- **Cache Settings**: Enable/disable based on CI environment

## 🤝 Contributing

1. Follow the existing code structure
2. Add comprehensive tests for new features
3. Update documentation for configuration changes
4. Ensure all tests pass before submitting

## 📄 License

[Add your license information here]

## 🆘 Support

For issues and questions:
- Check the troubleshooting section
- Review test logs and reports
- Contact the development team


## Git flows
- To include into the project as a subtree:
    ```bash
    # Add the framework repo as subtree under a folder (e.g. "framework")
    git remote add framework git@github.com:xen-rybak/xenoss-automation-framework.git
    
    # Fetch it
    git fetch framework
    
    # Pull it into your repo under the "framework" folder
    git subtree add --prefix=framework framework main --squash
    ```
- To update the subtree from the framework repo:
    ```bash
    git fetch framework
    git subtree pull --prefix=framework framework main --squash
    ```

- To contribute from child project back to the framework repo:
    ```bash
    git subtree push --prefix=framework framework main
    ```