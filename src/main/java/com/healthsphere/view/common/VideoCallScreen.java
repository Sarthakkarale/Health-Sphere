package com.healthsphere.view.common;

import com.healthsphere.dao.CallDao;
import com.healthsphere.model.Call;
import com.healthsphere.model.UserModel;
import com.google.cloud.firestore.ListenerRegistration;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.effect.DropShadow;
import javafx.util.Duration;

import java.awt.Desktop;
import java.net.URI;

public class VideoCallScreen extends VBox {

    private static final String JITSI_SERVER_URL = "https://meet.ffmuc.net/";

    private final Call call;
    private final CallDao callDao = new CallDao();
    private final Runnable onCallTerminated;
    private final String currentUserEmail = UserModel.getInstance().getEmail();
    
    private StackPane centerStack;
    private VBox loadingOverlay;
    private Label statusLabel;
    private Label timerLabel;
    private Text cardHeader;
    private Text cardBody;
    private boolean browserOpened = false;
    
    private Timeline callTimer;
    private int secondsElapsed = 0;
    private ListenerRegistration callStatusListenerReg;
    private boolean isTerminated = false;

    public VideoCallScreen(Call call, Runnable onCallTerminated) {
        this.call = call;
        this.onCallTerminated = onCallTerminated;

        setStyle("-fx-background-color: #121222;");
        setPadding(new Insets(10));
        setSpacing(10);
        VBox.setVgrow(this, Priority.ALWAYS);

        // --- 1. Call Header ---
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(10, 20, 10, 20));
        header.setStyle("-fx-background-color: rgba(30, 30, 47, 0.8); -fx-background-radius: 12px; -fx-border-color: rgba(255, 255, 255, 0.08); -fx-border-width: 1px;");

        VBox headerDetails = new VBox(3);
        HBox.setHgrow(headerDetails, Priority.ALWAYS);

        boolean isCaller = call.getCallerId() != null && call.getCallerId().equalsIgnoreCase(currentUserEmail);
        String participantName = isCaller ? call.getReceiverName() : call.getCallerName();
        if (participantName == null || participantName.isBlank()) participantName = "Participant";

        Text titleText = new Text("Direct Video Call");
        titleText.setFont(Font.font("System", FontWeight.BOLD, 15));
        titleText.setFill(Color.WHITE);

        statusLabel = new Label("Connecting...");
        statusLabel.setFont(Font.font("System", 13));
        statusLabel.setTextFill(Color.web("#deb7ff"));

        headerDetails.getChildren().addAll(titleText, statusLabel);

        // Timer Panel
        timerLabel = new Label("00:00");
        timerLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        timerLabel.setTextFill(Color.web("#deb7ff"));
        timerLabel.setPadding(new Insets(6, 12, 6, 12));
        timerLabel.setStyle("-fx-background-color: rgba(186, 84, 245, 0.15); -fx-background-radius: 8px; -fx-border-color: rgba(186, 84, 245, 0.3); -fx-border-radius: 8px;");

        header.getChildren().addAll(headerDetails, timerLabel);
        getChildren().add(header);

        // --- 2. Call Center (Browser Redirect Glassmorphic Card & Status Panel) ---
        centerStack = new StackPane();
        VBox.setVgrow(centerStack, Priority.ALWAYS);

        // Glassmorphic Info/Redirect Card
        VBox redirectInfoCard = new VBox(20);
        redirectInfoCard.setAlignment(Pos.CENTER);
        redirectInfoCard.setPadding(new Insets(30));
        redirectInfoCard.setStyle("-fx-background-color: rgba(30, 30, 47, 0.85); -fx-background-radius: 16px; -fx-border-color: rgba(186, 84, 245, 0.25); -fx-border-width: 1.5px; -fx-max-width: 480px; -fx-max-height: 280px;");

        DropShadow infoShadow = new DropShadow();
        infoShadow.setRadius(15);
        infoShadow.setColor(Color.web("#000000", 0.5));
        redirectInfoCard.setEffect(infoShadow);

        cardHeader = new Text("Connecting to Call...");
        cardHeader.setFont(Font.font("System", FontWeight.BOLD, 18));
        cardHeader.setFill(Color.WHITE);

