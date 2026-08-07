package com.healthsphere.view.authentication;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Pure JavaFX Patient Registration View.
 * Matches design layout image_11639b.png.
 */
public class RegisterView {

    private final Stage stage;

    public RegisterView(Stage stage) {
        this.stage = stage;
    }

    public Scene getScene() {
        VBox outerContainer = new VBox();
        outerContainer.getStyleClass().add("register-root");
        outerContainer.setAlignment(Pos.CENTER);
        outerContainer.setPadding(new Insets(30, 0, 30, 0));

        VBox formCard = createRegisterCard();
        outerContainer.getChildren().add(formCard);

        ScrollPane scrollPane = new ScrollPane(outerContainer);
        scrollPane.getStyleClass().add("scroll-pane-clean");

        Scene scene = new Scene(scrollPane, 1280, 880);

        // Load CSS safely
        String cssPath = getClass().getResource("/css/register.css") != null 
                ? getClass().getResource("/css/register.css").toExternalForm() 
                : null;
        if (cssPath != null) {
            scene.getStylesheets().add(cssPath);
        }

        return scene;
    }

    private VBox createRegisterCard() {
        VBox card = new VBox(22);
        card.getStyleClass().add("register-card");
        card.setMaxWidth(680);
        card.setAlignment(Pos.TOP_LEFT);

        // --- Header Section ---
        HBox headerRow = new HBox();
        headerRow.setAlignment(Pos.CENTER_LEFT);

        // Brand Logo + Title
        HBox brandBox = new HBox(8);
        brandBox.setAlignment(Pos.CENTER_LEFT);
        ImageView logoView = createSafeImageView("/images/brand_logo.png", 28, 28);
        Text brandTitle = new Text("Health-Sphere");
        brandTitle.getStyleClass().add("brand-text");
        brandBox.getChildren().addAll(logoView, brandTitle);

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        // Step Badge (Right aligned)
        HBox stepBadge = new HBox();
        stepBadge.getStyleClass().add("step-badge");
        Label stepText = new Label("Step 1 of 2: Basic Info");
        stepText.getStyleClass().add("step-badge-text");
        stepBadge.getChildren().add(stepText);

        headerRow.getChildren().addAll(brandBox, headerSpacer, stepBadge);

        // Title and Subtitle Block
        VBox titleBlock = new VBox(4);
        Text title = new Text("Create Patient Account");
        title.getStyleClass().add("reg-title");

        Text subtitle = new Text("Join Health-Sphere for personalized care and medical tracking.");
        subtitle.getStyleClass().add("reg-subtitle");
        titleBlock.getChildren().addAll(title, subtitle);

        // --- Form Grid (2 Equal Columns) ---
        GridPane grid = new GridPane();
        grid.setHgap(18);
        grid.setVgap(16);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(col1, col2);

        // Row 1: First Name & Last Name
        VBox fnameBox = createFormField("First Name", new TextField(), "John");
        VBox lnameBox = createFormField("Last Name", new TextField(), "Doe");
        grid.add(fnameBox, 0, 0);
        grid.add(lnameBox, 1, 0);

        // Row 2: Email Address & Phone Number
        VBox emailBox = createFormField("Email Address", new TextField(), "john.doe@example.com");
        VBox phoneBox = createFormField("Phone Number", new TextField(), "+1 (555) 000-0000");
        grid.add(emailBox, 0, 1);
        grid.add(phoneBox, 1, 1);

        // Row 3: Date of Birth & Gender
        DatePicker dobPicker = new DatePicker();
        dobPicker.getStyleClass().add("date-picker-custom");
        dobPicker.setMaxWidth(Double.MAX_VALUE);
        VBox dobBox = createFormField("Date of Birth", dobPicker, "Select Date");

        ComboBox<String> genderCombo = new ComboBox<>(FXCollections.observableArrayList("Male", "Female", "Other", "Prefer not to say"));
        genderCombo.getStyleClass().add("combo-box-custom");
        genderCombo.setMaxWidth(Double.MAX_VALUE);
        genderCombo.setPromptText("Select Gender");
        VBox genderBox = createFormField("Gender", genderCombo, null);

        grid.add(dobBox, 0, 2);
        grid.add(genderBox, 1, 2);

        // Row 4: Blood Group & Emergency Contact
        ComboBox<String> bloodCombo = new ComboBox<>(FXCollections.observableArrayList("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"));
        bloodCombo.getStyleClass().add("combo-box-custom");
        bloodCombo.setMaxWidth(Double.MAX_VALUE);
        bloodCombo.setPromptText("Select Blood Group");
        VBox bloodBox = createFormField("Blood Group", bloodCombo, null);

        VBox emergencyBox = createFormField("Emergency Contact", new TextField(), "Contact Name & Number");
        grid.add(bloodBox, 0, 3);
        grid.add(emergencyBox, 1, 3);

        // Row 5: Password & Confirm Password (With Eye Icons)
        VBox passBox = createPasswordField("Password", "••••••••");
        VBox confirmPassBox = createPasswordField("Confirm Password", "••••••••");
        grid.add(passBox, 0, 4);
        grid.add(confirmPassBox, 1, 4);

        // --- Checkboxes Section ---
        VBox checkboxGroup = new VBox(10);
        CheckBox termsCheck = new CheckBox("I agree to the Terms of Service and Privacy Policy");
        termsCheck.setStyle("-fx-font-size: 12px; -fx-text-fill: #475569;");

        CheckBox consentCheck = new CheckBox("I consent to Health-Sphere storing and processing my medical data");
        consentCheck.setStyle("-fx-font-size: 12px; -fx-text-fill: #475569;");

        checkboxGroup.getChildren().addAll(termsCheck, consentCheck);

        // --- Action Buttons ---
        Button createAccountBtn = new Button("Create Account");
        createAccountBtn.getStyleClass().add("btn-register-primary");
        createAccountBtn.setMaxWidth(Double.MAX_VALUE);

        // Navigation back to Login View
        createAccountBtn.setOnAction(e -> {
            LoginView loginView = new LoginView(stage);
            stage.getScene().setRoot(loginView.getScene().getRoot());
        });

        // Sign In Link Row
        HBox signInRow = new HBox(4);
        signInRow.setAlignment(Pos.CENTER);
        Text haveAccountText = new Text("Already have an account?");
        haveAccountText.setStyle("-fx-font-size: 12px; -fx-fill: #64748B;");

        Hyperlink signInLink = new Hyperlink("Sign In");
        signInLink.setStyle("-fx-font-size: 12px; -fx-text-fill: #0256D0; -fx-padding: 0;");
        signInLink.setOnAction(e -> {
            LoginView loginView = new LoginView(stage);
            stage.getScene().setRoot(loginView.getScene().getRoot());
        });

        signInRow.getChildren().addAll(haveAccountText, signInLink);

        // --- Bottom Security Banner ---
        HBox securityFooter = new HBox(8);
        securityFooter.getStyleClass().add("security-footer-box");
        securityFooter.setAlignment(Pos.CENTER);

        ImageView shieldIcon = createSafeImageView("/images/icon_shield.png", 16, 16);
        Node iconNode = shieldIcon.getImage() != null ? shieldIcon : new Text("🛡");

        Text secText = new Text("HIPAA Compliant • 256-bit AES Encryption • Your data is secure");
        secText.getStyleClass().add("security-footer-text");

        securityFooter.getChildren().addAll(iconNode, secText);

        // Assemble Layout
        card.getChildren().addAll(
                headerRow, titleBlock, grid, checkboxGroup,
                createAccountBtn, signInRow, securityFooter
        );

        return card;
    }

