package com.healthsphere.controller.patient;

import java.util.List;

import com.healthsphere.dao.patient.NotificationDAO;
import com.healthsphere.model.Notification;
import com.healthsphere.util.SessionManager;

public class NotificationController {

    private final NotificationDAO notificationDAO;

    public NotificationController() {
        this.notificationDAO =
                new NotificationDAO();
    }

    // ============================================================
    // CREATE NOTIFICATION FOR CURRENT PATIENT
    // ============================================================

    public Notification createNotification(
            String title,
            String description,
            String type) {

        validateSession();

        String uid =
                SessionManager
                        .getCurrentUser()
                        .getUid();

        Notification notification =
                new Notification();

        notification.setPatientUid(uid);

        notification.setTitle(
                clean(title)
        );

        notification.setDescription(
                clean(description)
        );

        notification.setType(
                clean(type)
        );

        notification.setRead(false);

        return notificationDAO.createNotification(
                notification
        );
    }

    // ============================================================
    // GET CURRENT PATIENT NOTIFICATIONS
    // ============================================================

    public List<Notification>
    getCurrentPatientNotifications() {

        validateSession();

        String uid =
                SessionManager
                        .getCurrentUser()
                        .getUid();

        return notificationDAO
                .getPatientNotifications(uid);
    }

    // ============================================================
    // MARK AS READ
    // ============================================================

    public void markAsRead(
            String notificationId) {

        validateSession();

        notificationDAO.markAsRead(
                notificationId
        );
    }

    // ============================================================
    // MARK AS UNREAD
    // ============================================================

    public void markAsUnread(
            String notificationId) {

        validateSession();

        notificationDAO.markAsUnread(
                notificationId
        );
    }

    // ============================================================
    // DELETE
    // ============================================================

    public void deleteNotification(
            String notificationId) {

        validateSession();

        notificationDAO.deleteNotification(
                notificationId
        );
    }

    // ============================================================
    // UNREAD COUNT
    // ============================================================

    public int getUnreadCount() {

        List<Notification> notifications =
                getCurrentPatientNotifications();

        int count = 0;

        for (Notification notification :
                notifications) {

            if (!notification.isRead()) {
                count++;
            }
        }

        return count;
    }

    // ============================================================
    // SESSION VALIDATION
    // ============================================================

    private void validateSession() {

        if (!SessionManager.isLoggedIn()) {

            throw new IllegalStateException(
                    "No active user session."
            );
        }

        if (SessionManager
                .getCurrentUser()
                .getUid() == null ||
                SessionManager
                        .getCurrentUser()
                        .getUid()
                        .isBlank()) {

            throw new IllegalStateException(
                    "Current user UID is missing."
            );
        }
    }

    // ============================================================
    // CLEAN
    // ============================================================

    private String clean(String value) {

        if (value == null) {
            return "";
        }

        return value.trim();
    }
}