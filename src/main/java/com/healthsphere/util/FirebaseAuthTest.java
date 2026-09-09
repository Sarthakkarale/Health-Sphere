package com.healthsphere.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthsphere.config.FirebaseAuthConfig;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class FirebaseAuthTest {

    public static void main(String[] args) {

        try {

            HttpClient httpClient = SharedHttpClient.getInstance();

            ObjectMapper objectMapper = new ObjectMapper();

            String url =
                    FirebaseAuthConfig.AUTH_BASE_URL
                    + "/accounts:signUp?key="
                    + FirebaseAuthConfig.API_KEY;

            String email = "test@example.com";
            String password = "Test@12345";

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
                            "application/json"
                    )
                    .POST(
                            HttpRequest.BodyPublishers
                                    .ofString(requestBody)
                    )
                    .build();

            HttpResponse<String> response =
                    httpClient.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            System.out.println(
                    "HTTP Status: "
                    + response.statusCode()
            );

            System.out.println(
                    "Firebase Response:"
            );

            System.out.println(
                    response.body()
            );

            if (response.statusCode() == 200) {

                JsonNode json =
                        objectMapper.readTree(response.body());

                String uid =
                        json.get("localId").asText();

                String idToken =
                        json.get("idToken").asText();

                System.out.println(
                        "Registration successful!"
                );

                System.out.println(
                        "UID: " + uid
                );

                System.out.println(
                        "ID Token received: "
                        + (idToken != null)
                );

            } else {

                System.out.println(
                        "Registration failed."
                );
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}