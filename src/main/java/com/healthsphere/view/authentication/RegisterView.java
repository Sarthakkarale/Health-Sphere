package com.healthsphere.view.authentication;

import com.healthsphere.controller.authentication.PatientRegistrationController;
import com.healthsphere.controller.authentication.DoctorRegistrationController;
import com.healthsphere.controller.authentication.HospitalRegistrationController;
import com.healthsphere.model.DoctorProfile;
import com.healthsphere.model.HospitalProfile;
import com.healthsphere.model.PatientProfile;
import com.healthsphere.model.UserProfile;

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

public class RegisterView {

    private final Stage stage;

    // Controllers for registration handling
    private final PatientRegistrationController patientRegistrationController = new PatientRegistrationController();
    private final DoctorRegistrationController doctorRegistrationController = new DoctorRegistrationController();
    private final HospitalRegistrationController hospitalRegistrationController = new HospitalRegistrationController();

    // Persisted UserProfile returned after registration
    private PatientProfile registeredPatientProfile;
    private DoctorProfile registeredDoctorProfile;
    private HospitalProfile registeredHospitalProfile;

    // Multi-Step State Tracking// 1: Portal, 2: Personal Information, 3: Role Details, 4: Registration Outcome
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

    // Patient role fields
    private final DatePicker patientDobField = new DatePicker();
    private final ComboBox<String> patientGenderField = new ComboBox<>();
    private final ComboBox<String> patientBloodGroupField = new ComboBox<>();
    private final TextField patientEmergencyContactField = new TextField();
    private final TextField patientAddressField = new TextField();

    // Doctor role fields
    private final TextField doctorLicenseField = new TextField();
    private final TextField doctorSpecializationField = new TextField();
    private final TextField doctorQualificationField = new TextField();
    private final TextField doctorExperienceField = new TextField();
    private final TextField doctorFeeField = new TextField();

    // Hospital role fields
    private final TextField hospitalNameField = new TextField();
    private final TextField hospitalRegistrationField = new TextField();
    private final ComboBox<String> hospitalTypeField = new ComboBox<>();
    private final TextField hospitalBedsField = new TextField();
    private final TextField hospitalAddressField = new TextField();
    private final TextField hospitalContactField = new TextField();

    // Administrator role fields
    private final TextField adminInvitationCodeField = new TextField();
    private final TextField adminDepartmentField = new TextField();
    private final TextField adminIdField = new TextField();

