@UserProfile
Feature: User Profile Management
  As a system user
  I want to manage user profiles
  So that I can maintain user information effectively

  Background:
    Given the user service is running
    And the API is available at "http://localhost:8080"

  @CreateUser
  Scenario: Create a new user profile
    When I create a user with details:
      | firstName | John        |
      | lastName  | Doe         |
      | email     | john@example.com |
      | age       | 30          |
    Then the user should be created successfully
    And the response should contain the user ID
    And the user should have firstName "John"
    And the user should have lastName "Doe"
    And the user should have email "john@example.com"
    And the user should have age 30

  @GetUser
  Scenario: Retrieve an existing user profile
    Given a user exists with ID "123"
    When I request the user profile for ID "123"
    Then the user profile should be returned
    And the response should contain:
      | firstName | John        |
      | lastName  | Doe         |
      | email     | john@example.com |
      | age       | 30          |

  @UpdateUser
  Scenario: Update user profile information
    Given a user exists with ID "123"
    When I update the user profile with:
      | firstName | Jane        |
      | lastName  | Smith       |
      | email     | jane@example.com |
      | age       | 25          |
    Then the user should be updated successfully
    And the updated profile should reflect the changes

  @DeleteUser
  Scenario: Delete a user profile
    Given a user exists with ID "123"
    When I delete the user with ID "123"
    Then the user should be deleted successfully
    And the user profile should no longer exist

  @ValidationError
  Scenario: Handle invalid user data
    When I attempt to create a user with invalid email "invalid-email"
    Then the request should fail with status code 400
    And the error message should indicate "Invalid email format"

  @NotFound
  Scenario: Handle non-existent user
    Given the user service is running
    And the API is available at "http://localhost:8080"
    And a user does not exist with ID "999"
    When I request the user profile for ID "999"
    Then the request should fail with status code 404
    And the error message should indicate "User with ID 999 does not exist"

  @DataDriven
  Scenario Outline: Create users with different valid data sets
    When I create a user with details:
      | firstName | <firstName> |
      | lastName  | <lastName>  |
      | email     | <email>     |
      | age       | <age>       |
    Then the user should be created successfully
    And the response should contain the user ID
    And the user should have firstName "<firstName>"
    And the user should have lastName "<lastName>"
    And the user should have email "<email>"
    And the user should have age <age>

    Examples:
      | firstName | lastName | email                | age |
      | John      | Doe      | john.doe@email.com   | 25  |
      | Jane      | Smith    | jane.smith@email.com | 30  |
      | Alice     | Johnson  | alice.j@company.org  | 28  |
      | Bob       | Brown    | bob.brown@test.net   | 35  |
      | Emma      | Wilson   | emma.w@example.co.uk | 22  |

  @ValidationOutline
  Scenario Outline: Validate user creation with invalid data
    When I attempt to create a user with invalid data:
      | firstName | <firstName> |
      | lastName  | <lastName>  |
      | email     | <email>     |
      | age       | <age>       |
    Then the request should fail with status code <statusCode>
    And the error message should indicate "<errorMessage>"

    Examples:
      | firstName | lastName | email           | age | statusCode | errorMessage        |
      |           | Doe      | john@email.com  | 25  | 400        | First name required |
      | John      |          | john@email.com  | 25  | 400        | Last name required  |
      | John      | Doe      | invalid-email   | 25  | 400        | Invalid email       |
      | John      | Doe      | john@email.com  | -1  | 400        | Invalid age         |
      | John      | Doe      | john@email.com  | 200 | 400        | Invalid age         |

  @UpdateOutline
  Scenario Outline: Update user profile with different data combinations
    Given a user exists with ID "test-123"
    When I update the user profile with details:
      | firstName | <newFirstName> |
      | lastName  | <newLastName>  |
      | email     | <newEmail>     |
      | age       | <newAge>       |
    Then the user should be updated successfully
    And the updated profile should contain:
      | firstName | <newFirstName> |
      | lastName  | <newLastName>  |
      | email     | <newEmail>     |
      | age       | <newAge>       |

    Examples:
      | newFirstName | newLastName | newEmail              | newAge |
      | Michael      | Johnson     | m.johnson@email.com   | 32     |
      | Sarah        | Davis       | sarah.davis@test.org  | 27     |
      | David        | Miller      | david.m@company.net   | 40     |