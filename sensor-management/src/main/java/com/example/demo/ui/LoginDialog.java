package com.example.demo.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginDialog extends JDialog {
    private boolean loggedIn = false;
    private JTextField emailField;
    private JPasswordField passwordField;

    public LoginDialog(Frame parent) {
        super(parent, "Login", true);
        setLayout(new GridBagLayout());
        setSize(350, 200);
        setLocationRelativeTo(parent);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(20);
        add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Contraseña:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        add(passwordField, gbc);

        JButton loginButton = new JButton("Ingresar");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        add(loginButton, gbc);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());
                // Llama a AuthController.getInstance(ctx).login(email, pass)
                String token = com.example.demo.controllers.AuthController.getInstance(null).login(email, password);
                if (token != null) {
                    JOptionPane.showMessageDialog(LoginDialog.this, "Login OK");
                    loggedIn = true;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(LoginDialog.this, "Login fallido", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }
}
