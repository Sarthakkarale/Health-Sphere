package com.healthsphere.controller.doctor;

import com.healthsphere.dao.doctor.DoctorScheduleDAO;
import com.healthsphere.model.DoctorSchedule;

public class DoctorScheduleController {

    private final DoctorScheduleDAO doctorScheduleDAO;


    public DoctorScheduleController() {

        this.doctorScheduleDAO =
                new DoctorScheduleDAO();
    }


    // ============================================================
    // GET SCHEDULE
    // ============================================================

    public DoctorSchedule getDoctorSchedule(
            String doctorUid) {

        validateDoctorUid(
                doctorUid
        );


        try {

            return doctorScheduleDAO
                    .getDoctorSchedule(
                            doctorUid
                    );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to load doctor schedule.",
                    e
            );
        }
    }


    // ============================================================
    // SAVE SCHEDULE
    // ============================================================

    public void saveDoctorSchedule(
            DoctorSchedule schedule) {

        if (schedule == null) {

            throw new IllegalArgumentException(
                    "Schedule cannot be null."
            );
        }


        validateDoctorUid(
                schedule.getDoctorUid()
        );


        validateSchedule(
                schedule
        );


        try {

            doctorScheduleDAO
                    .saveDoctorSchedule(
                            schedule
                    );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to save doctor schedule.",
                    e
            );
        }
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


    private void validateSchedule(
            DoctorSchedule schedule) {

        if (schedule.getDefaultAppointmentSlotMinutes()
                <= 0) {

            throw new IllegalArgumentException(
                    "Appointment slot must be greater than zero."
            );
        }


        validateTimePair(
                schedule.isMondayEnabled(),
                schedule.getMondayStartTime(),
                schedule.getMondayEndTime(),
                "Monday"
        );


        validateTimePair(
                schedule.isTuesdayEnabled(),
                schedule.getTuesdayStartTime(),
                schedule.getTuesdayEndTime(),
                "Tuesday"
        );


        validateTimePair(
                schedule.isWednesdayEnabled(),
                schedule.getWednesdayStartTime(),
                schedule.getWednesdayEndTime(),
                "Wednesday"
        );


        validateTimePair(
                schedule.isThursdayEnabled(),
                schedule.getThursdayStartTime(),
                schedule.getThursdayEndTime(),
                "Thursday"
        );


        validateTimePair(
                schedule.isFridayEnabled(),
                schedule.getFridayStartTime(),
                schedule.getFridayEndTime(),
                "Friday"
        );
    }


    private void validateTimePair(
            boolean enabled,
            String start,
            String end,
            String day) {

        if (!enabled) {
            return;
        }


        if (start == null
                || start.trim().isEmpty()
                || end == null
                || end.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    day
                            + " start and end times are required."
            );
        }
    }
}