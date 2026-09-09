package com.healthsphere.view.authentication;

import com.healthsphere.dao.authentication.DoctorDAO;
import com.healthsphere.model.AuthenticationResponse;
import com.healthsphere.model.UserProfile;
import com.healthsphere.util.SessionManager;
import com.healthsphere.view.doctor.DoctorDashboardView;

import javafx.animation.ScaleTransition;
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

public class DoctorPendingApprovalView {

    private final Stage stage;

    private Label pendingBadge;
    private Text statusMessage;
    private Button trackStatusBtn;

    private final DoctorDAO doctorDAO;

    public DoctorPendingApprovalView(Stage stage) {
        this.stage = stage;
        this.doctorDAO = new DoctorDAO();
    }

    // =========================================================================
    // MAIN SCENE
    // =========================================================================

    public Scene getScene() {

        BorderPane root = new BorderPane();
        root.getStyleClass().add("root-container");

        HBox mainContent = new HBox();

        StackPane leftPanel = createLeftMarketingPanel();
        ScrollPane rightPanel = createRightFormPanel();

        HBox.setHgrow(leftPanel, Priority.ALWAYS);
        HBox.setHgrow(rightPanel, Priority.ALWAYS);

        // 40% / 60% split
        leftPanel.prefWidthProperty()
                .bind(mainContent.widthProperty().multiply(0.40));

        leftPanel.maxWidthProperty()
                .bind(mainContent.widthProperty().multiply(0.40));

        leftPanel.minWidthProperty()
                .bind(mainContent.widthProperty().multiply(0.40));

        rightPanel.prefWidthProperty()
                .bind(mainContent.widthProperty().multiply(0.60));

        mainContent.getChildren().addAll(
                leftPanel,
                rightPanel
        );

        root.setCenter(mainContent);
        root.setBottom(createFooter());

        Scene scene = new Scene(
                root,
                stage.getWidth(),
                stage.getHeight()
        );

        String cssResource =
                getClass().getResource("/css/auth.css") != null
                        ? getClass()
                                .getResource("/css/auth.css")
                                .toExternalForm()
                        : null;

        if (cssResource != null) {
            scene.getStylesheets().add(cssResource);
        }

        return scene;
    }

    // =========================================================================
    // LEFT MARKETING PANEL
    // =========================================================================

    private StackPane createLeftMarketingPanel() {

        StackPane leftStack = new StackPane();

        ImageView bgImageView = new ImageView();

        bgImageView.setPreserveRatio(false);

        bgImageView.fitWidthProperty()
                .bind(leftStack.widthProperty());

        bgImageView.fitHeightProperty()
                .bind(leftStack.heightProperty());

        Rectangle clip = new Rectangle();

        clip.widthProperty()
                .bind(leftStack.widthProperty());

        clip.heightProperty()
                .bind(leftStack.heightProperty());

        leftStack.setClip(clip);

        try {

            if (getClass().getResource(
                    "/images/auth-background.jpg"
            ) != null) {

                bgImageView.setImage(
                        new Image(
                                getClass().getResourceAsStream(
                                        "/images/auth-background.jpg"
                                )
                        )
                );
            }

        } catch (Exception ignored) {
        }

        Rectangle overlay = new Rectangle();

        overlay.getStyleClass()
                .add("overlay-rect");

        overlay.widthProperty()
                .bind(leftStack.widthProperty());

        overlay.heightProperty()
                .bind(leftStack.heightProperty());

        VBox contentBox = new VBox();

        contentBox.setPadding(
                new Insets(48)
        );

        contentBox.setAlignment(
                Pos.TOP_LEFT
        );

        HBox logoBox = new HBox(12);

        logoBox.setAlignment(
                Pos.CENTER_LEFT
        );

        ImageView logoIcon =
                createSafeImageView(
                        "/images/logo.png",
                        45,
                        45
                );

        Node logoGraphic =
                logoIcon.getImage() != null
                        ? logoIcon
                        : createFallbackLogoGraphic();

        Text brandTitle =
                new Text("Health-Sphere AI");

        brandTitle.getStyleClass()
                .add("left-logo-text");

        logoBox.getChildren().addAll(
                logoGraphic,
                brandTitle
        );

        VBox cardContainer =
                new VBox(20);

        cardContainer.setAlignment(
                Pos.CENTER_LEFT
        );

        cardContainer.setPadding(
                new Insets(40, 0, 0, 0)
        );

        cardContainer.getChildren().addAll(

                createFeatureCard(
                        "🔒 Secure Patient Vault",
                        "Your personal medical profile is encrypted and accessible only by you and authorized doctors."
                ),

                createFeatureCard(
                        "🤖 Personal Health Assistant",
                        "AI-driven symptom tracking, appointment scheduling, and automated prescription reminders."
                ),

                createFeatureCard(
                        "⚡ Instant Emergency Profile",
                        "Emergency contact and medical baseline data immediately available during urgent care visits."
                )
        );

        Region verticalSpacer =
                new Region();

        VBox.setVgrow(
                verticalSpacer,
                Priority.ALWAYS
        );

        VBox bottomCaptionBox =
                new VBox(4);

        Text line1 =
                new Text("Clinical grade precision.");

        line1.getStyleClass()
                .add("bottom-caption");

        Text line2 =
                new Text("Empowering digital healthcare.");

        line2.getStyleClass()
                .add("bottom-caption");

        bottomCaptionBox.getChildren().addAll(
                line1,
                line2
        );

        contentBox.getChildren().addAll(
                logoBox,
                cardContainer,
                verticalSpacer,
                bottomCaptionBox
        );

        leftStack.getChildren().addAll(
                bgImageView,
                overlay,
                contentBox
        );

        return leftStack;
    }

