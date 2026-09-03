package com.healthsphere.dao.hospital;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.SetOptions;
import com.healthsphere.config.FirebaseConfig;
import com.healthsphere.exceptions.DatabaseException;
import com.healthsphere.model.HospitalProfileModel;
import com.healthsphere.util.SessionManager;

import java.util.HashMap;
import java.util.Map;

/**
 * HospitalProfileSettingsDAO
 *
 * Handles all Firestore operations for Hospital Settings.
 *
 * Firestore collection:
 *
 * hospitals
 *
 * Document:
 *
 * hospitals/{hospitalUid}
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
public class HospitalProfileSettingsDAO {

    private static final String COLLECTION =
            "hospitals";

    private final Firestore firestore;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public HospitalProfileSettingsDAO() {

        this.firestore =
                FirebaseConfig.getFirestore();
    }

    // =========================================================
    // CURRENT HOSPITAL ID
    // =========================================================

    private String getCurrentHospitalId() {

        if (!SessionManager.isLoggedIn()) {

            throw new IllegalStateException(
                    "No active hospital session."
            );
        }

        if (SessionManager.getCurrentUser() == null) {

            throw new IllegalStateException(
                    "Current user session is unavailable."
            );
        }

        String hospitalId =
                SessionManager
                        .getCurrentUser()
                        .getUid();

        if (hospitalId == null
                || hospitalId.trim().isEmpty()) {

            throw new IllegalStateException(
                    "Hospital ID is not available."
            );
        }

        return hospitalId.trim();
    }

    // =========================================================
    // GET PROFILE
    // =========================================================

    /**
     * Loads the current hospital profile/settings.
     *
     * If the hospital document does not exist,
     * a default model is returned.
     */
    public HospitalProfileModel getProfile() {

        String hospitalId =
                getCurrentHospitalId();

        try {

            DocumentSnapshot document =
                    firestore
                            .collection(COLLECTION)
                            .document(hospitalId)
                            .get()
                            .get();

            HospitalProfileModel model =
                    createDefaultModel(
                            hospitalId
                    );

            if (!document.exists()) {

                return model;
            }

            Map<String, Object> data =
                    document.getData();

            if (data == null) {

                return model;
            }

            loadDataIntoModel(
                    data,
                    model
            );

            return model;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to load hospital profile settings.",
                    e
            );
        }
    }

    // =========================================================
    // SAVE PROFILE
    // =========================================================

    /**
     * Saves the hospital settings into the existing
     * hospitals/{hospitalUid} document.
     *
     * SetOptions.merge() is intentionally used so that
     * fields owned by other modules are not deleted.
     */
    public void saveProfile(
            HospitalProfileModel model) {

        if (model == null) {

            throw new IllegalArgumentException(
                    "Hospital profile cannot be null."
            );
        }

        String hospitalId =
                getCurrentHospitalId();

        try {

            model.setUid(
                    hospitalId
            );

            Map<String, Object> data =
                    convertModelToMap(model);

            data.put(
                    "updatedAt",
                    Timestamp.now()
            );

            firestore
                    .collection(COLLECTION)
                    .document(hospitalId)
                    .set(
                            data,
                            SetOptions.merge()
                    )
                    .get();

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to save hospital profile settings.",
                    e
            );
        }
    }

    // =========================================================
    // CHECK PROFILE
    // =========================================================

    public boolean profileExists() {

        String hospitalId =
                getCurrentHospitalId();

        try {

            DocumentSnapshot document =
                    firestore
                            .collection(COLLECTION)
                            .document(hospitalId)
                            .get()
                            .get();

            return document.exists();

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to check hospital profile.",
                    e
            );
        }
    }

    // =========================================================
    // LOAD FIRESTORE DATA INTO MODEL
    // =========================================================

    private void loadDataIntoModel(
            Map<String, Object> data,
            HospitalProfileModel model) {

        // =====================================================
        // BASIC HOSPITAL INFORMATION
        // =====================================================

        model.setHospitalName(
                getString(
                        data,
                        "hospitalName",
                        ""
                )
        );

        model.setRegistrationNumber(
                getString(
                        data,
                        "registrationNumber",
                        ""
                )
        );

        model.setHospitalType(
                getString(
                        data,
                        "hospitalType",
                        ""
                )
        );

        model.setEstablishedYear(
                getString(
                        data,
                        "establishedYear",
                        ""
                )
        );

        // =====================================================
        // EMAIL
        // =====================================================

        String email =
                getString(
                        data,
                        "email",
                        ""
                );

        if (isBlank(email)) {

            email =
                    getString(
                            data,
                            "emailAddress",
                            ""
                    );
        }

        model.setEmailAddress(
                email
        );

        // =====================================================
        // PHONE / CONTACT
        // =====================================================

        String contact =
                getString(
                        data,
                        "contact",
                        ""
                );

        if (isBlank(contact)) {

            contact =
                    getString(
                            data,
                            "phoneNumber",
                            ""
                    );
        }

        model.setPhoneNumber(
                contact
        );

        // =====================================================
        // ADDRESS
        // =====================================================

        model.setAddress(
                getString(
                        data,
                        "address",
                        ""
                )
        );

        model.setCity(
                getString(
                        data,
                        "city",
                        ""
                )
        );

        model.setState(
                getString(
                        data,
                        "state",
                        ""
                )
        );

        model.setPostalCode(
                getString(
                        data,
                        "postalCode",
                        ""
                )
        );

        // =====================================================
        // WEBSITE
        // =====================================================

        model.setWebsite(
                getString(
                        data,
                        "website",
                        ""
                )
        );

        // =====================================================
        // OPERATING HOURS
        // =====================================================

        model.setMondayHours(
                getString(
                        data,
                        "mondayHours",
                        "08:00 AM - 08:00 PM"
                )
        );

        model.setTuesdayHours(
                getString(
                        data,
                        "tuesdayHours",
                        "08:00 AM - 08:00 PM"
                )
        );

        model.setWednesdayHours(
                getString(
                        data,
                        "wednesdayHours",
                        "08:00 AM - 08:00 PM"
                )
        );

        model.setThursdayHours(
                getString(
                        data,
                        "thursdayHours",
                        "08:00 AM - 08:00 PM"
                )
        );

        model.setFridayHours(
                getString(
                        data,
                        "fridayHours",
                        "08:00 AM - 08:00 PM"
                )
        );

        model.setSaturdayHours(
                getString(
                        data,
                        "saturdayHours",
                        "09:00 AM - 04:00 PM"
                )
        );

        model.setSundayHours(
                getString(
                        data,
                        "sundayHours",
                        "Emergency Only"
                )
        );

        // =====================================================
        // ACTIVE DAYS
        // =====================================================

        model.setMondayActive(
                getBoolean(
                        data,
                        "mondayActive",
                        true
                )
        );

        model.setTuesdayActive(
                getBoolean(
                        data,
                        "tuesdayActive",
                        true
                )
        );

        model.setWednesdayActive(
                getBoolean(
                        data,
                        "wednesdayActive",
                        true
                )
        );

        model.setThursdayActive(
                getBoolean(
                        data,
                        "thursdayActive",
                        true
                )
        );

        model.setFridayActive(
                getBoolean(
                        data,
                        "fridayActive",
                        true
                )
        );

        model.setSaturdayActive(
                getBoolean(
                        data,
                        "saturdayActive",
                        true
                )
        );

        model.setSundayActive(
                getBoolean(
                        data,
                        "sundayActive",
                        false
                )
        );

        // =====================================================
        // EMERGENCY SERVICES
        // =====================================================

        model.setAmbulance24x7(
                getBoolean(
                        data,
                        "ambulance24x7",
                        false
                )
        );

        model.setEmergencyWard24x7(
                getBoolean(
                        data,
                        "emergencyWard24x7",
                        false
                )
        );

        model.setTraumaUnit(
                getBoolean(
                        data,
                        "traumaUnit",
                        false
                )
        );

        // =====================================================
        // MEDICAL TOURISM
        // =====================================================

        model.setMedicalTourismAvailable(
                getBoolean(
                        data,
                        "medicalTourismAvailable",
                        false
                )
        );

        model.setInternationalDeskAvailable(
                getBoolean(
                        data,
                        "internationalDeskAvailable",
                        false
                )
        );

        model.setAirportPickupAvailable(
                getBoolean(
                        data,
                        "airportPickupAvailable",
                        false
                )
        );

        model.setPrimaryLanguage(
                getString(
                        data,
                        "primaryLanguage",
                        "English"
                )
        );

        // =====================================================
        // IMAGES
        // =====================================================

        model.setHospitalFrontImage(
                getString(
                        data,
                        "hospitalFrontImage",
                        ""
                )
        );

        model.setReceptionImage(
                getString(
                        data,
                        "receptionImage",
                        ""
                )
        );

        model.setEmergencyImage(
                getString(
                        data,
                        "emergencyImage",
                        ""
                )
        );

        // =====================================================
        // DOCUMENTS
        // =====================================================

        model.setHospitalRegistrationDocument(
                getString(
                        data,
                        "hospitalRegistrationDocument",
                        ""
                )
        );

        model.setMedicalLicenseDocument(
                getString(
                        data,
                        "medicalLicenseDocument",
                        ""
                )
        );

        model.setNabhAccreditationDocument(
                getString(
                        data,
                        "nabhAccreditationDocument",
                        ""
                )
        );

        // =====================================================
        // ADMINISTRATOR
        // =====================================================

        model.setAdminName(
                getString(
                        data,
                        "adminName",
                        ""
                )
        );

        model.setAdminEmail(
                getString(
                        data,
                        "adminEmail",
                        ""
                )
        );

        model.setNotificationPreference(
                getString(
                        data,
                        "notificationPreference",
                        "All Notifications"
                )
        );

        model.setEmailNotifications(
                getBoolean(
                        data,
                        "emailNotifications",
                        true
                )
        );

        model.setSecurityAlerts(
                getBoolean(
                        data,
                        "securityAlerts",
                        true
                )
        );
    }

    // =========================================================
    // MODEL → FIRESTORE MAP
    // =========================================================

    private Map<String, Object> convertModelToMap(
            HospitalProfileModel model) {

        Map<String, Object> data =
                new HashMap<>();

        // =====================================================
        // BASIC INFORMATION
        // =====================================================

        data.put(
                "uid",
                safe(model.getUid())
        );

        data.put(
                "hospitalName",
                safe(model.getHospitalName())
        );

        data.put(
                "registrationNumber",
                safe(model.getRegistrationNumber())
        );

        data.put(
                "hospitalType",
                safe(model.getHospitalType())
        );

        data.put(
                "establishedYear",
                safe(model.getEstablishedYear())
        );

        // =====================================================
        // CONTACT
        // =====================================================

        data.put(
                "email",
                safe(model.getEmailAddress())
        );

        data.put(
                "emailAddress",
                safe(model.getEmailAddress())
        );

        data.put(
                "contact",
                safe(model.getPhoneNumber())
        );

        data.put(
                "phoneNumber",
                safe(model.getPhoneNumber())
        );

        data.put(
                "website",
                safe(model.getWebsite())
        );

        // =====================================================
        // ADDRESS
        // =====================================================

        data.put(
                "address",
                safe(model.getAddress())
        );

        data.put(
                "city",
                safe(model.getCity())
        );

        data.put(
                "state",
                safe(model.getState())
        );

        data.put(
                "postalCode",
                safe(model.getPostalCode())
        );

        // =====================================================
        // OPERATING HOURS
        // =====================================================

        data.put(
                "mondayHours",
                safe(model.getMondayHours())
        );

        data.put(
                "tuesdayHours",
                safe(model.getTuesdayHours())
        );

        data.put(
                "wednesdayHours",
                safe(model.getWednesdayHours())
        );

        data.put(
                "thursdayHours",
                safe(model.getThursdayHours())
        );

        data.put(
                "fridayHours",
                safe(model.getFridayHours())
        );

        data.put(
                "saturdayHours",
                safe(model.getSaturdayHours())
        );

        data.put(
                "sundayHours",
                safe(model.getSundayHours())
        );

        // =====================================================
        // ACTIVE DAYS
        // =====================================================

        data.put(
                "mondayActive",
                model.isMondayActive()
        );

        data.put(
                "tuesdayActive",
                model.isTuesdayActive()
        );

        data.put(
                "wednesdayActive",
                model.isWednesdayActive()
        );

        data.put(
                "thursdayActive",
                model.isThursdayActive()
        );

        data.put(
                "fridayActive",
                model.isFridayActive()
        );

        data.put(
                "saturdayActive",
                model.isSaturdayActive()
        );

        data.put(
                "sundayActive",
                model.isSundayActive()
        );

        // =====================================================
        // EMERGENCY
        // =====================================================

        data.put(
                "ambulance24x7",
                model.isAmbulance24x7()
        );

        data.put(
                "emergencyWard24x7",
                model.isEmergencyWard24x7()
        );

        data.put(
                "traumaUnit",
                model.isTraumaUnit()
        );

        // =====================================================
        // MEDICAL TOURISM
        // =====================================================

        data.put(
                "medicalTourismAvailable",
                model.isMedicalTourismAvailable()
        );

        data.put(
                "internationalDeskAvailable",
                model.isInternationalDeskAvailable()
        );

        data.put(
                "airportPickupAvailable",
                model.isAirportPickupAvailable()
        );

        data.put(
                "primaryLanguage",
                safe(model.getPrimaryLanguage())
        );

        // =====================================================
        // IMAGES
        // =====================================================

        data.put(
                "hospitalFrontImage",
                safe(model.getHospitalFrontImage())
        );

        data.put(
                "receptionImage",
                safe(model.getReceptionImage())
        );

        data.put(
                "emergencyImage",
                safe(model.getEmergencyImage())
        );

        // =====================================================
        // DOCUMENTS
        // =====================================================

        data.put(
                "hospitalRegistrationDocument",
                safe(
                        model
                                .getHospitalRegistrationDocument()
                )
        );

        data.put(
                "medicalLicenseDocument",
                safe(
                        model
                                .getMedicalLicenseDocument()
                )
        );

        data.put(
                "nabhAccreditationDocument",
                safe(
                        model
                                .getNabhAccreditationDocument()
                )
        );

        // =====================================================
        // ADMINISTRATOR
        // =====================================================

        data.put(
                "adminName",
                safe(model.getAdminName())
        );

        data.put(
                "adminEmail",
                safe(model.getAdminEmail())
        );

        data.put(
                "notificationPreference",
                safe(
                        model
                                .getNotificationPreference()
                )
        );

        data.put(
                "emailNotifications",
                model.isEmailNotifications()
        );

        data.put(
                "securityAlerts",
                model.isSecurityAlerts()
        );

        return data;
    }

    // =========================================================
    // DEFAULT MODEL
    // =========================================================

    private HospitalProfileModel createDefaultModel(
            String hospitalId) {

        HospitalProfileModel model =
                new HospitalProfileModel();

        model.setUid(
                hospitalId
        );

        model.setMondayHours(
                "08:00 AM - 08:00 PM"
        );

        model.setTuesdayHours(
                "08:00 AM - 08:00 PM"
        );

        model.setWednesdayHours(
                "08:00 AM - 08:00 PM"
        );

        model.setThursdayHours(
                "08:00 AM - 08:00 PM"
        );

        model.setFridayHours(
                "08:00 AM - 08:00 PM"
        );

        model.setSaturdayHours(
                "09:00 AM - 04:00 PM"
        );

        model.setSundayHours(
                "Emergency Only"
        );

        model.setMondayActive(true);
        model.setTuesdayActive(true);
        model.setWednesdayActive(true);
        model.setThursdayActive(true);
        model.setFridayActive(true);
        model.setSaturdayActive(true);
        model.setSundayActive(false);

        model.setPrimaryLanguage(
                "English"
        );

        model.setNotificationPreference(
                "All Notifications"
        );

        model.setEmailNotifications(
                true
        );

        model.setSecurityAlerts(
                true
        );

        return model;
    }

    // =========================================================
    // GET STRING
    // =========================================================

    private String getString(
            Map<String, Object> data,
            String field,
            String defaultValue) {

        Object value =
                data.get(field);

        if (value == null) {

            return defaultValue;
        }

        return String.valueOf(value).trim();
    }

    // =========================================================
    // GET BOOLEAN
    // =========================================================

    private boolean getBoolean(
            Map<String, Object> data,
            String field,
            boolean defaultValue) {

        Object value =
                data.get(field);

        if (value == null) {

            return defaultValue;
        }

        if (value instanceof Boolean) {

            return (Boolean) value;
        }

        if (value instanceof String) {

            String text =
                    ((String) value).trim();

            if ("true".equalsIgnoreCase(text)) {

                return true;
            }

            if ("false".equalsIgnoreCase(text)) {

                return false;
            }
        }

        return defaultValue;
    }

    // =========================================================
    // SAFE STRING
    // =========================================================

    private String safe(String value) {

        if (value == null) {

            return "";
        }

        return value.trim();
    }

    // =========================================================
    // BLANK
    // =========================================================

    private boolean isBlank(String value) {

        return value == null
                || value.trim().isEmpty();
    }
}