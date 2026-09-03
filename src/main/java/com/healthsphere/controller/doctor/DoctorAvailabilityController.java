package com.healthsphere.controller.doctor;

import com.healthsphere.dao.authentication.DoctorDAO;
import com.healthsphere.dao.doctor.DoctorAvailabilityDAO;
import com.healthsphere.model.DoctorAvailability;
import com.healthsphere.model.DoctorProfile;

import java.util.concurrent.ExecutionException;

/**
 * Controller for Doctor Availability and Consultation Settings.
 *
 * Architecture:
 *
 * View
 *   ↓
 * Controller
 *   ↓
 * DAO
 *   ↓
 * Firestore
 */
public class DoctorAvailabilityController {

    private final DoctorAvailabilityDAO availabilityDAO;

    private final DoctorDAO doctorDAO;

    public DoctorAvailabilityController() {

        availabilityDAO =
                new DoctorAvailabilityDAO();

        doctorDAO =
                new DoctorDAO();
    }

    // =========================================================
    // GET AVAILABILITY
    // =========================================================

    public DoctorAvailability getAvailability(
            String doctorUid) {

        validateDoctorUid(doctorUid);

        try {

            return availabilityDAO.getAvailability(
                    doctorUid
            );

        } catch (
                ExecutionException
                        | InterruptedException e) {

            if (e instanceof InterruptedException) {

                Thread.currentThread().interrupt();
            }

            throw new IllegalStateException(
                    "Unable to load doctor availability.",
                    e
            );
        }
    }

    // =========================================================
    // SAVE AVAILABILITY
    // =========================================================

    public void saveAvailability(
            DoctorAvailability availability) {

        validateAvailability(
                availability
        );

        try {

            availabilityDAO.saveAvailability(
                    availability
            );

        } catch (
                ExecutionException
                        | InterruptedException e) {

            if (e instanceof InterruptedException) {

                Thread.currentThread().interrupt();
            }

            throw new IllegalStateException(
                    "Unable to save doctor availability.",
                    e
            );
        }
    }

    // =========================================================
    // DELETE
    // =========================================================

    public void deleteAvailability(
            String doctorUid) {

        validateDoctorUid(doctorUid);

        try {

            availabilityDAO.deleteAvailability(
                    doctorUid
            );

        } catch (
                ExecutionException
                        | InterruptedException e) {

            if (e instanceof InterruptedException) {

                Thread.currentThread().interrupt();
            }

            throw new IllegalStateException(
                    "Unable to delete doctor availability.",
                    e
            );
        }
    }

    // =========================================================
    // GET DOCTOR PROFILE
    // =========================================================

    public DoctorProfile getDoctorProfile(
            String doctorUid) {

        validateDoctorUid(doctorUid);

        try {

            return doctorDAO.getDoctorProfile(
                    doctorUid
            );

        } catch (Exception e) {

            throw new IllegalStateException(
                    "Unable to load doctor profile.",
                    e
            );
        }
    }

    // =========================================================
    // VALIDATION
    // =========================================================