        cardBody = new Text("Your call is being routed to your default web browser to ensure seamless audio, video, and mic access.");
        cardBody.setFont(Font.font("System", 13));
        cardBody.setFill(Color.web("#a0a5c0"));
        cardBody.setWrappingWidth(400);
        cardBody.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        Button reopenBtn = new Button("Launch Browser / Reopen Call");
        reopenBtn.setStyle(
                "-fx-background-color: #ba54f5;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 20px;" +
                "-fx-padding: 10px 24px;" +
                "-fx-cursor: hand;"
        );
        reopenBtn.setOnMouseEntered(e -> reopenBtn.setStyle("-fx-background-color: #d175ff; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20px; -fx-padding: 10px 24px; -fx-cursor: hand;"));
        reopenBtn.setOnMouseExited(e -> reopenBtn.setStyle("-fx-background-color: #ba54f5; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20px; -fx-padding: 10px 24px; -fx-cursor: hand;"));
        reopenBtn.setOnAction(e -> handleOpenBrowser());

        redirectInfoCard.getChildren().addAll(cardHeader, cardBody, reopenBtn);

        // Glassmorphic Loading Overlay
        loadingOverlay = new VBox(15);
        loadingOverlay.setAlignment(Pos.CENTER);
        loadingOverlay.setStyle("-fx-background-color: rgba(18, 18, 34, 0.9); -fx-background-radius: 12px;");

        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setProgress(-1);
        spinner.setMinSize(50, 50);
        spinner.setMaxSize(50, 50);
        spinner.setStyle("-fx-progress-color: #ba54f5;");

        Text loadingText = new Text("Waiting for recipient to accept...");
        loadingText.setFont(Font.font("System", FontWeight.BOLD, 14));
        loadingText.setFill(Color.WHITE);
        
        Text subText = new Text("Once accepted, the video call will open automatically in your browser.");
        subText.setFont(Font.font("System", 12));
        subText.setFill(Color.web("#a0a5c0"));

        loadingOverlay.getChildren().addAll(spinner, loadingText, subText);

        centerStack.getChildren().addAll(redirectInfoCard, loadingOverlay);
        getChildren().add(centerStack);