    public RegisterView(Stage stage) {
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

        Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());

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
        if (currentStep == 3) {
            // Trigger Controller registration logic when moving from step 3 to step 4
            String dob = patientDobField.getValue() != null
            ? patientDobField.getValue().toString()
            : null;
            try {
                switch (selectedRole) {
                    case "Patient":
                        registeredPatientProfile = patientRegistrationController.register(
                        firstNameField.getText().trim(),
                        lastNameField.getText().trim(),
                        emailField.getText().trim(),
                        passwordField.getText(),
                        phoneField.getText().trim(),
                        dob,
                        patientGenderField.getValue(),
                        patientBloodGroupField.getValue(),
                        patientEmergencyContactField.getText().trim(),
                        patientAddressField.getText().trim()
                );
                        break;
                    case "Doctor":
                        registeredDoctorProfile =
                            doctorRegistrationController.register(
                                    firstNameField.getText().trim(),
                                    lastNameField.getText().trim(),
                                    emailField.getText().trim(),
                                    passwordField.getText(),
                                    phoneField.getText().trim(),
                                    doctorLicenseField
                                            .getText()
                                            .trim(),
                                    doctorSpecializationField.getText().trim(),
                                    doctorExperienceField
                                            .getText()
                                            .trim(),
                                    doctorQualificationField
                                            .getText()
                                            .trim(),
                                    doctorFeeField
                                            .getText()
                                            .trim()
                            );
                        break;
                    case "Hospital":

                        registeredHospitalProfile =
                                hospitalRegistrationController.register(

                                        emailField
                                                .getText()
                                                .trim(),

                                        passwordField
                                                .getText(),

                                        hospitalNameField
                                                .getText()
                                                .trim(),

                                        hospitalRegistrationField
                                                .getText()
                                                .trim(),

                                        hospitalTypeField
                                                .getValue(),

                                        hospitalBedsField
                                                .getText()
                                                .trim(),

                                        hospitalContactField
                                                .getText()
                                                .trim(),

                                        hospitalAddressField
                                                .getText()
                                                .trim()
                                );

                        break;
                    case "Administrator":
                        // Administrator registration disabled
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();

                showRegistrationError(
                        e.getMessage()
                );

                return;
            }
        }

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
                headingText.setText("Select Portal");
                subtitleText.setText("Choose your portal type to get started");
                stepIndicatorContainer.setVisible(true);
                stepIndicatorContainer.setManaged(true);
                formContentContainer.getChildren().setAll(createStep1Pane());
                break;
            case 2:
                headingText.setText("Personal Information");
                subtitleText.setText("Provide your personal details for identity verification");
                stepIndicatorContainer.setVisible(true);
                stepIndicatorContainer.setManaged(true);
                formContentContainer.getChildren().setAll(createStep2Pane());
                break;
            case 3:
                headingText.setText(selectedRole + " Information");
                subtitleText.setText("Enter role-specific credentials for onboarding");
                stepIndicatorContainer.setVisible(true);
                stepIndicatorContainer.setManaged(true);
                formContentContainer.getChildren().setAll(createStep3Pane());
                break;
            case 4:
                headingText.setText("Registration Complete");
                subtitleText.setText("Your account status and next steps");
                stepIndicatorContainer.setVisible(false);
                stepIndicatorContainer.setManaged(false);
                formContentContainer.getChildren().setAll(createStep4Pane());
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
        else if (selectedRole.equalsIgnoreCase("Administrator")) currentlySelectedRoleCard = adminCard;
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
            stage.setScene(new LoginView(stage).getScene());
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
    // STEP 3 - ROLE INFORMATION
    // =========================================================================
    private Node createStep3Pane() {

        VBox container = new VBox(18);
        container.setAlignment(Pos.TOP_LEFT);
        container.setPadding(new Insets(18, 24, 10, 24));
        container.getStyleClass().add("form-step-pane");

        switch (selectedRole) {

            case "Patient":
                container.getChildren().add(createPatientRoleInfo());
                break;

            case "Doctor":
                container.getChildren().add(createDoctorRoleInfo());
                break;

            case "Hospital":
                container.getChildren().add(createHospitalRoleInfo());
                break;

            case "Administrator":
                container.getChildren().add(createAdminRoleInfo());
                break;

            default:
                Label message = new Label("Please select a valid role.");
                message.getStyleClass().add("form-section-subtitle");
                container.getChildren().add(message);
        }

        return container;
    }

    private VBox createPatientRoleInfo() {

        VBox box = new VBox(16);

        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(14);

        patientDobField.setPromptText("Select date");
        patientDobField.getStyleClass().add("text-field-custom");

        patientGenderField.getItems().setAll(
                "Male",
                "Female",
                "Other",
                "Prefer not to say"
        );
        patientGenderField.setPromptText("Select gender");
        patientGenderField.getStyleClass().add("text-field-custom");

        patientBloodGroupField.getItems().setAll(
                "A+",
                "A-",
                "B+",
                "B-",
                "AB+",
                "AB-",
                "O+",
                "O-"
        );
        patientBloodGroupField.setPromptText("Select blood group");
        patientBloodGroupField.getStyleClass().add("text-field-custom");

        patientEmergencyContactField.setPromptText("+91 9876543210");
        patientEmergencyContactField.getStyleClass().add("text-field-custom");

        patientAddressField.setPromptText("Enter your address");
        patientAddressField.getStyleClass().add("text-field-custom");

        grid.add(createFieldWrapper("Date of Birth", patientDobField), 0, 0);
        grid.add(createFieldWrapper("Gender", patientGenderField), 1, 0);

        grid.add(createFieldWrapper("Blood Group", patientBloodGroupField), 0, 1);
        grid.add(createFieldWrapper("Emergency Contact", patientEmergencyContactField), 1, 1);

        grid.add(createFieldWrapper("Address", patientAddressField), 0, 2, 2, 1);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);

        grid.getColumnConstraints().addAll(col1, col2);

        box.getChildren().addAll(
                createRoleInfoHeading(
                        "Patient Information",
                        "Provide your personal health profile details"
                ),
                grid,
                createStep3Buttons()
        );

        return box;
    }

