# Mock API Test

A comprehensive BDD (Behavior-Driven Development) test suite for user profile management API using Cucumber, WireMock, and REST Assured.

## Overview

This test suite provides automated testing for a user profile management system with full CRUD operations. It uses mock services to simulate API responses and validates user management functionality through readable Gherkin scenarios.

## Technology Stack

- **Java** - Programming language
- **Cucumber** - BDD testing framework
- **JUnit 5** - Test runner platform
- **WireMock** - HTTP service mocking
- **REST Assured** - API testing library
- **Jackson** - JSON serialization/deserialization
- **AssertJ** - Fluent assertion library

## Project Structure

```
src/
├── main/java/com/example/
│   ├── config/
│   │   └── TestConfiguration.java      # WireMock configuration and stubs
│   └── models/
│       └── User.java                   # User model with builder pattern
└── test/java/com/example/
    ├── runners/
    │   └── CucumberTestRunner.java     # JUnit 5 test runner
    └── stepdefs/
        └── UserProfileStepDefinitions.java # Cucumber step implementations
resources/
└── features/
    └── user_profile.feature            # BDD scenarios in Gherkin
```

## Prerequisites

- Java 17 or higher
- Gradle 7.0+

## Dependencies

The project uses the following key dependencies in `build.gradle`:

```gradle
dependencies {
    // Testing frameworks
    testImplementation 'io.cucumber:cucumber-java:7.14.0'
    testImplementation 'io.cucumber:cucumber-junit-platform-engine:7.14.0'
    testImplementation 'org.junit.platform:junit-platform-suite:1.10.0'
    testImplementation 'org.junit.jupiter:junit-jupiter:5.10.0'

    // REST API testing
    testImplementation 'io.rest-assured:rest-assured:5.3.2'
    testImplementation 'io.rest-assured:json-schema-validator:5.3.2'

    // WireMock for API mocking
    testImplementation 'com.github.tomakehurst:wiremock-jre8-standalone:2.35.0'

    // JSON processing
    testImplementation 'com.fasterxml.jackson.core:jackson-databind:2.15.2'
    implementation 'com.fasterxml.jackson.core:jackson-annotations:2.15.2'

    // Assertions
    testImplementation 'org.assertj:assertj-core:3.24.2'

    // Logging
    testImplementation 'ch.qos.logback:logback-classic:1.4.11'
}
```

## Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd mock-api-test
```

### 2. Build the Project

```bash
./gradlew build
```

### 3. Run All Tests

```bash
./gradlew test
```

### 4. Run Specific Tagged Scenarios

```bash
# Run only user creation tests
./gradlew test -Dcucumber.filter.tags="@CreateUser"

# Run only validation error tests
./gradlew test -Dcucumber.filter.tags="@ValidationError"

# Run multiple tags
./gradlew test -Dcucumber.filter.tags="@CreateUser or @UpdateUser"
```
