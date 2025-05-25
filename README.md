# Mock API Test

A comprehensive BDD (Behavior-Driven Development) test suite for user profile management API using Cucumber, WireMock, and REST Assured.

## Overview

This test suite provides automated testing for a user profile management system with full CRUD operations. It uses mock services to simulate API responses and validates user management functionality through readable Gherkin scenarios.

## Features Tested

- ✅ User creation with validation
- ✅ User profile retrieval
- ✅ User profile updates
- ✅ User profile deletion
- ✅ Error handling (validation errors, not found scenarios)
- ✅ API health checks

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

## Test Scenarios

### 📝 Available Scenarios

| Tag | Scenario | Description |
|-----|----------|-------------|
| `@CreateUser` | Create a new user profile | Tests user creation with valid data |
| `@GetUser` | Retrieve an existing user profile | Tests user profile retrieval by ID |
| `@UpdateUser` | Update user profile information | Tests user profile updates |
| `@DeleteUser` | Delete a user profile | Tests user deletion and verification |
| `@ValidationError` | Handle invalid user data | Tests validation error handling |
| `@NotFound` | Handle non-existent user | Tests 404 error scenarios |

### 🔧 Example Test Execution

```gherkin
Scenario: Create a new user profile
  When I create a user with the following details:
    | firstName | John             |
    | lastName  | Doe              |
    | email     | john@example.com |
    | age       | 30               |
  Then the user should be created successfully
  And the response should contain the user ID
  And the user details should match the provided information
```

## Configuration

### WireMock Configuration

- **Port**: 8080 (configurable in `TestConfiguration.java`)
- **Base URL**: `http://localhost:8080`
- **Content Type**: `application/json`

### API Endpoints Mocked

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/health` | Service health check |
| POST | `/users` | Create new user |
| GET | `/users/{id}` | Get user by ID |
| PUT | `/users/{id}` | Update user by ID |
| DELETE | `/users/{id}` | Delete user by ID |

## User Model

The `User` class includes:

```java
{
    "id": "string",
    "firstName": "string", 
    "lastName": "string",
    "email": "string",
    "age": integer
}
```

**Features:**
- Builder pattern for flexible object creation
- Jackson annotations for JSON serialization
- Proper getters/setters and toString method

## Reporting

Test results are generated in:
- Console output with pretty formatting
- Standard JUnit XML reports
- Cucumber HTML reports

## Best Practices Implemented

- **Page Object Pattern**: Clean separation of test logic
- **Builder Pattern**: Flexible object creation
- **Dependency Injection**: Proper test isolation
- **Proper Setup/Teardown**: Resource management
- **Meaningful Assertions**: Clear test validation
- **Tagged Scenarios**: Organized test execution
- **Data Tables**: Structured test data input

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/new-test`)
3. Add your test scenarios
4. Commit changes (`git commit -am 'Add new test scenarios'`)
5. Push to branch (`git push origin feature/new-test`)
6. Create a Pull Request