    // Helper: Standard Input Box
    private VBox createFormField(String labelText, Node inputNode, String promptText) {
        VBox box = new VBox(6);
        Label label = new Label(labelText);
        label.getStyleClass().add("input-label");

        if (inputNode instanceof TextField textField && promptText != null) {
            textField.setPromptText(promptText);
            textField.getStyleClass().add("text-field-custom");
        }

        box.getChildren().addAll(label, inputNode);
        return box;
    }

    // Helper: Password Box with embedded Eye Icon
    private VBox createPasswordField(String labelText, String promptText) {
        VBox box = new VBox(6);
        Label label = new Label(labelText);
        label.getStyleClass().add("input-label");

        PasswordField passField = new PasswordField();
        passField.setPromptText(promptText);
        passField.getStyleClass().add("text-field-custom");
        passField.setMaxWidth(Double.MAX_VALUE);

        ImageView eyeIcon = createSafeImageView("/images/icon_eye.png", 16, 16);
        Node eyeGraphic = eyeIcon.getImage() != null ? eyeIcon : new Text("👁");

        StackPane passPane = new StackPane();
        passPane.setAlignment(Pos.CENTER_RIGHT);
        StackPane.setMargin(eyeGraphic, new Insets(0, 12, 0, 0));

        passPane.getChildren().addAll(passField, eyeGraphic);

        box.getChildren().addAll(label, passPane);
        return box;
    }

    private ImageView createSafeImageView(String path, double width, double height) {
        ImageView imgView = new ImageView();
        imgView.setFitWidth(width);
        imgView.setFitHeight(height);
        imgView.setPreserveRatio(true);

        try {
            if (getClass().getResource(path) != null) {
                imgView.setImage(new Image(getClass().getResourceAsStream(path)));
            }
        } catch (Exception ignored) {
            // Development fallback
        }
        return imgView;
    }
}