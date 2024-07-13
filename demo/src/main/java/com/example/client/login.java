package com.example.client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import com.example.client.facultyMarksEntry;
import java.net.URL;

public class login {

    public static void main() {
        // Create Swing components
        JFrame frame = new JFrame("Login");
        JPanel panel = new JPanel();
        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField(20);
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(20);
        JButton loginButton = new JButton("Login");

        // Add components to panel
        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(loginButton);

        // Add panel to frame
        frame.add(panel);
        frame.setSize(300, 150);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Action listener for login button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());

                try {
                    // Send login request to backend
                    URL url = new URL("http://localhost:4567/faculty/login");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);

                    String requestBody = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";
                    try (OutputStream os = conn.getOutputStream()) {
                        byte[] input = requestBody.getBytes("utf-8");
                        os.write(input, 0, input.length);
                    }

                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // Login successful
                        JOptionPane.showMessageDialog(frame, "Login successful!");
                        facultyMarksEntry.main(null);
                    } else {
                        // Login failed
                        JOptionPane.showMessageDialog(frame, "Login failed! Invalid credentials.");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "An error occurred!");
                }
            }
        });
    }
}

