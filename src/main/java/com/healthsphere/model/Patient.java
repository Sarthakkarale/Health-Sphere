package com.healthsphere.model;

public class Patient {
    private String name;
    private String age;
    private String gender;
    private String bloodType;
    private String phone;
    private String avatarPath;
    private String initials;
    private String bgColor;
    private String textColor;

    public Patient(String name, String age, String gender, String bloodType, String phone, 
                   String avatarPath, String initials, String bgColor, String textColor) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.bloodType = bloodType;
        this.phone = phone;
        this.avatarPath = avatarPath;
        this.initials = initials;
        this.bgColor = bgColor;
        this.textColor = textColor;
    }

    // Getters
    public String getName() { return name; }
    public String getAge() { return age; }
    public String getGender() { return gender; }
    public String getBloodType() { return bloodType; }
    public String getPhone() { return phone; }
    public String getAvatarPath() { return avatarPath; }
    public String getInitials() { return initials; }
    public String getBgColor() { return bgColor; }
    public String getTextColor() { return textColor; }
}