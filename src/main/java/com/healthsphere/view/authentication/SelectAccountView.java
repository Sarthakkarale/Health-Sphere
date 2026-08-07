package com.healthsphere.view.authentication;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SelectAccountView {

    private final Stage stage;

    // Multi-Step State Tracking
    private int currentStep = 1;
    private String selectedRole = "Patient";

    // Dynamic UI Container Handles
    private VBox currentlySelectedRoleCard = null;
    private HBox stepIndicatorContainer;
    private StackPane formContentContainer;
    private Text headingText;
    private Text subtitleText;

    // Persisted Input Fields across steps
    private final TextField firstNameField = new TextField();
    private final TextField lastNameField = new TextField();
    private final TextField emailField = new TextField();
    private final PasswordField passwordField = new PasswordField();
    private final PasswordField confirmPasswordField = new PasswordField();

    public SelectAccountView(Stage stage) {
        this.stage = stage;
    }

    public Scene getScene() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root-container");

        HBox mainContent = new HBox();

        StackPane leftPanel = createLeftMarketingPanel();
        ScrollPane rightPanel = createRightRegistrationPanel();

        // 38% Left, 62% Right layout proportions
        HBox.setHgrow(leftPanel, Priority.ALWAYS);
        HBox.setHgrow(rightPanel, Priority.ALWAYS);

        leftPanel.prefWidthProperty().bind(mainContent.widthProperty().multiply(0.38));
        leftPanel.maxWidthProperty().bind(mainContent.widthProperty().multiply(0.38));
        leftPanel.minWidthProperty().bind(mainContent.widthProperty().multiply(0.38));

        rightPanel.prefWidthProperty().bind(mainContent.widthProperty().multiply(0.62));

        mainContent.getChildren().addAll(leftPanel, rightPanel);
        root.setCenter(mainContent);
        root.setBottom(createFooter());

        Scene scene = new Scene(root, 1280, 850);

        String cssResource = getClass().getResource("/css/auth.css") != null
                ? getClass().getResource("/css/auth.css").toExternalForm()
                : null;
        if (cssResource != null) {
            scene.getStylesheets().add(cssResource);
        }

        return scene;
    }

    // =========================================================================
    // LEFT MARKETING PANEL (Design Preserved)
    // =========================================================================
    private StackPane createLeftMarketingPanel() {
        StackPane leftStack = new StackPane();

        ImageView bgImageView = new ImageView();
        bgImageView.setPreserveRatio(false);
        bgImageView.fitWidthProperty().bind(leftStack.widthProperty());
        bgImageView.fitHeightProperty().bind(leftStack.heightProperty());

        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(leftStack.widthProperty());
        clip.heightProperty().bind(leftStack.heightProperty());
        leftStack.setClip(clip);

        try {
            if (getClass().getResource("/images/auth-background.jpg") != null) {
                bgImageView.setImage(new Image(getClass().getResourceAsStream("/images/auth-background.jpg")));
            }
        } catch (Exception ignored) {}

        Rectangle overlay = new Rectangle();
        overlay.getStyleClass().add("overlay-rect");
        overlay.widthProperty().bind(leftStack.widthProperty());
        overlay.heightProperty().bind(leftStack.heightProperty());

        VBox contentBox = new VBox();
        contentBox.setPadding(new Insets(48));
        contentBox.setAlignment(Pos.TOP_LEFT);

        HBox logoBox = new HBox(12);
        logoBox.setAlignment(Pos.CENTER_LEFT);

        ImageView logoIcon = createSafeImageView("/images/logo.png", 45, 45);
        Node logoGraphic = logoIcon.getImage() != null ? logoIcon : createFallbackLogoGraphic();

        Text brandTitle = new Text("MediNexus AI");
        brandTitle.getStyleClass().add("left-logo-text");
        logoBox.getChildren().addAll(logoGraphic, brandTitle);

        VBox cardContainer = new VBox(20);
        cardContainer.setAlignment(Pos.CENTER_LEFT);
        cardContainer.setPadding(new Insets(40, 0, 0, 0));

        cardContainer.getChildren().addAll(
                createFeatureCard("🔒 Secure Healthcare Platform", "End-to-end encrypted medical data complying with global privacy standards."),
                createFeatureCard("🤖 AI-Powered Medical Insights", "Automated clinical triage, decision assistance, and predictive diagnostics."),
                createFeatureCard("🛡️ Trusted by Patients", "Over 100,000+ verified appointments and records delivered seamlessly.")
        );

        Region verticalSpacer = new Region();
        VBox.setVgrow(verticalSpacer, Priority.ALWAYS);

        VBox bottomCaptionBox = new VBox(4);
        Text line1 = new Text("Clinical grade precision.");
        line1.getStyleClass().add("bottom-caption");
        Text line2 = new Text("Empowering digital healthcare.");
        line2.getStyleClass().add("bottom-caption");
        bottomCaptionBox.getChildren().addAll(line1, line2);

        contentBox.getChildren().addAll(logoBox, cardContainer, verticalSpacer, bottomCaptionBox);
        leftStack.getChildren().addAll(bgImageView, overlay, contentBox);

        return leftStack;
    }

    private Node createFallbackLogoGraphic() {
        StackPane pane = new StackPane();
        Circle circle = new Circle(22, Color.web("#2563EB"));
        Text cross = new Text("+");
        cross.setStyle("-fx-fill: white; -fx-font-weight: bold; -fx-font-size: 24px;");
        pane.getChildren().addAll(circle, cross);
        return pane;
    }

    private HBox createFeatureCard(String titleText, String descText) {
        HBox card = new HBox(14);
        card.getStyleClass().add("glass-feature-card");
        card.setAlignment(Pos.CENTER_LEFT);

        StackPane iconCircle = new StackPane();
        Circle circle = new Circle(16);
        circle.getStyleClass().add("card-icon-circle");
        Text iconSymbol = new Text("✓");
        iconSymbol.setStyle("-fx-fill: white; -fx-font-weight: bold; -fx-font-size: 13px;");
        iconCircle.getChildren().addAll(circle, iconSymbol);

        VBox textBox = new VBox(4);
        Text title = new Text(titleText);
        title.getStyleClass().add("feature-title");

        Label desc = new Label(descText);
        desc.getStyleClass().add("feature-description");
        desc.setWrapText(true);

        textBox.getChildren().addAll(title, desc);
        HBox.setHgrow(textBox, Priority.ALWAYS);

        card.getChildren().addAll(iconCircle, textBox);
        return card;
    }

    // =========================================================================
    // RIGHT PANEL & MAIN CONTAINER
    // =========================================================================
    private ScrollPane createRightRegistrationPanel() {
        VBox outerWrapper = new VBox();
        outerWrapper.setAlignment(Pos.CENTER);
        outerWrapper.setPadding(new Insets(32, 24, 32, 24));

        VBox mainCard = new VBox(20);
        mainCard.getStyleClass().add("main-white-card");
        mainCard.setMaxWidth(780);
        mainCard.setAlignment(Pos.TOP_CENTER);

        ImageView centeredLogo = createSafeImageView("/images/logo.png", 52, 52);
        Node logoGraphic = centeredLogo.getImage() != null ? centeredLogo : createFallbackLogoGraphic();

        VBox textHeader = new VBox(4);
        textHeader.setAlignment(Pos.CENTER);

        headingText = new Text("Create Your Account");
        headingText.getStyleClass().add("heading-text");

        subtitleText = new Text("Join the future of smart healthcare management");
        subtitleText.getStyleClass().add("subtitle-text");

        textHeader.getChildren().addAll(headingText, subtitleText);

        stepIndicatorContainer = new HBox(10);
        stepIndicatorContainer.setAlignment(Pos.CENTER);
        stepIndicatorContainer.setPadding(new Insets(4, 0, 8, 0));
        updateStepIndicator();

        formContentContainer = new StackPane();
        formContentContainer.getChildren().setAll(createStep1Pane());

        mainCard.getChildren().addAll(logoGraphic, textHeader, stepIndicatorContainer, formContentContainer);
        outerWrapper.getChildren().add(mainCard);

        ScrollPane scrollPane = new ScrollPane(outerWrapper);
        scrollPane.getStyleClass().add("right-scroll-pane");
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }

    // =========================================================================
    // NAVIGATION & STEP INDICATOR UPDATES
    // =========================================================================
    private void goToNextStep() {
        if (currentStep < 4) {
            currentStep++;
            updateStepIndicator();
            renderCurrentStepView();
        }
    }

    private void goToPreviousStep() {
        if (currentStep > 1) {
            currentStep--;
            updateStepIndicator();
            renderCurrentStepView();
        }
    }

    private void renderCurrentStepView() {
        switch (currentStep) {
            case 1:
                headingText.setText("Create Your Account");
                subtitleText.setText("Join the future of smart healthcare management");
                formContentContainer.getChildren().setAll(createStep1Pane());
                break;
            case 2:
                headingText.setText("Personal Information");
                subtitleText.setText("Provide your identity details for verification");
                formContentContainer.getChildren().setAll(createStep2Pane());
                break;
            case 3:
                headingText.setText(selectedRole + " Information");
                subtitleText.setText("Enter specific details required for your role");
                formContentContainer.getChildren().setAll(createStep3Pane());
                break;
            case 4:
                headingText.setText("Verify Email Address");
                subtitleText.setText("Enter the 6-digit activation code sent to your email");
                formContentContainer.getChildren().setAll(createStep4Pane());
                break;
        }
    }

    private void updateStepIndicator() {
        stepIndicatorContainer.getChildren().clear();

        stepIndicatorContainer.getChildren().addAll(
                createStepNode(1, "Account"),
                createStepLine(),
                createStepNode(2, "Profile"),
                createStepLine(),
                createStepNode(3, "Role Details"),
                createStepLine(),
                createStepNode(4, "Complete")
        );
    }

    private HBox createStepNode(int stepNum, String title) {
        HBox step = new HBox(6);
        step.setAlignment(Pos.CENTER);

        StackPane circleStack = new StackPane();
        Circle circle = new Circle(11);
        Text num = new Text();

        if (stepNum < currentStep) {
            circle.getStyleClass().add("step-circle-completed");
            num.setText("✓");
            num.getStyleClass().add("step-number-active");
        } else if (stepNum == currentStep) {
            circle.getStyleClass().add("step-circle-active");
            num.setText(String.valueOf(stepNum));
            num.getStyleClass().add("step-number-active");
        } else {
            circle.getStyleClass().add("step-circle-inactive");
            num.setText(String.valueOf(stepNum));
            num.getStyleClass().add("step-number-inactive");
        }

        circleStack.getChildren().addAll(circle, num);

        Label label = new Label(title);
        if (stepNum < currentStep) {
            label.getStyleClass().add("step-label-completed");
        } else if (stepNum == currentStep) {
            label.getStyleClass().add("step-label-active");
        } else {
            label.getStyleClass().add("step-label-inactive");
        }

        step.getChildren().addAll(circleStack, label);
        return step;
    }

    private Line createStepLine() {
        Line line = new Line(0, 0, 24, 0);
        line.getStyleClass().add("step-line");
        return line;
    }

    // =========================================================================
    // STEP 1 PANE (Preserved Role Cards)
    // =========================================================================
    private VBox createStep1Pane() {
        VBox container = new VBox(20);

        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(16);

        VBox patientCard = createRoleCard("/images/patient.png", "👤", "Patient", "Manage health records, appointments & AI medical assistance.", false, false);
        VBox doctorCard = createRoleCard("/images/doctor.png", "🩺", "Doctor", "Access AI diagnostics, write prescriptions & manage clinic workflows.", false, false);
        VBox hospitalCard = createRoleCard("/images/hospital.png", "🏥", "Hospital", "Organization level operations, bed tracking & triage scheduling.", false, false);
        VBox adminCard = createRoleCard("/images/admin.png", "🛡️", "Administrator", "System controls, security audit logs & user governance.", true, true);

        if (selectedRole.equalsIgnoreCase("Doctor")) currentlySelectedRoleCard = doctorCard;
        else if (selectedRole.equalsIgnoreCase("Hospital")) currentlySelectedRoleCard = hospitalCard;
        else currentlySelectedRoleCard = patientCard;

        currentlySelectedRoleCard.getStyleClass().add("role-card-selected");

        grid.add(patientCard, 0, 0);
        grid.add(doctorCard, 1, 0);
        grid.add(hospitalCard, 0, 1);
        grid.add(adminCard, 1, 1);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(col1, col2);

        HBox bottomControls = new HBox(16);
        bottomControls.setAlignment(Pos.CENTER_RIGHT);

        HBox loginPromptBox = new HBox(4);
        loginPromptBox.setAlignment(Pos.CENTER_LEFT);

        Text promptText = new Text("Already have an account? ");
        promptText.getStyleClass().add("login-prompt-text");

        Text loginLink = new Text("Log In");
        loginLink.getStyleClass().add("login-link-text");

        loginLink.setOnMouseClicked(e -> {
            LoginView loginView = new LoginView(stage);
            stage.getScene().setRoot(loginView.getScene().getRoot());
        });

        loginPromptBox.getChildren().addAll(promptText, loginLink);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button continueBtn = new Button("Continue");
        continueBtn.getStyleClass().add("btn-continue");
        addBtnAnimations(continueBtn);
        continueBtn.setOnAction(e -> goToNextStep());

        bottomControls.getChildren().addAll(loginPromptBox, spacer, continueBtn);
        container.getChildren().addAll(grid, bottomControls);

        return container;
    }

    private VBox createRoleCard(String iconPath, String fallbackEmoji, String titleText, String descText, boolean isDisabled, boolean isInviteOnly) {
        VBox card = new VBox(8);
        card.getStyleClass().add("role-card");
        card.setAlignment(Pos.CENTER_LEFT);

        HBox cardHeader = new HBox();
        cardHeader.setAlignment(Pos.CENTER_LEFT);

        StackPane iconBox = new StackPane();
        iconBox.getStyleClass().add("icon-box-blue");

        ImageView iconView = createSafeImageView(iconPath, 24, 24);
        Node iconNode = iconView.getImage() != null ? iconView : new Text(fallbackEmoji);
        iconBox.getChildren().add(iconNode);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        cardHeader.getChildren().addAll(iconBox, spacer);

        if (isInviteOnly) {
            Label badge = new Label("INVITE ONLY");
            badge.getStyleClass().add("badge-invite-only");
            cardHeader.getChildren().add(badge);
        }

        Text title = new Text(titleText);
        title.getStyleClass().add(isDisabled ? "role-title-disabled" : "role-title");

        Label desc = new Label(descText);
        desc.getStyleClass().add("role-description");
        desc.setWrapText(true);

        card.getChildren().addAll(cardHeader, title, desc);

        if (isDisabled) {
            card.getStyleClass().add("role-card-disabled");
        } else {
            addCardHoverAndSelection(card, titleText);
        }

        return card;
    }

    private void addCardHoverAndSelection(VBox card, String roleName) {
        DropShadow hoverShadow = new DropShadow();
        hoverShadow.setColor(Color.rgb(37, 99, 235, 0.16));
        hoverShadow.setRadius(14);
        hoverShadow.setOffsetY(4);

        TranslateTransition liftUp = new TranslateTransition(Duration.millis(150), card);
        TranslateTransition dropDown = new TranslateTransition(Duration.millis(150), card);

        card.setOnMouseEntered(e -> {
            if (card != currentlySelectedRoleCard) {
                card.setEffect(hoverShadow);
                card.setStyle("-fx-border-color: #2563EB;");
                liftUp.setToY(-2);
                liftUp.playFromStart();
            }
        });

        card.setOnMouseExited(e -> {
            if (card != currentlySelectedRoleCard) {
                card.setEffect(null);
                card.setStyle(null);
                dropDown.setToY(0);
                dropDown.playFromStart();
            }
        });

        card.setOnMouseClicked(e -> {
            if (currentlySelectedRoleCard != null) {
                currentlySelectedRoleCard.getStyleClass().remove("role-card-selected");
                currentlySelectedRoleCard.setTranslateY(0);
                currentlySelectedRoleCard.setEffect(null);
                currentlySelectedRoleCard.setStyle(null);
            }

            currentlySelectedRoleCard = card;
            selectedRole = roleName;
            card.getStyleClass().add("role-card-selected");
        });
    }

    // =========================================================================
    // STEP 2 PANE (Personal Information)
    // =========================================================================
    private VBox createStep2Pane() {
        VBox container = new VBox(16);

        GridPane formGrid = new GridPane();
        formGrid.setHgap(16);
        formGrid.setVgap(14);

        firstNameField.setPromptText("John");
        firstNameField.getStyleClass().add("text-field-custom");

        lastNameField.setPromptText("Doe");
        lastNameField.getStyleClass().add("text-field-custom");

        formGrid.add(createFieldWrapper("First Name", firstNameField), 0, 0);
        formGrid.add(createFieldWrapper("Last Name", lastNameField), 1, 0);

        emailField.setPromptText("john.doe@example.com");
        emailField.getStyleClass().add("text-field-custom");
        formGrid.add(createFieldWrapper("Email Address", emailField), 0, 1, 2, 1);

        passwordField.setPromptText("••••••••");
        passwordField.getStyleClass().add("text-field-custom");

        confirmPasswordField.setPromptText("••••••••");
        confirmPasswordField.getStyleClass().add("text-field-custom");

        formGrid.add(createFieldWrapper("Password", passwordField), 0, 2);
        formGrid.add(createFieldWrapper("Confirm Password", confirmPasswordField), 1, 2);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        formGrid.getColumnConstraints().addAll(col1, col2);

        HBox bottomControls = new HBox(16);
        bottomControls.setAlignment(Pos.CENTER_RIGHT);
        bottomControls.setPadding(new Insets(10, 0, 0, 0));

        Button backBtn = new Button("Back");
        backBtn.getStyleClass().add("btn-back");
        backBtn.setOnAction(e -> goToPreviousStep());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button continueBtn = new Button("Continue");
        continueBtn.getStyleClass().add("btn-continue");
        addBtnAnimations(continueBtn);
        continueBtn.setOnAction(e -> goToNextStep());

        bottomControls.getChildren().addAll(backBtn, spacer, continueBtn);
        container.getChildren().addAll(formGrid, bottomControls);

        return container;
    }

    // =========================================================================
    // STEP 3 PANE (Role Information)
    // =========================================================================
    private VBox createStep3Pane() {
        VBox container = new VBox(16);

        GridPane formGrid = new GridPane();
        formGrid.setHgap(16);
        formGrid.setVgap(14);

        if (selectedRole.equalsIgnoreCase("Patient")) {
            TextField dobField = new TextField();
            dobField.setPromptText("YYYY-MM-DD");
            dobField.getStyleClass().add("text-field-custom");

            ComboBox<String> genderCombo = new ComboBox<>();
            genderCombo.getItems().addAll("Male", "Female", "Other");
            genderCombo.setPromptText("Select Gender");
            genderCombo.getStyleClass().add("combo-box-custom");
            genderCombo.setMaxWidth(Double.MAX_VALUE);

            ComboBox<String> bloodGroupCombo = new ComboBox<>();
            bloodGroupCombo.getItems().addAll("A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-");
            bloodGroupCombo.setPromptText("Select Blood Group");
            bloodGroupCombo.getStyleClass().add("combo-box-custom");
            bloodGroupCombo.setMaxWidth(Double.MAX_VALUE);

            TextField phoneField = new TextField();
            phoneField.setPromptText("+1 (555) 000-0000");
            phoneField.getStyleClass().add("text-field-custom");

            TextField addressField = new TextField();
            addressField.setPromptText("123 Health Street, City, Country");
            addressField.getStyleClass().add("text-field-custom");

            formGrid.add(createFieldWrapper("Date of Birth", dobField), 0, 0);
            formGrid.add(createFieldWrapper("Gender", genderCombo), 1, 0);
            formGrid.add(createFieldWrapper("Blood Group", bloodGroupCombo), 0, 1);
            formGrid.add(createFieldWrapper("Phone Number", phoneField), 1, 1);
            formGrid.add(createFieldWrapper("Residential Address", addressField), 0, 2, 2, 1);

        } else if (selectedRole.equalsIgnoreCase("Doctor")) {
            TextField licenseField = new TextField();
            licenseField.setPromptText("MED-897452-X");
            licenseField.getStyleClass().add("text-field-custom");

            ComboBox<String> specCombo = new ComboBox<>();
            specCombo.getItems().addAll("Cardiology", "Neurology", "Pediatrics", "General Practice", "Orthopedics", "Dermatology");
            specCombo.setPromptText("Select Specialty");
            specCombo.getStyleClass().add("combo-box-custom");
            specCombo.setMaxWidth(Double.MAX_VALUE);

            TextField hospitalAffiliation = new TextField();
            hospitalAffiliation.setPromptText("St. Jude Memorial Hospital");
            hospitalAffiliation.getStyleClass().add("text-field-custom");

            TextField experienceField = new TextField();
            experienceField.setPromptText("Years of Practice (e.g. 8)");
            experienceField.getStyleClass().add("text-field-custom");

            formGrid.add(createFieldWrapper("Medical License Number", licenseField), 0, 0);
            formGrid.add(createFieldWrapper("Specialization", specCombo), 1, 0);
            formGrid.add(createFieldWrapper("Hospital Affiliation", hospitalAffiliation), 0, 1);
            formGrid.add(createFieldWrapper("Years of Experience", experienceField), 1, 1);

        } else if (selectedRole.equalsIgnoreCase("Hospital")) {
            TextField hospitalName = new TextField();
            hospitalName.setPromptText("City Care General Hospital");
            hospitalName.getStyleClass().add("text-field-custom");

            TextField regNo = new TextField();
            regNo.setPromptText("HOSP-REG-9941");
            regNo.getStyleClass().add("text-field-custom");

            TextField addressField = new TextField();
            addressField.setPromptText("Hospital Facility Address");
            addressField.getStyleClass().add("text-field-custom");

            TextField cityField = new TextField();
            cityField.setPromptText("City / Region");
            cityField.getStyleClass().add("text-field-custom");

            formGrid.add(createFieldWrapper("Hospital / Facility Name", hospitalName), 0, 0, 2, 1);
            formGrid.add(createFieldWrapper("Registration Number", regNo), 0, 1);
            formGrid.add(createFieldWrapper("City / Location", cityField), 1, 1);
            formGrid.add(createFieldWrapper("Full Facility Address", addressField), 0, 2, 2, 1);
        }

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        formGrid.getColumnConstraints().addAll(col1, col2);

        HBox bottomControls = new HBox(16);
        bottomControls.setAlignment(Pos.CENTER_RIGHT);
        bottomControls.setPadding(new Insets(10, 0, 0, 0));

        Button backBtn = new Button("Back");
        backBtn.getStyleClass().add("btn-back");
        backBtn.setOnAction(e -> goToPreviousStep());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button continueBtn = new Button("Continue");
        continueBtn.getStyleClass().add("btn-continue");
        addBtnAnimations(continueBtn);
        continueBtn.setOnAction(e -> goToNextStep());

        bottomControls.getChildren().addAll(backBtn, spacer, continueBtn);
        container.getChildren().addAll(formGrid, bottomControls);

        return container;
    }

    // =========================================================================
    // STEP 4 PANE (Verification)
    // =========================================================================
    private VBox createStep4Pane() {
        VBox container = new VBox(20);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(16, 40, 10, 40));

        Text infoText = new Text("We have sent a 6-digit verification code to:\n" 
                + (emailField.getText().isEmpty() ? "your registered email address" : emailField.getText()));
        infoText.setStyle("-fx-font-size: 14px; -fx-fill: #4B5563; -fx-text-alignment: center;");

        HBox otpBox = new HBox(10);
        otpBox.setAlignment(Pos.CENTER);

        for (int i = 0; i < 6; i++) {
            TextField digitField = new TextField();
            digitField.setPrefWidth(46);
            digitField.setPrefHeight(50);
            digitField.setAlignment(Pos.CENTER);
            digitField.getStyleClass().add("text-field-custom");
            digitField.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
            otpBox.getChildren().add(digitField);
        }

        HBox resendBox = new HBox(4);
        resendBox.setAlignment(Pos.CENTER);
        Text resendPrompt = new Text("Didn't receive code? ");
        resendPrompt.setStyle("-fx-font-size: 13px; -fx-fill: #6B7280;");

        Text resendLink = new Text("Resend OTP");
        resendLink.getStyleClass().add("login-link-text");

        resendBox.getChildren().addAll(resendPrompt, resendLink);

        HBox bottomControls = new HBox(16);
        bottomControls.setAlignment(Pos.CENTER_RIGHT);
        bottomControls.setPadding(new Insets(10, 0, 0, 0));

        Button backBtn = new Button("Back");
        backBtn.getStyleClass().add("btn-back");
        backBtn.setOnAction(e -> goToPreviousStep());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button verifyBtn = new Button("Complete Registration");
        verifyBtn.getStyleClass().add("btn-continue");
        verifyBtn.setPrefWidth(220);
        addBtnAnimations(verifyBtn);

        verifyBtn.setOnAction(e -> {
            LoginView loginView = new LoginView(stage);
            stage.getScene().setRoot(loginView.getScene().getRoot());
        });

        bottomControls.getChildren().addAll(backBtn, spacer, verifyBtn);
        container.getChildren().addAll(infoText, otpBox, resendBox, bottomControls);

        return container;
    }

    // =========================================================================
    // HELPER UI FACTORIES
    // =========================================================================
    private VBox createFieldWrapper(String labelText, Node inputNode) {
        VBox box = new VBox(6);
        box.setAlignment(Pos.CENTER_LEFT);

        Label label = new Label(labelText);
        label.getStyleClass().add("form-label");

        box.getChildren().addAll(label, inputNode);
        return box;
    }

    private void addBtnAnimations(Button btn) {
        DropShadow btnGlow = new DropShadow();
        btnGlow.setColor(Color.rgb(37, 99, 235, 0.40));
        btnGlow.setRadius(12);

        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(120), btn);
        scaleUp.setToX(1.02);
        scaleUp.setToY(1.02);

        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(120), btn);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);

        btn.setOnMouseEntered(e -> {
            btn.setEffect(btnGlow);
            scaleUp.playFromStart();
        });

        btn.setOnMouseExited(e -> {
            btn.setEffect(null);
            scaleDown.playFromStart();
        });
    }

    private HBox createFooter() {
        HBox footer = new HBox();
        footer.getStyleClass().add("footer-bar");
        footer.setAlignment(Pos.CENTER);

        Text copyright = new Text("MediNexus AI © 2026 Health-Sphere Systems Inc.");
        copyright.getStyleClass().add("footer-copyright");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox linksBox = new HBox(20);
        linksBox.setAlignment(Pos.CENTER_RIGHT);

        Text privacy = new Text("Privacy");
        privacy.getStyleClass().add("footer-hyperlink");

        Text terms = new Text("Terms");
        terms.getStyleClass().add("footer-hyperlink");

        Text security = new Text("Security");
        security.getStyleClass().add("footer-hyperlink");

        Text hipaa = new Text("HIPAA");
        hipaa.getStyleClass().add("footer-hyperlink");

        linksBox.getChildren().addAll(privacy, terms, security, hipaa);
        footer.getChildren().addAll(copyright, spacer, linksBox);

        return footer;
    }

    private ImageView createSafeImageView(String path, double width, double height) {
        ImageView img = new ImageView();
        img.setFitWidth(width);
        img.setFitHeight(height);
        img.setPreserveRatio(true);

        try {
            if (getClass().getResource(path) != null) {
                img.setImage(new Image(getClass().getResourceAsStream(path)));
            }
        } catch (Exception ignored) {}

        return img;
    }
}