    // =========================================================================
    // FALLBACK LOGO
    // =========================================================================

    private Node createFallbackLogoGraphic() {

        StackPane pane =
                new StackPane();

        Circle circle =
                new Circle(
                        22,
                        Color.web("#2563EB")
                );

        Text cross =
                new Text("+");

        cross.setStyle(
                "-fx-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 24px;"
        );

        pane.getChildren().addAll(
                circle,
                cross
        );

        return pane;
    }

    // =========================================================================
    // FEATURE CARD
    // =========================================================================

    private HBox createFeatureCard(
            String titleText,
            String descText) {

        HBox card =
                new HBox(14);

        card.getStyleClass()
                .add("glass-feature-card");

        card.setAlignment(
                Pos.CENTER_LEFT
        );

        StackPane iconCircle =
                new StackPane();

        Circle circle =
                new Circle(16);

        circle.getStyleClass()
                .add("card-icon-circle");

        Text iconSymbol =
                new Text("✓");

        iconSymbol.setStyle(
                "-fx-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 13px;"
        );

        iconCircle.getChildren().addAll(
                circle,
                iconSymbol
        );

        VBox textBox =
                new VBox(4);

        Text title =
                new Text(titleText);

        title.getStyleClass()
                .add("feature-title");

        Label desc =
                new Label(descText);

        desc.getStyleClass()
                .add("feature-description");

        desc.setWrapText(true);

        textBox.getChildren().addAll(
                title,
                desc
        );

        HBox.setHgrow(
                textBox,
                Priority.ALWAYS
        );

        card.getChildren().addAll(
                iconCircle,
                textBox
        );

        return card;
    }

    // =========================================================================
    // RIGHT PANEL
    // =========================================================================

