package com.healthsphere.view.admin;

import com.healthsphere.util.UIUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class ComplaintsManagementView {

    private final Stage stage;

    public ComplaintsManagementView(Stage stage) {
        this.stage = stage;
    }

    public Node getView() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: " + UIUtils.COLOR_BG_DARK + ";");

        VBox titleBox = new VBox(4);
        Label title = new Label("Reports & Content Moderation Desk");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        title.setTextFill(Color.web(UIUtils.COLOR_TEXT_MAIN));

        Label subTitle = new Label("Investigate user tickets, disputed consultation charges, and system logs.");
        subTitle.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));
        subTitle.setTextFill(Color.web(UIUtils.COLOR_TEXT_MUTED));
        titleBox.getChildren().addAll(title, subTitle);

        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: " + UIUtils.COLOR_CARD_DARK + "; -fx-background-radius: 12; -fx-border-color: #334155; -fx-border-radius: 12;");

        VBox list = new VBox(10);
        list.getChildren().addAll(
                createTicket("TKT-8801", "Incorrect Billing Charge", "Patient reported double charge for consultation.", "HIGH"),
                createTicket("TKT-8802", "Doctor No-Show", "Doctor missed scheduled Tele-consultation appointment.", "MEDIUM")
        );

        card.getChildren().add(list);
        root.getChildren().addAll(titleBox, card);

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: " + UIUtils.COLOR_BG_DARK + "; -fx-border-color: transparent;");
        return scroll;
    }

    private VBox createTicket(String tktId, String issue, String desc, String priority) {
        VBox item = new VBox(8);
        item.setPadding(new Insets(12));
        item.setStyle("-fx-background-color: " + UIUtils.COLOR_BG_DARK + "; -fx-background-radius: 8; -fx-border-color: #1E293B; -fx-border-radius: 8;");

        HBox top = new HBox(10);
        top.setAlignment(Pos.CENTER_LEFT);

        Label id = new Label(tktId);
        id.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        id.setTextFill(Color.web(UIUtils.COLOR_TEXT_MUTED));

        Label head = new Label(issue);
        head.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        head.setTextFill(Color.web(UIUtils.COLOR_TEXT_MAIN));

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Label pBadge = new Label("Priority: " + priority);
        pBadge.setStyle("-fx-background-color: #881337; -fx-text-fill: #FDA4AF; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 3 8; -fx-background-radius: 6;");

        top.getChildren().addAll(id, head, sp, pBadge);

        Label body = new Label(desc);
        body.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 11));
        body.setTextFill(Color.web(UIUtils.COLOR_TEXT_MUTED));

        Button resolveBtn = new Button("Mark Resolved");
        resolveBtn.setStyle("-fx-background-color: #6366F1; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 6 12; -fx-background-radius: 6; -fx-cursor: hand;");
        resolveBtn.setOnAction(e -> UIUtils.showInfoDialog("Ticket Resolved", "Ticket " + tktId + " has been closed successfully."));

        item.getChildren().addAll(top, body, resolveBtn);
        return item;
    }
}