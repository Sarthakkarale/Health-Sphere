package com.healthsphere.dao.patient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.healthsphere.config.FirebaseConfig;
import com.healthsphere.exceptions.DatabaseException;
import com.healthsphere.model.Notification;

public class NotificationDAO {

    private final Firestore db;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public NotificationDAO() {
        this.db = FirebaseConfig.getFirestore();
    }

    // ============================================================
    // CREATE NOTIFICATION
    // ============================================================

    public Notification createNotification(
            Notification notification) {

        try {

            if (notification.getNotificationId() == null ||
                    notification.getNotificationId().isBlank()) {

                notification.setNotificationId(
                        UUID.randomUUID().toString()
                );
            }

            if (notification.getCreatedAt() == null ||
                    notification.getCreatedAt().isBlank()) {

                notification.setCreatedAt(
                        LocalDateTime.now()
                                .format(FORMATTER)
                );
            }

            db.collection("notifications")
                    .document(
                            notification.getNotificationId()
                    )
                    .set(notification)
                    .get();

            System.out.println(
                    "Notification created successfully."
            );

            return notification;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to create notification.",
                    e
            );
        }
    }

    // ============================================================
    // GET PATIENT NOTIFICATIONS
    // ============================================================

    public List<Notification> getPatientNotifications(
            String patientUid) {

        try {

            ApiFuture<QuerySnapshot> future =
                    db.collection("notifications")
                            .whereEqualTo(
                                    "patientUid",
                                    patientUid
                            )
                            .get();

            QuerySnapshot snapshot =
                    future.get();

            List<Notification> notifications =
                    new ArrayList<>();

            for (DocumentSnapshot document :
                    snapshot.getDocuments()) {

                Notification notification =
                        document.toObject(
                                Notification.class
                        );

                if (notification != null) {

                    notifications.add(
                            notification
                    );
                }
            }

            return notifications;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to retrieve notifications.",
                    e
            );
        }
    }

    // ============================================================
    // MARK NOTIFICATION AS READ
    // ============================================================

    public void markAsRead(
            String notificationId) {

        try {

            db.collection("notifications")
                    .document(notificationId)
                    .update(
                            "read",
                            true
                    )
                    .get();

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to mark notification as read.",
                    e
            );
        }
    }

    // ============================================================
    // MARK NOTIFICATION AS UNREAD
    // ============================================================

    public void markAsUnread(
            String notificationId) {

        try {

            db.collection("notifications")
                    .document(notificationId)
                    .update(
                            "read",
                            false
                    )
                    .get();

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to mark notification as unread.",
                    e
            );
        }
    }

    // ============================================================
    // DELETE NOTIFICATION
    // ============================================================

    public void deleteNotification(
            String notificationId) {

        try {

            db.collection("notifications")
                    .document(notificationId)
                    .delete()
                    .get();

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to delete notification.",
                    e
            );
        }
    }
}