    private ScrollPane createRightFormPanel() {

        VBox outerWrapper =
                new VBox();

        outerWrapper.setAlignment(
                Pos.CENTER
        );

        outerWrapper.setPadding(
                new Insets(
                        32,
                        24,
                        32,
                        24
                )
        );

        VBox mainCard =
                new VBox(22);

        mainCard.getStyleClass()
                .add("main-white-card");

        mainCard.setMaxWidth(680);

        mainCard.setAlignment(
                Pos.TOP_CENTER
        );

        // ---------------------------------------------------------------------
        // LOGO
        // ---------------------------------------------------------------------

        ImageView centeredLogo =
                createSafeImageView(
                        "/images/logo.png",
                        48,
                        48
                );

        Node logoGraphic =
                centeredLogo.getImage() != null
                        ? centeredLogo
                        : createFallbackLogoGraphic();

        // ---------------------------------------------------------------------
        // ILLUSTRATION
        // ---------------------------------------------------------------------

        Node illustrationNode =
                createApprovalIllustration();

        // ---------------------------------------------------------------------
        // HEADING
        // ---------------------------------------------------------------------

        Text headingText =
                new Text("Application Submitted");

        headingText.getStyleClass()
                .add("heading-text");

        // ---------------------------------------------------------------------
        // SUBTITLE
        // ---------------------------------------------------------------------

        Text subtitleText =
                new Text(
                        "Thank you for registering. Our verification team will review your submitted credentials."
                );

        subtitleText.getStyleClass()
                .add("subtitle-text");

        subtitleText.setStyle(
                "-fx-text-alignment: center;"
        );

        subtitleText.setWrappingWidth(520);

        // ---------------------------------------------------------------------
        // TIME BADGE
        // ---------------------------------------------------------------------

        HBox timeBadge =
                createTimeBadge();

        // ---------------------------------------------------------------------
        // STATUS CARD
        // ---------------------------------------------------------------------

        VBox statusCard =
                createStatusCard();

        // ---------------------------------------------------------------------
        // WHY VERIFICATION
        // ---------------------------------------------------------------------

        HBox whyVerificationCard =
                createWhyVerificationCard();

        // ---------------------------------------------------------------------
        // STATUS MESSAGE
        // ---------------------------------------------------------------------

        statusMessage =
                new Text();

        statusMessage.setWrappingWidth(560);

        statusMessage.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-fill: #6B7280;" +
                "-fx-text-alignment: center;"
        );

        // ---------------------------------------------------------------------
        // TRACK STATUS BUTTON
        // ---------------------------------------------------------------------

        trackStatusBtn =
                new Button(
                        "Track Application Status"
                );

        trackStatusBtn.getStyleClass()
                .add("btn-continue");

        trackStatusBtn.setMaxWidth(
                Double.MAX_VALUE
        );

        addBtnAnimations(
                trackStatusBtn,
                Color.rgb(
                        37,
                        99,
                        235,
                        0.40
                )
        );

        trackStatusBtn.setOnAction(
                e -> checkApplicationStatus()
        );

        // ---------------------------------------------------------------------
        // BACK TO LOGIN
        // ---------------------------------------------------------------------

        Button backToLoginBtn =
                new Button("Back to Login");

        backToLoginBtn.getStyleClass()
                .add("btn-back");

        backToLoginBtn.setMaxWidth(
                Double.MAX_VALUE
        );

        backToLoginBtn.setOnAction(e -> com.healthsphere.util.Navigation.logout(stage));

        // ---------------------------------------------------------------------
        // SUPPORT
        // ---------------------------------------------------------------------

        Hyperlink supportLink =
                new Hyperlink(
                        "Need help? Contact Support"
                );

        supportLink.setStyle(
                "-fx-text-fill: #6B7280;" +
                "-fx-font-size: 13px;" +
                "-fx-underline: false;"
        );

        // ---------------------------------------------------------------------
        // ADD COMPONENTS
        // ---------------------------------------------------------------------

        mainCard.getChildren().addAll(

                logoGraphic,

                illustrationNode,

                headingText,

                subtitleText,

                timeBadge,

                statusCard,

                whyVerificationCard,

                statusMessage,

                trackStatusBtn,

                backToLoginBtn,

                supportLink
        );

        outerWrapper.getChildren()
                .add(mainCard);

        ScrollPane scrollPane =
                new ScrollPane(
                        outerWrapper
                );

        scrollPane.getStyleClass()
                .add("right-scroll-pane");

        scrollPane.setFitToWidth(true);

