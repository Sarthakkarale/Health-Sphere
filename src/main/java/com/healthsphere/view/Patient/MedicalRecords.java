package com.healthsphere.view.Patient;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import java.util.List;

import com.healthsphere.controller.patient.MedicalReportController;
import com.healthsphere.controller.patient.PrescriptionController;
import com.healthsphere.model.MedicalReport;
import com.healthsphere.model.Prescription;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MedicalRecords {

    private final Stage stage;

    private final MedicalReportController medicalReportController;

    private final PrescriptionController prescriptionController;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public MedicalRecords(Stage stage) {

        this.stage = stage;

        this.medicalReportController =
                new MedicalReportController();

        this.prescriptionController =
                new PrescriptionController();
    }


    // =========================================================
    // MAIN SCENE
    // =========================================================

    public Scene getScene() {

        VBox content =
                new VBox(22);

        content.setPadding(
                new Insets(0)
        );


        // =====================================================
        // IMAGE GALLERY
        // =====================================================

        HBox gallery =
                createImageGallery();


        // =====================================================
        // UPLOADED MEDICAL REPORTS
        // =====================================================

        VBox reportsCard =
                PatientUI.coloredCard(
                        "📁  My Uploaded Medical Reports",
                        "#f3e8ff"
                );

        Button uploadReportButton =
                PatientUI.button(
                        "➕ Upload Medical Report",
                        this::showUploadReportDialog
                );

        reportsCard.getChildren().add(
                uploadReportButton
        );

        loadMedicalReports(
                reportsCard
        );


        // =====================================================
        // PRESCRIPTIONS
        // =====================================================

        VBox prescriptions =
                PatientUI.coloredCard(
                        "💊  Prescriptions",
                        "#dcfce7"
                );

        prescriptions.setMaxWidth(
                Double.MAX_VALUE
        );

        loadPrescriptions(
                prescriptions
        );


        // =====================================================
        // QUICK ACTIONS
        // =====================================================

        VBox quickActions =
                PatientUI.coloredCard(
                        "⚡  Quick Actions",
                        "#ede9fe"
                );

        HBox actions =
                new HBox(12);

        actions.setAlignment(
                Pos.CENTER_LEFT
        );


        Button healthPassport =
                PatientUI.button(
                        "Health Passport",
                        () -> stage.setScene(
                                new HealthPassport(stage)
                                        .getScene()
                        )
                );


        Button appointments =
                PatientUI.button(
                        "Appointments",
                        () -> stage.setScene(
                                new Appointments(stage)
                                        .getScene()
                        )
                );


        Button aiAssistant =
                PatientUI.button(
                        "AI Health Assistant",
                        () -> stage.setScene(
                                new AiHealthAssistant(stage)
                                        .getScene()
                        )
                );


        actions.getChildren().addAll(
                healthPassport,
                appointments,
                aiAssistant
        );

        quickActions.getChildren().add(
                actions
        );


        // =====================================================
        // ADD CONTENT
        // =====================================================

        content.getChildren().addAll(
                gallery,
                reportsCard,
                prescriptions,
                quickActions
        );


        // =====================================================
        // PATIENT UI
        // =====================================================

        return PatientUI.createScene(
                stage,
                "Medical Records",
                "Medical Records",
                "Access your medical history, reports, prescriptions and clinical documents.",
                content
        );
    }


    // =========================================================
    // UPLOAD REPORT DIALOG
    // =========================================================

    private void showUploadReportDialog() {

        Dialog<ButtonType> dialog =
                new Dialog<>();

        dialog.setTitle(
                "Upload Medical Report"
        );

        dialog.setHeaderText(
                "Select and upload your medical document"
        );


        VBox form =
                new VBox(15);

        form.setPadding(
                new Insets(15)
        );


        // -----------------------------------------------------
        // REPORT NAME
        // -----------------------------------------------------

        Label nameLabel =
                new Label(
                        "Report Name"
                );

        TextField reportName =
                new TextField();

        reportName.setPromptText(
                "Example: Blood Test Report"
        );


        // -----------------------------------------------------
        // REPORT TYPE
        // -----------------------------------------------------

        Label typeLabel =
                new Label(
                        "Report Type"
                );

        ComboBox<String> reportType =
                new ComboBox<>();

        reportType.getItems().addAll(
                "Lab Report",
                "Blood Test",
                "X-Ray",
                "MRI Scan",
                "CT Scan",
                "Prescription",
                "Medical Certificate",
                "Other"
        );

        reportType.setPromptText(
                "Select report type"
        );

        reportType.setMaxWidth(
                Double.MAX_VALUE
        );


        // -----------------------------------------------------
        // FILE SELECTION
        // -----------------------------------------------------

        Label fileLabel =
                new Label(
                        "No file selected"
                );

        fileLabel.setStyle(
                "-fx-text-fill: #64748b;"
        );


        final File[] selectedFile =
                new File[1];


        Button chooseFile =
                PatientUI.secondaryButton(
                        "Choose File",
                        () -> {

                            FileChooser fileChooser =
                                    new FileChooser();

                            fileChooser.setTitle(
                                    "Select Medical Report"
                            );

                            fileChooser
                                    .getExtensionFilters()
                                    .addAll(

                                            new FileChooser.ExtensionFilter(
                                                    "Medical Documents",
                                                    "*.pdf",
                                                    "*.png",
                                                    "*.jpg",
                                                    "*.jpeg"
                                            ),

                                            new FileChooser.ExtensionFilter(
                                                    "PDF Files",
                                                    "*.pdf"
                                            ),

                                            new FileChooser.ExtensionFilter(
                                                    "Image Files",
                                                    "*.png",
                                                    "*.jpg",
                                                    "*.jpeg"
                                            )
                                    );


                            File file =
                                    fileChooser.showOpenDialog(
                                            stage
                                    );


                            if (file != null) {

                                selectedFile[0] =
                                        file;

                                fileLabel.setText(
                                        file.getName()
                                );
                            }
                        }
                );


        HBox fileRow =
                new HBox(
                        12,
                        chooseFile,
                        fileLabel
                );

        fileRow.setAlignment(
                Pos.CENTER_LEFT
        );


        form.getChildren().addAll(
                nameLabel,
                reportName,
                typeLabel,
                reportType,
                fileRow
        );


        dialog.getDialogPane()
                .setContent(
                        form
                );


        ButtonType uploadButtonType =
                new ButtonType(
                        "Upload",
                        ButtonBar.ButtonData.OK_DONE
                );


        ButtonType cancelButtonType =
                new ButtonType(
                        "Cancel",
                        ButtonBar.ButtonData.CANCEL_CLOSE
                );


        dialog.getDialogPane()
                .getButtonTypes()
                .addAll(
                        uploadButtonType,
                        cancelButtonType
                );


        dialog.showAndWait()
                .ifPresent(
                        result -> {

                            if (result != uploadButtonType) {
                                return;
                            }

                            try {

                                if (selectedFile[0] == null) {

                                    showError(
                                            "Please select a file."
                                    );

                                    return;
                                }


                                if (reportName.getText() == null
                                        || reportName.getText().isBlank()) {

                                    showError(
                                            "Please enter a report name."
                                    );

                                    return;
                                }


                                if (reportType.getValue() == null
                                        || reportType.getValue().isBlank()) {

                                    showError(
                                            "Please select a report type."
                                    );

                                    return;
                                }


                                medicalReportController
                                        .uploadReport(
                                                reportName.getText(),
                                                reportType.getValue(),
                                                selectedFile[0]
                                        );


                                showSuccess(
                                        "Medical report uploaded successfully."
                                );


                                refreshPage();

                            } catch (Exception e) {

                                e.printStackTrace();

                                showError(
                                        "Unable to upload medical report.\n\n"
                                                + safeMessage(
                                                e.getMessage()
                                        )
                                );
                            }
                        }
                );
    }


    // =========================================================
    // LOAD MEDICAL REPORTS
    // =========================================================

    private void loadMedicalReports(
            VBox reportsCard) {

        VBox shimmer = com.healthsphere.util.ShimmerPlaceholder.createListShimmer(2);
        reportsCard.getChildren().add(shimmer);

        javafx.concurrent.Task<List<MedicalReport>> task = new javafx.concurrent.Task<>() {
            @Override
            protected List<MedicalReport> call() throws Exception {
                return medicalReportController.getCurrentPatientReports();
            }
        };

        task.setOnSucceeded(e -> {
            reportsCard.getChildren().remove(shimmer);
            List<MedicalReport> reports = task.getValue();
            if (reports == null || reports.isEmpty()) {
                Label empty = new Label("No uploaded medical reports available.");
                empty.setStyle("-fx-text-fill: #64748b; -fx-font-size: 14px;");
                reportsCard.getChildren().add(empty);
                return;
            }

            for (MedicalReport report : reports) {
                reportsCard.getChildren().add(medicalReportCard(report));
            }
        });

        task.setOnFailed(e -> {
            reportsCard.getChildren().remove(shimmer);
            Throwable ex = task.getException();
            if (ex != null) ex.printStackTrace();
            Label error = new Label("Unable to load uploaded medical reports.");
            error.setStyle("-fx-text-fill: #dc2626; -fx-font-weight: bold;");
            reportsCard.getChildren().add(error);
        });

        com.healthsphere.util.PatientBackgroundExecutor.execute(task);
    }


    // =========================================================
    // MEDICAL REPORT CARD
    // =========================================================

    private VBox medicalReportCard(
            MedicalReport report) {

        VBox box =
                new VBox(8);

        box.setPadding(
                new Insets(12)
        );

        box.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: #d8b4fe;" +
                "-fx-border-radius: 10;"
        );


        Label name =
                new Label(
                        safe(
                                report.getReportName()
                        )
                );

        name.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );


        Label type =
                new Label(
                        "Type: "
                                + safe(
                                report.getReportType()
                        )
                );

        type.setStyle(
                "-fx-text-fill: #64748b;" +
                "-fx-font-size: 13px;"
        );


        Label fileName =
                new Label(
                        "File: "
                                + safe(
                                report.getFileName()
                        )
                );

        fileName.setStyle(
                "-fx-text-fill: #64748b;" +
                "-fx-font-size: 13px;"
        );


        Label date =
                new Label(
                        "Uploaded: "
                                + safe(
                                report.getUploadDate()
                        )
                );

        date.setStyle(
                "-fx-text-fill: #7c3aed;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 13px;"
        );


        Button view =
                PatientUI.secondaryButton(
                        "View Report",
                        () -> openReport(
                                report
                        )
                );


        Button delete =
                createDeleteButton(
                        () -> deleteReport(
                                report
                        )
                );


        HBox buttons =
                new HBox(
                        10,
                        view,
                        delete
                );


        box.getChildren().addAll(
                name,
                type,
                fileName,
                date,
                buttons
        );


        return box;
    }


    // =========================================================
    // OPEN REPORT
    // =========================================================

    private void openReport(
            MedicalReport report) {

        try {

            String fileUrl =
                    report.getFileUrl();


            if (fileUrl == null
                    || fileUrl.isBlank()) {

                showError(
                        "Report file URL is not available."
                );

                return;
            }


            if (!Desktop.isDesktopSupported()) {

                showError(
                        "Opening files is not supported on this system."
                );

                return;
            }


            Desktop.getDesktop()
                    .browse(
                            new URI(
                                    fileUrl
                            )
                    );

        } catch (Exception e) {

            e.printStackTrace();

            showError(
                    "Unable to open the medical report."
            );
        }
    }


    // =========================================================
    // DELETE REPORT
    // =========================================================

    private void deleteReport(
            MedicalReport report) {

        Alert confirmation =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );


        confirmation.setTitle(
                "Delete Medical Report"
        );

        confirmation.setHeaderText(
                "Are you sure?"
        );

        confirmation.setContentText(
                "This will permanently delete the report file and its metadata."
        );


        ButtonType deleteButton =
                new ButtonType(
                        "Delete",
                        ButtonBar.ButtonData.YES
                );


        ButtonType cancelButton =
                new ButtonType(
                        "Cancel",
                        ButtonBar.ButtonData.CANCEL_CLOSE
                );


        confirmation
                .getButtonTypes()
                .setAll(
                        deleteButton,
                        cancelButton
                );


        confirmation
                .showAndWait()
                .ifPresent(
                        result -> {

                            if (result != deleteButton) {
                                return;
                            }

                            try {

                                medicalReportController
                                        .deleteCurrentPatientReport(
                                                report.getReportId()
                                        );


                                showSuccess(
                                        "Medical report deleted successfully."
                                );


                                refreshPage();

                            } catch (Exception e) {

                                e.printStackTrace();

                                showError(
                                        "Unable to delete medical report.\n\n"
                                                + safeMessage(
                                                e.getMessage()
                                        )
                                );
                            }
                        }
                );
    }


    // =========================================================
    // LOAD PRESCRIPTIONS
    // =========================================================

    private void loadPrescriptions(
            VBox prescriptionsCard) {

        VBox shimmer = com.healthsphere.util.ShimmerPlaceholder.createListShimmer(2);
        prescriptionsCard.getChildren().add(shimmer);

        javafx.concurrent.Task<List<Prescription>> task = new javafx.concurrent.Task<>() {
            @Override
            protected List<Prescription> call() throws Exception {
                return prescriptionController.getCurrentPatientPrescriptions();
            }
        };

        task.setOnSucceeded(e -> {
            prescriptionsCard.getChildren().remove(shimmer);
            List<Prescription> prescriptions = task.getValue();
            if (prescriptions == null || prescriptions.isEmpty()) {
                Label empty = new Label("No prescriptions available.");
                empty.setStyle("-fx-text-fill: #64748b; -fx-font-size: 14px;");
                prescriptionsCard.getChildren().add(empty);
                return;
            }

            for (Prescription prescription : prescriptions) {
                prescriptionsCard.getChildren().add(prescriptionCard(prescription));
            }
        });

        task.setOnFailed(e -> {
            prescriptionsCard.getChildren().remove(shimmer);
            Throwable ex = task.getException();
            if (ex != null) ex.printStackTrace();
            Label error = new Label("Unable to load prescriptions.");
            error.setStyle("-fx-text-fill: #dc2626; -fx-font-weight: bold;");
            prescriptionsCard.getChildren().add(error);
        });

        com.healthsphere.util.PatientBackgroundExecutor.execute(task);
    }


    // =========================================================
    // PRESCRIPTION CARD
    // SIMPLE CARD + IMAGE ON RIGHT CENTER
    // =========================================================

    private VBox prescriptionCard(
            Prescription prescription) {

        VBox outerCard =
                new VBox();

        outerCard.setPadding(
                new Insets(10)
        );

        outerCard.setMaxWidth(
                Double.MAX_VALUE
        );

        outerCard.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 16;" +
                "-fx-border-color: #bbf7d0;" +
                "-fx-border-radius: 16;"
        );


        // =====================================================
        // MAIN CONTENT
        // =====================================================

        HBox mainContent =
                new HBox(25);

        mainContent.setAlignment(
                Pos.CENTER_LEFT
        );

        mainContent.setPadding(
                new Insets(
                        18,
                        20,
                        15,
                        20
                )
        );


        // =====================================================
        // LEFT SIDE
        // =====================================================

        VBox prescriptionContent =
                new VBox(10);

        prescriptionContent.setAlignment(
                Pos.CENTER_LEFT
        );

        prescriptionContent.setMaxWidth(
                Double.MAX_VALUE
        );

        HBox.setHgrow(
                prescriptionContent,
                Priority.ALWAYS
        );


        // DOCTOR

        Label doctor =
                new Label(
                        "👨‍⚕️ "
                                + safe(
                                prescription.getDoctorName()
                        )
                );

        doctor.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #166534;"
        );


        // MEDICINE

        Label medicine =
                new Label(
                        "💊 "
                                + safe(
                                prescription.getMedication()
                        )
                );

        medicine.setStyle(
                "-fx-font-size: 18px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );


        // DETAILS

        Label strength =
                createPrescriptionText(
                        "Strength: "
                                + safe(
                                prescription.getStrength()
                        )
                );


        Label dosage =
                createPrescriptionText(
                        "Dosage: "
                                + safe(
                                prescription.getDosage()
                        )
                );


        Label frequency =
                createPrescriptionText(
                        "Frequency: "
                                + safe(
                                prescription.getFrequency()
                        )
                );


        Label duration =
                createPrescriptionText(
                        "Duration: "
                                + safe(
                                prescription.getDuration()
                        )
                );


        Label timing =
                createPrescriptionText(
                        "Timing: "
                                + safe(
                                prescription.getTiming()
                        )
                );


        Label prescribed =
                new Label(
                        "Prescribed: "
                                + safe(
                                prescription.getCreatedAt()
                        )
                );

        prescribed.setStyle(
                "-fx-text-fill: #15803d;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;"
        );


        prescriptionContent.getChildren().addAll(
                doctor,
                medicine,
                strength,
                dosage,
                frequency,
                duration,
                timing,
                prescribed
        );


        // =====================================================
        // RIGHT SIDE IMAGE
        // =====================================================

        StackPane imageContainer =
                new StackPane();

        imageContainer.setAlignment(
                Pos.CENTER
        );

        imageContainer.setPrefWidth(
                260
        );

        imageContainer.setMinWidth(
                260
        );

        imageContainer.setMaxWidth(
                260
        );

        imageContainer.setPrefHeight(
                220
        );

        imageContainer.setMinHeight(
                220
        );


        imageContainer.setStyle(
                "-fx-background-color: #f8fafc;" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: #e2e8f0;" +
                "-fx-border-radius: 14;"
        );


        var imageResource =
                getClass().getResource(
                        "/images/medicalrecords/medicalrecord5.jpg"
                );


        if (imageResource != null) {

            Image image =
                    new Image(
                            imageResource.toExternalForm()
                    );


            ImageView imageView =
                    new ImageView(
                            image
                    );


            imageView.setFitWidth(
                    240
            );

            imageView.setFitHeight(
                    200
            );

            imageView.setPreserveRatio(
                    true
            );

            imageView.setSmooth(
                    true
            );


            imageContainer.getChildren().add(
                    imageView
            );

        } else {

            Label imageError =
                    new Label(
                            "Image unavailable"
                    );

            imageError.setStyle(
                    "-fx-text-fill: #64748b;" +
                    "-fx-font-size: 13px;"
            );

            imageContainer.getChildren().add(
                    imageError
            );
        }


        // =====================================================
        // ADD LEFT + RIGHT
        // =====================================================

        mainContent.getChildren().addAll(
                prescriptionContent,
                imageContainer
        );


        // =====================================================
        // BUTTONS
        // =====================================================

        Button viewButton =
                PatientUI.secondaryButton(
                        "View Prescription",
                        () -> openPrescriptionScreen(
                                prescription
                        )
                );


        Button deleteButton =
                createDeleteButton(
                        () -> deletePrescription(
                                prescription
                        )
                );


        HBox buttons =
                new HBox(10);

        buttons.setAlignment(
                Pos.CENTER_RIGHT
        );

        buttons.setPadding(
                new Insets(
                        0,
                        10,
                        10,
                        10
                )
        );


        buttons.getChildren().addAll(
                viewButton,
                deleteButton
        );


        // =====================================================
        // FINAL CARD
        // =====================================================

        outerCard.getChildren().addAll(
                mainContent,
                buttons
        );


        return outerCard;
    }


    // =========================================================
    // PRESCRIPTION TEXT
    // =========================================================

    private Label createPrescriptionText(
            String text) {

        Label label =
                new Label(
                        text
                );

        label.setStyle(
                "-fx-text-fill: #64748b;" +
                "-fx-font-size: 14px;"
        );

        return label;
    }


    // =========================================================
    // OPEN PRESCRIPTION SCREEN
    // =========================================================

    private void openPrescriptionScreen(
            Prescription prescription) {

        stage.setScene(
                createPrescriptionScene(
                        prescription
                )
        );
    }


    // =========================================================
    // PRESCRIPTION DETAIL SCREEN
    // IMAGE CARD WITH PRESCRIPTION
    // =========================================================

    private Scene createPrescriptionScene(
            Prescription prescription) {

        VBox content =
                new VBox(20);

        content.setPadding(
                new Insets(0)
        );


        // =====================================================
        // BACK BUTTON
        // =====================================================

        Button backButton =
                PatientUI.secondaryButton(
                        "← Back to Medical Records",
                        () -> stage.setScene(
                                new MedicalRecords(stage)
                                        .getScene()
                        )
                );


        // =====================================================
        // OUTER CARD
        // =====================================================

        VBox outerCard =
                new VBox();

        outerCard.setMaxWidth(
                Double.MAX_VALUE
        );

        outerCard.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 18;" +
                "-fx-border-color: #cbd5e1;" +
                "-fx-border-radius: 18;"
        );


        // =====================================================
        // PRESCRIPTION PAPER
        // =====================================================

        StackPane prescriptionPaper =
                new StackPane();

        prescriptionPaper.setPrefHeight(
                570
        );

        prescriptionPaper.setMinHeight(
                570
        );

        prescriptionPaper.setMaxWidth(
                Double.MAX_VALUE
        );


        // =====================================================
        // BACKGROUND IMAGE
        // =====================================================

        var templateResource =
                getClass().getResource(
                        "/images/medicalrecords/prescription_template.jpg"
                );


        if (templateResource != null) {

            Image backgroundImage =
                    new Image(
                            templateResource.toExternalForm()
                    );


            ImageView background =
                    new ImageView(
                            backgroundImage
                    );


            background.setPreserveRatio(
                    false
            );

            background.setFitHeight(
                    570
            );

            background.setOpacity(
                    0.98
            );


            background.fitWidthProperty()
                    .bind(
                            prescriptionPaper.widthProperty()
                    );


            prescriptionPaper.getChildren().add(
                    background
            );

        } else {

            prescriptionPaper.setStyle(
                    "-fx-background-color: white;" +
                    "-fx-border-color: #1e3a8a;" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 14;"
            );
        }


        // =====================================================
        // PRESCRIPTION CONTENT
        // =====================================================

        VBox prescriptionContent =
                new VBox(16);

        prescriptionContent.setPadding(
                new Insets(
                        45,
                        60,
                        40,
                        75
                )
        );

        prescriptionContent.setMaxWidth(
                Double.MAX_VALUE
        );

        prescriptionContent.setMaxHeight(
                Double.MAX_VALUE
        );


        // =====================================================
        // HEADER
        // =====================================================

        HBox header =
                new HBox(15);

        header.setAlignment(
                Pos.CENTER_LEFT
        );


        VBox doctorSection =
                new VBox(5);


        Label healthSphere =
                new Label(
                        "HEALTHSPHERE"
                );

        healthSphere.setStyle(
                "-fx-font-size: 22px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #1e3a8a;"
        );


        Label doctor =
                new Label(
                        "Dr. "
                                + safe(
                                prescription.getDoctorName()
                        )
                );

        doctor.setStyle(
                "-fx-font-size: 17px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #334155;"
        );


        doctorSection.getChildren().addAll(
                healthSphere,
                doctor
        );


        HBox.setHgrow(
                doctorSection,
                Priority.ALWAYS
        );


        Label date =
                new Label(
                        safe(
                                prescription.getCreatedAt()
                        )
                );

        date.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-text-fill: #64748b;" +
                "-fx-font-weight: bold;"
        );


        header.getChildren().addAll(
                doctorSection,
                date
        );


        // =====================================================
        // DIVIDER
        // =====================================================

        Line divider =
                new Line();

        divider.setStyle(
                "-fx-stroke: #cbd5e1;" +
                "-fx-stroke-width: 1;"
        );


        // =====================================================
        // TITLE
        // =====================================================

        Label prescriptionTitle =
                new Label(
                        "Prescription"
                );

        prescriptionTitle.setStyle(
                "-fx-font-size: 25px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #1e3a8a;"
        );


        // =====================================================
        // MEDICINE
        // =====================================================

        VBox medicineSection =
                new VBox(5);


        Label medicineTitle =
                new Label(
                        "MEDICINE"
                );

        medicineTitle.setStyle(
                "-fx-font-size: 11px;" +
                "-fx-text-fill: #64748b;" +
                "-fx-font-weight: bold;"
        );


        Label medicine =
                new Label(
                        safe(
                                prescription.getMedication()
                        )
                );

        medicine.setStyle(
                "-fx-font-size: 23px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        medicine.setWrapText(
                true
        );


        medicineSection.getChildren().addAll(
                medicineTitle,
                medicine
        );


        // =====================================================
        // DETAILS
        // =====================================================

        HBox details =
                new HBox(12);

        details.setAlignment(
                Pos.CENTER_LEFT
        );


        VBox strength =
                prescriptionDetail(
                        "STRENGTH",
                        prescription.getStrength()
                );


        VBox dosage =
                prescriptionDetail(
                        "DOSAGE",
                        prescription.getDosage()
                );


        VBox frequency =
                prescriptionDetail(
                        "FREQUENCY",
                        prescription.getFrequency()
                );


        VBox duration =
                prescriptionDetail(
                        "DURATION",
                        prescription.getDuration()
                );


        VBox timing =
                prescriptionDetail(
                        "TIMING",
                        prescription.getTiming()
                );


        details.getChildren().addAll(
                strength,
                dosage,
                frequency,
                duration,
                timing
        );


        // =====================================================
        // NOTE
        // =====================================================

        Label note =
                new Label(
                        "Follow the prescribed dosage and timing."
                );

        note.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-text-fill: #64748b;" +
                "-fx-font-style: italic;"
        );


        // =====================================================
        // ADD CONTENT
        // =====================================================

        prescriptionContent.getChildren().addAll(
                header,
                divider,
                prescriptionTitle,
                medicineSection,
                details,
                note
        );


        prescriptionPaper.getChildren().add(
                prescriptionContent
        );


        outerCard.getChildren().add(
                prescriptionPaper
        );


        content.getChildren().addAll(
                backButton,
                outerCard
        );


        return PatientUI.createScene(
                stage,
                "Medical Records",
                "Prescription",
                "View your complete prescription details.",
                content
        );
    }


    // =========================================================
    // PRESCRIPTION DETAIL BOX
    // =========================================================

    private VBox prescriptionDetail(
            String title,
            String value) {

        VBox box =
                new VBox(5);

        box.setPadding(
                new Insets(
                        10,
                        13,
                        10,
                        13
                )
        );

        box.setPrefWidth(
                145
        );

        box.setStyle(
                "-fx-background-color: rgba(248,250,252,0.88);" +
                "-fx-border-color: #e2e8f0;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;"
        );


        Label titleLabel =
                new Label(
                        title
                );

        titleLabel.setStyle(
                "-fx-font-size: 9px;" +
                "-fx-text-fill: #64748b;" +
                "-fx-font-weight: bold;"
        );


        Label valueLabel =
                new Label(
                        safe(value)
                );

        valueLabel.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-text-fill: #1e3a8a;" +
                "-fx-font-weight: bold;"
        );

        valueLabel.setWrapText(
                true
        );


        box.getChildren().addAll(
                titleLabel,
                valueLabel
        );


        return box;
    }


    // =========================================================
    // DELETE PRESCRIPTION
    // =========================================================

    private void deletePrescription(
            Prescription prescription) {

        Alert confirmation =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );


        confirmation.setTitle(
                "Delete Prescription"
        );

        confirmation.setHeaderText(
                "Delete this prescription?"
        );

        confirmation.setContentText(
                "Prescription for "
                        + safe(
                        prescription.getMedication()
                )
                        + " will be permanently deleted."
        );


        ButtonType deleteButton =
                new ButtonType(
                        "Delete",
                        ButtonBar.ButtonData.YES
                );


        ButtonType cancelButton =
                new ButtonType(
                        "Cancel",
                        ButtonBar.ButtonData.CANCEL_CLOSE
                );


        confirmation
                .getButtonTypes()
                .setAll(
                        deleteButton,
                        cancelButton
                );


        confirmation
                .showAndWait()
                .ifPresent(
                        result -> {

                            if (result != deleteButton) {
                                return;
                            }

                            try {

                                prescriptionController
                                        .deleteCurrentPatientPrescription(
                                                prescription
                                                        .getPrescriptionId()
                                        );


                                showSuccess(
                                        "Prescription deleted successfully."
                                );


                                refreshPage();

                            } catch (Exception e) {

                                e.printStackTrace();

                                showError(
                                        "Unable to delete prescription.\n\n"
                                                + safeMessage(
                                                e.getMessage()
                                        )
                                );
                            }
                        }
                );
    }


    // =========================================================
    // DELETE BUTTON
    // =========================================================

    private Button createDeleteButton(
            Runnable action) {

        Button button =
                new Button(
                        "Delete"
                );

        button.setPrefHeight(
                38
        );

        button.setStyle(
                "-fx-background-color: #dc2626;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 8 14;" +
                "-fx-cursor: hand;"
        );


        button.setOnAction(
                e -> action.run()
        );


        return button;
    }


    // =========================================================
    // IMAGE GALLERY
    // =========================================================

    private HBox createImageGallery() {

        HBox gallery =
                new HBox(15);

        gallery.setAlignment(
                Pos.CENTER_LEFT
        );


        gallery.getChildren().addAll(

                imageCard(
                        "/images/medicalrecords/medicalrecord1.jpg"
                ),

                imageCard(
                        "/images/medicalrecords/medicalrecord2.jpg"
                ),

                imageCard(
                        "/images/medicalrecords/medicalrecord3.jpg"
                ),

                imageCard(
                        "/images/medicalrecords/medicalrecord4.jpg"
                )
        );


        return gallery;
    }


    // =========================================================
    // IMAGE CARD
    // =========================================================

    private VBox imageCard(
            String path) {

        VBox box =
                new VBox();

        box.setAlignment(
                Pos.CENTER
        );

        box.setPrefWidth(
                260
        );

        box.setPrefHeight(
                145
        );

        box.setMinWidth(
                260
        );

        box.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: #e2e8f0;" +
                "-fx-border-radius: 14;"
        );


        var resource =
                getClass().getResource(
                        path
                );


        if (resource == null) {

            Label error =
                    new Label(
                            "Image unavailable"
                    );

            error.setStyle(
                    "-fx-text-fill: #64748b;" +
                    "-fx-font-size: 13px;"
            );

            box.getChildren().add(
                    error
            );


            System.err.println(
                    "Medical Records image not found: "
                            + path
            );


            return box;
        }


        Image image =
                new Image(
                        resource.toExternalForm(),
                        260,
                        145,
                        true,
                        true
                );


        ImageView imageView =
                new ImageView(
                        image
                );


        imageView.setFitWidth(
                260
        );

        imageView.setFitHeight(
                145
        );

        imageView.setPreserveRatio(
                false
        );


        box.getChildren().add(
                imageView
        );


        return box;
    }


    // =========================================================
    // REFRESH PAGE
    // =========================================================

    private void refreshPage() {

        stage.setScene(
                new MedicalRecords(
                        stage
                ).getScene()
        );
    }


    // =========================================================
    // SUCCESS ALERT
    // =========================================================

    private void showSuccess(
            String message) {

        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );


        alert.setTitle(
                "HealthSphere"
        );

        alert.setHeaderText(
                "Success"
        );

        alert.setContentText(
                message
        );

        alert.showAndWait();
    }


    // =========================================================
    // ERROR ALERT
    // =========================================================

    private void showError(
            String message) {

        Alert alert =
                new Alert(
                        Alert.AlertType.ERROR
                );


        alert.setTitle(
                "HealthSphere"
        );

        alert.setHeaderText(
                "Operation Failed"
        );

        alert.setContentText(
                message
        );

        alert.showAndWait();
    }


    // =========================================================
    // SAFE STRING
    // =========================================================

    private String safe(
            String value) {

        if (value == null
                || value.isBlank()) {

            return "Not available";
        }

        return value;
    }


    // =========================================================
    // SAFE ERROR MESSAGE
    // =========================================================

    private String safeMessage(
            String message) {

        if (message == null
                || message.isBlank()) {

            return "Please try again.";
        }

        return message;
    }
}