package com.healthsphere.model;

public class DoctorSchedule {

    private String doctorUid;

    // Monday
    private boolean mondayEnabled;
    private String mondayStartTime;
    private String mondayEndTime;

    // Tuesday
    private boolean tuesdayEnabled;
    private String tuesdayStartTime;
    private String tuesdayEndTime;

    // Wednesday
    private boolean wednesdayEnabled;
    private String wednesdayStartTime;
    private String wednesdayEndTime;

    // Thursday
    private boolean thursdayEnabled;
    private String thursdayStartTime;
    private String thursdayEndTime;

    // Friday
    private boolean fridayEnabled;
    private String fridayStartTime;
    private String fridayEndTime;

    // General settings
    private int defaultAppointmentSlotMinutes;
    private boolean emergencyAvailable;

    private String updatedAt;


    public DoctorSchedule() {
        // Required by Firestore
    }


    public DoctorSchedule(String doctorUid) {

        this.doctorUid = doctorUid;

        this.mondayEnabled = true;
        this.mondayStartTime = "09:00 AM";
        this.mondayEndTime = "05:00 PM";

        this.tuesdayEnabled = true;
        this.tuesdayStartTime = "09:00 AM";
        this.tuesdayEndTime = "05:00 PM";

        this.wednesdayEnabled = true;
        this.wednesdayStartTime = "09:00 AM";
        this.wednesdayEndTime = "05:00 PM";

        this.thursdayEnabled = true;
        this.thursdayStartTime = "09:00 AM";
        this.thursdayEndTime = "05:00 PM";

        this.fridayEnabled = true;
        this.fridayStartTime = "09:00 AM";
        this.fridayEndTime = "02:00 PM";

        this.defaultAppointmentSlotMinutes = 30;

        this.emergencyAvailable = true;
    }


    public String getDoctorUid() {
        return doctorUid;
    }

    public void setDoctorUid(String doctorUid) {
        this.doctorUid = doctorUid;
    }


    public boolean isMondayEnabled() {
        return mondayEnabled;
    }

    public void setMondayEnabled(boolean mondayEnabled) {
        this.mondayEnabled = mondayEnabled;
    }

    public String getMondayStartTime() {
        return mondayStartTime;
    }

    public void setMondayStartTime(String mondayStartTime) {
        this.mondayStartTime = mondayStartTime;
    }

    public String getMondayEndTime() {
        return mondayEndTime;
    }

    public void setMondayEndTime(String mondayEndTime) {
        this.mondayEndTime = mondayEndTime;
    }


    public boolean isTuesdayEnabled() {
        return tuesdayEnabled;
    }

    public void setTuesdayEnabled(boolean tuesdayEnabled) {
        this.tuesdayEnabled = tuesdayEnabled;
    }

    public String getTuesdayStartTime() {
        return tuesdayStartTime;
    }

    public void setTuesdayStartTime(String tuesdayStartTime) {
        this.tuesdayStartTime = tuesdayStartTime;
    }

    public String getTuesdayEndTime() {
        return tuesdayEndTime;
    }

    public void setTuesdayEndTime(String tuesdayEndTime) {
        this.tuesdayEndTime = tuesdayEndTime;
    }


    public boolean isWednesdayEnabled() {
        return wednesdayEnabled;
    }

    public void setWednesdayEnabled(boolean wednesdayEnabled) {
        this.wednesdayEnabled = wednesdayEnabled;
    }

    public String getWednesdayStartTime() {
        return wednesdayStartTime;
    }

    public void setWednesdayStartTime(String wednesdayStartTime) {
        this.wednesdayStartTime = wednesdayStartTime;
    }

    public String getWednesdayEndTime() {
        return wednesdayEndTime;
    }

    public void setWednesdayEndTime(String wednesdayEndTime) {
        this.wednesdayEndTime = wednesdayEndTime;
    }


    public boolean isThursdayEnabled() {
        return thursdayEnabled;
    }

    public void setThursdayEnabled(boolean thursdayEnabled) {
        this.thursdayEnabled = thursdayEnabled;
    }

    public String getThursdayStartTime() {
        return thursdayStartTime;
    }

    public void setThursdayStartTime(String thursdayStartTime) {
        this.thursdayStartTime = thursdayStartTime;
    }

    public String getThursdayEndTime() {
        return thursdayEndTime;
    }

    public void setThursdayEndTime(String thursdayEndTime) {
        this.thursdayEndTime = thursdayEndTime;
    }


    public boolean isFridayEnabled() {
        return fridayEnabled;
    }

    public void setFridayEnabled(boolean fridayEnabled) {
        this.fridayEnabled = fridayEnabled;
    }

    public String getFridayStartTime() {
        return fridayStartTime;
    }

    public void setFridayStartTime(String fridayStartTime) {
        this.fridayStartTime = fridayStartTime;
    }

    public String getFridayEndTime() {
        return fridayEndTime;
    }

    public void setFridayEndTime(String fridayEndTime) {
        this.fridayEndTime = fridayEndTime;
    }


    public int getDefaultAppointmentSlotMinutes() {
        return defaultAppointmentSlotMinutes;
    }

    public void setDefaultAppointmentSlotMinutes(
            int defaultAppointmentSlotMinutes) {

        this.defaultAppointmentSlotMinutes =
                defaultAppointmentSlotMinutes;
    }


    public boolean isEmergencyAvailable() {
        return emergencyAvailable;
    }

    public void setEmergencyAvailable(
            boolean emergencyAvailable) {

        this.emergencyAvailable =
                emergencyAvailable;
    }


    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}