package com.example.client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class signup extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Teacher Signup");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField();
        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);

        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        grid.add(emailLabel, 0, 1);
        grid.add(emailField, 1, 1);

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        grid.add(passwordLabel, 0, 2);
        grid.add(passwordField, 1, 2);

        Button signupButton = new Button("Signup");
        grid.add(signupButton, 1, 3);

        Hyperlink loginLink = new Hyperlink("Already have an account? Login");
        loginLink.setOnAction(e -> {
            // Open the login URL in the default web browser
            primaryStage.close();
            login.main();
        });
        grid.add(loginLink, 1, 4);

        signupButton.setOnAction(e -> {
            // Handle signup action here
            String name = nameField.getText();
            String email = emailField.getText();
            String password = passwordField.getText();
            
            try {
                // Create JSON request body
                String requestBody = "{\"name\":\"" + name + "\",\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";
                
                // Send signup request to backend
                URL url = new URL("http://localhost:4567/faculty/signup");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = requestBody.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Signup successful
                    System.out.println("Signup successful!");
                    displayAlert(Alert.AlertType.INFORMATION, "Success", "Signup Successful", "Signup Successful");
                    login.main();
                } else {
                    // Signup failed
                    System.out.println("Signup failed! Please try again.");
                    displayAlert(Alert.AlertType.ERROR, "Error", "Signup Failed", "Failed to sign up. Please try again.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                // Handle exceptions
                System.out.println("An error occurred!");
                displayAlert(Alert.AlertType.ERROR, "Error", "Error Occurred", "An error occurred while processing your request.");
            }
        });

        Scene scene = new Scene(grid, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void displayAlert(Alert.AlertType type, String title, String headerText, String contentText) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