        scrollPane.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        return scrollPane;
    }

    // =========================================================================
    // CHECK APPLICATION STATUS
    // =========================================================================

    private void checkApplicationStatus() {

        try {

            if (!SessionManager.isLoggedIn()) {

                showStatusMessage(
                        "Your session has expired. Please log in again.",
                        "#DC2626"
                );

                return;
            }

            UserProfile currentUser =
                    SessionManager.getCurrentUser();

            String doctorUid =
                    currentUser.getUid();

            if (doctorUid == null ||
                    doctorUid.trim().isEmpty()) {

                showStatusMessage(
                        "Unable to identify your doctor account.",
                        "#DC2626"
                );

                return;
            }

            trackStatusBtn.setDisable(true);

            trackStatusBtn.setText(
                    "Checking Status..."
            );

            String status =
                    doctorDAO.getVerificationStatus(
                            doctorUid
                    );

            if (status == null ||
                    status.trim().isEmpty()) {

                status = "PENDING";
            }

            status =
                    status.trim()
                            .toUpperCase();

            switch (status) {

                case "APPROVED":
                case "VERIFIED":

                    updateStatusCardApproved();

                    showStatusMessage(
                            "Your professional credentials have been approved. Redirecting to your Doctor Dashboard...",
                            "#16A34A"
                    );

                    trackStatusBtn.setDisable(false);

                    trackStatusBtn.setText(
                            "Application Approved"
                    );

                    /*
                     * Small delay gives the user time
                     * to see the approval message.
                     */
                    javafx.animation.PauseTransition pause =
                            new javafx.animation.PauseTransition(
                                    Duration.seconds(1.2)
                            );

                    pause.setOnFinished(
                            event -> stage.setScene(
                                    new DoctorDashboardView(stage)
                                            .getScene()
                            )
                    );

                    pause.play();

                    break;

                case "REJECTED":

                    updateStatusCardRejected();

                    showStatusMessage(
                            "Your application has been rejected. Please contact support for more information.",
                            "#DC2626"
                    );

                    trackStatusBtn.setDisable(false);

                    trackStatusBtn.setText(
                            "Check Status Again"
                    );

                    break;

                case "PENDING":
                default:

                    updateStatusCardPending();

                    showStatusMessage(
                            "Your application is still under professional verification. Please check again later.",
                            "#6B7280"
                    );

                    trackStatusBtn.setDisable(false);

                    trackStatusBtn.setText(
                            "Check Status Again"
                    );

                    break;
            }

        } catch (IllegalStateException e) {

            showStatusMessage(
                    "Your session is no longer active. Please log in again.",
                    "#DC2626"
            );

            trackStatusBtn.setDisable(false);

            trackStatusBtn.setText(
                    "Track Application Status"
            );

        } catch (Exception e) {

            showStatusMessage(
                    "Unable to check application status. Please try again.",
                    "#DC2626"
            );

            trackStatusBtn.setDisable(false);

            trackStatusBtn.setText(
                    "Track Application Status"
            );
        }
    }

    // =========================================================================
    // STATUS MESSAGE
    // =========================================================================

    private void showStatusMessage(
            String message,
            String color) {

        if (statusMessage == null) {
            return;
        }

        statusMessage.setText(
                message
        );

        statusMessage.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-fill: " + color + ";" +
                "-fx-text-alignment: center;"
        );
    }

    // =========================================================================
    // UPDATE STATUS CARD - PENDING
    // =========================================================================

    private void updateStatusCardPending() {

        if (pendingBadge != null) {

            pendingBadge.setText(
                    "Pending Review"
            );

            pendingBadge.setStyle(
                    "-fx-background-color: #F3E8FF;" +
                    "-fx-text-fill: #7E22CE;" +
                    "-fx-font-size: 12px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-padding: 4px 10px;" +
                    "-fx-background-radius: 12px;"
            );
        }
    }

    // =========================================================================
    // UPDATE STATUS CARD - APPROVED
    // =========================================================================

    private void updateStatusCardApproved() {

        if (pendingBadge != null) {

            pendingBadge.setText(
                    "Approved"
            );

            pendingBadge.setStyle(
                    "-fx-background-color: #DCFCE7;" +
                    "-fx-text-fill: #15803D;" +
                    "-fx-font-size: 12px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-padding: 4px 10px;" +
                    "-fx-background-radius: 12px;"
            );
        }
    }

    // =========================================================================
    // UPDATE STATUS CARD - REJECTED
    // =========================================================================

    private void updateStatusCardRejected() {

        if (pendingBadge != null) {

            pendingBadge.setText(
                    "Rejected"
            );

            pendingBadge.setStyle(
                    "-fx-background-color: #FEE2E2;" +
                    "-fx-text-fill: #DC2626;" +
                    "-fx-font-size: 12px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-padding: 4px 10px;" +
                    "-fx-background-radius: 12px;"
            );
        }
    }

    // =========================================================================
    // CUSTOM ILLUSTRATION
    // =========================================================================

    private Node createApprovalIllustration() {

        ImageView illustrationImg =
                createSafeImageView(
                        "/images/pending-approval.png",
                        160,
                        160
                );

        if (illustrationImg.getImage() != null) {
            return illustrationImg;
        }

        StackPane illustrationPane =
                new StackPane();

        illustrationPane.setPrefSize(
                160,
                160
        );

        illustrationPane.setMaxSize(
                160,
                160
        );

        Circle outerCircle =
                new Circle(
                        70,
                        Color.web("#EFF6FF")
                );

        Circle innerCircle =
                new Circle(
                        52,
                        Color.web("#DBEAFE")
                );

        Text shieldIcon =
                new Text("🛡️");

        shieldIcon.setStyle(
                "-fx-font-size: 48px;"
        );

        Text clockBadge =
                new Text("⏳");

        clockBadge.setStyle(
                "-fx-font-size: 22px;"
        );

        StackPane.setAlignment(
                clockBadge,
                Pos.BOTTOM_RIGHT
        );

        StackPane.setMargin(
                clockBadge,
                new Insets(
                        0,
                        24,
                        24,
                        0
                )
        );

        illustrationPane.getChildren().addAll(
                outerCircle,
                innerCircle,
                shieldIcon,
                clockBadge
        );

        return illustrationPane;
    }

    // =========================================================================
    // TIME BADGE
    // =========================================================================

    private HBox createTimeBadge() {

        HBox badge =
                new HBox(6);

        badge.setAlignment(
                Pos.CENTER
        );

        badge.setPadding(
                new Insets(
                        8,
                        16,
                        8,
                        16
                )
        );

        badge.setStyle(
                "-fx-background-color: #F3F4F6;" +
                "-fx-background-radius: 20px;" +
                "-fx-border-color: #E5E7EB;" +
                "-fx-border-radius: 20px;"
        );

        Label labelText =
                new Label(
                        "Estimated verification time:"
                );

        labelText.setStyle(
                "-fx-text-fill: #4B5563;" +
                "-fx-font-size: 13px;"
        );

        Label timeText =
                new Label(
                        "24–48 Hours"
                );

        timeText.setStyle(
                "-fx-text-fill: #2563EB;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;"
        );

        badge.getChildren().addAll(
                labelText,
                timeText
        );

        return badge;
    }

    // =========================================================================
    // STATUS CARD
    // =========================================================================

    private VBox createStatusCard() {

        VBox card =
                new VBox(12);

        card.setPadding(
                new Insets(18)
        );

        card.setStyle(
                "-fx-background-color: #FFFFFF;" +
                "-fx-border-color: #E5E7EB;" +
                "-fx-border-width: 1px;" +
                "-fx-border-radius: 12px;" +
                "-fx-background-radius: 12px;"
        );

        HBox header =
                new HBox();

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        Text headerTitle =
                new Text(
                        "Verification Status"
                );

        headerTitle.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-fill: #1F2937;"
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        pendingBadge =
                new Label(
                        "Pending Review"
                );

        pendingBadge.setStyle(
                "-fx-background-color: #F3E8FF;" +
                "-fx-text-fill: #7E22CE;" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 4px 10px;" +
                "-fx-background-radius: 12px;"
        );

        header.getChildren().addAll(
                headerTitle,
                spacer,
                pendingBadge
        );

        Line divider =
                new Line(
                        0,
                        0,
                        580,
                        0
                );

        divider.setStyle(
                "-fx-stroke: #F3F4F6;" +
                "-fx-stroke-width: 1px;"
        );

        VBox checklist =
                new VBox(12);

        HBox row1 =
                createChecklistRow(
                        "✓",
                        "#16A34A",
                        "Application received",
                        "#16A34A"
                );

        HBox row2 =
                createChecklistRow(
                        "⏳",
                        "#6B7280",
                        "Professional verification pending",
                        "#374151"
                );

        HBox row3 =
                createChecklistRow(
                        "✉",
                        "#2563EB",
                        "Email notification after approval",
                        "#6B7280"
                );

        checklist.getChildren().addAll(
                row1,
                row2,
                row3
        );

        card.getChildren().addAll(
                header,
                divider,
                checklist
        );

        return card;
    }

    // =========================================================================
    // CHECKLIST ROW
    // =========================================================================

    private HBox createChecklistRow(
            String icon,
            String iconColor,
            String text,
            String textColor) {

        HBox row =
                new HBox(12);

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        Text iconText =
                new Text(icon);

        iconText.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-fill: " + iconColor + ";"
        );

        Text descText =
                new Text(text);

        descText.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-fill: " + textColor + ";"
        );

        row.getChildren().addAll(
                iconText,
                descText
        );

        return row;
    }

    // =========================================================================
    // WHY VERIFICATION CARD
    // =========================================================================

    private HBox createWhyVerificationCard() {

        HBox card =
                new HBox(12);

        card.setPadding(
                new Insets(16)
        );

        card.setAlignment(
                Pos.CENTER_LEFT
        );

        card.setStyle(
                "-fx-background-color: #EFF6FF;" +
                "-fx-border-color: #BFDBFE;" +
                "-fx-border-width: 1px;" +
                "-fx-border-radius: 12px;" +
                "-fx-background-radius: 12px;"
        );

        Text infoIcon =
                new Text("ℹ️");

        infoIcon.setStyle(
                "-fx-font-size: 20px;"
        );

        VBox textContainer =
                new VBox(4);

        Text title =
                new Text(
                        "Why Verification?"
                );

        title.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-fill: #1E40AF;"
        );

        Text body =
                new Text(
                        "Every Doctor and Hospital is verified to maintain patient safety."
                );

        body.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-fill: #3B82F6;"
        );

        body.setWrappingWidth(480);

        textContainer.getChildren().addAll(
                title,
                body
        );

        card.getChildren().addAll(
                infoIcon,
                textContainer
        );

        return card;
    }

    // =========================================================================
    // FOOTER
    // =========================================================================

    private HBox createFooter() {

        HBox footer =
                new HBox();

        footer.getStyleClass()
                .add("footer-bar");

        footer.setAlignment(
                Pos.CENTER
        );

        Text copyright =
                new Text(
                        "Health-Sphere AI © 2026 Health-Sphere Systems Inc."
                );

        copyright.getStyleClass()
                .add("footer-copyright");

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        HBox linksBox =
                new HBox(20);

        linksBox.setAlignment(
                Pos.CENTER_RIGHT
        );

        Text privacy =
                new Text("Privacy");

        privacy.getStyleClass()
                .add("footer-hyperlink");

        Text terms =
                new Text("Terms");

        terms.getStyleClass()
                .add("footer-hyperlink");

        Text security =
                new Text("Security");

        security.getStyleClass()
                .add("footer-hyperlink");

        Text hipaa =
                new Text("HIPAA");

        hipaa.getStyleClass()
                .add("footer-hyperlink");

        linksBox.getChildren().addAll(
                privacy,
                terms,
                security,
                hipaa
        );

        footer.getChildren().addAll(
                copyright,
                spacer,
                linksBox
        );

        return footer;
    }

    // =========================================================================
    // BUTTON ANIMATIONS
    // =========================================================================

    private void addBtnAnimations(
            Button btn,
            Color glowColor) {

        DropShadow btnGlow =
                new DropShadow();

        btnGlow.setColor(
                glowColor
        );

        btnGlow.setRadius(12);

        ScaleTransition scaleUp =
                new ScaleTransition(
                        Duration.millis(120),
                        btn
                );

        scaleUp.setToX(1.01);
        scaleUp.setToY(1.01);

        ScaleTransition scaleDown =
                new ScaleTransition(
                        Duration.millis(120),
                        btn
                );

        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);

        btn.setOnMouseEntered(e -> {

            if (!btn.isDisabled()) {

                btn.setEffect(
                        btnGlow
                );

                scaleUp.playFromStart();
            }
        });

        btn.setOnMouseExited(e -> {

            btn.setEffect(null);

            scaleDown.playFromStart();
        });
    }

    // =========================================================================
    // SAFE IMAGE LOADER
    // =========================================================================

    private ImageView createSafeImageView(
            String path,
            double width,
            double height) {

        ImageView img =
                new ImageView();

        img.setFitWidth(width);
        img.setFitHeight(height);

        img.setPreserveRatio(true);

        try {

            if (getClass().getResource(path) != null) {

                img.setImage(
                        new Image(
                                getClass()
                                        .getResourceAsStream(path)
                        )
                );
            }

        } catch (Exception ignored) {
        }

        return img;
    }
}