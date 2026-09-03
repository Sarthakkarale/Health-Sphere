// package com.healthsphere.util;

// import com.healthsphere.config.FirebaseConfig;
// import com.healthsphere.controller.appointment.AppointmentController;
// import com.healthsphere.model.Appointment;

// import java.time.LocalDateTime;
// import java.time.format.DateTimeFormatter;
// import java.util.List;

// public class AppointmentBackendTest {

//     // ============================================================
//     // REAL FIRESTORE IDS FROM YOUR PROJECT
//     // ============================================================

//     private static final String PATIENT_UID =
//             "gVWuuQYMyvfp5UKbfP5QSyrRMk63";

//     private static final String DOCTOR_UID =
//             "BRcC2XYzonWxNRkmPchhpObynp62";

//     private static final String HOSPITAL_ID =
//             "VKCmnJyDwCapIxpjhsAtlgLKcp13";

//     // ============================================================
//     // DISPLAY NAMES
//     // ============================================================

//     private static final String PATIENT_NAME =
//             "Sarthak Jagtap";

//     private static final String DOCTOR_NAME =
//             "Dr. Jonty 421";

//     private static final String HOSPITAL_NAME =
//             "Sai";

//     // ============================================================
//     // MAIN
//     // ============================================================

//     public static void main(String[] args) {

//         try {

//             System.out.println(
//                     "================================================"
//             );

//             System.out.println(
//                     "     APPOINTMENT BACKEND TEST"
//             );

//             System.out.println(
//                     "================================================"
//             );

//             // ----------------------------------------------------
//             // Initialize Firebase
//             // ----------------------------------------------------

//             FirebaseConfig.initialize();

//             AppointmentController controller =
//                     new AppointmentController();

//             // ====================================================
//             // TEST 1
//             // PATIENT BOOKS DIRECTLY WITH DOCTOR
//             // ====================================================

//             System.out.println();
//             System.out.println(
//                     "TEST 1: Doctor Appointment"
//             );

//             Appointment doctorAppointment =
//                     createDoctorAppointment();

//             Appointment createdDoctorAppointment =
//                     controller.createAppointment(
//                             doctorAppointment
//                     );

//             System.out.println(
//                     "PASS: Doctor appointment created."
//             );

//             System.out.println(
//                     "Appointment ID: "
//                             + createdDoctorAppointment
//                             .getAppointmentId()
//             );

//             // ====================================================
//             // TEST 2
//             // PATIENT BOOKS HOSPITAL
//             // ====================================================

//             System.out.println();
//             System.out.println(
//                     "TEST 2: Hospital Appointment"
//             );

//             Appointment hospitalAppointment =
//                     createHospitalAppointment();

//             Appointment createdHospitalAppointment =
//                     controller.createAppointment(
//                             hospitalAppointment
//                     );

//             System.out.println(
//                     "PASS: Hospital appointment created."
//             );

//             System.out.println(
//                     "Appointment ID: "
//                             + createdHospitalAppointment
//                             .getAppointmentId()
//             );

//             // ====================================================
//             // TEST 3
//             // GET DOCTOR APPOINTMENTS
//             // ====================================================

//             System.out.println();
//             System.out.println(
//                     "TEST 3: Get Doctor Appointments"
//             );

//             List<Appointment> doctorAppointments =
//                     controller.getDoctorAppointments(
//                             DOCTOR_UID
//                     );

//             System.out.println(
//                     "Doctor appointments found: "
//                             + doctorAppointments.size()
//             );

//             for (Appointment appointment :
//                     doctorAppointments) {

//                 System.out.println(
//                         "  - "
//                                 + appointment.getAppointmentId()
//                                 + " | "
//                                 + appointment.getPatientName()
//                                 + " | "
//                                 + appointment.getStatus()
//                 );
//             }

//             System.out.println(
//                     "PASS: Doctor appointment query completed."
//             );

//             // ====================================================
//             // TEST 4
//             // GET HOSPITAL APPOINTMENTS
//             // ====================================================

//             System.out.println();
//             System.out.println(
//                     "TEST 4: Get Hospital Appointments"
//             );

//             List<Appointment> hospitalAppointments =
//                     controller.getHospitalAppointments(
//                             HOSPITAL_ID
//                     );

//             System.out.println(
//                     "Hospital appointments found: "
//                             + hospitalAppointments.size()
//             );

//             for (Appointment appointment :
//                     hospitalAppointments) {

//                 System.out.println(
//                         "  - "
//                                 + appointment.getAppointmentId()
//                                 + " | "
//                                 + appointment.getPatientName()
//                                 + " | "
//                                 + appointment.getStatus()
//                 );
//             }

//             System.out.println(
//                     "PASS: Hospital appointment query completed."
//             );

//             // ====================================================
//             // TEST 5
//             // GET PATIENT APPOINTMENTS
//             // ====================================================

//             System.out.println();
//             System.out.println(
//                     "TEST 5: Get Patient Appointments"
//             );

//             List<Appointment> patientAppointments =
//                     controller.getPatientAppointments(
//                             PATIENT_UID
//                     );

//             System.out.println(
//                     "Patient appointments found: "
//                             + patientAppointments.size()
//             );

//             for (Appointment appointment :
//                     patientAppointments) {

//                 System.out.println(
//                         "  - "
//                                 + appointment.getAppointmentId()
//                                 + " | "
//                                 + appointment.getBookingType()
//                                 + " | "
//                                 + appointment.getStatus()
//                 );
//             }

