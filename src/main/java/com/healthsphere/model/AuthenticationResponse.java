package com.healthsphere.model;

public class AuthenticationResponse {

    private String uid;
    private String email;
    private String idToken;
    private String refreshToken;
    private String expiresIn;

    public AuthenticationResponse() {
    }

    public AuthenticationResponse(
            String uid,
            String email,
            String idToken,
            String refreshToken,
            String expiresIn) {

        this.uid = uid;
        this.email = email;
        this.idToken = idToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
    }

    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }

    public String getIdToken() {
        return idToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getExpiresIn() {
        return expiresIn;
    }
}