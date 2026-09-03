package com.healthsphere.controller.hospital;

import com.healthsphere.dao.hospital.BedDAO;
import com.healthsphere.exceptions.DatabaseException;
import com.healthsphere.model.HospitalBed;
import com.healthsphere.model.HospitalBed.BedStatus;

import java.util.List;

/**
 * Controller for Hospital Bed Management.
 *
 * Responsibilities:
 * - Validate UI input
 * - Coordinate between View and BedDAO
 * - Provide compatibility methods for existing JavaFX views
 *
 * Database operations are handled only by BedDAO.
 */
public class BedController {

    private final BedDAO bedDAO;

    public BedController() {
        this.bedDAO = new BedDAO();
    }

    // =========================================================
    // CREATE BED - MODEL VERSION
    // =========================================================

    public void createBed(
            HospitalBed bed
    ) {

        validateBed(bed);

        bedDAO.createBed(bed);
    }

    // =========================================================
    // CREATE BED - EXISTING UI COMPATIBILITY VERSION
    // =========================================================
    /*
     * Existing AddBedView expects:
     *
     * String result = bedController.createBed(...);
     *
     * Therefore this method MUST return String.
     */
    public String createBed(
            String bedNumber,
            String bedType,
            String wardId,
            String status
    ) {

        // -----------------------------------------------------
        // VALIDATE BED NUMBER
        // -----------------------------------------------------

        if (
                bedNumber == null ||
                bedNumber.trim().isEmpty()
        ) {

            throw new DatabaseException(
                    "Bed number is required."
            );
        }

        // -----------------------------------------------------
        // VALIDATE BED TYPE
        // -----------------------------------------------------

        if (
                bedType == null ||
                bedType.trim().isEmpty()
        ) {

            throw new DatabaseException(
                    "Bed type is required."
            );
        }

        // -----------------------------------------------------
        // VALIDATE STATUS
        // -----------------------------------------------------

        BedStatus bedStatus =
                parseStatus(status);

        // -----------------------------------------------------
        // GENERATE BED ID
        // -----------------------------------------------------

        String bedId =
                generateBedId(
                        bedNumber
                );

        // -----------------------------------------------------
        // CREATE MODEL
        // -----------------------------------------------------

        HospitalBed bed =
                new HospitalBed(
                        bedId,
                        null,
                        wardId,
                        bedNumber.trim(),
                        bedType.trim(),
                        bedStatus,
                        null,
                        true
                );

        // -----------------------------------------------------
        // SAVE THROUGH DAO
        // -----------------------------------------------------

        createBed(bed);

        // -----------------------------------------------------
        // RETURN BED ID
        // -----------------------------------------------------

        return bedId;
    }

    // =========================================================
    // GENERATE BED ID
    // =========================================================

    private String generateBedId(
            String bedNumber
    ) {

        /*
         * Firestore document ID.
         *
         * Timestamp guarantees that two consecutive
         * additions don't normally receive the same ID.
         */
        return "BED_"
                + System.currentTimeMillis()
                + "_"
                + Math.abs(
                        bedNumber.hashCode()
                );
    }

    // =========================================================
    // GET ALL BEDS
    // =========================================================

    public List<HospitalBed> getAllBeds() {

        return bedDAO.getAllBeds();
    }

    // =========================================================
    // GET ACTIVE BEDS
    // =========================================================

    public List<HospitalBed> getActiveBeds() {

        return bedDAO.getActiveBeds();
    }

    // =========================================================
    // GET BED BY ID
    // =========================================================

    public HospitalBed getBedById(
            String bedId
    ) {

        validateId(
                bedId,
                "Bed ID"
        );

        return bedDAO.getBedById(
                bedId
        );
    }

    // =========================================================
    // GET BEDS BY WARD
    // =========================================================

    public List<HospitalBed> getBedsByWard(
            String wardId
    ) {

        validateId(
                wardId,
                "Ward ID"
        );

        return bedDAO.getBedsByWard(
                wardId
        );
    }

    // =========================================================
    // UPDATE BED
    // =========================================================

    public void updateBed(
            HospitalBed bed
    ) {

        validateBed(bed);

        validateId(
                bed.getBedId(),
                "Bed ID"
        );

        bedDAO.updateBed(
                bed
        );
    }

    // =========================================================
    // UPDATE STATUS - ENUM
    // =========================================================

    public void updateBedStatus(
            String bedId,
            BedStatus status
    ) {

        validateId(
                bedId,
                "Bed ID"
        );

        if (status == null) {

            throw new DatabaseException(
                    "Bed status is required."
            );
        }

        bedDAO.updateBedStatus(
                bedId,
                status
        );
    }

