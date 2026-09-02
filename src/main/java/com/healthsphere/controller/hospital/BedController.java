package com.healthsphere.controller.hospital;

import com.healthsphere.dao.hospital.BedDAO;
import com.healthsphere.model.HospitalBed;
import com.healthsphere.model.HospitalBed.BedStatus;

import java.util.List;

/**
 * Controller responsible for hospital bed operations.
 *
 * Architecture:
 *
 * View
 *   ↓
 * BedController
 *   ↓
 * BedDAO
 *   ↓
 * Firestore
 */
public class BedController {

    // =========================================================
    // DAO
    // =========================================================

    private final BedDAO bedDAO;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public BedController() {

        this.bedDAO = new BedDAO();
    }

    // =========================================================
    // CREATE BED
    // =========================================================

    /**
     * Creates a new hospital bed.
     *
     * @param wardId ward to which the bed belongs
     * @param bedNumber bed number / identifier
     * @param bedType type of bed
     * @param status initial bed status
     * @return generated bed ID
     */
    public String createBed(
            String wardId,
            String bedNumber,
            String bedType,
            String status) {

        validateCreateBedInput(
                wardId,
                bedNumber,
                bedType
        );

        BedStatus parsedStatus =
                parseStatus(status);

        /*
         * The Add Bed screen does not provide a patient
         * selector, therefore a bed cannot initially be
         * created as OCCUPIED.
         */
        if (parsedStatus == BedStatus.OCCUPIED) {

            throw new IllegalArgumentException(
                    "A new bed cannot be created as OCCUPIED. "
                            + "Create the bed as AVAILABLE or RESERVED, "
                            + "then assign a patient."
            );
        }

        HospitalBed bed =
                new HospitalBed();

        bed.setWardId(
                wardId.trim()
        );

        bed.setBedNumber(
                bedNumber.trim()
        );

        bed.setBedType(
                bedType.trim()
        );

        bed.setStatus(
                parsedStatus
        );

        bed.setPatientId(
                null
        );

        bed.setActive(
                true
        );

        return bedDAO.createBed(
                bed
        );
    }

    // =========================================================
    // GET ALL BEDS
    // =========================================================

    /**
     * Returns all active beds belonging to the
     * currently logged-in hospital.
     */
    public List<HospitalBed> getAllBeds() {

        return bedDAO.getAllBeds();
    }

    // =========================================================
    // GET BED BY ID
    // =========================================================

    /**
     * Gets one bed by its ID.
     */
    public HospitalBed getBedById(
            String bedId) {

        if (bedId == null
                || bedId.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Bed ID is required."
            );
        }

