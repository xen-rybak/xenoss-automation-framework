# Automated Testing Framework for DSP Solution

A comprehensive test automation framework for testing DSP (Demand-Side Platform) server functionality, including bid request/response testing and analysis.

## 🚀 Features

- **API Testing**: REST Assured integration for comprehensive API testing
- **UI Testing**: Playwright for modern web application testing
- **TestNG Framework**: Advanced test execution and reporting
- **ReportPortal Integration**: Professional test reporting and analytics
- **Multiple Test Suites**: Backend, Frontend, MLM, and E2E testing

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

| Task | Description | Group |
|------|-------------|-------|
| `testAll` | Run all test suites | verification |
| `testBackend` | Backend regression tests | verification |
| `testFrontend` | Frontend regression tests | verification |
| `testMlm` | MLM-specific tests | verification |
| `testBackendE2E` | Backend E2E tests | verification |
| `cleanTestResults` | Clean test results and reports | build |
| `configureReportPortalBE` | Configure ReportPortal for backend | - |
| `configureReportPortalFE` | Configure ReportPortal for frontend | - |

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

```
src/
├── main/java/io/xenoss/
│   ├── backend/          # Backend testing components
│   ├── frontend/         # Frontend testing components
│   ├── config/           # Configuration management
│   ├── utils/            # Utility classes
│   └── telemetry/        # Test reporting and monitoring
├── test/java/io/xenoss/
│   ├── backend/          # Backend test suites
│   ├── frontend/         # Frontend test suites
│   └── e2e/              # End-to-end tests
└── test/resources/
    ├── suites/            # TestNG suite configurations
    ├── bidrequests/       # Test bid request data
    └── config.yaml        # Test configuration
```

## 🔒 Security Features

- **Secure Dependencies**: Latest stable versions with security patches
- **Environment Isolation**: Separate configurations for different environments

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