    private VBox createDoctorRoleInfo() {

        VBox box = new VBox(16);

        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(14);

        doctorLicenseField.setPromptText(
                "Enter medical license number"
        );
        doctorLicenseField.getStyleClass()
                .add("text-field-custom");

        doctorSpecializationField.setPromptText(
                "e.g. Cardiology"
        );
        doctorSpecializationField.getStyleClass()
                .add("text-field-custom");

        doctorQualificationField.setPromptText(
                "e.g. MBBS, MD"
        );
        doctorQualificationField.getStyleClass()
                .add("text-field-custom");

        doctorExperienceField.setPromptText(
                "Years of experience"
        );
        doctorExperienceField.getStyleClass()
                .add("text-field-custom");

        doctorFeeField.setPromptText(
                "Consultation fee"
        );
        doctorFeeField.getStyleClass()
                .add("text-field-custom");

        grid.add(
                createFieldWrapper(
                        "Medical License Number",
                        doctorLicenseField
                ),
                0, 0
        );

        grid.add(
                createFieldWrapper(
                        "Specialization",
                        doctorSpecializationField
                ),
                1, 0
        );

        grid.add(
                createFieldWrapper(
                        "Qualification",
                        doctorQualificationField
                ),
                0, 1
        );

        grid.add(
                createFieldWrapper(
                        "Years of Experience",
                        doctorExperienceField
                ),
                1, 1
        );

        grid.add(
                createFieldWrapper(
                        "Consultation Fee",
                        doctorFeeField
                ),
                0, 2
        );

        ColumnConstraints col1 =
                new ColumnConstraints();

        col1.setPercentWidth(50);

        ColumnConstraints col2 =
                new ColumnConstraints();

        col2.setPercentWidth(50);

        grid.getColumnConstraints()
                .addAll(col1, col2);

        box.getChildren().addAll(
                createRoleInfoHeading(
                        "Doctor Information",
                        "Provide your professional credentials for verification"
                ),
                grid,
                createStep3Buttons()
        );

        return box;
    }

    private VBox createHospitalRoleInfo() {

        VBox box = new VBox(16);

        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(14);

        // ============================================================
        // HOSPITAL NAME
        // ============================================================

        hospitalNameField.setPromptText(
                "Hospital name"
        );

        hospitalNameField.getStyleClass()
                .add("text-field-custom");

        // ============================================================
        // REGISTRATION NUMBER
        // ============================================================

        hospitalRegistrationField.setPromptText(
                "Hospital registration number"
        );

        hospitalRegistrationField.getStyleClass()
                .add("text-field-custom");

        // ============================================================
        // HOSPITAL TYPE
        // ============================================================

        hospitalTypeField.getItems().setAll(
                "Government Hospital",
                "Private Hospital",
                "Clinic",
                "Specialty Hospital",
                "Multi-Specialty Hospital"
        );

        hospitalTypeField.setPromptText(
                "Select hospital type"
        );

        hospitalTypeField.getStyleClass()
                .add("text-field-custom");

        // ============================================================
        // NUMBER OF BEDS
        // ============================================================

        hospitalBedsField.setPromptText(
                "Number of beds"
        );

        hospitalBedsField.getStyleClass()
                .add("text-field-custom");

        // ============================================================
        // CONTACT
        // ============================================================

        hospitalContactField.setPromptText(
                "+91 9876543210"
        );

        hospitalContactField.getStyleClass()
                .add("text-field-custom");

        // ============================================================
        // ADDRESS
        // ============================================================

        hospitalAddressField.setPromptText(
                "Hospital address"
        );

        hospitalAddressField.getStyleClass()
                .add("text-field-custom");

        // ============================================================
        // GRID
        // ============================================================

        grid.add(
                createFieldWrapper(
                        "Hospital Name",
                        hospitalNameField
                ),
                0, 0
        );

        grid.add(
                createFieldWrapper(
                        "Hospital Registration Number",
                        hospitalRegistrationField
                ),
                1, 0
        );

        grid.add(
                createFieldWrapper(
                        "Hospital Type",
                        hospitalTypeField
                ),
                0, 1
        );

        grid.add(
                createFieldWrapper(
                        "Number of Beds",
                        hospitalBedsField
                ),
                1, 1
        );

        grid.add(
                createFieldWrapper(
                        "Contact Number",
                        hospitalContactField
                ),
                0, 2
        );

        grid.add(
                createFieldWrapper(
                        "Hospital Address",
                        hospitalAddressField
                ),
                1, 2
        );

        ColumnConstraints col1 =
                new ColumnConstraints();

        col1.setPercentWidth(50);

        ColumnConstraints col2 =
                new ColumnConstraints();

        col2.setPercentWidth(50);

        grid.getColumnConstraints()
                .addAll(col1, col2);

        box.getChildren().addAll(
                createRoleInfoHeading(
                        "Hospital Information",
                        "Provide your organization's registration details"
                ),
                grid,
                createStep3Buttons()
        );

        return box;
    }

