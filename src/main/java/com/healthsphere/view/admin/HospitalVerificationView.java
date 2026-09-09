package com.healthsphere.view.admin;

import com.healthsphere.controller.admin.HospitalVerificationController;
import com.healthsphere.model.HospitalVerification;
import com.healthsphere.util.SessionManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;

public class HospitalVerificationView {

    private final Stage stage;

    private final HospitalVerificationController controller;

    private VBox rowList;

    /*
     * Root UI is created only once.
     * This prevents getScene() from rebuilding
     * the entire page every time it is called.
     */
    private final ScrollPane root;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public HospitalVerificationView(Stage stage) {

        this.stage = stage;

        this.controller =
                new HospitalVerificationController();

        this.root = createView();
    }

    // =========================================================
    // CREATE VIEW
    // =========================================================

    private ScrollPane createView() {

        VBox mainContainer =
                new VBox(24);

        mainContainer.setPadding(
                new Insets(28)
        );

        mainContainer.setStyle(
                "-fx-background-color: #F8FAFC;"
        );

        // =====================================================
        // HEADER
        // =====================================================

        HBox header =
                createHeader();

        // =====================================================
        // AI OCR BANNER
        // =====================================================

        HBox aiBanner =
                createAiOcrBanner();

        // =====================================================
        // VERIFICATION CARD
        // =====================================================

        VBox card =
                new VBox(16);

        card.setPadding(
                new Insets(20)
        );

        card.setStyle(
                "-fx-background-color: #FFFFFF;" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: #E2E8F0;" +
                "-fx-border-radius: 14;"
        );

        card.setEffect(
                getCardShadow()
        );

        // =====================================================
        // TABLE HEADER
        // =====================================================

        HBox tableHeader =
                new HBox();

        tableHeader.setPadding(
                new Insets(
                        10,
                        16,
                        10,
                        16
                )
        );

        tableHeader.setStyle(
                "-fx-background-color: #F8FAFC;" +
                "-fx-background-radius: 8;" +
                "-fx-border-color: #E2E8F0;" +
                "-fx-border-width: 0 0 1 0;"
        );

        tableHeader.getChildren().addAll(
                createColHeader(
                        "APPLICANT HOSPITAL",
                        240
                ),

                createColHeader(
                        "NABH LICENSE #",
                        160
                ),

                createColHeader(
                        "AI OCR MATCH",
                        160
                ),

                createColHeader(
                        "STATUS",
                        120
                ),

                createColHeader(
                        "ACTION",
                        150
                )
        );

        // =====================================================
        // DYNAMIC ROW CONTAINER
        // =====================================================

        rowList =
                new VBox(0);

        card.getChildren().addAll(
                tableHeader,
                rowList
        );

        mainContainer.getChildren().addAll(
                header,
                aiBanner,
                card
        );

        // =====================================================
        // SCROLL PANE
        // =====================================================

        ScrollPane scroll =
                new ScrollPane(
                        mainContainer
                );

        scroll.setFitToWidth(true);

        scroll.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-background: #F8FAFC;" +
                "-fx-border-color: transparent;"
        );

        /*
         * Load Firestore data after rowList has been created.
         */
        loadVerificationData();

