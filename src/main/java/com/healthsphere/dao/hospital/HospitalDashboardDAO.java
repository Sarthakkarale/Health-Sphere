package com.healthsphere.dao.hospital;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.healthsphere.config.FirebaseConfig;
import com.healthsphere.exceptions.DatabaseException;
import com.healthsphere.model.Appointment;
import com.healthsphere.model.DashboardStats;
import com.healthsphere.util.SessionManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for the Hospital Dashboard.
 *
 * Firestore collections used:
 *
 * 1. hospitalDoctors
 * 2. appointments
 * 3. hospitalBeds
 * 4. emergencyCases
 *
 * Firebase remains the persistent source of truth.
 */
public class HospitalDashboardDAO {

    private static final String DOCTOR_COLLECTION =
            "hospitalDoctors";

    private static final String APPOINTMENT_COLLECTION =
            "appointments";

    private static final String BED_COLLECTION =
            "hospitalBeds";

    private static final String EMERGENCY_COLLECTION =
            "emergencyCases";

    private final Firestore db;

    public HospitalDashboardDAO() {
        this.db = FirebaseConfig.getFirestore();
    }

    // =========================================================
    // GET COMPLETE DASHBOARD STATS
    // =========================================================

    public DashboardStats getDashboardStats() {

        String hospitalId =
                getCurrentHospitalId();

        DashboardStats stats =
                new DashboardStats();

        /*
         * Every section is loaded independently.
         *
         * If one optional backend such as emergencyCases
         * does not exist yet, the remaining dashboard data
         * should still be displayed.
         */

        loadDoctorStats(
                stats,
                hospitalId
        );

        loadAppointmentStats(
                stats,
                hospitalId
        );

        loadBedStats(
                stats,
                hospitalId
        );

        loadEmergencyStats(
                stats,
                hospitalId
        );

        return stats;
    }

    // =========================================================
    // CURRENT HOSPITAL
    // =========================================================

