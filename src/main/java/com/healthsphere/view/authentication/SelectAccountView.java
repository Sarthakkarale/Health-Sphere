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

    // Multi-Step State Tracking (1: Account, 2: Profile, 3: Role Details, 4: Verification, 5: Status Outcome)
    private int currentStep = 1;
    private String selectedRole = "Patient";

    // Dynamic UI Handles
    private VBox currentlySelectedRoleCard = null;
    private HBox stepIndicatorContainer;
    private StackPane formContentContainer;
    private Text headingText;
    private Text subtitleText;

    // Persisted Inputs across steps
    private final TextField firstNameField = new TextField();
    private final TextField lastNameField = new TextField();
    private final TextField emailField = new TextField();
    private final PasswordField passwordField = new PasswordField();
    private final PasswordField confirmPasswordField = new PasswordField();
    private final TextField phoneField = new TextField();

    public SelectAccountView(Stage stage) {
        this.stage = stage;
    }

    public Scene getScene() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root-container");

        HBox mainContent = new HBox();

        StackPane leftPanel = createLeftMarketingPanel();
        ScrollPane rightPanel = createRightRegistrationPanel();

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
    // LEFT PANEL (Preserved Design)
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

        Text brandTitle = new Text("Health-Sphere AI");
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
    // RIGHT PANEL MAIN CONTAINER
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
    // STEP NAVIGATION CONTROLLER
    // =========================================================================
    private void goToNextStep() {
        if (currentStep < 5) {
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
                headingText.setText("Select Portal");
                subtitleText.setText("Choose your portal type to get started");
                formContentContainer.getChildren().setAll(createStep1Pane());
                break;
            case 2:
                headingText.setText("Personal Information");
                subtitleText.setText("Provide your personal details for identity verification");
                formContentContainer.getChildren().setAll(createStep2Pane());
                break;
            case 3:
                headingText.setText(selectedRole + " Information");
                subtitleText.setText("Enter role-specific credentials for onboarding");
                formContentContainer.getChildren().setAll(createStep3Pane());
                break;
            case 4:
                headingText.setText("Email Verification");
                subtitleText.setText("Enter the 6-digit activation code sent to your email");
                formContentContainer.getChildren().setAll(createStep4Pane());
                break;
            case 5:
                stepIndicatorContainer.setVisible(false);
                stepIndicatorContainer.setManaged(false);
                formContentContainer.getChildren().setAll(createStep5Pane());
                break;
        }
    }

    private void updateStepIndicator() {
        stepIndicatorContainer.getChildren().clear();

        stepIndicatorContainer.getChildren().addAll(
                createStepNode(1, "Portal"),
                createStepLine(),
                createStepNode(2, "Personal"),
                createStepLine(),
                createStepNode(3, "Role Info"),
                createStepLine(),
                createStepNode(4, "Verify")
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
    // STEP 1 - SELECT PORTAL (INFORMATIVE CARDS)
    // =========================================================================
    private VBox createStep1Pane() {
        VBox container = new VBox(20);

        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(16);

        VBox patientCard = createRoleCard("/images/patient.png", "👤", "Patient", 
                "Book appointments • Access reports • AI Assistant", false, "Instant Access", "badge-instant-access");
        
        VBox doctorCard = createRoleCard("/images/doctor.png", "🩺", "Doctor", 
                "Medical License Required • Professional Verification", false, "Verification Required", "badge-verification-required");
        
        VBox hospitalCard = createRoleCard("/images/hospital.png", "🏥", "Hospital", 
                "Organization Registration • Admin Approval Required", false, "Approval Required", "badge-verification-required");
        
        VBox adminCard = createRoleCard("/images/admin.png", "🛡️", "Administrator", 
                "System controls • Audit logs • User governance", true, "Invite Only", "badge-invite-only");

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

        Button continueBtn = new Button("Continue →");
        continueBtn.getStyleClass().add("btn-continue");
        addBtnAnimations(continueBtn);
        continueBtn.setOnAction(e -> goToNextStep());

        bottomControls.getChildren().addAll(loginPromptBox, spacer, continueBtn);
        container.getChildren().addAll(grid, bottomControls);

        return container;
    }

    private VBox createRoleCard(String iconPath, String fallbackEmoji, String titleText, String descText, boolean isDisabled, String badgeText, String badgeStyleClass) {
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

        if (badgeText != null && !badgeText.isEmpty()) {
            Label badge = new Label(badgeText);
            badge.getStyleClass().add(badgeStyleClass);
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
    // STEP 2 - PERSONAL INFORMATION
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
        formGrid.add(createFieldWrapper("Email Address", emailField), 0, 1);

        phoneField.setPromptText("+1 (555) 000-0000");
        phoneField.getStyleClass().add("text-field-custom");
        formGrid.add(createFieldWrapper("Phone Number", phoneField), 1, 1);

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

        Button continueBtn = new Button("Continue →");
        continueBtn.getStyleClass().add("btn-continue");
        addBtnAnimations(continueBtn);
        continueBtn.setOnAction(e -> goToNextStep());

        bottomControls.getChildren().addAll(backBtn, spacer, continueBtn);
        container.getChildren().addAll(formGrid, bottomControls);

        return container;
    }

    // =========================================================================
    // STEP 3 - DYNAMIC ROLE SPECIFIC INFORMATION
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

            TextField emergencyContactField = new TextField();
            emergencyContactField.setPromptText("Emergency Contact Name / Phone");
            emergencyContactField.getStyleClass().add("text-field-custom");

            TextField addressField = new TextField();
            addressField.setPromptText("123 Health Street, City, State");
            addressField.getStyleClass().add("text-field-custom");

            formGrid.add(createFieldWrapper("Date of Birth", dobField), 0, 0);
            formGrid.add(createFieldWrapper("Gender", genderCombo), 1, 0);
            formGrid.add(createFieldWrapper("Blood Group", bloodGroupCombo), 0, 1);
            formGrid.add(createFieldWrapper("Emergency Contact", emergencyContactField), 1, 1);
            formGrid.add(createFieldWrapper("Residential Address", addressField), 0, 2, 2, 1);

        } else if (selectedRole.equalsIgnoreCase("Doctor")) {
            TextField regNoField = new TextField();
            regNoField.setPromptText("MED-REG-897452");
            regNoField.getStyleClass().add("text-field-custom");

            ComboBox<String> specCombo = new ComboBox<>();
            specCombo.getItems().addAll("Cardiology", "Neurology", "Pediatrics", "General Practice", "Orthopedics", "Dermatology");
            specCombo.setPromptText("Select Specialization");
            specCombo.getStyleClass().add("combo-box-custom");
            specCombo.setMaxWidth(Double.MAX_VALUE);

            TextField expField = new TextField();
            expField.setPromptText("Years of Experience (e.g. 8)");
            expField.getStyleClass().add("text-field-custom");

            TextField hospitalAffiliation = new TextField();
            hospitalAffiliation.setPromptText("St. Jude Memorial Hospital");
            hospitalAffiliation.getStyleClass().add("text-field-custom");

            TextField medicalCouncilField = new TextField();
            medicalCouncilField.setPromptText("Medical Council Name");
            medicalCouncilField.getStyleClass().add("text-field-custom");

            HBox uploadStubBox = new HBox();
            uploadStubBox.setAlignment(Pos.CENTER_LEFT);
            uploadStubBox.getStyleClass().add("upload-stub-box");
            Text uploadText = new Text("📄 Medical License (Upload Later via Firebase Storage)");
            uploadText.getStyleClass().add("upload-stub-text");
            uploadStubBox.getChildren().add(uploadText);

            formGrid.add(createFieldWrapper("Medical Registration Number", regNoField), 0, 0);
            formGrid.add(createFieldWrapper("Specialization", specCombo), 1, 0);
            formGrid.add(createFieldWrapper("Experience (Years)", expField), 0, 1);
            formGrid.add(createFieldWrapper("Hospital Name", hospitalAffiliation), 1, 1);
            formGrid.add(createFieldWrapper("Medical Council", medicalCouncilField), 0, 2);
            formGrid.add(createFieldWrapper("Upload License Document", uploadStubBox), 1, 2);

        } else if (selectedRole.equalsIgnoreCase("Hospital")) {
            TextField hospitalName = new TextField();
            hospitalName.setPromptText("City Care General Hospital");
            hospitalName.getStyleClass().add("text-field-custom");

            TextField regNo = new TextField();
            regNo.setPromptText("HOSP-REG-9941");
            regNo.getStyleClass().add("text-field-custom");

            ComboBox<String> typeCombo = new ComboBox<>();
            typeCombo.getItems().addAll("General Hospital", "Specialized Clinic", "Multispecialty Center", "Trauma Care");
            typeCombo.setPromptText("Select Type");
            typeCombo.getStyleClass().add("combo-box-custom");
            typeCombo.setMaxWidth(Double.MAX_VALUE);

            TextField bedsField = new TextField();
            bedsField.setPromptText("e.g. 250");
            bedsField.getStyleClass().add("text-field-custom");

            TextField repNameField = new TextField();
            repNameField.setPromptText("Authorized Representative Name");
            repNameField.getStyleClass().add("text-field-custom");

            TextField cityField = new TextField();
            cityField.setPromptText("City");
            cityField.getStyleClass().add("text-field-custom");

            TextField stateField = new TextField();
            stateField.setPromptText("State / Province");
            stateField.getStyleClass().add("text-field-custom");

            TextField addressField = new TextField();
            addressField.setPromptText("Full Facility Street Address");
            addressField.getStyleClass().add("text-field-custom");

            formGrid.add(createFieldWrapper("Hospital / Facility Name", hospitalName), 0, 0, 2, 1);
            formGrid.add(createFieldWrapper("Registration Number", regNo), 0, 1);
            formGrid.add(createFieldWrapper("Facility Type", typeCombo), 1, 1);
            formGrid.add(createFieldWrapper("Number of Beds", bedsField), 0, 2);
            formGrid.add(createFieldWrapper("Representative Name", repNameField), 1, 2);
            formGrid.add(createFieldWrapper("City", cityField), 0, 3);
            formGrid.add(createFieldWrapper("State", stateField), 1, 3);
            formGrid.add(createFieldWrapper("Facility Address", addressField), 0, 4, 2, 1);
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

        Button continueBtn = new Button("Continue →");
        continueBtn.getStyleClass().add("btn-continue");
        addBtnAnimations(continueBtn);
        continueBtn.setOnAction(e -> goToNextStep());

        bottomControls.getChildren().addAll(backBtn, spacer, continueBtn);
        container.getChildren().addAll(formGrid, bottomControls);

        return container;
    }

    // =========================================================================
    // STEP 4 - EMAIL VERIFICATION
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

        Text resendLink = new Text("Resend Code");
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

        Button verifyBtn = new Button("Verify Email →");
        verifyBtn.getStyleClass().add("btn-continue");
        verifyBtn.setPrefWidth(180);
        addBtnAnimations(verifyBtn);

        verifyBtn.setOnAction(e -> goToNextStep());

        bottomControls.getChildren().addAll(backBtn, spacer, verifyBtn);
        container.getChildren().addAll(infoText, otpBox, resendBox, bottomControls);

        return container;
    }

    // =========================================================================
    // STEP 5 - DYNAMIC REGISTRATION OUTCOME SCREEN
    // =========================================================================
    private VBox createStep5Pane() {
        VBox container = new VBox(20);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(20, 20, 20, 20));

        if (selectedRole.equalsIgnoreCase("Patient")) {
            headingText.setText("Registration Successful");
            subtitleText.setText("Your account is ready for use");

            Label badge = new Label("STATUS: ACTIVE");
            badge.getStyleClass().add("badge-success");

            Text mainDesc = new Text("Welcome to Health-Sphere! You can now book appointments, access health records, and utilize AI medical assistance.");
            mainDesc.setStyle("-fx-font-size: 14px; -fx-fill: #4B5563; -fx-text-alignment: center;");
            mainDesc.setWrappingWidth(500);

            Button actionBtn = new Button("Go to Dashboard →");
            actionBtn.getStyleClass().add("btn-continue");
            actionBtn.setPrefWidth(200);
            addBtnAnimations(actionBtn);
            actionBtn.setOnAction(e -> {
                LoginView loginView = new LoginView(stage);
                stage.getScene().setRoot(loginView.getScene().getRoot());
            });

            container.getChildren().addAll(badge, mainDesc, actionBtn);

        } else if (selectedRole.equalsIgnoreCase("Doctor")) {
            headingText.setText("Registration Submitted");
            subtitleText.setText("Your professional credentials will now be verified");

            Label badge = new Label("STATUS: PENDING APPROVAL");
            badge.getStyleClass().add("badge-pending");

            Text mainDesc = new Text("Thank you for registering. Our administrative team is reviewing your medical registration details.\n\nEstimated Verification Time: 24–48 Hours");
            mainDesc.setStyle("-fx-font-size: 14px; -fx-fill: #4B5563; -fx-text-alignment: center;");
            mainDesc.setWrappingWidth(500);

            Button actionBtn = new Button("Back to Login");
            actionBtn.getStyleClass().add("btn-back");
            actionBtn.setPrefWidth(180);
            actionBtn.setOnAction(e -> {
                LoginView loginView = new LoginView(stage);
                stage.getScene().setRoot(loginView.getScene().getRoot());
            });

            container.getChildren().addAll(badge, mainDesc, actionBtn);

        } else if (selectedRole.equalsIgnoreCase("Hospital")) {
            headingText.setText("Registration Submitted");
            subtitleText.setText("Organization verification has started");

            Label badge = new Label("STATUS: PENDING APPROVAL");
            badge.getStyleClass().add("badge-pending");

            Text mainDesc = new Text("Thank you for onboarding your hospital facility. Our administrative board will contact your official representative for identity confirmation.\n\nEstimated Verification Time: 24–48 Hours");
            mainDesc.setStyle("-fx-font-size: 14px; -fx-fill: #4B5563; -fx-text-alignment: center;");
            mainDesc.setWrappingWidth(500);

            Button actionBtn = new Button("Back to Login");
            actionBtn.getStyleClass().add("btn-back");
            actionBtn.setPrefWidth(180);
            actionBtn.setOnAction(e -> {
                LoginView loginView = new LoginView(stage);
                stage.getScene().setRoot(loginView.getScene().getRoot());
            });

            container.getChildren().addAll(badge, mainDesc, actionBtn);
        }

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

        Text copyright = new Text("Health-Sphere AI © 2026 Health-Sphere Systems Inc.");
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