package com.healthsphere.dao.common;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.healthsphere.config.FirebaseConfig;
import com.healthsphere.exceptions.DatabaseException;
import com.healthsphere.model.DoctorProfile;
import com.healthsphere.model.PatientProfile;
import com.healthsphere.model.PaymentRecord;

public class PaymentDAO {

    private final Firestore db;

    public PaymentDAO() {
        this.db = FirebaseConfig.getFirestore();
    }

    // =========================================================
    // SAVE / UPDATE PAYMENT RECORD
    // =========================================================

    public PaymentRecord savePayment(PaymentRecord record) {
        try {
            if (record.getPaymentId() == null || record.getPaymentId().isBlank()) {
                record.setPaymentId(UUID.randomUUID().toString());
            }

            String now = LocalDateTime.now().toString();
            if (record.getCreatedAt() == null || record.getCreatedAt().isBlank()) {
                record.setCreatedAt(now);
            }
            record.setUpdatedAt(now);

            db.collection("payments")
                    .document(record.getPaymentId())
                    .set(record)
                    .get();

            return record;
        } catch (Exception e) {
            throw new DatabaseException("Unable to save payment record.", e);
        }
    }

    public PaymentRecord getPaymentByAppointment(String appointmentId) {
        try {
            ApiFuture<QuerySnapshot> future = db.collection("payments")
                    .whereEqualTo("appointmentId", appointmentId)
                    .get();

            QuerySnapshot snapshot = future.get();
            if (!snapshot.getDocuments().isEmpty()) {
                DocumentSnapshot doc = snapshot.getDocuments().get(0);
                PaymentRecord record = doc.toObject(PaymentRecord.class);
                if (record != null && (record.getPaymentId() == null || record.getPaymentId().isBlank())) {
                    record.setPaymentId(doc.getId());
                }
                return record;
            }
            return null;
        } catch (Exception e) {
            System.err.println("Unable to get payment by appointment: " + e.getMessage());
            return null;
        }
    }

    public List<PaymentRecord> getPaymentsForPatient(String patientUid) {
        List<PaymentRecord> list = new ArrayList<>();
        try {
            ApiFuture<QuerySnapshot> future = db.collection("payments")
                    .whereEqualTo("patientUid", patientUid)
                    .get();

            for (DocumentSnapshot doc : future.get().getDocuments()) {
                PaymentRecord rec = doc.toObject(PaymentRecord.class);
                if (rec != null) {
                    if (rec.getPaymentId() == null || rec.getPaymentId().isBlank()) {
                        rec.setPaymentId(doc.getId());
                    }
                    list.add(rec);
                }
            }
        } catch (Exception e) {
            System.err.println("Unable to get payments for patient: " + e.getMessage());
        }
        return list;
    }

    public List<PaymentRecord> getPaymentsForDoctor(String doctorUid) {
        List<PaymentRecord> list = new ArrayList<>();
        try {
            ApiFuture<QuerySnapshot> future = db.collection("payments")
                    .whereEqualTo("doctorUid", doctorUid)
                    .get();

            for (DocumentSnapshot doc : future.get().getDocuments()) {
                PaymentRecord rec = doc.toObject(PaymentRecord.class);
                if (rec != null) {
                    if (rec.getPaymentId() == null || rec.getPaymentId().isBlank()) {
                        rec.setPaymentId(doc.getId());
                    }
                    list.add(rec);
                }
            }
        } catch (Exception e) {
            System.err.println("Unable to get payments for doctor: " + e.getMessage());
        }
        return list;
    }