    private void validateDoctorUid(
            String doctorUid) {

        if (doctorUid == null
                || doctorUid.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Doctor UID is required."
            );
        }
    }

    private void validateAvailability(
            DoctorAvailability availability) {

        if (availability == null) {

            throw new IllegalArgumentException(
                    "Availability information is required."
            );
        }

        validateDoctorUid(
                availability.getDoctorUid()
        );

        if (availability.getSlotDurationMinutes() <= 0) {

            throw new IllegalArgumentException(
                    "Appointment duration must be greater than zero."
            );
        }

        if (availability.getSlotDurationMinutes() > 240) {

            throw new IllegalArgumentException(
                    "Appointment duration cannot exceed 240 minutes."
            );
        }

        if (availability.getConsultationFee() < 0) {

            throw new IllegalArgumentException(
                    "Consultation fee cannot be negative."
            );
        }

        if (availability.getAdvanceBookingDays() < 0) {

            throw new IllegalArgumentException(
                    "Advance booking days cannot be negative."
            );
        }

        if (availability.getCancellationNoticeHours() < 0) {

            throw new IllegalArgumentException(
                    "Cancellation notice cannot be negative."
            );
        }

        validateDay(
                availability.isMondayEnabled(),
                availability.getMondayStartTime(),
                availability.getMondayEndTime(),
                "Monday"
        );

        validateDay(
                availability.isTuesdayEnabled(),
                availability.getTuesdayStartTime(),
                availability.getTuesdayEndTime(),
                "Tuesday"
        );

        validateDay(
                availability.isWednesdayEnabled(),
                availability.getWednesdayStartTime(),
                availability.getWednesdayEndTime(),
                "Wednesday"
        );

        validateDay(
                availability.isThursdayEnabled(),
                availability.getThursdayStartTime(),
                availability.getThursdayEndTime(),
                "Thursday"
        );

        validateDay(
                availability.isFridayEnabled(),
                availability.getFridayStartTime(),
                availability.getFridayEndTime(),
                "Friday"
        );

        validateDay(
                availability.isSaturdayEnabled(),
                availability.getSaturdayStartTime(),
                availability.getSaturdayEndTime(),
                "Saturday"
        );

        validateDay(
                availability.isSundayEnabled(),
                availability.getSundayStartTime(),
                availability.getSundayEndTime(),
                "Sunday"
        );

        if (!availability.isInClinicAvailable()
                && !availability.isVideoConsultationAvailable()) {

            throw new IllegalArgumentException(
                    "Select at least one consultation type."
            );
        }
    }

    private void validateDay(
            boolean enabled,
            String start,
            String end,
            String dayName) {

        if (!enabled) {

            return;
        }

        if (start == null
                || start.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    dayName
                            + " start time is required."
            );
        }

        if (end == null
                || end.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    dayName
                            + " end time is required."
            );
        }

        int startMinutes =
                convertTimeToMinutes(start);

        int endMinutes =
                convertTimeToMinutes(end);

        if (endMinutes <= startMinutes) {

            throw new IllegalArgumentException(
                    dayName
                            + " end time must be after start time."
            );
        }
    }

    // =========================================================
    // TIME VALIDATION
    // =========================================================

    private int convertTimeToMinutes(
            String value) {

        if (value == null
                || value.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Time cannot be empty."
            );
        }

        String normalized =
                value.trim()
                        .toUpperCase();

        String[] parts =
                normalized.split(" ");

        if (parts.length != 2) {

            throw new IllegalArgumentException(
                    "Invalid time format: "
                            + value
            );
        }

        String[] hm =
                parts[0].split(":");

        if (hm.length != 2) {

            throw new IllegalArgumentException(
                    "Invalid time format: "
                            + value
            );
        }

        try {

            int hour =
                    Integer.parseInt(
                            hm[0]
                    );

            int minute =
                    Integer.parseInt(
                            hm[1]
                    );

            String meridiem =
                    parts[1];

            if (hour < 1 || hour > 12) {

                throw new IllegalArgumentException(
                        "Invalid hour: "
                                + value
                );
            }

            if (minute < 0 || minute > 59) {

                throw new IllegalArgumentException(
                        "Invalid minute: "
                                + value
                );
            }

            if (!"AM".equals(meridiem)
                    && !"PM".equals(meridiem)) {

                throw new IllegalArgumentException(
                        "Invalid AM/PM value: "
                                + value
                );
            }

            if (hour == 12) {

                hour = 0;
            }

            if ("PM".equals(meridiem)) {

                hour += 12;
            }

            return hour * 60 + minute;

        } catch (NumberFormatException e) {

            throw new IllegalArgumentException(
                    "Invalid time: "
                            + value,
                    e
            );
        }
    }
}