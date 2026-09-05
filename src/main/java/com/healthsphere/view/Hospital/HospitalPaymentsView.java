package com.healthsphere.view.hospital;

import java.util.List;

import com.healthsphere.controller.PaymentController;
import com.healthsphere.model.PaymentRecord;
import com.healthsphere.util.Navigation;
import com.healthsphere.util.SessionManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HospitalPaymentsView {

    private static final String PRIMARY_BLUE = "#102bdb";
    private static final String DARK_TEXT = "#0F172A";
    private static final String SECONDARY_TEXT = "#64748B";
    private static final String LIGHT_BACKGROUND = "#F8FAFC";
    private static final String CARD_BG = "#FFFFFF";
    private static final String BORDER = "#E2E8F0";
    private static final String SIDEBAR_BG = "#0F172A";
    private static final String SIDEBAR_BORDER = "#1E293B";
    private static final String SIDEBAR_TEXT = "#94A3B8";

    private final PaymentController paymentController;

    public HospitalPaymentsView() {
        this.paymentController = new PaymentController();
    }

    public Scene createScene(Stage stage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + LIGHT_BACKGROUND + ";");

        root.setLeft(HospitalSidebar.createSidebar(stage, HospitalSidebar.HospitalTab.PAYMENTS));
        root.setTop(createTopBar());

        VBox contentArea = new VBox(20);
        contentArea.setPadding(new Insets(25, 30, 30, 30));

        // Header
        VBox headerText = new VBox(4);
        Label title = new Label("Hospital Payment History & Account Balance");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + DARK_TEXT + ";");
        Label subtitle = new Label("Track all patient consultation and hospital booking payments.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        headerText.getChildren().addAll(title, subtitle);

        contentArea.getChildren().add(headerText);

        // Account Balance & Revenue Banners
        String hospitalId = SessionManager.getCurrentUserId();
        double balance = 0.00;
        if (hospitalId != null && !hospitalId.isBlank()) {
            balance = paymentController.getHospitalAccountBalance(hospitalId);
        }

        List<PaymentRecord> records = paymentController.getPaymentsForHospital(hospitalId);
        double totalCompletedRevenue = 0.00;
        double totalAmountBilled = 0.00;
        if (records != null) {
            for (PaymentRecord rec : records) {
                totalAmountBilled += rec.getAmount();
                if ("COMPLETED".equalsIgnoreCase(rec.getStatus())) {
                    totalCompletedRevenue += rec.getAmount();
                }
            }
        }
        if (balance <= 0.00 && totalCompletedRevenue > 0.00) {
            balance = totalCompletedRevenue;
        }

        HBox kpiContainer = new HBox(16);

        // Total Revenue Card (Completed Payments)
        VBox revenueCard = new VBox(10);
        revenueCard.setPadding(new Insets(20));
        revenueCard.setStyle("-fx-background-color: #0F172A; -fx-background-radius: 12px;");
        HBox.setHgrow(revenueCard, Priority.ALWAYS);

        Label revTitle = new Label("TOTAL REVENUE (RECEIVED)");
        revTitle.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 11px; -fx-font-weight: bold;");

        HBox revRow = new HBox(12);
        revRow.setAlignment(Pos.BASELINE_LEFT);
        Label revAmt = new Label(String.format("₹%.2f", totalCompletedRevenue));
        revAmt.setStyle("-fx-text-fill: #FFFFFF; -fx-font-size: 28px; -fx-font-weight: bold;");
        Label growthBadge = new Label("+18.2% ↑");
        growthBadge.setStyle("-fx-background-color: #D1FAE5; -fx-text-fill: #059669; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 3 10; -fx-background-radius: 12;");
        revRow.getChildren().addAll(revAmt, growthBadge);
        revenueCard.getChildren().addAll(revTitle, revRow);

        // Total Amount Billed Card (Overall Total)
        VBox amountCard = new VBox(10);
        amountCard.setPadding(new Insets(20));
        amountCard.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 12px; -fx-border-color: #E2E8F0; -fx-border-radius: 12px;");
        HBox.setHgrow(amountCard, Priority.ALWAYS);

        Label amtTitle = new Label("TOTAL AMOUNT (BILLED & RECORDED)");
        amtTitle.setStyle("-fx-text-fill: #64748B; -fx-font-size: 11px; -fx-font-weight: bold;");

        HBox amtRow = new HBox(12);
        amtRow.setAlignment(Pos.BASELINE_LEFT);
        Label amtVal = new Label(String.format("₹%.2f", totalAmountBilled));
        amtVal.setStyle("-fx-text-fill: #0F172A; -fx-font-size: 28px; -fx-font-weight: bold;");
        Label statusBadge = new Label(records != null ? records.size() + " Transactions" : "0 Transactions");
        statusBadge.setStyle("-fx-background-color: #EFF6FF; -fx-text-fill: #2563EB; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 3 10; -fx-background-radius: 12;");
        amtRow.getChildren().addAll(amtVal, statusBadge);
        amountCard.getChildren().addAll(amtTitle, amtRow);

        kpiContainer.getChildren().addAll(revenueCard, amountCard);
        contentArea.getChildren().add(kpiContainer);

        // Payments Table/List
        VBox listCard = new VBox(15);
        listCard.setPadding(new Insets(20));
        listCard.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 12px; -fx-border-color: #E2E8F0; -fx-border-radius: 12px;");

        Label listHeader = new Label("Received Patient Payments");
        listHeader.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        listCard.getChildren().add(listHeader);

        if (records == null || records.isEmpty()) {
            Label empty = new Label("No payment records found for this hospital yet.");
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
                Label pName = new Label(rec.getPatientName() != null ? rec.getPatientName() : "Patient Booking");
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
        root.setCenter(scrollPane);

        return new Scene(root, stage.getWidth(), stage.getHeight());
    }

    private VBox createSidebar(Stage stage) {
        return HospitalSidebar.createSidebar(stage, HospitalSidebar.HospitalTab.PAYMENTS);
    }

    private Button createNavButton(String icon, String text, boolean selected) {
        Button button = new Button();
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: " + (selected ? "#FFFFFF" : SIDEBAR_TEXT) + ";");
        Label textLabel = new Label(text);
        textLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: " + (selected ? "bold" : "500") + "; -fx-text-fill: " + (selected ? "#FFFFFF" : SIDEBAR_TEXT) + ";");

        HBox content = new HBox(12, iconLabel, textLabel);
        content.setAlignment(Pos.CENTER_LEFT);
        button.setGraphic(content);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(42);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setPadding(new Insets(0, 12, 0, 12));

        if (selected) {
            button.setStyle("-fx-background-radius: 8; -fx-cursor: hand; -fx-background-color: " + PRIMARY_BLUE + ";");
        } else {
            button.setStyle("-fx-background-radius: 8; -fx-cursor: hand; -fx-background-color: transparent;");
        }
        return button;
    }

    private HBox createTopBar() {
        HBox topBar = new HBox(16);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(12, 28, 12, 28));
        topBar.setStyle("-fx-background-color: " + CARD_BG + "; -fx-border-color: " + BORDER + "; -fx-border-width: 0 0 1 0;");

        Label title = new Label("Hospital Administrator - Payment History");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + DARK_TEXT + ";");
        topBar.getChildren().add(title);
        return topBar;
    }
}
