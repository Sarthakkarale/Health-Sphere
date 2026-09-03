package com.healthsphere.dao.hospital;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.healthsphere.config.FirebaseConfig;
import com.healthsphere.exceptions.DatabaseException;
import com.healthsphere.model.HospitalBed;
import com.healthsphere.model.HospitalBed.BedStatus;
import com.healthsphere.util.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BedDAO {

    private static final String COLLECTION =
            "hospitalBeds";

    private final Firestore db;

    public BedDAO() {
        db = FirebaseConfig.getFirestore();
    }

    // =========================================================
    // HOSPITAL ID
    // =========================================================

    private String getHospitalId() {

        if (!SessionManager.isLoggedIn()) {
            throw new DatabaseException(
                    "No authenticated hospital session found."
            );
        }

        if (SessionManager.getCurrentUser() == null) {
            throw new DatabaseException(
                    "Current user session is unavailable."
            );
        }

        String hospitalId =
                SessionManager.getCurrentUser().getUid();

        if (
                hospitalId == null ||
                hospitalId.trim().isEmpty()
        ) {
            throw new DatabaseException(
                    "Hospital ID is unavailable."
            );
        }

        return hospitalId.trim();
    }

    // =========================================================
    // CREATE BED
    // =========================================================

    public void createBed(HospitalBed bed) {

        validateBed(bed);

        try {

            String hospitalId =
                    getHospitalId();

            bed.setHospitalId(hospitalId);

            String bedId =
                    bed.getBedId();

            if (
                    bedId == null ||
                    bedId.trim().isEmpty()
            ) {

                throw new DatabaseException(
                        "Bed ID is required."
                );
            }

            if (bedExists(bedId)) {

                throw new DatabaseException(
                        "Bed already exists."
                );
            }

            Map<String, Object> data =
                    toMap(bed);

            db.collection(COLLECTION)
                    .document(bedId.trim())
                    .set(data)
                    .get();

        } catch (DatabaseException e) {

            throw e;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to create bed.",
                    e
            );
        }
    }

    // =========================================================
    // GET ALL BEDS
    // =========================================================

    public List<HospitalBed> getAllBeds() {

        try {

            String hospitalId =
                    getHospitalId();

            QuerySnapshot snapshot =
                    db.collection(COLLECTION)
                            .whereEqualTo(
                                    "hospitalId",
                                    hospitalId
                            )
                            .get()
                            .get();

            List<HospitalBed> beds =
                    new ArrayList<>();

            for (
                    QueryDocumentSnapshot document :
                    snapshot.getDocuments()
            ) {

                HospitalBed bed =
                        document.toObject(
                                HospitalBed.class
                        );

                if (bed != null) {
                    beds.add(bed);
                }
            }

            return beds;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to retrieve hospital beds.",
                    e
            );
        }
    }

    // =========================================================
    // ACTIVE BEDS
    // =========================================================

    public List<HospitalBed> getActiveBeds() {

        try {

            String hospitalId =
                    getHospitalId();

            QuerySnapshot snapshot =
                    db.collection(COLLECTION)
                            .whereEqualTo(
                                    "hospitalId",
                                    hospitalId
                            )
                            .whereEqualTo(
                                    "active",
                                    true
                            )
                            .get()
                            .get();

            List<HospitalBed> beds =
                    new ArrayList<>();

            for (
                    QueryDocumentSnapshot document :
                    snapshot.getDocuments()
            ) {

                HospitalBed bed =
                        document.toObject(
                                HospitalBed.class
                        );

                if (bed != null) {
                    beds.add(bed);
                }
            }

            return beds;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to retrieve active beds.",
                    e
            );
        }
    }

    // =========================================================
    // GET BED
    // =========================================================

    public HospitalBed getBedById(
            String bedId
    ) {

        if (
                bedId == null ||
                bedId.trim().isEmpty()
        ) {
            throw new DatabaseException(
                    "Bed ID is required."
            );
        }

        try {

            String hospitalId =
                    getHospitalId();

            DocumentSnapshot document =
                    db.collection(COLLECTION)
                            .document(bedId.trim())
                            .get()
                            .get();

            if (!document.exists()) {

                throw new DatabaseException(
                        "Bed not found."
                );
            }

            HospitalBed bed =
                    document.toObject(
                            HospitalBed.class
                    );

            if (bed == null) {

                throw new DatabaseException(
                        "Unable to read bed."
                );
            }

            if (
                    !hospitalId.equals(
                            bed.getHospitalId()
                    )
            ) {

                throw new DatabaseException(
                        "You are not authorized to access this bed."
                );
            }

            return bed;

        } catch (DatabaseException e) {

            throw e;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to retrieve bed.",
                    e
            );
        }
    }

    // =========================================================
    // GET BEDS BY WARD
    // =========================================================

    public List<HospitalBed> getBedsByWard(
            String wardId
    ) {

        if (
                wardId == null ||
                wardId.trim().isEmpty()
        ) {

            throw new DatabaseException(
                    "Ward ID is required."
            );
        }

        try {

            String hospitalId =
                    getHospitalId();

            QuerySnapshot snapshot =
                    db.collection(COLLECTION)
                            .whereEqualTo(
                                    "hospitalId",
                                    hospitalId
                            )
                            .whereEqualTo(
                                    "wardId",
                                    wardId.trim()
                            )
                            .get()
                            .get();

            List<HospitalBed> beds =
                    new ArrayList<>();

            for (
                    QueryDocumentSnapshot document :
                    snapshot.getDocuments()
            ) {

                HospitalBed bed =
                        document.toObject(
                                HospitalBed.class
                        );

                if (bed != null) {
                    beds.add(bed);
                }
            }

            return beds;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to retrieve ward beds.",
                    e
            );
        }
    }

    // =========================================================
    // UPDATE BED
    // =========================================================

    public void updateBed(
            HospitalBed bed
    ) {

        validateBed(bed);

        if (
                bed.getBedId() == null ||
                bed.getBedId().trim().isEmpty()
        ) {

            throw new DatabaseException(
                    "Bed ID is required."
            );
        }

        try {

            HospitalBed existing =
                    getBedById(
                            bed.getBedId()
                    );

            bed.setHospitalId(
                    existing.getHospitalId()
            );

            db.collection(COLLECTION)
                    .document(
                            bed.getBedId()
                    )
                    .set(
                            toMap(bed)
                    )
                    .get();

        } catch (DatabaseException e) {

            throw e;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to update bed.",
                    e
            );
        }
    }

    // =========================================================
    // UPDATE STATUS
    // =========================================================

    public void updateBedStatus(
            String bedId,
            BedStatus status
    ) {

        if (status == null) {

            throw new DatabaseException(
                    "Bed status is required."
            );
        }

        try {

            getBedById(bedId);

            Map<String, Object> updates =
                    new HashMap<>();

            updates.put(
                    "status",
                    status.name()
            );

            if (
                    status == BedStatus.AVAILABLE
                    ||
                    status == BedStatus.MAINTENANCE
            ) {

                updates.put(
                        "patientId",
                        null
                );
            }

            if (
                    status == BedStatus.MAINTENANCE
            ) {

                updates.put(
                        "active",
                        false
                );
            }

            db.collection(COLLECTION)
                    .document(bedId)
                    .update(updates)
                    .get();

        } catch (DatabaseException e) {

            throw e;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to update bed status.",
                    e
            );
        }
    }

    // =========================================================
    // STRING STATUS OVERLOAD
    // =========================================================

    public void updateBedStatus(
            String bedId,
            String status
    ) {

        updateBedStatus(
                bedId,
                parseStatus(status)
        );
    }

    // =========================================================
    // OCCUPY BED
    // =========================================================

    public void occupyBed(
            String bedId,
            String patientId
    ) {

        if (
                patientId == null ||
                patientId.trim().isEmpty()
        ) {

            throw new DatabaseException(
                    "Patient ID is required."
            );
        }

        try {

            HospitalBed bed =
                    getBedById(bedId);

            if (!bed.isActive()) {

                throw new DatabaseException(
                        "Inactive bed cannot be occupied."
                );
            }

            if (
                    bed.getStatus()
                            == BedStatus.OCCUPIED
            ) {

                throw new DatabaseException(
                        "Bed is already occupied."
                );
            }

            Map<String, Object> updates =
                    new HashMap<>();

            updates.put(
                    "status",
                    BedStatus.OCCUPIED.name()
            );

            updates.put(
                    "patientId",
                    patientId.trim()
            );

            updates.put(
                    "active",
                    true
            );

            db.collection(COLLECTION)
                    .document(bedId)
                    .update(updates)
                    .get();

        } catch (DatabaseException e) {

            throw e;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to occupy bed.",
                    e
            );
        }
    }

    // =========================================================
    // RESERVE BED
    // =========================================================

    public void reserveBed(
            String bedId,
            String patientId
    ) {

        if (
                patientId == null ||
                patientId.trim().isEmpty()
        ) {

            throw new DatabaseException(
                    "Patient ID is required."
            );
        }

        try {

            HospitalBed bed =
                    getBedById(bedId);

            if (!bed.isActive()) {

                throw new DatabaseException(
                        "Inactive bed cannot be reserved."
                );
            }

            if (
                    bed.getStatus()
                            != BedStatus.AVAILABLE
            ) {

                throw new DatabaseException(
                        "Only available beds can be reserved."
                );
            }

            Map<String, Object> updates =
                    new HashMap<>();

            updates.put(
                    "status",
                    BedStatus.RESERVED.name()
            );

            updates.put(
                    "patientId",
                    patientId.trim()
            );

            db.collection(COLLECTION)
                    .document(bedId)
                    .update(updates)
                    .get();

        } catch (DatabaseException e) {

            throw e;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to reserve bed.",
                    e
            );
        }
    }

    // =========================================================
    // RESERVE WITHOUT PATIENT
    // =========================================================

    public void reserveBed(
            String bedId
    ) {

        try {

            HospitalBed bed =
                    getBedById(bedId);

            if (!bed.isActive()) {

                throw new DatabaseException(
                        "Inactive bed cannot be reserved."
                );
            }

            if (
                    bed.getStatus()
                            != BedStatus.AVAILABLE
            ) {

                throw new DatabaseException(
                        "Only available beds can be reserved."
                );
            }

            Map<String, Object> updates =
                    new HashMap<>();

            updates.put(
                    "status",
                    BedStatus.RESERVED.name()
            );

            db.collection(COLLECTION)
                    .document(bedId)
                    .update(updates)
                    .get();

        } catch (DatabaseException e) {

            throw e;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to reserve bed.",
                    e
            );
        }
    }

    // =========================================================
    // RELEASE BED
    // =========================================================

    public void releaseBed(
            String bedId
    ) {

        try {

            getBedById(bedId);

            Map<String, Object> updates =
                    new HashMap<>();

            updates.put(
                    "status",
                    BedStatus.AVAILABLE.name()
            );

            updates.put(
                    "patientId",
                    null
            );

            updates.put(
                    "active",
                    true
            );

            db.collection(COLLECTION)
                    .document(bedId)
                    .update(updates)
                    .get();

        } catch (DatabaseException e) {

            throw e;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to release bed.",
                    e
            );
        }
    }

    // =========================================================
    // MAINTENANCE
    // =========================================================

    public void setMaintenance(
            String bedId
    ) {

        try {

            getBedById(bedId);

            Map<String, Object> updates =
                    new HashMap<>();

            updates.put(
                    "status",
                    BedStatus.MAINTENANCE.name()
            );

            updates.put(
                    "patientId",
                    null
            );

            updates.put(
                    "active",
                    false
            );

            db.collection(COLLECTION)
                    .document(bedId)
                    .update(updates)
                    .get();

        } catch (DatabaseException e) {

            throw e;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to place bed under maintenance.",
                    e
            );
        }
    }

    // =========================================================
    // ACTIVATE
    // =========================================================

    public void activateBed(
            String bedId
    ) {

        try {

            getBedById(bedId);

            Map<String, Object> updates =
                    new HashMap<>();

            updates.put(
                    "active",
                    true
            );

            updates.put(
                    "status",
                    BedStatus.AVAILABLE.name()
            );

            updates.put(
                    "patientId",
                    null
            );

            db.collection(COLLECTION)
                    .document(bedId)
                    .update(updates)
                    .get();

        } catch (DatabaseException e) {

            throw e;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to activate bed.",
                    e
            );
        }
    }

    // =========================================================
    // DEACTIVATE
    // =========================================================

    public void deactivateBed(
            String bedId
    ) {

        try {

            getBedById(bedId);

            Map<String, Object> updates =
                    new HashMap<>();

            updates.put(
                    "active",
                    false
            );

            updates.put(
                    "status",
                    BedStatus.MAINTENANCE.name()
            );

            updates.put(
                    "patientId",
                    null
            );

            db.collection(COLLECTION)
                    .document(bedId)
                    .update(updates)
                    .get();

        } catch (DatabaseException e) {

            throw e;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to deactivate bed.",
                    e
            );
        }
    }

    // =========================================================
    // EXISTS
    // =========================================================

    public boolean bedExists(
            String bedId
    ) {

        if (
                bedId == null ||
                bedId.trim().isEmpty()
        ) {

            return false;
        }

        try {

            String hospitalId =
                    getHospitalId();

            DocumentSnapshot document =
                    db.collection(COLLECTION)
                            .document(
                                    bedId.trim()
                            )
                            .get()
                            .get();

            if (!document.exists()) {
                return false;
            }

            String documentHospitalId =
                    document.getString(
                            "hospitalId"
                    );

            return hospitalId.equals(
                    documentHospitalId
            );

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to check bed.",
                    e
            );
        }
    }

    // =========================================================
    // DASHBOARD COUNTS
    // =========================================================

    public int getTotalBeds() {

        return getActiveBeds().size();
    }

    public int getAvailableBeds() {

        int count = 0;

        for (
                HospitalBed bed :
                getActiveBeds()
        ) {

            if (
                    bed.getStatus()
                            == BedStatus.AVAILABLE
            ) {

                count++;
            }
        }

        return count;
    }

    public int getOccupiedBeds() {

        int count = 0;

        for (
                HospitalBed bed :
                getActiveBeds()
        ) {

            if (
                    bed.getStatus()
                            == BedStatus.OCCUPIED
            ) {

                count++;
            }
        }

        return count;
    }

    public int getReservedBeds() {

        int count = 0;

        for (
                HospitalBed bed :
                getActiveBeds()
        ) {

            if (
                    bed.getStatus()
                            == BedStatus.RESERVED
            ) {

                count++;
            }
        }

        return count;
    }

    // =========================================================
    // MAP
    // =========================================================

    private Map<String, Object> toMap(
            HospitalBed bed
    ) {

        Map<String, Object> data =
                new HashMap<>();

        data.put(
                "bedId",
                bed.getBedId()
        );

        data.put(
                "hospitalId",
                bed.getHospitalId()
        );

        data.put(
                "wardId",
                bed.getWardId()
        );

        data.put(
                "bedNumber",
                bed.getBedNumber()
        );

        data.put(
                "bedType",
                bed.getBedType()
        );

        data.put(
                "status",
                bed.getStatus() == null
                        ? null
                        : bed.getStatus().name()
        );

        data.put(
                "patientId",
                bed.getPatientId()
        );

        data.put(
                "active",
                bed.isActive()
        );

        return data;
    }

    // =========================================================
    // VALIDATION
    // =========================================================

    private void validateBed(
            HospitalBed bed
    ) {

        if (bed == null) {

            throw new DatabaseException(
                    "Bed information is required."
            );
        }

        if (
                bed.getBedNumber() == null ||
                bed.getBedNumber()
                        .trim()
                        .isEmpty()
        ) {

            throw new DatabaseException(
                    "Bed number is required."
            );
        }

        if (
                bed.getBedType() == null ||
                bed.getBedType()
                        .trim()
                        .isEmpty()
        ) {

            throw new DatabaseException(
                    "Bed type is required."
            );
        }

        if (bed.getStatus() == null) {

            throw new DatabaseException(
                    "Bed status is required."
            );
        }
    }

    private BedStatus parseStatus(
            String status
    ) {

        if (
                status == null ||
                status.trim().isEmpty()
        ) {

            throw new DatabaseException(
                    "Bed status is required."
            );
        }

        try {

            return BedStatus.valueOf(
                    status.trim().toUpperCase()
            );

        } catch (IllegalArgumentException e) {

            throw new DatabaseException(
                    "Invalid bed status: "
                            + status
            );
        }
    }
}