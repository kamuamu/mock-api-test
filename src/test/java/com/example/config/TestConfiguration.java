package com.example.config;

import com.example.models.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import java.util.Map;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

public class TestConfiguration {

    private WireMockServer wireMockServer;
    private ObjectMapper objectMapper;
    private static final int WIREMOCK_PORT = 8080;

    public TestConfiguration() {
        this.objectMapper = new ObjectMapper();
        this.wireMockServer = new WireMockServer(
                WireMockConfiguration.options()
                        .port(WIREMOCK_PORT)
        );
    }

    public void startWireMockServer() {
        if (!wireMockServer.isRunning()) {
            wireMockServer.start();
            WireMock.configureFor("localhost", WIREMOCK_PORT);
        }
    }

    public void stopWireMockServer() {
        if (wireMockServer.isRunning()) {
            wireMockServer.stop();
        }
    }

    public String getBaseUrl() {
        return "http://localhost:" + WIREMOCK_PORT;
    }

    public void setupCommonStubs() {
        // Health check endpoint
        stubFor(get(urlEqualTo("/health"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":\"UP\",\"service\":\"user-service\"}")));
    }

    public void setupCreateUserStub(User user) {
        try {
            User responseUser = User.builder()
                    .id(UUID.randomUUID().toString())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .email(user.getEmail())
                    .age(user.getAge())
                    .build();

            String responseBody = objectMapper.writeValueAsString(responseUser);

            stubFor(post(urlEqualTo("/users"))
                    .withHeader("Content-Type", equalTo("application/json"))
                    .willReturn(aResponse()
                            .withStatus(201)
                            .withHeader("Content-Type", "application/json")
                            .withBody(responseBody)));

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to setup create user stub", e);
        }
    }

    public void setupUserExistsStub(String userId) {
        try {
            User existingUser = User.builder()
                    .id(userId)
                    .firstName("John")
                    .lastName("Doe")
                    .email("john@example.com")
                    .age(30)
                    .build();

            String responseBody = objectMapper.writeValueAsString(existingUser);

            stubFor(get(urlEqualTo("/users/" + userId))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody(responseBody)));

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to setup user exists stub", e);
        }
    }

    public void setupUpdateUserStub(User updatedUser) {
        try {
            String responseBody = objectMapper.writeValueAsString(updatedUser);

            stubFor(put(urlEqualTo("/users/" + updatedUser.getId()))
                    .withHeader("Content-Type", equalTo("application/json"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody(responseBody)));

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to setup update user stub", e);
        }
    }

    public void setupDeleteUserStub(String userId) {
        stubFor(delete(urlEqualTo("/users/" + userId))
                .willReturn(aResponse()
                        .withStatus(204)));
    }

    public void setupUserNotFoundStub(String userId) {
        stubFor(get(urlEqualTo("/users/" + userId))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\":\"User not found\",\"message\":\"User with ID " + userId + " does not exist\"}")));
    }

    public void setupValidationErrorStub(Map<String, String> userData) {
        String errorMessage = "Validation error";

        // Determine specific error message based on invalid data
        if (userData.get("firstName") == null || userData.get("firstName").isEmpty()) {
            errorMessage = "First name required";
        } else if (userData.get("lastName") == null || userData.get("lastName").isEmpty()) {
            errorMessage = "Last name required";
        } else if (!userData.get("email").contains("@") || userData.get("email").equals("invalid-email")) {
            errorMessage = "Invalid email format";
        } else if (userData.get("age").equals("-1") || userData.get("age").equals("200")) {
            errorMessage = "Invalid age";
        }

        stubFor(post(urlEqualTo("/users"))
                .withHeader("Content-Type", equalTo("application/json"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\":\"Validation Error\",\"message\":\"" + errorMessage + "\"}")));
    }

    public void setupInvalidEmailStub() {
        stubFor(post(urlEqualTo("/users"))
                .withHeader("Content-Type", equalTo("application/json"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\":\"Validation Error\",\"message\":\"Invalid email format\"}")));
    }

    public void resetStubs() {
        WireMock.reset();
        setupCommonStubs();
    }
}