        // --- 3. Footer Control Bar ---
        HBox footer = new HBox(20);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(10));
        footer.setStyle("-fx-background-color: rgba(30, 30, 47, 0.8); -fx-background-radius: 12px;");

        // Open in Browser Fallback Button
        Button openBrowserBtn = new Button("Open in Default Browser");
        openBrowserBtn.setStyle(
                "-fx-background-color: rgba(43, 225, 136, 0.15);" +
                "-fx-text-fill: #43e188;" +
                "-fx-font-weight: bold;" +
                "-fx-border-color: #43e188;" +
                "-fx-border-radius: 20px;" +
                "-fx-background-radius: 20px;" +
                "-fx-padding: 8px 20px;" +
                "-fx-cursor: hand;"
        );
        openBrowserBtn.setOnMouseEntered(e -> openBrowserBtn.setStyle("-fx-background-color: rgba(43, 225, 136, 0.3); -fx-text-fill: #43e188; -fx-font-weight: bold; -fx-border-color: #43e188; -fx-border-radius: 20px; -fx-background-radius: 20px; -fx-padding: 8px 20px; -fx-cursor: hand;"));
        openBrowserBtn.setOnMouseExited(e -> openBrowserBtn.setStyle("-fx-background-color: rgba(43, 225, 136, 0.15); -fx-text-fill: #43e188; -fx-font-weight: bold; -fx-border-color: #43e188; -fx-border-radius: 20px; -fx-background-radius: 20px; -fx-padding: 8px 20px; -fx-cursor: hand;"));

        // End Call Button
        Button endCallBtn = new Button("End Call");
        endCallBtn.setStyle(
                "-fx-background-color: #f5365c;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 20px;" +
                "-fx-padding: 8px 24px;" +
                "-fx-cursor: hand;"
        );
        endCallBtn.setOnMouseEntered(e -> endCallBtn.setStyle("-fx-background-color: #ff5e7e; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20px; -fx-padding: 8px 24px; -fx-cursor: hand;"));
        endCallBtn.setOnMouseExited(e -> endCallBtn.setStyle("-fx-background-color: #f5365c; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20px; -fx-padding: 8px 24px; -fx-cursor: hand;"));

        openBrowserBtn.setOnAction(e -> handleOpenBrowser());
        endCallBtn.setOnAction(e -> handleEndCall());

        footer.getChildren().addAll(openBrowserBtn, endCallBtn);
        getChildren().add(footer);

        // Setup timer & status monitoring
        startCallTimer();
        setupCallStatusMonitoring();

        // If status is already ACCEPTED (receiver side), redirect right away
        checkAndOpenBrowser(call.getStatus());
    }

    private void startCallTimer() {
        callTimer = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            secondsElapsed++;
            int minutes = secondsElapsed / 60;
            int seconds = secondsElapsed % 60;
            timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
        }));
        callTimer.setCycleCount(Animation.INDEFINITE);
        callTimer.play();
    }

    private void checkAndOpenBrowser(String status) {
        if ("ACCEPTED".equals(status) && !browserOpened) {
            browserOpened = true;
            handleOpenBrowser();
        }
    }

    private void setupCallStatusMonitoring() {
        boolean isCaller = call.getCallerId() != null && call.getCallerId().equalsIgnoreCase(currentUserEmail);
        String participantName = isCaller ? call.getReceiverName() : call.getCallerName();
        if (participantName == null || participantName.isBlank()) participantName = "Participant";

        final String targetName = participantName;

        callStatusListenerReg = callDao.listenToCallStatus(call.getCallId() != null ? call.getCallId() : "", updatedCall -> {
            if (updatedCall != null) {
                Platform.runLater(() -> {
                    String status = updatedCall.getStatus();
                    if ("CALLING".equals(status)) {
                        statusLabel.setText("Calling " + targetName + "...");
                        loadingOverlay.setVisible(true);
                    } else if ("ACCEPTED".equals(status)) {
                        statusLabel.setText("Call active with " + targetName);
                        statusLabel.setTextFill(Color.web("#43e188"));
                        if (cardHeader != null) {
                            cardHeader.setText("Call In Progress");
                            cardHeader.setFill(Color.web("#43e188"));
                        }
                        if (cardBody != null) {
                            cardBody.setText("Direct Jitsi Meet call is open in your external default browser.");
                        }
                        loadingOverlay.setVisible(false);
                        checkAndOpenBrowser(status);
                    } else if ("DECLINED".equals(status)) {
                        statusLabel.setText("Call Declined");
                        statusLabel.setTextFill(Color.web("#f5365c"));
                        terminateCallSilently();
                    } else if ("ENDED".equals(status)) {
                        statusLabel.setText("Call Ended");
                        statusLabel.setTextFill(Color.web("#f5365c"));
                        terminateCallSilently();
                    }
                });
            }
        });
    }

    private void handleOpenBrowser() {
        String url;
        try {
            URI baseUri = URI.create(JITSI_SERVER_URL);
            URI finalUri = new URI(baseUri.getScheme(), baseUri.getHost(), "/" + call.getRoomId(),
                    "config.prejoinConfig.enabled=false&userInfo.displayName=\"" + UserModel.getInstance().getName() + "\"");
            url = finalUri.toASCIIString();
        } catch (Exception e) {
            url = JITSI_SERVER_URL + call.getRoomId();
        }
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                Runtime rt = Runtime.getRuntime();
                String os = System.getProperty("os.name").toLowerCase();
                if (os.contains("win")) {
                    rt.exec("rundll32 url.dll,FileProtocolHandler " + url);
                } else if (os.contains("mac")) {
                    rt.exec("open " + url);
                } else if (os.contains("nix") || os.contains("nux")) {
                    rt.exec("xdg-open " + url);
                } else {
                    System.err.println("Unsupported platform for browser launch.");
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to open Jitsi URL in browser: " + e.getMessage());
        }
    }

    private void handleEndCall() {
        if (isTerminated) return;
        
        javafx.concurrent.Task<Void> endTask = new javafx.concurrent.Task<>() {
            @Override
            protected Void call() throws Exception {
                callDao.endCall(call.getCallId());
                return null;
            }
        };
        endTask.setOnSucceeded(evt -> Platform.runLater(this::terminateCallSilently));
        endTask.setOnFailed(evt -> Platform.runLater(this::terminateCallSilently));
        
        new Thread(endTask).start();
    }

    private void terminateCallSilently() {
        if (isTerminated) return;
        isTerminated = true;

        if (callTimer != null) {
            callTimer.stop();
        }
        if (callStatusListenerReg != null) {
            callStatusListenerReg.remove();
        }

        if (onCallTerminated != null) {
            onCallTerminated.run();
        }
    }
}
