package com.example.config;

import com.example.models.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import java.util.HashMap;
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

    private static final int WIREMOCK_PORT = 8080;

    private final WireMockServer wireMockServer;
    private final ObjectMapper objectMapper;

    public TestConfiguration() {
        this.objectMapper = new ObjectMapper();
        this.wireMockServer = new WireMockServer(WireMockConfiguration.options().port(WIREMOCK_PORT));
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
        stubFor(get(urlEqualTo("/health"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonBody(Map.of(
                                "status", "UP",
                                "service", "user-service"
                        )))));
    }

    public void setupCreateUserStub(User user) {
        User responseUser = User.builder()
                .id(UUID.randomUUID().toString())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .age(user.getAge())
                .build();

        stubFor(post(urlEqualTo("/users"))
                .withHeader("Content-Type", equalTo("application/json"))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody(toJson(responseUser))));
    }

    public void setupUserExistsStub(String userId) {
        User user = User.builder()
                .id(userId)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .age(30)
                .build();

        stubFor(get(urlEqualTo("/users/" + userId))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(toJson(user))));
    }

    public void setupUpdateUserStub(User updatedUser) {
        stubFor(put(urlEqualTo("/users/" + updatedUser.getId()))
                .withHeader("Content-Type", equalTo("application/json"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(toJson(updatedUser))));
    }

    public void setupDeleteUserStub(String userId) {
        stubFor(delete(urlEqualTo("/users/" + userId))
                .willReturn(aResponse().withStatus(204)));
    }

    public void setupUserNotFoundStub(String userId) {
        stubFor(get(urlEqualTo("/users/" + userId))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\":\"User not found\"}")));
    }


    public void setupInvalidEmailStub() {
        stubFor(post(urlEqualTo("/users"))
                .withHeader("Content-Type", equalTo("application/json"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonBody(Map.of(
                                "error", "Validation Error",
                                "message", "Invalid email format"
                        )))));
    }

    public void resetStubs() {
        WireMock.reset();
        setupCommonStubs();
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON serialization failed", e);
        }
    }

    private String jsonBody(Map<String, String> map) {
        return toJson(map);
    }
}
