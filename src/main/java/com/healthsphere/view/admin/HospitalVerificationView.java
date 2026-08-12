package com.healthsphere.view.admin;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
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

import java.util.Optional;

public class HospitalVerificationView {

    private final Stage stage;
    private VBox rowList;

    public HospitalVerificationView(Stage stage) {
        this.stage = stage;
    }

    public Node getView() {
        VBox mainContainer = new VBox(24);
        mainContainer.setPadding(new Insets(28));
        mainContainer.setStyle("-fx-background-color: #F8FAFC;");

        // Header
        HBox header = createHeader();

        // Visual AI Banner
        HBox aiBanner = createAiOcrBanner();

        // Verification Card
        VBox card = new VBox(16);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 14; -fx-border-color: #E2E8F0; -fx-border-radius: 14;");
        card.setEffect(getCardShadow());

        HBox tableHeader = new HBox();
        tableHeader.setPadding(new Insets(10, 16, 10, 16));
        tableHeader.setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 8; -fx-border-color: #E2E8F0; -fx-border-width: 0 0 1 0;");

        tableHeader.getChildren().addAll(
            createColHeader("APPLICANT HOSPITAL", 240),
            createColHeader("NABH LICENSE #", 160),
            createColHeader("AI OCR MATCH", 160),
            createColHeader("STATUS", 120),
            createColHeader("ACTION", 100)
        );

        rowList = new VBox(0);
        rowList.getChildren().addAll(
            createVerifyRow("St. Jude Hospital", "NABH-2026-8812", "98.8% Verified", "#059669", "APPROVED", "#059669", "#ECFDF5"),
            createVerifyRow("Sanjeevani Clinic", "NABH-2026-3310", "64.2% Missing Doc", "#D97706", "PENDING", "#D97706", "#FFFBEB"),
            createVerifyRow("City Life Care", "NABH-2026-1049", "91.5% Verified", "#059669", "PENDING", "#D97706", "#FFFBEB")
        );

        card.getChildren().addAll(tableHeader, rowList);
        mainContainer.getChildren().addAll(header, aiBanner, card);

        ScrollPane scroll = new ScrollPane(mainContainer);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: #F8FAFC; -fx-border-color: transparent;");
        return scroll;
    }

