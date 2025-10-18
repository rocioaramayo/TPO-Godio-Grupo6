package com.example.demo.ui;

import javax.swing.*;
import java.awt.*;

public class DashboardFrame extends JFrame {
    public DashboardFrame() {
        setTitle("Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Sensores", new JPanel());
        tabbedPane.addTab("Mediciones", new JPanel());
        tabbedPane.addTab("Procesos", new JPanel());
        tabbedPane.addTab("Facturación", new JPanel());
        tabbedPane.addTab("Mensajería", new JPanel());
        tabbedPane.addTab("Alertas", new JPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }
}