    // =========================================================
    // UPDATE STATUS - STRING
    // =========================================================

    public void updateBedStatus(
            String bedId,
            String status
    ) {

        validateId(
                bedId,
                "Bed ID"
        );

        BedStatus parsedStatus =
                parseStatus(status);

        bedDAO.updateBedStatus(
                bedId,
                parsedStatus
        );
    }

    // =========================================================
    // OCCUPY BED
    // =========================================================

    public void occupyBed(
            String bedId,
            String patientId
    ) {

        validateId(
                bedId,
                "Bed ID"
        );

        validateId(
                patientId,
                "Patient ID"
        );

        bedDAO.occupyBed(
                bedId,
                patientId
        );
    }

    // =========================================================
    // RESERVE BED - WITH PATIENT
    // =========================================================

    public void reserveBed(
            String bedId,
            String patientId
    ) {

        validateId(
                bedId,
                "Bed ID"
        );

        validateId(
                patientId,
                "Patient ID"
        );

        bedDAO.reserveBed(
                bedId,
                patientId
        );
    }

    // =========================================================
    // RESERVE BED - EXISTING UI VERSION
    // =========================================================
    /*
     * Existing BedManagementView uses:
     *
     * controller.reserveBed(bedId);
     */
    public void reserveBed(
            String bedId
    ) {

        validateId(
                bedId,
                "Bed ID"
        );

        bedDAO.reserveBed(
                bedId
        );
    }

    // =========================================================
    // RELEASE BED
    // =========================================================

    public void releaseBed(
            String bedId
    ) {

        validateId(
                bedId,
                "Bed ID"
        );

        bedDAO.releaseBed(
                bedId
        );
    }

    // =========================================================
    // SET MAINTENANCE
    // =========================================================

    public void setMaintenance(
            String bedId
    ) {

        validateId(
                bedId,
                "Bed ID"
        );

        bedDAO.setMaintenance(
                bedId
        );
    }

    // =========================================================
    // ACTIVATE BED
    // =========================================================

    public void activateBed(
            String bedId
    ) {

        validateId(
                bedId,
                "Bed ID"
        );

        bedDAO.activateBed(
                bedId
        );
    }

    // =========================================================
    // DEACTIVATE BED
    // =========================================================

    public void deactivateBed(
            String bedId
    ) {

        validateId(
                bedId,
                "Bed ID"
        );

        bedDAO.deactivateBed(
                bedId
        );
    }

    // =========================================================
    // CHECK BED EXISTS
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

        return bedDAO.bedExists(
                bedId
        );
    }

    // =========================================================
    // TOTAL BEDS
    // =========================================================

    public int getTotalBeds() {

        return bedDAO.getTotalBeds();
    }

    // =========================================================
    // AVAILABLE BEDS
    // =========================================================

    public int getAvailableBeds() {

        return bedDAO.getAvailableBeds();
    }

    // =========================================================
    // OCCUPIED BEDS
    // =========================================================

    public int getOccupiedBeds() {

        return bedDAO.getOccupiedBeds();
    }

    // =========================================================
    // RESERVED BEDS
    // =========================================================

    public int getReservedBeds() {

        return bedDAO.getReservedBeds();
    }

    // =========================================================
    // OCCUPANCY PERCENTAGE
    // =========================================================

    public double getOccupancyPercentage() {

        int total =
                getTotalBeds();

        if (total <= 0) {

            return 0.0;
        }

        int occupied =
                getOccupiedBeds();

        return (
                (double) occupied
                /
                total
        ) * 100.0;
    }

    // =========================================================
    // VALIDATE BED
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

    // =========================================================
    // VALIDATE ID
    // =========================================================

    private void validateId(
            String id,
            String fieldName
    ) {

        if (
                id == null ||
                id.trim().isEmpty()
        ) {

            throw new DatabaseException(
                    fieldName
                            + " is required."
            );
        }
    }

    // =========================================================
    // PARSE STATUS
    // =========================================================

    private BedStatus parseStatus(
            String status
    ) {

        /*
         * If AddBedView does not provide a status,
         * new beds are AVAILABLE by default.
         */
        if (
                status == null ||
                status.trim().isEmpty()
        ) {

            return BedStatus.AVAILABLE;
        }

        try {

            return BedStatus.valueOf(
                    status.trim()
                            .toUpperCase()
            );

        } catch (
                IllegalArgumentException e
        ) {

            throw new DatabaseException(
                    "Invalid bed status: "
                            + status
            );
        }
    }
}