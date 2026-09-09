package com.healthsphere.controller;

import java.awt.Desktop;
import java.net.URI;
import java.util.List;
import java.util.UUID;

import com.google.gson.JsonObject;
import com.healthsphere.config.RazorpayService;
import com.healthsphere.dao.common.PaymentDAO;
import com.healthsphere.dao.patient.AppointmentDAO;
import com.healthsphere.model.Appointment;
import com.healthsphere.model.PaymentRecord;

public class PaymentController {

    private final RazorpayService razorpayService;
    private final PaymentDAO paymentDAO;
    private final AppointmentDAO appointmentDAO;

    public PaymentController() {
        this.razorpayService = new RazorpayService();
        this.paymentDAO = new PaymentDAO();
        this.appointmentDAO = new AppointmentDAO();
    }

    /**
     * Initiates payment for an appointment:
     * 1. Creates Razorpay payment link.
     * 2. Saves pending PaymentRecord in Firestore 'payments' collection.
     * 3. Opens the payment link directly in default system browser.
     */
    public PaymentRecord initiatePayment(Appointment appointment) throws Exception {
        if (appointment == null) {
            throw new IllegalArgumentException("Appointment cannot be null.");
        }

        double fee = appointment.getFee();
        if (fee <= 0 && appointment.getDoctorUid() != null && !appointment.getDoctorUid().isBlank()) {
            try {
                com.healthsphere.dao.doctor.DoctorAvailabilityDAO availDAO = new com.healthsphere.dao.doctor.DoctorAvailabilityDAO();
                com.healthsphere.model.DoctorAvailability avail = availDAO.getAvailability(appointment.getDoctorUid().trim());
                if (avail != null && avail.getConsultationFee() > 0) {
                    fee = avail.getConsultationFee();
                    appointment.setFee(fee);
                }
            } catch (Exception ignored) {}
        }
        int feePaise = (int) (fee * 100);
        String orderId = "ORD_" + System.currentTimeMillis();

        boolean isHospital = "HOSPITAL".equalsIgnoreCase(appointment.getBookingType()) || (appointment.getHospitalId() != null && !appointment.getHospitalId().isBlank());
        String targetName = isHospital ? (appointment.getHospitalName() != null ? appointment.getHospitalName() : "Hospital") : (appointment.getDoctorName() != null ? appointment.getDoctorName() : "Doctor");
        String description = (isHospital ? "Hospital Fee - " : "Consultation Fee - ") + targetName;

        // Create Razorpay Payment Link
        JsonObject response = razorpayService.createPaymentLinkFull(orderId, feePaise, description);
        
        String paymentLinkId = response.has("id") ? response.get("id").getAsString() : UUID.randomUUID().toString();
        String shortUrl = response.has("short_url") ? response.get("short_url").getAsString() : "";

        // Create & Save PaymentRecord in Firestore
        PaymentRecord record = new PaymentRecord();
        record.setAppointmentId(appointment.getAppointmentId());
        record.setPatientUid(appointment.getPatientUid());
        record.setPatientName(appointment.getPatientName());
        record.setDoctorUid(appointment.getDoctorUid());
        record.setDoctorName(appointment.getDoctorName());
        record.setHospitalId(appointment.getHospitalId());
        record.setHospitalName(appointment.getHospitalName());
        record.setAmount(fee);
        record.setStatus("PENDING");
        record.setPaymentLinkId(paymentLinkId);
        record.setPaymentLinkUrl(shortUrl);

        PaymentRecord savedRecord = paymentDAO.savePayment(record);

        // Open Razorpay URL directly in Browser
        if (shortUrl != null && !shortUrl.isBlank() && Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(new URI(shortUrl));
            } catch (Exception e) {
                System.err.println("Could not launch default browser: " + e.getMessage());
            }
        }

        return savedRecord;
    }

    /**
     * Checks Razorpay payment link status and updates status, balances, and appointment.
     * When Razorpay status is "paid" (code 200 success), user balance decreases and doctor/hospital balance increases.
     */
    public boolean checkAndUpdatePaymentStatus(PaymentRecord record, Appointment appointment) {
        if (record == null || record.getPaymentLinkId() == null) {
            return false;
        }

        try {
            String razorpayStatus = razorpayService.getPaymentLinkStatus(record.getPaymentLinkId());

            if ("paid".equalsIgnoreCase(razorpayStatus)) {
                record.setStatus("COMPLETED");
                paymentDAO.savePayment(record);

                // Update Appointment payment status to PAID
                if (appointment != null) {
                    appointment.setPaymentStatus("PAID");
                    appointmentDAO.updateAppointment(appointment);
                }

                // Deduct User Account Balance
                if (record.getPatientUid() != null) {
                    double currentPatientBal = paymentDAO.getPatientAccountBalance(record.getPatientUid());
                    double newPatientBal = Math.max(0, currentPatientBal - record.getAmount());
                    paymentDAO.updatePatientAccountBalance(record.getPatientUid(), newPatientBal);
                }

                // Credit Doctor Account Balance (if Doctor Appointment)
                if (record.getDoctorUid() != null && !record.getDoctorUid().isBlank()) {
                    double currentDocBal = paymentDAO.getDoctorAccountBalance(record.getDoctorUid());
                    double newDocBal = currentDocBal + record.getAmount();
                    paymentDAO.updateDoctorAccountBalance(record.getDoctorUid(), newDocBal);
                }

                // Credit Hospital Account Balance (if Hospital Appointment)
                String hospitalId = record.getHospitalId() != null ? record.getHospitalId() : (appointment != null ? appointment.getHospitalId() : null);
                if (hospitalId != null && !hospitalId.isBlank()) {
                    double currentHospBal = paymentDAO.getHospitalAccountBalance(hospitalId);
                    double newHospBal = currentHospBal + record.getAmount();
                    paymentDAO.updateHospitalAccountBalance(hospitalId, newHospBal);
                }

                return true;
            }
        } catch (Exception e) {
            System.err.println("Error checking payment status: " + e.getMessage());
        }
        return false;
    }

    public PaymentRecord getPaymentByAppointment(String appointmentId) {
        return paymentDAO.getPaymentByAppointment(appointmentId);
    }

    public List<PaymentRecord> getPaymentsForPatient(String patientUid) {
        return paymentDAO.getPaymentsForPatient(patientUid);
    }

    public List<PaymentRecord> getPaymentsForDoctor(String doctorUid) {
        return paymentDAO.getPaymentsForDoctor(doctorUid);
    }

    public double getPatientAccountBalance(String patientUid) {
        return paymentDAO.getPatientAccountBalance(patientUid);
    }

    public double getDoctorAccountBalance(String doctorUid) {
        return paymentDAO.getDoctorAccountBalance(doctorUid);
    }

    public List<PaymentRecord> getPaymentsForHospital(String hospitalId) {
        return paymentDAO.getPaymentsForHospital(hospitalId);
    }

    public double getHospitalAccountBalance(String hospitalId) {
        return paymentDAO.getHospitalAccountBalance(hospitalId);
    }
}