        return scroll;
    }

    // =========================================================
    // VIEW
    // =========================================================

    public Node getView() {

        return root;
    }

    // =========================================================
    // SCENE
    // =========================================================

    /*
     * This is the navigation method used by your project.
     *
     * Example:
     *
     * stage.setScene(
     *     new HospitalVerificationView(stage).getScene()
     * );
     */
    public Scene getScene() {

        return new Scene(root);
    }

    // =========================================================
    // CREATE SCENE COMPATIBILITY
    // =========================================================

    public Scene createScene() {

        return getScene();
    }

    // =========================================================
    // LOAD VERIFICATION DATA
    // =========================================================

    private void loadVerificationData() {

        if (rowList == null) {
            return;
        }

        rowList.getChildren().clear();

        try {

            List<HospitalVerification> verifications =
                    controller.getAllVerifications();

            if (verifications == null ||
                    verifications.isEmpty()) {

                Label emptyLabel =
                        new Label(
                                "No hospital verification records found."
                        );

                emptyLabel.setFont(
                        Font.font(
                                "Segoe UI",
                                FontWeight.NORMAL,
                                13
                        )
                );

                emptyLabel.setTextFill(
                        Color.web("#64748B")
                );

                emptyLabel.setPadding(
                        new Insets(25)
                );

                rowList.getChildren().add(
                        emptyLabel
                );

                return;
            }

            for (
                    HospitalVerification verification
                    : verifications
            ) {

                if (verification != null) {

                    rowList.getChildren().add(
                            createVerifyRow(
                                    verification
                            )
                    );
                }
            }

        } catch (Exception e) {

            Label errorLabel =
                    new Label(
                            "Unable to load hospital verification records."
                    );

            errorLabel.setFont(
                    Font.font(
                            "Segoe UI",
                            FontWeight.NORMAL,
                            13
                    )
            );

            errorLabel.setTextFill(
                    Color.web("#DC2626")
            );

            errorLabel.setPadding(
                    new Insets(25)
            );

            rowList.getChildren().add(
                    errorLabel
            );

            e.printStackTrace();
        }
    }

    // =========================================================
    // HEADER
    // =========================================================

    private HBox createHeader() {

        HBox header =
                new HBox(16);

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox titleBox =
                new VBox(4);

        Label title =
                new Label(
                        "Hospital Verification & NABH Audit"
                );

        title.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        22
                )
        );

        title.setTextFill(
                Color.web("#0F172A")
        );

        Label sub =
                new Label(
                        "AI-driven verification of clinical licenses, NABH accreditation, and infrastructure compliance."
                );

        sub.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.NORMAL,
                        12
                )
        );

        sub.setTextFill(
                Color.web("#64748B")
        );

        sub.setWrapText(true);

        titleBox.getChildren().addAll(
                title,
                sub
        );

        Region sp =
                new Region();

        HBox.setHgrow(
                sp,
                Priority.ALWAYS
        );

        Button scanBtn =
                new Button(
                        "⚡ Run AI OCR Audit"
                );

        scanBtn.setStyle(
                "-fx-background-color: #2563EB;" +
                "-fx-text-fill: #FFFFFF;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 8 18;" +
                "-fx-cursor: hand;"
        );

        scanBtn.setOnAction(
                e ->
                        handleRunOcrScan()
        );

        header.getChildren().addAll(
                titleBox,
                sp,
                scanBtn
        );

        return header;
    }

    // =========================================================
    // AI OCR BANNER
    // =========================================================

    private HBox createAiOcrBanner() {

        HBox banner =
                new HBox(20);

        banner.setAlignment(
                Pos.CENTER_LEFT
        );

        banner.setPadding(
                new Insets(
                        18,
                        20,
                        18,
                        20
                )
        );

        banner.setStyle(
                "-fx-background-color: linear-gradient(to right, #EEF2FF, #ECFDF5);" +
                "-fx-border-color: #C7D2FE;" +
                "-fx-border-radius: 14;" +
                "-fx-background-radius: 14;"
        );

        banner.setEffect(
                getCardShadow()
        );

        ImageView img =
                createSafeImageView(
                        "https://images.unsplash.com/photo-1516549655169-df83a0774514?w=300",
                        110,
                        70
                );

        Rectangle clip =
                new Rectangle(
                        110,
                        70
                );

        clip.setArcWidth(10);
        clip.setArcHeight(10);

        img.setClip(clip);

        VBox textGroup =
                new VBox(6);

        Label aiTitle =
                new Label(
                        "🔍 Automated Document Vision OCR & NABH Verification Engine"
                );

        aiTitle.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        14
                )
        );

        aiTitle.setTextFill(
                Color.web("#1E40AF")
        );

        aiTitle.setWrapText(true);

        Label aiDesc =
                new Label(
                        "AI extracts certificate serial numbers, cross-checks official NABH government registries, and alerts on fraudulent or expired clinical licenses instantly."
                );

        aiDesc.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.NORMAL,
                        12
                )
        );

        aiDesc.setTextFill(
                Color.web("#334155")
        );

        aiDesc.setWrapText(true);

        textGroup.getChildren().addAll(
                aiTitle,
                aiDesc
        );

        HBox.setHgrow(
                textGroup,
                Priority.ALWAYS
        );

        banner.getChildren().addAll(
                img,
                textGroup
        );

        return banner;
    }

    // =========================================================
    // VERIFICATION ROW
    // =========================================================

    private HBox createVerifyRow(
            HospitalVerification verification
    ) {

        HBox row =
                new HBox();

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        row.setPadding(
                new Insets(
                        12,
                        16,
                        12,
                        16
                )
        );

        row.setStyle(
                "-fx-background-color: #FFFFFF;" +
                "-fx-border-color: #F1F5F9;" +
                "-fx-border-width: 0 0 1 0;"
        );

        // =====================================================
        // HOSPITAL NAME
        // =====================================================

        String hospitalName =
                safe(
                        verification.getHospitalName()
                );

        if (hospitalName.isBlank()) {

            hospitalName =
                    "Unknown Hospital";
        }

        Label nameLbl =
                new Label(
                        hospitalName
                );

        nameLbl.setPrefWidth(240);

        nameLbl.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        13
                )
        );

        nameLbl.setTextFill(
                Color.web("#0F172A")
        );

        nameLbl.setWrapText(true);

        // =====================================================
        // NABH LICENSE
        // =====================================================

        String licenseNumber =
                safe(
                        verification.getNabhLicenseNumber()
                );

        if (licenseNumber.isBlank()) {

            licenseNumber =
                    "N/A";
        }

        Label licLbl =
                new Label(
                        licenseNumber
                );

        licLbl.setPrefWidth(160);

        licLbl.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.SEMI_BOLD,
                        12
                )
        );

        licLbl.setTextFill(
                Color.web("#334155")
        );

        // =====================================================
        // OCR MATCH
        // =====================================================

        String match =
                String.format(
                        "%.1f%%",
                        verification.getAiOcrMatchScore()
                );

        Label matchLbl =
                new Label(
                        match
                );

        matchLbl.setPrefWidth(160);

        matchLbl.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        12
                )
        );

        matchLbl.setTextFill(
                getOcrMatchColor(
                        verification.getAiOcrMatchScore()
                )
        );

        // =====================================================
        // STATUS
        // =====================================================

        String status =
                safe(
                        verification.getVerificationStatus()
                );

        if (status.isBlank()) {

            status =
                    "PENDING";

        } else {

            status =
                    status
                            .trim()
                            .toUpperCase();

            /*
             * Display canonical approved status as APPROVED.
             */
            if ("VERIFIED".equals(status)) {

                status =
                        "APPROVED";
            }
        }

        HBox stBox =
                new HBox();

        stBox.setPrefWidth(120);

        stBox.setAlignment(
                Pos.CENTER_LEFT
        );

        String statusColor =
                getStatusColor(status);

        String statusBackground =
                getStatusBackground(status);

        Label stBadge =
                new Label(
                        status
                );

        stBadge.setStyle(
                String.format(
                        "-fx-background-color: %s;" +
                        "-fx-text-fill: %s;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 10px;" +
                        "-fx-padding: 3 8;" +
                        "-fx-background-radius: 12;",
                        statusBackground,
                        statusColor
                )
        );

        stBox.getChildren().add(
                stBadge
        );

        // =====================================================
        // ACTION BUTTON
        // =====================================================

        Button btn =
                new Button(
                        "Verify Documents"
                );

        btn.setStyle(
                "-fx-background-color: #EFF6FF;" +
                "-fx-text-fill: #2563EB;" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 6;" +
                "-fx-cursor: hand;"
        );

        btn.setOnAction(
                e ->
                        handleAuditDocument(
                                verification
                        )
        );

        row.getChildren().addAll(
                nameLbl,
                licLbl,
                matchLbl,
                stBox,
                btn
        );

        return row;
    }

    // =========================================================
    // VERIFY DOCUMENT
    // =========================================================

    private void handleAuditDocument(
            HospitalVerification verification
    ) {

        String hospitalName =
                safe(
                        verification.getHospitalName()
                );

        String licenseNumber =
                safe(
                        verification.getNabhLicenseNumber()
                );

        String match =
                String.format(
                        "%.1f%%",
                        verification.getAiOcrMatchScore()
                );

        Alert alert =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        alert.setTitle(
                "NABH Document Verification"
        );

        alert.setHeaderText(
                "Audit Details: "
                        + hospitalName
                        + " ("
                        + licenseNumber
                        + ")"
        );

        alert.setContentText(
                "AI Confidence Score: "
                        + match
                        + "\n\n"
                        + "Select verification action:"
        );

        ButtonType approveBtn =
                new ButtonType(
                        "Approve NABH Accreditation"
                );

        ButtonType rejectBtn =
                new ButtonType(
                        "Reject / Flag Registration"
                );

        ButtonType cancelBtn =
                new ButtonType(
                        "Close",
                        ButtonBar.ButtonData.CANCEL_CLOSE
                );

        alert.getButtonTypes().setAll(
                approveBtn,
                rejectBtn,
                cancelBtn
        );

        Optional<ButtonType> result =
                alert.showAndWait();

        if (result.isEmpty()) {
            return;
        }

        if (result.get() == approveBtn) {

            handleApprove(
                    verification
            );

        } else if (result.get() == rejectBtn) {

            handleReject(
                    verification
            );
        }
    }

    // =========================================================
    // APPROVE
    // =========================================================

    private void handleApprove(
            HospitalVerification verification
    ) {

        String verificationId =
                safe(
                        verification.getVerificationId()
                );

        if (verificationId.isBlank()) {

            showAlert(
                    "Error",
                    "Verification ID is missing."
            );

            return;
        }

        String adminUid =
                getCurrentAdminUid();

        if (adminUid == null) {
            return;
        }

        try {

            boolean success =
                    controller.approveVerification(
                            verificationId,
                            adminUid
                    );

            if (success) {

                showAlert(
                        "Success",
                        safe(
                                verification.getHospitalName()
                        )
                                + " NABH Accreditation has been APPROVED!"
                );

                loadVerificationData();

            } else {

                showAlert(
                        "Error",
                        "Unable to approve the hospital verification."
                );
            }

        } catch (Exception e) {

            e.printStackTrace();

            showAlert(
                    "Firebase Error",
                    "An error occurred while approving the verification.\n\n"
                            + safe(
                                    e.getMessage()
                            )
            );
        }
    }

    // =========================================================
    // REJECT
    // =========================================================

    private void handleReject(
            HospitalVerification verification
    ) {

        TextInputDialog dialog =
                new TextInputDialog();

        dialog.setTitle(
                "Reject Hospital Verification"
        );

        dialog.setHeaderText(
                "Reject: "
                        + safe(
                                verification.getHospitalName()
                        )
        );

        dialog.setContentText(
                "Enter rejection reason:"
        );

        Optional<String> result =
                dialog.showAndWait();

        if (result.isEmpty()) {
            return;
        }

        String reason =
                result.get().trim();

        if (reason.isBlank()) {

            showAlert(
                    "Validation Error",
                    "Rejection reason cannot be empty."
            );

            return;
        }

        String verificationId =
                safe(
                        verification.getVerificationId()
                );

        if (verificationId.isBlank()) {

            showAlert(
                    "Error",
                    "Verification ID is missing."
            );

            return;
        }

        String adminUid =
                getCurrentAdminUid();

        if (adminUid == null) {
            return;
        }

        try {

            boolean success =
                    controller.rejectVerification(
                            verificationId,
                            adminUid,
                            reason
                    );

            if (success) {

                showAlert(
                        "Rejected",
                        safe(
                                verification.getHospitalName()
                        )
                                + " registration has been rejected."
                );

                loadVerificationData();

            } else {

                showAlert(
                        "Error",
                        "Unable to reject the hospital verification."
                );
            }

        } catch (Exception e) {

            e.printStackTrace();

            showAlert(
                    "Firebase Error",
                    "An error occurred while rejecting the verification.\n\n"
                            + safe(
                                    e.getMessage()
                            )
            );
        }
    }

    // =========================================================
    // CURRENT ADMIN UID
    // =========================================================

    private String getCurrentAdminUid() {

        try {

            if (SessionManager.getCurrentUser() == null) {

                showAlert(
                        "Session Error",
                        "No active admin session found. Please login again."
                );

                return null;
            }

            String uid =
                    SessionManager
                            .getCurrentUser()
                            .getUid();

            if (uid == null ||
                    uid.isBlank()) {

                showAlert(
                        "Session Error",
                        "Admin UID is missing. Please login again."
                );

                return null;
            }

            return uid;

        } catch (IllegalStateException e) {

            showAlert(
                    "Session Error",
                    "No active admin session found. Please login again."
            );

            return null;

        } catch (Exception e) {

            e.printStackTrace();

            showAlert(
                    "Session Error",
                    "Unable to retrieve the current admin session."
            );

            return null;
        }
    }

    // =========================================================
    // AI OCR AUDIT
    // =========================================================

    private void handleRunOcrScan() {

        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

        alert.setTitle(
                "AI Audit"
        );

        alert.setHeaderText(
                "⚡ AI Vision OCR Audit"
        );

        alert.setContentText(
                "The OCR audit engine will process the available "
                        + "hospital verification documents."
        );

        alert.showAndWait();
    }

    // =========================================================
    // STATUS COLORS
    // =========================================================

    private String getStatusColor(
            String status
    ) {

        if (status == null) {
            return "#64748B";
        }

        switch (
                status.toUpperCase()
        ) {

            case "VERIFIED":
            case "APPROVED":

                return "#059669";

            case "REJECTED":
            case "REJECT":

                return "#DC2626";

            case "PENDING":

                return "#D97706";

            default:

                return "#64748B";
        }
    }

    // =========================================================
    // STATUS BACKGROUND
    // =========================================================

    private String getStatusBackground(
            String status
    ) {

        if (status == null) {
            return "#F1F5F9";
        }

        switch (
                status.toUpperCase()
        ) {

            case "VERIFIED":
            case "APPROVED":

                return "#ECFDF5";

            case "REJECTED":
            case "REJECT":

                return "#FEF2F2";

            case "PENDING":

                return "#FFFBEB";

            default:

                return "#F1F5F9";
        }
    }

    // =========================================================
    // OCR MATCH COLOR
    // =========================================================

    private Color getOcrMatchColor(
            double score
    ) {

        if (score >= 90) {

            return Color.web(
                    "#059669"
            );

        } else if (score >= 70) {

            return Color.web(
                    "#D97706"
            );

        } else {

            return Color.web(
                    "#DC2626"
            );
        }
    }

    // =========================================================
    // ALERT
    // =========================================================

    private void showAlert(
            String title,
            String message
    ) {

        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

        alert.setTitle(
                title
        );

        alert.setHeaderText(
                null
        );

        alert.setContentText(
                message == null
                        ? "An unexpected error occurred."
                        : message
        );

        alert.showAndWait();
    }

    // =========================================================
    // SAFE IMAGE
    // =========================================================

    private ImageView createSafeImageView(
            String url,
            double width,
            double height
    ) {

        ImageView img =
                new ImageView();

        try {

            Image image =
                    new Image(
                            url,
                            width,
                            height,
                            true,
                            true,
                            true
                    );

            img.setImage(
                    image
            );

        } catch (Exception ignored) {

            /*
             * Image failure must never crash
             * the Hospital Verification page.
             */
        }

        img.setFitWidth(
                width
        );

        img.setFitHeight(
                height
        );

        img.setPreserveRatio(
                true
        );

        return img;
    }

    // =========================================================
    // TABLE HEADER
    // =========================================================

    private Label createColHeader(
            String title,
            double width
    ) {

        Label lbl =
                new Label(
                        title
                );

        lbl.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        11
                )
        );

        lbl.setTextFill(
                Color.web("#475569")
        );

        lbl.setPrefWidth(
                width
        );

        return lbl;
    }

    // =========================================================
    // CARD SHADOW
    // =========================================================

    private DropShadow getCardShadow() {

        DropShadow shadow =
                new DropShadow();

        shadow.setColor(
                Color.rgb(
                        15,
                        23,
                        42,
                        0.05
                )
        );

        shadow.setRadius(
                10
        );

        shadow.setOffsetY(
                4
        );

        return shadow;
    }

    // =========================================================
    // SAFE STRING
    // =========================================================

    private String safe(
            String value
    ) {

        return value == null
                ? ""
                : value;
    }
}