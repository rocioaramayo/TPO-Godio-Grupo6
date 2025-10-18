package com.example.demo.ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    public MainFrame() {
        setTitle("Sistema de Gestión de Sensores");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        // Mostrar LoginDialog modal
        LoginDialog loginDialog = new LoginDialog(this);
        loginDialog.setVisible(true);

        if (loginDialog.isLoggedIn()) {
            // Login OK, cerrar MainFrame y abrir DashboardFrame
            dispose();
            DashboardFrame dashboard = new DashboardFrame();
            dashboard.setVisible(true);
        } else {
            // Si no loguea, cerrar app
            dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame();
        });
    }
}
