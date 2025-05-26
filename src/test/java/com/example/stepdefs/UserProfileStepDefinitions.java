package com.example.stepdefs;

import com.example.config.TestConfiguration;
import com.example.models.User;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class UserProfileStepDefinitions {

    private TestConfiguration testConfig;
    private RequestSpecification requestSpec;
    private Response response;
    private User currentUser;
    private String baseUrl;

    @Before
    public void setUp() {
        testConfig = new TestConfiguration();
        testConfig.startWireMockServer();
        baseUrl = testConfig.getBaseUrl();

        requestSpec = given()
                .baseUri(baseUrl)
                .contentType("application/json")
                .accept("application/json");
    }

    @After
    public void tearDown() {
        if (testConfig != null) {
            testConfig.stopWireMockServer();
        }
    }

    @Given("the user service is running")
    public void theUserServiceIsRunning() {
        testConfig.setupCommonStubs();
        // Verify service health
        response = requestSpec.get("/health");
        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    @Given("the API is available at {string}")
    public void theApiIsAvailableAt(String url) {
        this.baseUrl = url;
        requestSpec = given()
                .baseUri(baseUrl)
                .contentType("application/json")
                .accept("application/json");
    }

    @Given("a user exists with ID {string}")
    public void aUserExistsWithId(String userId) {
        testConfig.setupUserExistsStub(userId);
        currentUser = User.builder()
                .id(userId)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .age(30)
                .build();
    }

    @Given("a user does not exist with ID {string}")
    public void aUserDoesNotExistWithId(String userId) {
        testConfig.setupUserNotFoundStub(userId);
    }

    @When("I create a user with details:")
    public void iCreateAUserWithDetails(DataTable dataTable) {
        Map<String, String> userData = dataTable.asMap(String.class, String.class);

        currentUser = User.builder()
                .firstName(userData.get("firstName"))
                .lastName(userData.get("lastName"))
                .email(userData.get("email"))
                .age(Integer.parseInt(userData.get("age")))
                .build();

        testConfig.setupCreateUserStub(currentUser);

        response = requestSpec
                .body(currentUser)
                .post("/users");
    }

    @When("I attempt to create a user with invalid data:")
    public void iAttemptToCreateAUserWithInvalidData(DataTable dataTable) {
        Map<String, String> userData = dataTable.asMap(String.class, String.class);

        User invalidUser = User.builder()
                .firstName(userData.get("firstName"))
                .lastName(userData.get("lastName"))
                .email(userData.get("email"))
                .age(userData.get("age").isEmpty() ? 0 : Integer.parseInt(userData.get("age")))
                .build();

        testConfig.setupValidationErrorStub(userData);

        response = requestSpec
                .body(invalidUser)
                .post("/users");
    }

    @When("I update the user profile with details:")
    public void iUpdateTheUserProfileWithDetails(DataTable dataTable) {
        Map<String, String> userData = dataTable.asMap(String.class, String.class);

        User updatedUser = User.builder()
                .id(currentUser != null ? currentUser.getId() : "test-123")
                .firstName(userData.get("firstName"))
                .lastName(userData.get("lastName"))
                .email(userData.get("email"))
                .age(Integer.parseInt(userData.get("age")))
                .build();

        testConfig.setupUpdateUserStub(updatedUser);

        response = requestSpec
                .body(updatedUser)
                .put("/users/" + updatedUser.getId());

        currentUser = updatedUser;
    }

    @When("I request the user profile for ID {string}")
    public void iRequestTheUserProfileForId(String userId) {
        response = requestSpec.get("/users/" + userId);
    }

    @When("I update the user profile with:")
    public void iUpdateTheUserProfileWith(DataTable dataTable) {
        Map<String, String> userData = dataTable.asMap(String.class, String.class);

        User updatedUser = User.builder()
                .id(currentUser.getId())
                .firstName(userData.get("firstName"))
                .lastName(userData.get("lastName"))
                .email(userData.get("email"))
                .age(Integer.parseInt(userData.get("age")))
                .build();

        testConfig.setupUpdateUserStub(updatedUser);

        response = requestSpec
                .body(updatedUser)
                .put("/users/" + currentUser.getId());

        currentUser = updatedUser;
    }

    @When("I delete the user with ID {string}")
    public void iDeleteTheUserWithId(String userId) {
        testConfig.setupDeleteUserStub(userId);
        response = requestSpec.delete("/users/" + userId);
    }

    @When("I attempt to create a user with invalid email {string}")
    public void iAttemptToCreateAUserWithInvalidEmail(String invalidEmail) {
        User invalidUser = User.builder()
                .firstName("Test")
                .lastName("User")
                .email(invalidEmail)
                .age(25)
                .build();

        testConfig.setupInvalidEmailStub();

        response = requestSpec
                .body(invalidUser)
                .post("/users");
    }

    @Then("the user should be created successfully")
    public void theUserShouldBeCreatedSuccessfully() {
        assertThat(response.getStatusCode()).isEqualTo(201);
    }

    @Then("the response should contain the user ID")
    public void theResponseShouldContainTheUserId() {
        String userId = response.jsonPath().getString("id");
        assertThat(userId).isNotNull().isNotEmpty();
        currentUser.setId(userId);
    }

    @Then("the user should have firstName {string}")
    public void theUserShouldHaveFirstName(String expectedFirstName) {
        response.then().body("firstName", equalTo(expectedFirstName));
    }

    @Then("the user should have lastName {string}")
    public void theUserShouldHaveLastName(String expectedLastName) {
        response.then().body("lastName", equalTo(expectedLastName));
    }

    @Then("the user should have email {string}")
    public void theUserShouldHaveEmail(String expectedEmail) {
        response.then().body("email", equalTo(expectedEmail));
    }

    @Then("the user should have age {int}")
    public void theUserShouldHaveAge(int expectedAge) {
        response.then().body("age", equalTo(expectedAge));
    }

    @Then("the updated profile should contain:")
    public void theUpdatedProfileShouldContain(DataTable dataTable) {
        Map<String, String> expectedData = dataTable.asMap(String.class, String.class);

        response.then()
                .body("firstName", equalTo(expectedData.get("firstName")))
                .body("lastName", equalTo(expectedData.get("lastName")))
                .body("email", equalTo(expectedData.get("email")))
                .body("age", equalTo(Integer.parseInt(expectedData.get("age"))));
    }

    @Then("the user profile should be returned")
    public void theUserProfileShouldBeReturned() {
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getString("id")).isNotNull();
    }

    @Then("the response should contain:")
    public void theResponseShouldContain(DataTable dataTable) {
        Map<String, String> expectedData = dataTable.asMap(String.class, String.class);

        response.then()
                .body("firstName", equalTo(expectedData.get("firstName")))
                .body("lastName", equalTo(expectedData.get("lastName")))
                .body("email", equalTo(expectedData.get("email")))
                .body("age", equalTo(Integer.parseInt(expectedData.get("age"))));
    }

    @Then("the user should be updated successfully")
    public void theUserShouldBeUpdatedSuccessfully() {
        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    @Then("the updated profile should reflect the changes")
    public void theUpdatedProfileShouldReflectTheChanges() {
        response.then()
                .body("firstName", equalTo(currentUser.getFirstName()))
                .body("lastName", equalTo(currentUser.getLastName()))
                .body("email", equalTo(currentUser.getEmail()))
                .body("age", equalTo(currentUser.getAge()));
    }

    @Then("the user should be deleted successfully")
    public void theUserShouldBeDeletedSuccessfully() {
        assertThat(response.getStatusCode()).isEqualTo(204);
    }

    @Then("the user profile should no longer exist")
    public void theUserProfileShouldNoLongerExist() {
        testConfig.setupUserNotFoundStub(currentUser.getId());
        Response verifyResponse = requestSpec.get("/users/" + currentUser.getId());
        assertThat(verifyResponse.getStatusCode()).isEqualTo(404);
    }

    @Then("the request should fail with status code {int}")
    public void theRequestShouldFailWithStatusCode(int expectedStatusCode) {
        assertThat(response.getStatusCode()).isEqualTo(expectedStatusCode);
    }

    @Then("the error message should indicate {string}")
    public void theErrorMessageShouldIndicate(String expectedMessage) {
        String actualMessage = response.jsonPath().getString("message");
        assertThat(actualMessage).containsIgnoringCase(expectedMessage);
    }
}