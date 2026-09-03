package com.healthsphere.view.doctor;

import java.util.List;

import com.healthsphere.controller.PaymentController;
import com.healthsphere.model.PaymentRecord;
import com.healthsphere.util.ResourceImage;
import com.healthsphere.util.SessionManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DoctorPaymentsView {

    private final Stage stage;
    private final Scene scene;
    private final PaymentController paymentController;

    public DoctorPaymentsView(Stage stage) {
        this.stage = stage;
        this.paymentController = new PaymentController();
        this.scene = createScene();
    }

    public Scene getScene() {
        return this.scene;
    }

    private Scene createScene() {
        BorderPane mainRoot = new BorderPane();
        mainRoot.setStyle("-fx-background-color: #F8FAFC;");

        // Sidebar with Payments tab active (index 8)
        VBox sidebar = DoctorSidebar.create(stage, 8);
        mainRoot.setLeft(sidebar);

        // Content Area
        VBox contentArea = new VBox(20);
        contentArea.setPadding(new Insets(25, 30, 30, 30));

        // Top Header
        VBox headerText = new VBox(4);
        Label title = new Label("Payment History & Earnings");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        Label subtitle = new Label("Track all received patient consultation payments and total earnings.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748B;");
        headerText.getChildren().addAll(title, subtitle);

        contentArea.getChildren().add(headerText);

        // Doctor Account Balance Banner
        String doctorUid = SessionManager.getDoctorUid();
        double doctorBal = 3450.00;
        if (doctorUid != null && !doctorUid.isBlank()) {
            doctorBal = paymentController.getDoctorAccountBalance(doctorUid);
        }

        VBox balanceCard = new VBox(10);
        balanceCard.setPadding(new Insets(20));
        balanceCard.setStyle("-fx-background-color: #0F172A; -fx-background-radius: 12px;");

        Label balTitle = new Label("TOTAL ACCOUNT BALANCE");
        balTitle.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 11px; -fx-font-weight: bold;");

        HBox balRow = new HBox(12);
        balRow.setAlignment(Pos.BASELINE_LEFT);

        Label balAmt = new Label(String.format("₹%.2f", doctorBal));
        balAmt.setStyle("-fx-text-fill: #FFFFFF; -fx-font-size: 32px; -fx-font-weight: bold;");

        Label growthBadge = new Label("+14.5% ↑");
        growthBadge.setStyle("-fx-background-color: #D1FAE5; -fx-text-fill: #059669; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 3 10; -fx-background-radius: 12;");

        balRow.getChildren().addAll(balAmt, growthBadge);
        balanceCard.getChildren().addAll(balTitle, balRow);

        contentArea.getChildren().add(balanceCard);

        // Payments List Section
        VBox listCard = new VBox(15);
        listCard.setPadding(new Insets(20));
        listCard.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 12px; -fx-border-color: #E2E8F0; -fx-border-radius: 12px;");

        Label listHeader = new Label("Received Consultation Payments");
        listHeader.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        listCard.getChildren().add(listHeader);

        List<PaymentRecord> records = paymentController.getPaymentsForDoctor(doctorUid);
        if (records == null || records.isEmpty()) {
            Label empty = new Label("No payment records found yet.");
            empty.setStyle("-fx-text-fill: #64748B; -fx-font-size: 14px; -fx-padding: 10 0;");
            listCard.getChildren().add(empty);
        } else {
            VBox rows = new VBox(10);
            for (PaymentRecord rec : records) {
                HBox row = new HBox(15);
                row.setPadding(new Insets(14));
                row.setAlignment(Pos.CENTER_LEFT);
                row.setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 10px; -fx-border-color: #E2E8F0; -fx-border-radius: 10px;");

                VBox patientInfo = new VBox(3);
                Label pName = new Label(rec.getPatientName() != null ? rec.getPatientName() : "Patient Consultation");
                pName.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");
                Label pDate = new Label(rec.getCreatedAt() != null ? rec.getCreatedAt() : "Recent");
                pDate.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748B;");
                patientInfo.getChildren().addAll(pName, pDate);

                HBox.setHgrow(patientInfo, Priority.ALWAYS);

                boolean completed = "COMPLETED".equalsIgnoreCase(rec.getStatus());
                Label badge = new Label(completed ? "✓ RECEIVED" : "⚡ PENDING");
                badge.setStyle(completed ?
                        "-fx-background-color: #D1FAE5; -fx-text-fill: #059669; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 4 10; -fx-background-radius: 12;" :
                        "-fx-background-color: #FEF3C7; -fx-text-fill: #D97706; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 4 10; -fx-background-radius: 12;");

                Label amount = new Label(String.format("+₹%.2f", rec.getAmount()));
                amount.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #10B981;");

                row.getChildren().addAll(patientInfo, badge, amount);
                rows.getChildren().add(row);
            }
            listCard.getChildren().add(rows);
        }

        contentArea.getChildren().add(listCard);

        ScrollPane scrollPane = new ScrollPane(contentArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        mainRoot.setCenter(scrollPane);

        return new Scene(mainRoot, stage.getWidth(), stage.getHeight());
    }
}