    private HBox createHeader() {
        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        Label title = new Label("Hospital Verification & NABH Audit");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        title.setTextFill(Color.web("#0F172A"));

        Label sub = new Label("AI-driven verification of clinical licenses, NABH accreditation, and infrastructure compliance.");
        sub.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));
        sub.setTextFill(Color.web("#64748B"));
        titleBox.getChildren().addAll(title, sub);

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Button scanBtn = new Button("⚡ Run AI OCR Audit");
        scanBtn.setStyle("-fx-background-color: #2563EB; -fx-text-fill: #FFFFFF; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 18; -fx-cursor: hand;");
        scanBtn.setOnAction(e -> handleRunOcrScan());

        header.getChildren().addAll(titleBox, sp, scanBtn);
        return header;
    }

    private HBox createAiOcrBanner() {
        HBox banner = new HBox(20);
        banner.setAlignment(Pos.CENTER_LEFT);
        banner.setPadding(new Insets(18, 20, 18, 20));
        // Refined Light SaaS gradient with subtle border & card shadow
        banner.setStyle(
            "-fx-background-color: linear-gradient(to right, #EEF2FF, #ECFDF5); " +
            "-fx-border-color: #C7D2FE; " +
            "-fx-border-radius: 14; " +
            "-fx-background-radius: 14;"
        );
        banner.setEffect(getCardShadow());

        ImageView img = createSafeImageView("https://images.unsplash.com/photo-1516549655169-df83a0774514?w=300", 110, 70);
        Rectangle clip = new Rectangle(110, 70);
        clip.setArcWidth(10);
        clip.setArcHeight(10);
        img.setClip(clip);

        VBox textGroup = new VBox(6);
        Label aiTitle = new Label("🔍 Automated Document Vision OCR & NABH Verification Engine");
        aiTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        aiTitle.setTextFill(Color.web("#1E40AF")); // Deep Indigo text for high contrast

        Label aiDesc = new Label("AI extracts certificate serial numbers, cross-checks official NABH government registries, and alerts on fraudulent or expired clinical licenses instantly.");
        aiDesc.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));
        aiDesc.setTextFill(Color.web("#334155")); // Crisp Slate dark body text
        aiDesc.setWrapText(true);

        textGroup.getChildren().addAll(aiTitle, aiDesc);
        HBox.setHgrow(textGroup, Priority.ALWAYS);

        banner.getChildren().addAll(img, textGroup);
        return banner;
    }

    private HBox createVerifyRow(String name, String lic, String match, String matchColor, String status, String stColor, String stBg) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12, 16, 12, 16));
        row.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #F1F5F9; -fx-border-width: 0 0 1 0;");

        Label nameLbl = new Label(name);
        nameLbl.setPrefWidth(240);
        nameLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        nameLbl.setTextFill(Color.web("#0F172A"));

        Label licLbl = new Label(lic);
        licLbl.setPrefWidth(160);
        licLbl.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 12));
        licLbl.setTextFill(Color.web("#334155"));

        Label matchLbl = new Label(match);
        matchLbl.setPrefWidth(160);
        matchLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        matchLbl.setTextFill(Color.web(matchColor));

        HBox stBox = new HBox();
        stBox.setPrefWidth(120);
        stBox.setAlignment(Pos.CENTER_LEFT);
        Label stBadge = new Label(status);
        stBadge.setStyle(String.format("-fx-background-color: %s; -fx-text-fill: %s; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 3 8; -fx-background-radius: 12;", stBg, stColor));
        stBox.getChildren().add(stBadge);

        Button btn = new Button("Verify Documents");
        btn.setStyle("-fx-background-color: #EFF6FF; -fx-text-fill: #2563EB; -fx-font-size: 11px; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand;");
        btn.setOnAction(e -> handleAuditDocument(name, lic, match, row));

        row.getChildren().addAll(nameLbl, licLbl, matchLbl, stBox, btn);
        return row;
    }

    private void handleRunOcrScan() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("AI Audit Complete");
        alert.setHeaderText("⚡ AI Vision OCR Audit Finished");
        alert.setContentText("Scanned 14 pending hospital verification requests.\n• 12 Certificates cross-verified with NABH Registry.\n• 2 Suspicious licenses flagged for manual admin inspection.");
        alert.showAndWait();
    }

    private void handleAuditDocument(String name, String lic, String match, HBox currentRow) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("NABH Document Verification");
        alert.setHeaderText("Audit Details: " + name + " (" + lic + ")");
        alert.setContentText("AI Confidence Score: " + match + "\n\nSelect verification action:");

        ButtonType approveBtn = new ButtonType("Approve NABH Accreditation");
        ButtonType rejectBtn = new ButtonType("Reject / Flag Registration");
        ButtonType cancelBtn = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(approveBtn, rejectBtn, cancelBtn);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == approveBtn) {
                showAlert("Success", name + " NABH Accreditation has been APPROVED!");
            } else if (result.get() == rejectBtn) {
                rowList.getChildren().remove(currentRow);
                showAlert("Flagged", name + " registration flagged and removed from pending queue.");
            }
        }
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private ImageView createSafeImageView(String url, double width, double height) {
        ImageView img = new ImageView();
        try {
            img.setImage(new Image(url, true));
        } catch (Exception ignored) {}
        img.setFitWidth(width);
        img.setFitHeight(height);
        return img;
    }

    private Label createColHeader(String title, double width) {
        Label lbl = new Label(title);
        lbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        lbl.setTextFill(Color.web("#475569"));
        lbl.setPrefWidth(width);
        return lbl;
    }

    private DropShadow getCardShadow() {
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(15, 23, 42, 0.05));
        shadow.setRadius(10);
        shadow.setOffsetY(4);
        return shadow;
    }
}