    private VBox createAdminRoleInfo() {
        VBox box = new VBox(16);

        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(14);

        adminInvitationCodeField.setPromptText("Enter admin invite code");
        adminInvitationCodeField.getStyleClass().add("text-field-custom");

        adminDepartmentField.setPromptText("e.g. IT Operations");
        adminDepartmentField.getStyleClass().add("text-field-custom");

        adminIdField.setPromptText("Employee ID");
        adminIdField.getStyleClass().add("text-field-custom");

        grid.add(createFieldWrapper("Invitation Code", adminInvitationCodeField), 0, 0, 2, 1);
        grid.add(createFieldWrapper("Department", adminDepartmentField), 0, 1);
        grid.add(createFieldWrapper("Admin ID", adminIdField), 1, 1);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(col1, col2);

        box.getChildren().addAll(
                createRoleInfoHeading(
                        "Administrator Information",
                        "Provide administrative credentials for governance access"
                ),
                grid,
                createStep3Buttons()
        );

        return box;
    }

    private VBox createRoleInfoHeading(String titleText, String subtitleText) {
        VBox headingBox = new VBox(4);
        Text t = new Text(titleText);
        t.getStyleClass().add("form-section-title");
        Text sub = new Text(subtitleText);
        sub.getStyleClass().add("form-section-subtitle");
        headingBox.getChildren().addAll(t, sub);
        return headingBox;
    }

    private HBox createStep3Buttons() {
        HBox bottomControls = new HBox(16);
        bottomControls.setAlignment(Pos.CENTER_RIGHT);
        bottomControls.setPadding(new Insets(10, 0, 0, 0));

        Button backBtn = new Button("Back");
        backBtn.getStyleClass().add("btn-back");
        backBtn.setOnAction(e -> goToPreviousStep());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button continueBtn = new Button("Complete Registration →");
        continueBtn.getStyleClass().add("btn-continue");
        addBtnAnimations(continueBtn);
        continueBtn.setOnAction(e -> goToNextStep());

        bottomControls.getChildren().addAll(backBtn, spacer, continueBtn);
        return bottomControls;
    }

    // =========================================================================
    // STEP 4 - REGISTRATION OUTCOME
    // =========================================================================
    private VBox createStep4Pane() {
        VBox container = new VBox(20);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(20, 20, 20, 20));

        StackPane iconPane = new StackPane();
        Circle bg = new Circle(32, Color.web("#DEF7EC"));
        Text check = new Text("✓");
        check.setStyle("-fx-fill: #03543F; -fx-font-size: 28px; -fx-font-weight: bold;");
        iconPane.getChildren().addAll(bg, check);

        Text title = new Text("Registration Successful!");
        title.getStyleClass().add("heading-text");

        Label desc = new Label("Your account has been created successfully. You can now log in using your credentials.");
        desc.getStyleClass().add("subtitle-text");
        desc.setWrapText(true);
        desc.setAlignment(Pos.CENTER);

        Button loginBtn = new Button("Proceed to Login");
        loginBtn.getStyleClass().add("btn-continue");
        addBtnAnimations(loginBtn);
        loginBtn.setOnAction(e -> stage.setScene(new LoginView(stage).getScene()));

        container.getChildren().addAll(iconPane, title, desc, loginBtn);
        return container;
    }

    // =========================================================================
    // HELPERS & UI UTILITIES
    // =========================================================================
    private VBox createFieldWrapper(String labelText, Node field) {
        VBox wrapper = new VBox(6);
        Label label = new Label(labelText);
        label.getStyleClass().add("field-label");
        wrapper.getChildren().addAll(label, field);
        return wrapper;
    }

    private ImageView createSafeImageView(String path, double width, double height) {
        ImageView iv = new ImageView();
        iv.setFitWidth(width);
        iv.setFitHeight(height);
        iv.setPreserveRatio(true);
        try {
            if (getClass().getResource(path) != null) {
                iv.setImage(new Image(getClass().getResourceAsStream(path)));
            }
        } catch (Exception ignored) {}
        return iv;
    }

    private void addBtnAnimations(Button btn) {
        ScaleTransition stIn = new ScaleTransition(Duration.millis(100), btn);
        stIn.setToX(1.03);
        stIn.setToY(1.03);

        ScaleTransition stOut = new ScaleTransition(Duration.millis(100), btn);
        stOut.setToX(1.0);
        stOut.setToY(1.0);

        btn.setOnMouseEntered(e -> stIn.playFromStart());
        btn.setOnMouseExited(e -> stOut.playFromStart());
    }

    private void showRegistrationError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Registration Error");
        alert.setHeaderText("Failed to Complete Registration");
        alert.setContentText(message != null ? message : "An unexpected error occurred during registration.");
        alert.showAndWait();
    }

    private HBox createFooter() {
        HBox footer = new HBox();
        footer.getStyleClass().add("footer-container");
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(12));
        Text copyright = new Text("© 2026 Health-Sphere AI. All rights reserved.");
        copyright.getStyleClass().add("footer-text");
        footer.getChildren().add(copyright);
        return footer;
    }
}