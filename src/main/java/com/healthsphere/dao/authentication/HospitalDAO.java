package com.healthsphere.dao.authentication;

import java.util.ArrayList;
import java.util.List;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.healthsphere.config.FirebaseConfig;
import com.healthsphere.exceptions.DatabaseException;
import com.healthsphere.model.HospitalProfile;

import com.google.cloud.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class HospitalDAO {

    private final Firestore db;

    public HospitalDAO() {
        this.db = FirebaseConfig.getFirestore();
    }

    // =========================================================
    // CREATE HOSPITAL PROFILE
    // =========================================================

    public void createHospitalProfile(
            HospitalProfile hospitalProfile) {

        if (hospitalProfile == null) {
            throw new IllegalArgumentException(
                    "Hospital profile cannot be null."
            );
        }

        if (hospitalProfile.getUid() == null ||
                hospitalProfile.getUid().isBlank()) {

            throw new IllegalArgumentException(
                    "Hospital UID cannot be empty."
            );
        }

        try {

            db.collection("hospitals")
                    .document(hospitalProfile.getUid())
                    .set(toMap(hospitalProfile))
                    .get();

            System.out.println(
                    "Hospital profile created successfully."
            );

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to create hospital profile.",
                    e
            );
        }
    }

    // =========================================================
    // GET HOSPITAL PROFILE
    // =========================================================

    public HospitalProfile getHospitalProfile(
            String uid) {

        if (uid == null || uid.isBlank()) {

            throw new IllegalArgumentException(
                    "Hospital UID cannot be empty."
            );
        }

        try {

            DocumentSnapshot document =
                    db.collection("hospitals")
                            .document(uid.trim())
                            .get()
                            .get();

            if (!document.exists()) {

                throw new DatabaseException(
                        "Hospital profile not found."
                );
            }

            HospitalProfile hospital =
                    document.toObject(
                            HospitalProfile.class
                    );

            if (hospital != null &&
                    (hospital.getUid() == null ||
                     hospital.getUid().isBlank())) {

                hospital.setUid(
                        document.getId()
                );
            }

            return hospital;

        } catch (DatabaseException e) {

            throw e;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to retrieve hospital profile.",
                    e
            );
        }
    }

    // =========================================================
    // GET ALL HOSPITALS
    // =========================================================

    public List<HospitalProfile> getAllHospitals() {

        List<HospitalProfile> hospitals =
                new ArrayList<>();

        try {

            QuerySnapshot snapshot =
                    db.collection("hospitals")
                            .get()
                            .get();

            for (DocumentSnapshot document :
                    snapshot.getDocuments()) {

                if (!document.exists()) {
                    continue;
                }

                HospitalProfile hospital =
                        document.toObject(
                                HospitalProfile.class
                        );

                if (hospital == null) {
                    continue;
                }

                // -------------------------------------------------
                // Make sure UID is available.
                // If UID was not stored inside the document,
                // use Firestore document ID.
                // -------------------------------------------------

                if (hospital.getUid() == null ||
                        hospital.getUid().isBlank()) {

                    hospital.setUid(
                            document.getId()
                    );
                }

                hospitals.add(hospital);
            }

            System.out.println(
                    "Total hospitals loaded: "
                            + hospitals.size()
            );

            return hospitals;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to retrieve hospitals.",
                    e
            );
        }
    }

    // =========================================================
    // UPDATE HOSPITAL PROFILE
    // =========================================================

    public void updateHospitalProfile(
            HospitalProfile hospitalProfile) {

        if (hospitalProfile == null) {

            throw new IllegalArgumentException(
                    "Hospital profile cannot be null."
            );
        }

        if (hospitalProfile.getUid() == null ||
                hospitalProfile.getUid().isBlank()) {

            throw new IllegalArgumentException(
                    "Hospital UID cannot be empty."
            );
        }

        try {

            db.collection("hospitals")
                    .document(
                            hospitalProfile
                                    .getUid()
                                    .trim()
                    )
                    .set(toMap(hospitalProfile))
                    .get();

            System.out.println(
                    "Hospital profile updated successfully."
            );

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to update hospital profile.",
                    e
            );
        }
    }

    // ============================================================
    // DELETE HOSPITAL PROFILE
    // ============================================================

    public void deleteHospitalProfile(
            String uid) {

        validateUid(uid);

        try {

            db.collection("hospitals")
                    .document(uid.trim())
                    .delete()
                    .get();

            System.out.println(
                    "Hospital profile deleted successfully."
            );

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to delete hospital profile.",
                    e
            );
        }
    }

    // ============================================================
    // MODEL -> FIRESTORE MAP
    // ============================================================

    private Map<String, Object> toMap(
            HospitalProfile hospitalProfile) {

        Map<String, Object> map =
                new HashMap<>();

        map.put(
                "uid",
                hospitalProfile.getUid()
        );

        map.put(
                "email",
                hospitalProfile.getEmail()
        );

        map.put(
                "hospitalName",
                hospitalProfile.getHospitalName()
        );

        map.put(
                "registrationNumber",
                hospitalProfile.getRegistrationNumber()
        );

        map.put(
                "hospitalType",
                hospitalProfile.getHospitalType()
        );

        map.put(
                "beds",
                hospitalProfile.getBeds()
        );

        map.put(
                "contact",
                hospitalProfile.getContact()
        );

        map.put(
                "address",
                hospitalProfile.getAddress()
        );

        map.put(
                "verificationStatus",
                hospitalProfile.getVerificationStatus()
        );

        map.put(
                "verifiedBy",
                hospitalProfile.getVerifiedBy()
        );

        if (hospitalProfile.getUpdatedAt() != null) {

            map.put(
                    "updatedAt",
                    Timestamp.ofTimeSecondsAndNanos(
                            hospitalProfile
                                    .getUpdatedAt()
                                    .getEpochSecond(),
                            hospitalProfile
                                    .getUpdatedAt()
                                    .getNano()
                    )
            );
        }

        return map;
    }

    // ============================================================
    // FIRESTORE -> MODEL
    // ============================================================

    private HospitalProfile fromDocument(
            DocumentSnapshot document) {

        if (document == null
                || !document.exists()) {

            return null;
        }

        HospitalProfile hospital =
                new HospitalProfile();

        hospital.setUid(
                document.getString("uid")
        );

        /*
         * Backward compatibility:
         * if uid field is missing, use document ID.
         */
        if (hospital.getUid() == null
                || hospital.getUid().isBlank()) {

            hospital.setUid(
                    document.getId()
            );
        }

        hospital.setEmail(
                document.getString("email")
        );

        hospital.setHospitalName(
                document.getString("hospitalName")
        );

        hospital.setRegistrationNumber(
                document.getString(
                        "registrationNumber"
                )
        );

        hospital.setHospitalType(
                document.getString("hospitalType")
        );

        hospital.setBeds(
                document.getString("beds")
        );

        hospital.setContact(
                document.getString("contact")
        );

        hospital.setAddress(
                document.getString("address")
        );

        hospital.setVerificationStatus(
                document.getString(
                        "verificationStatus"
                )
        );

        hospital.setVerifiedBy(
                document.getString(
                        "verifiedBy"
                )
        );

        Timestamp updatedTimestamp =
                document.getTimestamp("updatedAt");

        if (updatedTimestamp != null) {

            hospital.setUpdatedAt(
                    Instant.ofEpochSecond(
                            updatedTimestamp.getSeconds(),
                            updatedTimestamp.getNanos()
                    )
            );
        }

        return hospital;
    }

    // ============================================================
    // VALIDATION
    // ============================================================

    private void validateUid(
            String uid) {

        if (uid == null
                || uid.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Hospital UID is required."
            );
        }
    }
}