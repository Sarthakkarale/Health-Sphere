package com.healthsphere.dao.authentication;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthsphere.config.FirebaseAuthConfig;
import com.healthsphere.exceptions.AuthenticationException;
import com.healthsphere.model.AuthenticationResponse;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AuthenticationDAO {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public AuthenticationDAO() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    // ============================================================
    // REGISTER
    // ============================================================

    public AuthenticationResponse register(
            String email,
            String password) {

        try {

            String url = FirebaseAuthConfig.AUTH_BASE_URL
                    + "/accounts:signUp?key="
                    + FirebaseAuthConfig.API_KEY;

            String requestBody = """
                    {
                        "email": "%s",
                        "password": "%s",
                        "returnSecureToken": true
                    }
                    """.formatted(email, password);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header(
                            "Content-Type",
                            "application/json")
                    .POST(
                            HttpRequest.BodyPublishers
                                    .ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {

                throw new AuthenticationException(
                        getFirebaseErrorMessage(
                                response.body()));
            }

            JsonNode json = objectMapper.readTree(
                    response.body());

            String uid = json.path("localId").asText();

            String userEmail = json.path("email").asText();

            String idToken = json.path("idToken").asText();

            String refreshToken = json.path("refreshToken").asText();

            String expiresIn = json.path("expiresIn").asText();

            return new AuthenticationResponse(
                    uid,
                    userEmail,
                    idToken,
                    refreshToken,
                    expiresIn);

        } catch (AuthenticationException e) {

            throw e;

        } catch (Exception e) {

            throw new AuthenticationException(
                    "Unable to connect to Firebase Authentication.",
                    e);
        }
    }

    // ============================================================
    // LOGIN
    // ============================================================

    public AuthenticationResponse login(
            String email,
            String password) {

        try {

            String url = FirebaseAuthConfig.AUTH_BASE_URL
                    + "/accounts:signInWithPassword?key="
                    + FirebaseAuthConfig.API_KEY;

            String requestBody = """
                    {
                        "email": "%s",
                        "password": "%s",
                        "returnSecureToken": true
                    }
                    """.formatted(email, password);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header(
                            "Content-Type",
                            "application/json")
                    .POST(
                            HttpRequest.BodyPublishers
                                    .ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {

                throw new AuthenticationException(
                        getFirebaseErrorMessage(
                                response.body()));
            }

            JsonNode json = objectMapper.readTree(
                    response.body());

            String uid = json.path("localId").asText();

            String userEmail = json.path("email").asText();

            String idToken = json.path("idToken").asText();

            String refreshToken = json.path("refreshToken").asText();

            String expiresIn = json.path("expiresIn").asText();

            return new AuthenticationResponse(
                    uid,
                    userEmail,
                    idToken,
                    refreshToken,
                    expiresIn);

        } catch (AuthenticationException e) {

            throw e;

        } catch (Exception e) {

            throw new AuthenticationException(
                    "Unable to connect to Firebase Authentication.",
                    e);
        }
    }

    // ============================================================
    // FIREBASE ERROR HANDLING
    // ============================================================

    private String getFirebaseErrorMessage(
            String responseBody) {

        try {

            JsonNode json = objectMapper.readTree(
                    responseBody);

            String errorCode = json.path("error")
                    .path("message")
                    .asText();

            return switch (errorCode) {

                case "EMAIL_EXISTS" ->
                    "Email is already registered.";

                case "INVALID_EMAIL" ->
                    "Please enter a valid email address.";

                case "WEAK_PASSWORD" ->
                    "Password is too weak.";

                case "OPERATION_NOT_ALLOWED" ->
                    "Email/password authentication is not enabled.";

                case "INVALID_LOGIN_CREDENTIALS" ->
                    "Invalid email or password.";

                case "USER_DISABLED" ->
                    "This account has been disabled.";
                
                case "EMAIL_NOT_FOUND" ->
                    "No account exists with this email address.";

                default ->
                    "Authentication failed. Please try again.";
            };

        } catch (Exception e) {

            return "Authentication failed. Please try again.";
        }
    }
    // ============================================================
    // PASSWORD RESET
    // ============================================================

    public void sendPasswordResetEmail(String email) {

        try {

            String url = FirebaseAuthConfig.AUTH_BASE_URL
                    + "/accounts:sendOobCode?key="
                    + FirebaseAuthConfig.API_KEY;

            String requestBody = """
                    {
                        "requestType": "PASSWORD_RESET",
                        "email": "%s"
                    }
                    """.formatted(email);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header(
                            "Content-Type",
                            "application/json")
                    .POST(
                            HttpRequest.BodyPublishers
                                    .ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {

                throw new AuthenticationException(
                        getFirebaseErrorMessage(
                                response.body()));
            }

        } catch (AuthenticationException e) {

            throw e;

        } catch (Exception e) {

            throw new AuthenticationException(
                    "Unable to send password reset request.",
                    e);
        }
    }
}