//             System.out.println(
//                     "PASS: Patient appointment query completed."
//             );

//             // ====================================================
//             // TEST 6
//             // HOSPITAL ASSIGNS DOCTOR
//             // ====================================================

//             System.out.println();
//             System.out.println(
//                     "TEST 6: Hospital Assigns Doctor"
//             );

//             String hospitalAppointmentId =
//                     createdHospitalAppointment
//                             .getAppointmentId();

//             controller.assignDoctor(
//                     hospitalAppointmentId,
//                     DOCTOR_UID,
//                     DOCTOR_NAME
//             );

//             System.out.println(
//                     "PASS: Doctor assigned successfully."
//             );

//             // ====================================================
//             // TEST 7
//             // VERIFY ASSIGNED DOCTOR
//             // ====================================================

//             System.out.println();
//             System.out.println(
//                     "TEST 7: Verify Doctor Assignment"
//             );

//             Appointment updatedAppointment =
//                     controller.getAppointment(
//                             hospitalAppointmentId
//                     );

//             System.out.println(
//                     "Appointment ID: "
//                             + updatedAppointment
//                             .getAppointmentId()
//             );

//             System.out.println(
//                     "Booking Type: "
//                             + updatedAppointment
//                             .getBookingType()
//             );

//             System.out.println(
//                     "Doctor UID: "
//                             + updatedAppointment
//                             .getDoctorUid()
//             );

//             System.out.println(
//                     "Doctor Name: "
//                             + updatedAppointment
//                             .getDoctorName()
//             );

//             System.out.println(
//                     "Hospital ID: "
//                             + updatedAppointment
//                             .getHospitalId()
//             );

//             System.out.println(
//                     "Status: "
//                             + updatedAppointment
//                             .getStatus()
//             );

//             if (DOCTOR_UID.equals(
//                     updatedAppointment.getDoctorUid())) {

//                 System.out.println(
//                         "PASS: Doctor UID correctly assigned."
//                 );

//             } else {

//                 System.out.println(
//                         "FAIL: Doctor UID was not assigned."
//                 );
//             }

//             if ("CONFIRMED".equals(
//                     updatedAppointment.getStatus())) {

//                 System.out.println(
//                         "PASS: Status changed to CONFIRMED."
//                 );

//             } else {

//                 System.out.println(
//                         "FAIL: Status was not changed."
//                 );
//             }

//             // ====================================================
//             // TEST 8
//             // DOCTOR SHOULD NOW SEE HOSPITAL APPOINTMENT
//             // ====================================================

//             System.out.println();
//             System.out.println(
//                     "TEST 8: Doctor Sees Assigned Patient"
//             );

//             List<Appointment> updatedDoctorAppointments =
//                     controller.getDoctorAppointments(
//                             DOCTOR_UID
//                     );

//             boolean found =
//                     false;

//             for (Appointment appointment :
//                     updatedDoctorAppointments) {

//                 if (hospitalAppointmentId.equals(
//                         appointment.getAppointmentId())) {

//                     found = true;

//                     break;
//                 }
//             }

//             if (found) {

//                 System.out.println(
//                         "PASS: Doctor can now see the "
//                                 + "hospital appointment."
//                 );

//             } else {

//                 System.out.println(
//                         "FAIL: Doctor cannot see "
//                                 + "the assigned appointment."
//                 );
//             }

//             // ====================================================
//             // FINISHED
//             // ====================================================

//             System.out.println();
//             System.out.println(
//                     "================================================"
//             );

//             System.out.println(
//                     "     ALL APPOINTMENT TESTS COMPLETED"
//             );

//             System.out.println(
//                     "================================================"
//             );

//         } catch (Exception e) {

//             System.out.println();
//             System.out.println(
//                     "================================================"
//             );

//             System.out.println(
//                     "APPOINTMENT BACKEND TEST FAILED"
//             );

//             System.out.println(
//                     "================================================"
//             );

//             e.printStackTrace();
//         }
//     }

//     // ============================================================
//     // CREATE DOCTOR APPOINTMENT
//     // ============================================================

//     private static Appointment createDoctorAppointment() {

//         String now =
//                 LocalDateTime.now()
//                         .format(
//                                 DateTimeFormatter
//                                         .ISO_LOCAL_DATE_TIME
//                         );

//         return new Appointment(

//                 null,

//                 PATIENT_UID,
//                 PATIENT_NAME,

//                 "DOCTOR",

//                 DOCTOR_UID,
//                 DOCTOR_NAME,

//                 null,
//                 null,

//                 "Cardiology",

//                 "2026-09-05",
//                 "10:00 AM",

//                 "Chest pain",

//                 "PENDING",

//                 now,
//                 now
//         );
//     }

//     // ============================================================
//     // CREATE HOSPITAL APPOINTMENT
//     // ============================================================

//     private static Appointment createHospitalAppointment() {

//         String now =
//                 LocalDateTime.now()
//                         .format(
//                                 DateTimeFormatter
//                                         .ISO_LOCAL_DATE_TIME
//                         );

//         return new Appointment(

//                 null,

//                 PATIENT_UID,
//                 PATIENT_NAME,

//                 "HOSPITAL",

//                 null,
//                 null,

//                 HOSPITAL_ID,
//                 HOSPITAL_NAME,

//                 "Cardiology",

//                 "2026-09-06",
//                 "11:00 AM",

//                 "General cardiac consultation",

//                 "PENDING_ASSIGNMENT",

//                 now,
//                 now
//         );
//     }
// }