        return bedDAO.getBedById(
                bedId.trim()
        );
    }

    // =========================================================
    // GET BEDS BY WARD
    // =========================================================

    /**
     * Returns all active beds belonging to
     * a specific ward.
     */
    public List<HospitalBed> getBedsByWard(
            String wardId) {

        if (wardId == null
                || wardId.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Ward ID is required."
            );
        }

        return bedDAO.getBedsByWard(
                wardId.trim()
        );
    }

    // =========================================================
    // UPDATE BED
    // =========================================================

    /**
     * Updates bed information.
     *
     * Note:
     * Status OCCUPIED requires a patient ID.
     * For normal status changes use updateBedStatus(),
     * occupyBed(), reserveBed(), etc.
     */
    public void updateBed(
            String bedId,
            String wardId,
            String bedNumber,
            String bedType,
            String status,
            String patientId) {

        // -----------------------------------------------------
        // Validate ID
        // -----------------------------------------------------

        if (bedId == null
                || bedId.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Bed ID is required."
            );
        }

        // -----------------------------------------------------
        // Validate input
        // -----------------------------------------------------

        validateCreateBedInput(
                wardId,
                bedNumber,
                bedType
        );

        BedStatus parsedStatus =
                parseStatus(status);

        // -----------------------------------------------------
        // Occupied validation
        // -----------------------------------------------------

        if (parsedStatus == BedStatus.OCCUPIED) {

            if (patientId == null
                    || patientId.trim().isEmpty()) {

                throw new IllegalArgumentException(
                        "Patient ID is required for an occupied bed."
                );
            }
        }

        // -----------------------------------------------------
        // Create model
        // -----------------------------------------------------

        HospitalBed bed =
                bedDAO.getBedById(
                        bedId.trim()
                );

        if (bed == null) {

            throw new IllegalArgumentException(
                    "Bed does not exist."
            );
        }

        bed.setWardId(
                wardId.trim()
        );

        bed.setBedNumber(
                bedNumber.trim()
        );

        bed.setBedType(
                bedType.trim()
        );

        bed.setStatus(
                parsedStatus
        );

        if (parsedStatus == BedStatus.OCCUPIED) {

            bed.setPatientId(
                    patientId.trim()
            );

        } else {

            bed.setPatientId(
                    null
            );
        }

        // -----------------------------------------------------
        // Save
        // -----------------------------------------------------

        bedDAO.updateBed(
                bed
        );
    }

    // =========================================================
    // UPDATE BED STATUS
    // =========================================================

    /**
     * Updates the status of a bed.
     *
     * OCCUPIED is not allowed through this method because
     * an occupied bed must have a patient ID.
     *
     * Use occupyBed() instead.
     */
    public void updateBedStatus(
            String bedId,
            String status) {

        if (bedId == null
                || bedId.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Bed ID is required."
            );
        }

        BedStatus parsedStatus =
                parseStatus(status);

        if (parsedStatus == BedStatus.OCCUPIED) {

            throw new IllegalArgumentException(
                    "Use occupyBed() when setting a bed to OCCUPIED."
            );
        }

        bedDAO.updateBedStatus(
                bedId.trim(),
                parsedStatus
        );
    }

    // =========================================================
    // OCCUPY BED
    // =========================================================

    /**
     * Occupies a bed and assigns it to a patient.
     */
    public void occupyBed(
            String bedId,
            String patientId) {

        if (bedId == null
                || bedId.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Bed ID is required."
            );
        }

        if (patientId == null
                || patientId.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Patient ID is required."
            );
        }

        bedDAO.occupyBed(
                bedId.trim(),
                patientId.trim()
        );
    }

    // =========================================================
    // RELEASE BED
    // =========================================================

    /**
     * Releases an occupied bed and makes it available.
     */
    public void releaseBed(
            String bedId) {

        if (bedId == null
                || bedId.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Bed ID is required."
            );
        }

        bedDAO.releaseBed(
                bedId.trim()
        );
    }

    // =========================================================
    // RESERVE BED
    // =========================================================

    /**
     * Reserves a bed.
     */
    public void reserveBed(
            String bedId) {

        if (bedId == null
                || bedId.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Bed ID is required."
            );
        }

        bedDAO.reserveBed(
                bedId.trim()
        );
    }

    // =========================================================
    // SET MAINTENANCE
    // =========================================================

    /**
     * Places a bed under maintenance.
     */
    public void setMaintenance(
            String bedId) {

        if (bedId == null
                || bedId.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Bed ID is required."
            );
        }

        bedDAO.setMaintenance(
                bedId.trim()
        );
    }

    // =========================================================
    // DEACTIVATE BED
    // =========================================================

    /**
     * Soft deletes/deactivates a bed.
     */
    public void deactivateBed(
            String bedId) {

        if (bedId == null
                || bedId.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Bed ID is required."
            );
        }

        bedDAO.deactivateBed(
                bedId.trim()
        );
    }

    // =========================================================
    // CHECK BED EXISTS
    // =========================================================

    public boolean bedExists(
            String bedId) {

        if (bedId == null
                || bedId.trim().isEmpty()) {

            return false;
        }

        return bedDAO.bedExists(
                bedId.trim()
        );
    }

    // =========================================================
    // PARSE STATUS
    // =========================================================

    /**
     * Converts UI status text into BedStatus enum.
     *
     * Supports:
     *
     * Available
     * Occupied
     * Reserved
     * Maintenance
     *
     * Also accepts enum-style values such as:
     *
     * AVAILABLE
     * OCCUPIED
     * RESERVED
     * MAINTENANCE
     */
    public BedStatus parseStatus(
            String status) {

        if (status == null
                || status.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Bed status is required."
            );
        }

        String normalized =
                status.trim()
                        .toUpperCase()
                        .replace(
                                " ",
                                "_"
                        )
                        .replace(
                                "-",
                                "_"
                        );

        switch (normalized) {

            case "AVAILABLE":

                return BedStatus.AVAILABLE;

            case "OCCUPIED":

                return BedStatus.OCCUPIED;

            case "RESERVED":

                return BedStatus.RESERVED;

            case "MAINTENANCE":

                return BedStatus.MAINTENANCE;

            default:

                throw new IllegalArgumentException(
                        "Invalid bed status: "
                                + status
                                + ". Valid statuses are "
                                + "Available, Occupied, Reserved and Maintenance."
                );
        }
    }

    // =========================================================
    // VALIDATE CREATE BED INPUT
    // =========================================================

    private void validateCreateBedInput(
            String wardId,
            String bedNumber,
            String bedType) {

        if (wardId == null
                || wardId.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Ward ID is required."
            );
        }

        if (bedNumber == null
                || bedNumber.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Bed number is required."
            );
        }

        if (bedType == null
                || bedType.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Bed type is required."
            );
        }
    }
}