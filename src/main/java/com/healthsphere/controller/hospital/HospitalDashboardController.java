package com.healthsphere.controller.hospital;

import com.healthsphere.model.Appointment;
import com.healthsphere.model.HospitalDepartment;
import com.healthsphere.model.HospitalDoctorDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HospitalDashboardController {

    private final DoctorController doctorController;
    private final DepartmentController departmentController;
    private final AppointmentController appointmentController;

    public HospitalDashboardController() {

        doctorController =
                new DoctorController();

        departmentController =
                new DepartmentController();

        appointmentController =
                new AppointmentController();
    }

    // =========================================================
    // DOCTORS
    // =========================================================

    public List<HospitalDoctorDetails> getDoctors() {

        try {

            List<HospitalDoctorDetails> doctors =
                    doctorController.getAllDoctorDetails();

            if (doctors == null) {
                return new ArrayList<>();
            }

            return new ArrayList<>(doctors);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to load hospital doctors.",
                    e
            );
        }
    }

    public int getTotalDoctors() {

        return getDoctors().size();
    }

    // =========================================================
    // DEPARTMENTS
    // =========================================================

    public List<HospitalDepartment> getDepartments() {

        try {

            List<HospitalDepartment> departments =
                    departmentController.getAllDepartments();

            if (departments == null) {
                return new ArrayList<>();
            }

            return new ArrayList<>(departments);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to load hospital departments.",
                    e
            );
        }
    }

    public int getTotalDepartments() {

        return getDepartments().size();
    }

    // =========================================================
    // APPOINTMENTS
    // =========================================================

    public List<Appointment> getAppointments() {

        try {

            List<Appointment> appointments =
                    appointmentController
                            .getHospitalAppointments();

            if (appointments == null) {
                return new ArrayList<>();
            }

            return new ArrayList<>(appointments);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to load hospital appointments.",
                    e
            );
        }
    }

    // =========================================================
    // TOTAL APPOINTMENTS
    // =========================================================

    public int getTotalAppointments() {

        return getAppointments().size();
    }

    // =========================================================
    // TODAY'S APPOINTMENTS
    // =========================================================

    public int getTodayAppointments() {

        String today =
                LocalDate.now().toString();

        int count = 0;

        for (Appointment appointment :
                getAppointments()) {

            if (appointment == null) {
                continue;
            }

            String appointmentDate =
                    safe(
                            appointment
                                    .getAppointmentDate()
                    );

            if (today.equals(
                    appointmentDate
            )) {

                count++;
            }
        }

        return count;
    }

    // =========================================================
    // COMPLETED
    // =========================================================

    public int getCompletedAppointments() {

        return countStatus(
                "COMPLETED"
        );
    }

    // =========================================================
    // CANCELLED
    // =========================================================

    public int getCancelledAppointments() {

        return countStatus(
                "CANCELLED"
        );
    }

    // =========================================================
    // WAITING
    // =========================================================

    public int getWaitingAppointments() {

        int count = 0;

        for (Appointment appointment :
                getAppointments()) {

            if (appointment == null) {
                continue;
            }

            String status =
                    safe(
                            appointment.getStatus()
                    );

            if (
                    "PENDING".equalsIgnoreCase(status)
                    ||
                    "PENDING_ASSIGNMENT"
                            .equalsIgnoreCase(status)
                    ||
                    "WAITING".equalsIgnoreCase(status)
            ) {

                count++;
            }
        }

        return count;
    }

    // =========================================================
    // UPCOMING
    // =========================================================

    public int getUpcomingAppointments() {

        String today =
                LocalDate.now().toString();

        int count = 0;

        for (Appointment appointment :
                getAppointments()) {

            if (appointment == null) {
                continue;
            }

            String status =
                    safe(
                            appointment.getStatus()
                    );

            String date =
                    safe(
                            appointment
                                    .getAppointmentDate()
                    );

            boolean activeStatus =
                    "CONFIRMED".equalsIgnoreCase(status)
                    ||
                    "ACCEPTED".equalsIgnoreCase(status)
                    ||
                    "UPCOMING".equalsIgnoreCase(status);

            boolean futureOrToday =
                    !date.isEmpty()
                    &&
                    date.compareTo(today) >= 0;

            if (
                    activeStatus
                    &&
                    futureOrToday
            ) {

                count++;
            }
        }

        return count;
    }

    // =========================================================
    // COUNT STATUS
    // =========================================================

    private int countStatus(
            String requiredStatus
    ) {

        int count = 0;

        for (Appointment appointment :
                getAppointments()) {

            if (appointment == null) {
                continue;
            }

            String status =
                    safe(
                            appointment.getStatus()
                    );

            if (
                    requiredStatus
                            .equalsIgnoreCase(
                                    status
                            )
            ) {

                count++;
            }
        }

        return count;
    }

    // =========================================================
    // DOCTORS BY DEPARTMENT
    // =========================================================

    public int getDoctorsForDepartment(
            String departmentId
    ) {

        if (
                departmentId == null
                ||
                departmentId.trim().isEmpty()
        ) {

            return 0;
        }

        int count = 0;

        for (HospitalDoctorDetails doctor :
                getDoctors()) {

            if (doctor == null) {
                continue;
            }

            String doctorDepartmentId =
                    safe(
                            doctor.getDepartmentId()
                    );

            if (
                    departmentId.equals(
                            doctorDepartmentId
                    )
            ) {

                count++;
            }
        }

        return count;
    }

    // =========================================================
    // RECENT APPOINTMENTS
    // =========================================================

    public List<Appointment>
    getRecentAppointments(
            int limit
    ) {

        if (limit <= 0) {
            return new ArrayList<>();
        }

        List<Appointment> appointments =
                getAppointments();

        appointments.removeIf(
                appointment ->
                        appointment == null
        );

        appointments.sort(
                Comparator.comparing(
                        appointment ->
                                safe(
                                        appointment
                                                .getUpdatedAt()
                                ),
                        Comparator.reverseOrder()
                )
        );

        if (
                appointments.size()
                <= limit
        ) {

            return appointments;
        }

        return new ArrayList<>(
                appointments.subList(
                        0,
                        limit
                )
        );
    }

    // =========================================================
    // SUMMARY
    // =========================================================

    public DashboardSummary getSummary() {

        return new DashboardSummary(
                getTotalDoctors(),
                getTotalAppointments(),
                getTodayAppointments(),
                getCompletedAppointments(),
                getWaitingAppointments(),
                getUpcomingAppointments(),
                getCancelledAppointments(),
                getTotalDepartments()
        );
    }

    // =========================================================
    // SAFE
    // =========================================================

    private String safe(
            String value
    ) {

        return value == null
                ? ""
                : value.trim();
    }

    // =========================================================
    // SUMMARY MODEL
    // =========================================================

    public static class DashboardSummary {

        private final int totalDoctors;
        private final int totalAppointments;
        private final int todayAppointments;
        private final int completedAppointments;
        private final int waitingAppointments;
        private final int upcomingAppointments;
        private final int cancelledAppointments;
        private final int totalDepartments;

        public DashboardSummary(
                int totalDoctors,
                int totalAppointments,
                int todayAppointments,
                int completedAppointments,
                int waitingAppointments,
                int upcomingAppointments,
                int cancelledAppointments,
                int totalDepartments
        ) {

            this.totalDoctors =
                    totalDoctors;

            this.totalAppointments =
                    totalAppointments;

            this.todayAppointments =
                    todayAppointments;

            this.completedAppointments =
                    completedAppointments;

            this.waitingAppointments =
                    waitingAppointments;

            this.upcomingAppointments =
                    upcomingAppointments;

            this.cancelledAppointments =
                    cancelledAppointments;

            this.totalDepartments =
                    totalDepartments;
        }

        public int getTotalDoctors() {
            return totalDoctors;
        }

        public int getTotalAppointments() {
            return totalAppointments;
        }

        public int getTodayAppointments() {
            return todayAppointments;
        }

        public int getCompletedAppointments() {
            return completedAppointments;
        }

        public int getWaitingAppointments() {
            return waitingAppointments;
        }

        public int getUpcomingAppointments() {
            return upcomingAppointments;
        }

        public int getCancelledAppointments() {
            return cancelledAppointments;
        }

        public int getTotalDepartments() {
            return totalDepartments;
        }
    }
}