    private String getCurrentHospitalId() {

        if (!SessionManager.isLoggedIn()) {

            throw new DatabaseException(
                    "No authenticated hospital session found."
            );
        }

        if (
                SessionManager.getCurrentUser() == null
        ) {

            throw new DatabaseException(
                    "Current user session is unavailable."
            );
        }

        String hospitalId =
                SessionManager
                        .getCurrentUser()
                        .getUid();

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
    // DOCTOR STATISTICS
    // =========================================================

    private void loadDoctorStats(
            DashboardStats stats,
            String hospitalId
    ) {

        try {

            QuerySnapshot snapshot =
                    db.collection(
                            DOCTOR_COLLECTION
                    )
                    .whereEqualTo(
                            "hospitalId",
                            hospitalId
                    )
                    .get()
                    .get();

            int totalDoctors = 0;

            for (
                    QueryDocumentSnapshot document :
                    snapshot.getDocuments()
            ) {

                if (
                        document != null &&
                        document.exists()
                ) {

                    /*
                     * Count active doctors.
                     *
                     * If the document does not contain
                     * active, it is counted as active for
                     * compatibility with older records.
                     */
                    Object activeValue =
                            document.get(
                                    "active"
                            );

                    if (
                            activeValue == null
                    ) {

                        totalDoctors++;

                    } else if (
                            Boolean.TRUE.equals(
                                    activeValue
                            )
                    ) {

                        totalDoctors++;
                    }
                }
            }

            stats.setTotalDoctors(
                    totalDoctors
            );

        } catch (Exception e) {

            /*
             * Do not crash the entire dashboard because
             * the doctor backend has a problem.
             */
            stats.setTotalDoctors(0);
        }
    }

    // =========================================================
    // APPOINTMENT STATISTICS
    // =========================================================

    private void loadAppointmentStats(
            DashboardStats stats,
            String hospitalId
    ) {

        try {

            QuerySnapshot snapshot =
                    db.collection(
                            APPOINTMENT_COLLECTION
                    )
                    .whereEqualTo(
                            "hospitalId",
                            hospitalId
                    )
                    .get()
                    .get();

            int total =
                    0;

            int today =
                    0;

            int completed =
                    0;

            int waiting =
                    0;

            int upcoming =
                    0;

            int cancelled =
                    0;

            LocalDate currentDate =
                    LocalDate.now();

            for (
                    QueryDocumentSnapshot document :
                    snapshot.getDocuments()
            ) {

                if (
                        document == null ||
                        !document.exists()
                ) {

                    continue;
                }

                total++;

                String status =
                        getString(
                                document,
                                "status"
                        );

                String appointmentDate =
                        getString(
                                document,
                                "appointmentDate"
                        );

                // ---------------------------------------------
                // TODAY
                // ---------------------------------------------

                if (
                        isToday(
                                appointmentDate,
                                currentDate
                        )
                ) {

                    /*
                     * Cancelled appointments should not be
                     * considered scheduled for today.
                     */
                    if (
                            !isStatus(
                                    status,
                                    "CANCELLED"
                            )
                            &&
                            !isStatus(
                                    status,
                                    "REJECTED"
                            )
                    ) {

                        today++;
                    }
                }

                // ---------------------------------------------
                // COMPLETED
                // ---------------------------------------------

                if (
                        isStatus(
                                status,
                                "COMPLETED"
                        )
                ) {

                    completed++;
                }

                // ---------------------------------------------
                // CANCELLED
                // ---------------------------------------------

                else if (
                        isStatus(
                                status,
                                "CANCELLED"
                        )
                        ||
                        isStatus(
                                status,
                                "REJECTED"
                        )
                ) {

                    cancelled++;
                }

                // ---------------------------------------------
                // WAITING
                // ---------------------------------------------

                else if (
                        isStatus(
                                status,
                                "WAITING"
                        )
                        ||
                        isStatus(
                                status,
                                "PENDING"
                        )
                        ||
                        isStatus(
                                status,
                                "PENDING_ASSIGNMENT"
                        )
                ) {

                    waiting++;
                }

                // ---------------------------------------------
                // UPCOMING
                // ---------------------------------------------

                else if (
                        isStatus(
                                status,
                                "CONFIRMED"
                        )
                        ||
                        isStatus(
                                status,
                                "ACCEPTED"
                        )
                        ||
                        isStatus(
                                status,
                                "UPCOMING"
                        )
                ) {

                    upcoming++;
                }
            }

            stats.setTotalAppointments(
                    total
            );

            stats.setTodayAppointments(
                    today
            );

            stats.setCompletedAppointments(
                    completed
            );

            stats.setWaitingAppointments(
                    waiting
            );

            stats.setUpcomingAppointments(
                    upcoming
            );

            stats.setCancelledAppointments(
                    cancelled
            );

        } catch (Exception e) {

            /*
             * Set safe values instead of breaking the
             * dashboard UI.
             */
            stats.setTotalAppointments(0);
            stats.setTodayAppointments(0);
            stats.setCompletedAppointments(0);
            stats.setWaitingAppointments(0);
            stats.setUpcomingAppointments(0);
            stats.setCancelledAppointments(0);
        }
    }

    // =========================================================
    // BED STATISTICS
    // =========================================================

    private void loadBedStats(
            DashboardStats stats,
            String hospitalId
    ) {

        try {

            QuerySnapshot snapshot =
                    db.collection(
                            BED_COLLECTION
                    )
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

            int total =
                    0;

            int available =
                    0;

            int occupied =
                    0;

            int reserved =
                    0;

            for (
                    QueryDocumentSnapshot document :
                    snapshot.getDocuments()
            ) {

                if (
                        document == null ||
                        !document.exists()
                ) {

                    continue;
                }

                total++;

                String status =
                        getString(
                                document,
                                "status"
                        );

                if (status == null) {
                    continue;
                }

                status =
                        status.trim()
                                .toUpperCase();

                switch (status) {

                    case "AVAILABLE":

                        available++;
                        break;

                    case "OCCUPIED":

                        occupied++;
                        break;

                    case "RESERVED":

                        reserved++;
                        break;

                    default:

                        /*
                         * MAINTENANCE or unknown status
                         * is not counted as available.
                         */
                        break;
                }
            }

            double occupancyPercentage =
                    calculateOccupancy(
                            total,
                            occupied
                    );

            stats.setTotalBeds(
                    total
            );

            stats.setAvailableBeds(
                    available
            );

            stats.setOccupiedBeds(
                    occupied
            );

            stats.setReservedBeds(
                    reserved
            );

            stats.setOccupancyPercentage(
                    occupancyPercentage
            );

        } catch (Exception e) {

            stats.setTotalBeds(0);
            stats.setAvailableBeds(0);
            stats.setOccupiedBeds(0);
            stats.setReservedBeds(0);
            stats.setOccupancyPercentage(0.0);
        }
    }

    // =========================================================
    // EMERGENCY STATISTICS
    // =========================================================

    private void loadEmergencyStats(
            DashboardStats stats,
            String hospitalId
    ) {

        try {

            QuerySnapshot snapshot =
                    db.collection(
                            EMERGENCY_COLLECTION
                    )
                    .whereEqualTo(
                            "hospitalId",
                            hospitalId
                    )
                    .get()
                    .get();

            int activeCases =
                    0;

            for (
                    QueryDocumentSnapshot document :
                    snapshot.getDocuments()
            ) {

                if (
                        document == null ||
                        !document.exists()
                ) {

                    continue;
                }

                String status =
                        getString(
                                document,
                                "status"
                        );

                /*
                 * Count only active emergency cases.
                 *
                 * Different modules may use different
                 * active status names, so we support the
                 * common ones.
                 */
                if (
                        isStatus(
                                status,
                                "ACTIVE"
                        )
                        ||
                        isStatus(
                                status,
                                "OPEN"
                        )
                        ||
                        isStatus(
                                status,
                                "PENDING"
                        )
                        ||
                        isStatus(
                                status,
                                "IN_PROGRESS"
                        )
                ) {

                    activeCases++;
                }
            }

            stats.setEmergencyCases(
                    activeCases
            );

        } catch (Exception e) {

            /*
             * Emergency backend may not be implemented
             * yet. Dashboard remains functional.
             */
            stats.setEmergencyCases(0);
        }
    }

    // =========================================================
    // GET HOSPITAL APPOINTMENTS
    // =========================================================

    public List<Appointment>
    getHospitalAppointments() {

        String hospitalId =
                getCurrentHospitalId();

        try {

            QuerySnapshot snapshot =
                    db.collection(
                            APPOINTMENT_COLLECTION
                    )
                    .whereEqualTo(
                            "hospitalId",
                            hospitalId
                    )
                    .get()
                    .get();

            List<Appointment> appointments =
                    new ArrayList<>();

            for (
                    QueryDocumentSnapshot document :
                    snapshot.getDocuments()
            ) {

                if (
                        document == null ||
                        !document.exists()
                ) {

                    continue;
                }

                Appointment appointment =
                        document.toObject(
                                Appointment.class
                        );

                if (
                        appointment != null
                ) {

                    appointments.add(
                            appointment
                    );
                }
            }

            return appointments;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to retrieve hospital appointments.",
                    e
            );
        }
    }

