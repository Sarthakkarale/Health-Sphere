// package com.healthsphere.util;
// import com.healthsphere.dao.patient.PatientDAO;

// import com.healthsphere.config.FirebaseConfig;
// import com.healthsphere.dao.appointment.AppointmentDAO;
// import com.healthsphere.dao.authentication.PatientDAO;
// import com.healthsphere.model.Appointment;
// import com.healthsphere.model.PatientProfile;

// import java.time.LocalDate;
// import java.util.UUID;

// /**
//  * Creates multiple test patients and appointments
//  * for one doctor in Firestore.
//  *
//  * PURPOSE:
//  * -------
//  * This class is only for testing the Doctor Dashboard.
//  *
//  * It creates:
//  * 10 patients
//  * 10 appointments
//  *
//  * All appointments are assigned to the doctor below.
//  */
// public class DoctorDashboardDataTest {

//     // ============================================================
//     // DOCTOR UID FROM FIREBASE
//     // ============================================================

//     private static final String DOCTOR_UID =
//             "Yu3YE5ej3XOdYnkSUQo3m2nw5G62";


//     // ============================================================
//     // DAO
//     // ============================================================

//     private static PatientDAO patientDAO;

//     private static AppointmentDAO appointmentDAO;


//     // ============================================================
//     // MAIN
//     // ============================================================

//     public static void main(String[] args) {

//         System.out.println(
//                 "=========================================="
//         );

//         System.out.println(
//                 "DOCTOR DASHBOARD TEST DATA"
//         );

//         System.out.println(
//                 "=========================================="
//         );


//         try {

//             // ----------------------------------------------------
//             // Initialize Firebase
//             // ----------------------------------------------------

//             FirebaseConfig.initialize();


//             patientDAO =
//                     new PatientDAO();


//             appointmentDAO =
//                     new AppointmentDAO();


//             System.out.println(
//                     "Firebase initialized successfully."
//             );


//             System.out.println(
//                     "Doctor UID: "
//                             + DOCTOR_UID
//             );


//             // ----------------------------------------------------
//             // Create patients
//             // ----------------------------------------------------

//             createPatient1();

//             createPatient2();

//             createPatient3();

//             createPatient4();

//             createPatient5();

//             createPatient6();

//             createPatient7();

//             createPatient8();

//             createPatient9();

//             createPatient10();


//             System.out.println();
//             System.out.println(
//                     "=========================================="
//             );

//             System.out.println(
//                     "ALL TEST DATA CREATED SUCCESSFULLY"
//             );

//             System.out.println(
//                     "=========================================="
//             );


//         } catch (Exception e) {

//             System.err.println();

//             System.err.println(
//                     "ERROR CREATING TEST DATA"
//             );

//             e.printStackTrace();
//         }
//     }


//     // ============================================================
//     // PATIENT 1
//     // ============================================================

//     private static void createPatient1()
//             throws Exception {

//         createPatientAndAppointment(
//                 "Rahul",
//                 "Sharma",
//                 "rahul.test@healthsphere.com",
//                 "9000000001",
//                 "Male",
//                 "A+",
//                 "10:00 AM",
//                 "General Consultation",
//                 "Confirmed"
//         );
//     }


//     // ============================================================
//     // PATIENT 2
//     // ============================================================

//     private static void createPatient2()
//             throws Exception {

//         createPatientAndAppointment(
//                 "Priya",
//                 "Patil",
//                 "priya.test@healthsphere.com",
//                 "9000000002",
//                 "Female",
//                 "B+",
//                 "10:30 AM",
//                 "Fever and weakness",
//                 "Pending"
//         );
//     }


//     // ============================================================
//     // PATIENT 3
//     // ============================================================

//     private static void createPatient3()
//             throws Exception {

//         createPatientAndAppointment(
//                 "Amit",
//                 "Kulkarni",
//                 "amit.test@healthsphere.com",
//                 "9000000003",
//                 "Male",
//                 "O+",
//                 "11:00 AM",
//                 "Chest pain",
//                 "Confirmed"
//         );
//     }


//     // ============================================================
//     // PATIENT 4
//     // ============================================================

//     private static void createPatient4()
//             throws Exception {

//         createPatientAndAppointment(
//                 "Sneha",
//                 "Deshmukh",
//                 "sneha.test@healthsphere.com",
//                 "9000000004",
//                 "Female",
//                 "AB+",
//                 "11:30 AM",
//                 "Headache",
//                 "Pending"
//         );
//     }


//     // ============================================================
//     // PATIENT 5
//     // ============================================================

//     private static void createPatient5()
//             throws Exception {

//         createPatientAndAppointment(
//                 "Rohit",
//                 "Jadhav",
//                 "rohit.test@healthsphere.com",
//                 "9000000005",
//                 "Male",
//                 "A-",
//                 "12:00 PM",
//                 "Blood pressure check",
//                 "Confirmed"
//         );
//     }


//     // ============================================================
//     // PATIENT 6
//     // ============================================================

//     private static void createPatient6()
//             throws Exception {

//         createPatientAndAppointment(
//                 "Neha",
//                 "Joshi",
//                 "neha.test@healthsphere.com",
//                 "9000000006",
//                 "Female",
//                 "O-",
//                 "01:00 PM",
//                 "Diabetes consultation",
//                 "Confirmed"
//         );
//     }


