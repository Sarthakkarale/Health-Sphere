package com.healthsphere.model;

/**
 * DoctorAvailability
 *
 * Stores the appointment availability and consultation information
 * configured by a doctor.
 *
 * Firestore document:
 *
 * doctorAvailability/{doctorUid}
 */
public class DoctorAvailability {

    private String doctorUid;

    // =========================================================
    // WORKING DAYS
    // =========================================================

    private boolean mondayEnabled;
    private boolean tuesdayEnabled;
    private boolean wednesdayEnabled;
    private boolean thursdayEnabled;
    private boolean fridayEnabled;
    private boolean saturdayEnabled;
    private boolean sundayEnabled;

    // =========================================================
    // WORKING HOURS
    // =========================================================

    private String mondayStartTime;
    private String mondayEndTime;

    private String tuesdayStartTime;
    private String tuesdayEndTime;

    private String wednesdayStartTime;
    private String wednesdayEndTime;

    private String thursdayStartTime;
    private String thursdayEndTime;

    private String fridayStartTime;
    private String fridayEndTime;

    private String saturdayStartTime;
    private String saturdayEndTime;

    private String sundayStartTime;
    private String sundayEndTime;

    // =========================================================
    // APPOINTMENT SETTINGS
    // =========================================================

    private int slotDurationMinutes;

    private double consultationFee;

    private boolean inClinicAvailable;

    private boolean videoConsultationAvailable;

    private int advanceBookingDays;

    private int cancellationNoticeHours;

    private boolean emergencyAvailability;

    // =========================================================
    // CLINIC / CONTACT INFORMATION
    // =========================================================

    private String clinicName;

    private String clinicPhone;

    private String clinicEmail;

    private String clinicAddress;

    private String consultationRoom;

    // =========================================================
    // DEFAULT CONSTRUCTOR
    // =========================================================

    public DoctorAvailability() {
    }

    // =========================================================
    // GETTERS / SETTERS
    // =========================================================

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

    public boolean isTuesdayEnabled() {
        return tuesdayEnabled;
    }

    public void setTuesdayEnabled(boolean tuesdayEnabled) {
        this.tuesdayEnabled = tuesdayEnabled;
    }

    public boolean isWednesdayEnabled() {
        return wednesdayEnabled;
    }

    public void setWednesdayEnabled(boolean wednesdayEnabled) {
        this.wednesdayEnabled = wednesdayEnabled;
    }

    public boolean isThursdayEnabled() {
        return thursdayEnabled;
    }

    public void setThursdayEnabled(boolean thursdayEnabled) {
        this.thursdayEnabled = thursdayEnabled;
    }

    public boolean isFridayEnabled() {
        return fridayEnabled;
    }

    public void setFridayEnabled(boolean fridayEnabled) {
        this.fridayEnabled = fridayEnabled;
    }

    public boolean isSaturdayEnabled() {
        return saturdayEnabled;
    }

    public void setSaturdayEnabled(boolean saturdayEnabled) {
        this.saturdayEnabled = saturdayEnabled;
    }

    public boolean isSundayEnabled() {
        return sundayEnabled;
    }

    public void setSundayEnabled(boolean sundayEnabled) {
        this.sundayEnabled = sundayEnabled;
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

    public String getSaturdayStartTime() {
        return saturdayStartTime;
    }

    public void setSaturdayStartTime(String saturdayStartTime) {
        this.saturdayStartTime = saturdayStartTime;
    }

    public String getSaturdayEndTime() {
        return saturdayEndTime;
    }

    public void setSaturdayEndTime(String saturdayEndTime) {
        this.saturdayEndTime = saturdayEndTime;
    }

    public String getSundayStartTime() {
        return sundayStartTime;
    }

    public void setSundayStartTime(String sundayStartTime) {
        this.sundayStartTime = sundayStartTime;
    }

    public String getSundayEndTime() {
        return sundayEndTime;
    }

    public void setSundayEndTime(String sundayEndTime) {
        this.sundayEndTime = sundayEndTime;
    }

    public int getSlotDurationMinutes() {
        return slotDurationMinutes;
    }

    public void setSlotDurationMinutes(int slotDurationMinutes) {
        this.slotDurationMinutes = slotDurationMinutes;
    }

    public double getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(double consultationFee) {
        this.consultationFee = consultationFee;
    }

    public boolean isInClinicAvailable() {
        return inClinicAvailable;
    }

    public void setInClinicAvailable(boolean inClinicAvailable) {
        this.inClinicAvailable = inClinicAvailable;
    }

    public boolean isVideoConsultationAvailable() {
        return videoConsultationAvailable;
    }

    public void setVideoConsultationAvailable(
            boolean videoConsultationAvailable) {

        this.videoConsultationAvailable =
                videoConsultationAvailable;
    }

    public int getAdvanceBookingDays() {
        return advanceBookingDays;
    }

    public void setAdvanceBookingDays(int advanceBookingDays) {
        this.advanceBookingDays = advanceBookingDays;
    }

    public int getCancellationNoticeHours() {
        return cancellationNoticeHours;
    }

    public void setCancellationNoticeHours(
            int cancellationNoticeHours) {

        this.cancellationNoticeHours =
                cancellationNoticeHours;
    }

    public boolean isEmergencyAvailability() {
        return emergencyAvailability;
    }

    public void setEmergencyAvailability(
            boolean emergencyAvailability) {

        this.emergencyAvailability =
                emergencyAvailability;
    }

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public String getClinicPhone() {
        return clinicPhone;
    }

    public void setClinicPhone(String clinicPhone) {
        this.clinicPhone = clinicPhone;
    }

    public String getClinicEmail() {
        return clinicEmail;
    }

    public void setClinicEmail(String clinicEmail) {
        this.clinicEmail = clinicEmail;
    }

    public String getClinicAddress() {
        return clinicAddress;
    }

    public void setClinicAddress(String clinicAddress) {
        this.clinicAddress = clinicAddress;
    }

    public String getConsultationRoom() {
        return consultationRoom;
    }

    public void setConsultationRoom(String consultationRoom) {
        this.consultationRoom = consultationRoom;
    }
}