    // =========================================================
    // GET TODAY'S APPOINTMENTS
    // =========================================================

    public List<Appointment>
    getTodayAppointments() {

        List<Appointment> all =
                getHospitalAppointments();

        List<Appointment> today =
                new ArrayList<>();

        LocalDate currentDate =
                LocalDate.now();

        for (
                Appointment appointment :
                all
        ) {

            if (
                    appointment == null
            ) {

                continue;
            }

            if (
                    isToday(
                            appointment
                                    .getAppointmentDate(),
                            currentDate
                    )
                    &&
                    !isStatus(
                            appointment.getStatus(),
                            "CANCELLED"
                    )
                    &&
                    !isStatus(
                            appointment.getStatus(),
                            "REJECTED"
                    )
            ) {

                today.add(
                        appointment
                );
            }
        }

        return today;
    }

    // =========================================================
    // GET ACTIVE BED COUNT
    // =========================================================

    public int getAvailableBeds() {

        String hospitalId =
                getCurrentHospitalId();

        try {

            QuerySnapshot snapshot =
                    db.collection(
                            BED_COLLECTION
                    )
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

            int available =
                    0;

            for (
                    QueryDocumentSnapshot document :
                    snapshot.getDocuments()
            ) {

                if (
                        document == null ||
                        !document.exists()
                ) {

                    continue;
                }

                String status =
                        getString(
                                document,
                                "status"
                        );

                if (
                        isStatus(
                                status,
                                "AVAILABLE"
                        )
                ) {

                    available++;
                }
            }

            return available;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to retrieve available beds.",
                    e
            );
        }
    }

    // =========================================================
    // GET TOTAL BED COUNT
    // =========================================================

    public int getTotalBeds() {

        String hospitalId =
                getCurrentHospitalId();

        try {

            QuerySnapshot snapshot =
                    db.collection(
                            BED_COLLECTION
                    )
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

            return snapshot
                    .getDocuments()
                    .size();

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to retrieve total beds.",
                    e
            );
        }
    }

    // =========================================================
    // GET OCCUPIED BED COUNT
    // =========================================================

    public int getOccupiedBeds() {

        String hospitalId =
                getCurrentHospitalId();

        try {

            QuerySnapshot snapshot =
                    db.collection(
                            BED_COLLECTION
                    )
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

            int occupied =
                    0;

            for (
                    QueryDocumentSnapshot document :
                    snapshot.getDocuments()
            ) {

                String status =
                        getString(
                                document,
                                "status"
                        );

                if (
                        isStatus(
                                status,
                                "OCCUPIED"
                        )
                ) {

                    occupied++;
                }
            }

            return occupied;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to retrieve occupied beds.",
                    e
            );
        }
    }

    // =========================================================
    // GET RESERVED BED COUNT
    // =========================================================

    public int getReservedBeds() {

        String hospitalId =
                getCurrentHospitalId();

        try {

            QuerySnapshot snapshot =
                    db.collection(
                            BED_COLLECTION
                    )
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

            int reserved =
                    0;

            for (
                    QueryDocumentSnapshot document :
                    snapshot.getDocuments()
            ) {

                String status =
                        getString(
                                document,
                                "status"
                        );

                if (
                        isStatus(
                                status,
                                "RESERVED"
                        )
                ) {

                    reserved++;
                }
            }

            return reserved;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to retrieve reserved beds.",
                    e
            );
        }
    }

    // =========================================================
    // GET TOTAL DOCTORS
    // =========================================================

    public int getTotalDoctors() {

        String hospitalId =
                getCurrentHospitalId();

        try {

            QuerySnapshot snapshot =
                    db.collection(
                            DOCTOR_COLLECTION
                    )
                    .whereEqualTo(
                            "hospitalId",
                            hospitalId
                    )
                    .get()
                    .get();

            int count =
                    0;

            for (
                    QueryDocumentSnapshot document :
                    snapshot.getDocuments()
            ) {

                Object active =
                        document.get(
                                "active"
                        );

                if (
                        active == null
                        ||
                        Boolean.TRUE.equals(
                                active
                        )
                ) {

                    count++;
                }
            }

            return count;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to retrieve total doctors.",
                    e
            );
        }
    }

    // =========================================================
    // GET EMERGENCY CASES
    // =========================================================

    public int getEmergencyCases() {

        String hospitalId =
                getCurrentHospitalId();

        try {

            QuerySnapshot snapshot =
                    db.collection(
                            EMERGENCY_COLLECTION
                    )
                    .whereEqualTo(
                            "hospitalId",
                            hospitalId
                    )
                    .get()
                    .get();

            int count =
                    0;

            for (
                    QueryDocumentSnapshot document :
                    snapshot.getDocuments()
            ) {

                String status =
                        getString(
                                document,
                                "status"
                        );

                if (
                        isStatus(
                                status,
                                "ACTIVE"
                        )
                        ||
                        isStatus(
                                status,
                                "OPEN"
                        )
                        ||
                        isStatus(
                                status,
                                "PENDING"
                        )
                        ||
                        isStatus(
                                status,
                                "IN_PROGRESS"
                        )
                ) {

                    count++;
                }
            }

            return count;

        } catch (Exception e) {

            return 0;
        }
    }

    // =========================================================
    // OCCUPANCY CALCULATION
    // =========================================================

    private double calculateOccupancy(
            int totalBeds,
            int occupiedBeds
    ) {

        if (totalBeds <= 0) {
            return 0.0;
        }

        return (
                (double) occupiedBeds
                /
                totalBeds
        ) * 100.0;
    }

    // =========================================================
    // TODAY CHECK
    // =========================================================

    private boolean isToday(
            String dateString,
            LocalDate today
    ) {

        if (
                dateString == null ||
                dateString.trim().isEmpty()
        ) {

            return false;
        }

        String value =
                dateString.trim();

        /*
         * Expected format:
         *
         * yyyy-MM-dd
         *
         * Example:
         * 2026-09-02
         */

        try {

            LocalDate date =
                    LocalDate.parse(
                            value,
                            DateTimeFormatter.ISO_LOCAL_DATE
                    );

            return date.equals(
                    today
            );

        } catch (DateTimeParseException ignored) {
        }

        /*
         * Compatibility with dd-MM-yyyy.
         */

        try {

            LocalDate date =
                    LocalDate.parse(
                            value,
                            DateTimeFormatter.ofPattern(
                                    "dd-MM-yyyy"
                            )
                    );

            return date.equals(
                    today
            );

        } catch (DateTimeParseException ignored) {
        }

        /*
         * Compatibility with dd/MM/yyyy.
         */

        try {

            LocalDate date =
                    LocalDate.parse(
                            value,
                            DateTimeFormatter.ofPattern(
                                    "dd/MM/yyyy"
                            )
                    );

            return date.equals(
                    today
            );

        } catch (DateTimeParseException ignored) {
        }

        return false;
    }

    // =========================================================
    // STATUS CHECK
    // =========================================================

    private boolean isStatus(
            String actual,
            String expected
    ) {

        if (
                actual == null ||
                expected == null
        ) {

            return false;
        }

        return actual.trim()
                .equalsIgnoreCase(
                        expected.trim()
                );
    }

    // =========================================================
    // FIRESTORE STRING
    // =========================================================

    private String getString(
            DocumentSnapshot document,
            String field
    ) {

        if (
                document == null ||
                field == null
        ) {

            return null;
        }

        Object value =
                document.get(field);

        if (value == null) {
            return null;
        }

        return String.valueOf(value);
    }
}