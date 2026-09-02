package com.healthsphere.dao.doctor;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.healthsphere.config.FirebaseConfig;
import com.healthsphere.model.DoctorSchedule;

import java.time.Instant;

public class DoctorScheduleDAO {

    private static final String COLLECTION =
            "doctorSchedules";

    private final Firestore db;


    public DoctorScheduleDAO() {

        this.db =
                FirebaseConfig.getFirestore();
    }


    // ============================================================
    // GET DOCTOR SCHEDULE
    // ============================================================

    public DoctorSchedule getDoctorSchedule(
            String doctorUid) throws Exception {

        validateDoctorUid(doctorUid);

        DocumentSnapshot snapshot =
                db.collection(COLLECTION)
                        .document(doctorUid)
                        .get()
                        .get();


        if (!snapshot.exists()) {

            /*
             * First time doctor opens this page.
             *
             * Return default schedule.
             * It will be saved when Save Changes is clicked.
             */
            return new DoctorSchedule(
                    doctorUid
            );
        }


        DoctorSchedule schedule =
                snapshot.toObject(
                        DoctorSchedule.class
                );


        if (schedule == null) {

            return new DoctorSchedule(
                    doctorUid
            );
        }


        return schedule;
    }


    // ============================================================
    // SAVE DOCTOR SCHEDULE
    // ============================================================

    public void saveDoctorSchedule(
            DoctorSchedule schedule) throws Exception {

        if (schedule == null) {

            throw new IllegalArgumentException(
                    "Schedule cannot be null."
            );
        }


        validateDoctorUid(
                schedule.getDoctorUid()
        );


        schedule.setUpdatedAt(
                Instant.now().toString()
        );


        db.collection(COLLECTION)
                .document(
                        schedule.getDoctorUid()
                )
                .set(schedule)
                .get();
    }


    // ============================================================
    // DELETE SCHEDULE
    // ============================================================

    public void deleteDoctorSchedule(
            String doctorUid) throws Exception {

        validateDoctorUid(doctorUid);

        db.collection(COLLECTION)
                .document(doctorUid)
                .delete()
                .get();
    }


    // ============================================================
    // CHECK SCHEDULE
    // ============================================================

    public boolean scheduleExists(
            String doctorUid) throws Exception {

        validateDoctorUid(doctorUid);

        DocumentSnapshot snapshot =
                db.collection(COLLECTION)
                        .document(doctorUid)
                        .get()
                        .get();

        return snapshot.exists();
    }


    // ============================================================
    // VALIDATION
    // ============================================================

    private void validateDoctorUid(
            String doctorUid) {

        if (doctorUid == null
                || doctorUid.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Doctor UID is required."
            );
        }
    }
}