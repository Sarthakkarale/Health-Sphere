package com.healthsphere.model;

public class UserProfile {

    private String uid;
    private String email;
    private String role;
    private String status;

    public UserProfile() {
    }

    public UserProfile(
            String uid,
            String email,
            String role,
            String status) {

        this.uid = uid;
        this.email = email;
        this.role = role;
        this.status = status;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}