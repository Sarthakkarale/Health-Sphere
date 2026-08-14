package com.healthsphere.model;

import java.time.LocalDateTime;

/**
 * Represents the common information shared
 * by all Health-Sphere users.
 *
 * Password is intentionally NOT stored here.
 * Firebase Authentication manages passwords.
 */
public class User {

    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;

    private Role role;
    private AccountStatus status;

    private boolean emailVerified;

    private LocalDateTime createdAt;

    // Default constructor
    public User() {
    }

    // Constructor
    public User(
            String userId,
            String firstName,
            String lastName,
            String email,
            String phone,
            Role role,
            AccountStatus status,
            boolean emailVerified,
            LocalDateTime createdAt) {

        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.status = status;
        this.emailVerified = emailVerified;
        this.createdAt = createdAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}