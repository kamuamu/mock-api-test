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
    When I create a user with the following details:
      | firstName | John        |
      | lastName  | Doe         |
      | email     | john@example.com |
      | age       | 30          |
    Then the user should be created successfully
    And the response should contain the user ID
    And the user details should match the provided information

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
    And the error message should indicate "User not found"