//     // ============================================================
//     // PATIENT 7
//     // ============================================================

//     private static void createPatient7()
//             throws Exception {

//         createPatientAndAppointment(
//                 "Vikram",
//                 "Pawar",
//                 "vikram.test@healthsphere.com",
//                 "9000000007",
//                 "Male",
//                 "B-",
//                 "02:00 PM",
//                 "Back pain",
//                 "Pending"
//         );
//     }


//     // ============================================================
//     // PATIENT 8
//     // ============================================================

//     private static void createPatient8()
//             throws Exception {

//         createPatientAndAppointment(
//                 "Pooja",
//                 "More",
//                 "pooja.test@healthsphere.com",
//                 "9000000008",
//                 "Female",
//                 "A+",
//                 "03:00 PM",
//                 "Migraine consultation",
//                 "Confirmed"
//         );
//     }


//     // ============================================================
//     // PATIENT 9
//     // ============================================================

//     private static void createPatient9()
//             throws Exception {

//         createPatientAndAppointment(
//                 "Karan",
//                 "Chavan",
//                 "karan.test@healthsphere.com",
//                 "9000000009",
//                 "Male",
//                 "O+",
//                 "04:00 PM",
//                 "Routine health checkup",
//                 "Confirmed"
//         );
//     }


//     // ============================================================
//     // PATIENT 10
//     // ============================================================

//     private static void createPatient10()
//             throws Exception {

//         createPatientAndAppointment(
//                 "Anjali",
//                 "Shinde",
//                 "anjali.test@healthsphere.com",
//                 "9000000010",
//                 "Female",
//                 "B+",
//                 "05:00 PM",
//                 "General cardiac consultation",
//                 "Pending"
//         );
//     }


//     // ============================================================
//     // CREATE PATIENT + APPOINTMENT
//     // ============================================================

//     private static void createPatientAndAppointment(
//             String firstName,
//             String lastName,
//             String email,
//             String phone,
//             String gender,
//             String bloodGroup,
//             String appointmentTime,
//             String reason,
//             String status
//     ) throws Exception {


//         // ========================================================
//         // 1. CREATE UNIQUE PATIENT UID
//         // ========================================================

//         String patientUid =
//                 UUID.randomUUID().toString();


//         // ========================================================
//         // 2. CREATE PATIENT PROFILE
//         // ========================================================

//         PatientProfile patient =
//                 new PatientProfile();


//         patient.setUid(
//                 patientUid
//         );


//         patient.setFirstName(
//                 firstName
//         );


//         patient.setLastName(
//                 lastName
//         );


//         patient.setEmail(
//                 email
//         );


//         patient.setPhone(
//                 phone
//         );


//         patient.setGender(
//                 gender
//         );


//         patient.setBloodGroup(
//                 bloodGroup
//         );


//         patient.setDateOfBirth(
//                 "2000-01-01"
//         );


//         patient.setEmergencyContact(
//                 "9111111111"
//         );


//         patient.setAddress(
//                 "Pune, Maharashtra"
//         );


//         // ========================================================
//         // 3. SAVE PATIENT TO FIRESTORE
//         // ========================================================

//         patientDAO.createPatient(
//                 patient
//         );


//         System.out.println();

//         System.out.println(
//                 "Patient created:"
//         );

//         System.out.println(
//                 "  Name: "
//                         + firstName
//                         + " "
//                         + lastName
//         );

//         System.out.println(
//                 "  UID: "
//                         + patientUid
//         );


//         // ========================================================
//         // 4. CREATE APPOINTMENT
//         // ========================================================

//         Appointment appointment =
//                 new Appointment();


//         String appointmentId =
//                 UUID.randomUUID().toString();


//         appointment.setAppointmentId(
//                 appointmentId
//         );


//         appointment.setPatientUid(
//                 patientUid
//         );


//         appointment.setPatientName(
//                 firstName
//                         + " "
//                         + lastName
//         );


//         appointment.setDoctorUid(
//                 DOCTOR_UID
//         );


//         appointment.setDoctorName(
//                 "Dr. sanskruti 2005"
//         );


//         appointment.setAppointmentDate(
//                 LocalDate.now().toString()
//         );


//         appointment.setAppointmentTime(
//                 appointmentTime
//         );


//         appointment.setReason(
//                 reason
//         );


//         appointment.setStatus(
//                 status
//         );


//         /*
//          * Direct doctor appointment.
//          */
//         appointment.setBookingType(
//                 "DOCTOR"
//         );


//         // ========================================================
//         // 5. SAVE APPOINTMENT
//         // ========================================================

//         appointmentDAO.createAppointment(
//                 appointment
//         );


//         System.out.println(
//                 "  Appointment created:"
//         );

//         System.out.println(
//                 "  Appointment ID: "
//                         + appointmentId
//         );

//         System.out.println(
//                 "  Doctor UID: "
//                         + DOCTOR_UID
//         );

//         System.out.println(
//                 "  Date: "
//                         + LocalDate.now()
//         );

//         System.out.println(
//                 "  Time: "
//                         + appointmentTime
//         );

//         System.out.println(
//                 "  Status: "
//                         + status
//         );

//         System.out.println(
//                 "------------------------------------------"
//         );
//     }
// }