    public List<PaymentRecord> getPaymentsForHospital(String hospitalId) {
        List<PaymentRecord> list = new ArrayList<>();
        try {
            ApiFuture<QuerySnapshot> future = db.collection("payments")
                    .whereEqualTo("hospitalId", hospitalId)
                    .get();

            for (DocumentSnapshot doc : future.get().getDocuments()) {
                PaymentRecord rec = doc.toObject(PaymentRecord.class);
                if (rec != null) {
                    if (rec.getPaymentId() == null || rec.getPaymentId().isBlank()) {
                        rec.setPaymentId(doc.getId());
                    }
                    list.add(rec);
                }
            }
        } catch (Exception e) {
            System.err.println("Unable to get payments for hospital: " + e.getMessage());
        }
        return list;
    }

    // =========================================================
    // ACCOUNT BALANCES (PATIENT & DOCTOR)
    // =========================================================

    public double getPatientAccountBalance(String patientUid) {
        try {
            DocumentSnapshot doc = db.collection("patients").document(patientUid).get().get();
            if (doc.exists()) {
                Double bal = doc.getDouble("accountBalance");
                return bal != null ? bal : 1000.00;
            }
            DocumentSnapshot userDoc = db.collection("user_profiles").document(patientUid).get().get();
            if (userDoc.exists()) {
                Double bal = userDoc.getDouble("accountBalance");
                return bal != null ? bal : 1000.00;
            }
        } catch (Exception e) {
            System.err.println("Error fetching patient account balance: " + e.getMessage());
        }
        return 1000.00;
    }

    public void updatePatientAccountBalance(String patientUid, double newBalance) {
        try {
            db.collection("patients").document(patientUid).update("accountBalance", newBalance);
        } catch (Exception e) {
            try {
                db.collection("user_profiles").document(patientUid).update("accountBalance", newBalance);
            } catch (Exception ex) {
                System.err.println("Error updating patient account balance: " + ex.getMessage());
            }
        }
    }

    public double getDoctorTotalEarnings(String doctorUid) {
        if (doctorUid == null || doctorUid.isBlank()) {
            return 0.00;
        }
        List<PaymentRecord> list = getPaymentsForDoctor(doctorUid);
        double sum = 0.00;
        for (PaymentRecord p : list) {
            if (p != null && p.getStatus() != null) {
                String st = p.getStatus().trim().toUpperCase();
                if (st.equals("COMPLETED") || st.equals("SUCCESS") || st.equals("PAID") || st.equals("RECEIVED")) {
                    sum += p.getAmount();
                }
            }
        }
        return sum;
    }

    public double getDoctorAccountBalance(String doctorUid) {
        return getDoctorTotalEarnings(doctorUid);
    }


    public void updateDoctorAccountBalance(String doctorUid, double newBalance) {
        try {
            db.collection("doctors").document(doctorUid).update("accountBalance", newBalance);
        } catch (Exception e) {
            try {
                db.collection("doctor_profiles").document(doctorUid).update("accountBalance", newBalance);
            } catch (Exception ex) {
                System.err.println("Error updating doctor account balance: " + ex.getMessage());
            }
        }
    }

    public double getHospitalAccountBalance(String hospitalId) {
        try {
            DocumentSnapshot doc = db.collection("hospitals").document(hospitalId).get().get();
            if (doc.exists()) {
                Double bal = doc.getDouble("accountBalance");
                return bal != null ? bal : 0.00;
            }
            DocumentSnapshot docProfile = db.collection("hospital_profiles").document(hospitalId).get().get();
            if (docProfile.exists()) {
                Double bal = docProfile.getDouble("accountBalance");
                return bal != null ? bal : 0.00;
            }
        } catch (Exception e) {
            System.err.println("Error fetching hospital account balance: " + e.getMessage());
        }
        return 0.00;
    }

    public void updateHospitalAccountBalance(String hospitalId, double newBalance) {
        try {
            db.collection("hospitals").document(hospitalId).update("accountBalance", newBalance);
        } catch (Exception e) {
            try {
                db.collection("hospital_profiles").document(hospitalId).update("accountBalance", newBalance);
            } catch (Exception ex) {
                System.err.println("Error updating hospital account balance: " + ex.getMessage());
            }
        }
    }
}
