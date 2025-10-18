package com.example.demo.ui.panels;

import org.springframework.context.ApplicationContext;
import javax.swing.*;
import java.awt.*;

import com.example.demo.controllers.AlertController;

public class AlertsPanel extends JPanel {
    public AlertsPanel(ApplicationContext ctx) {
        setLayout(new BorderLayout());
        JLabel title = new JLabel("Alertas", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        JTextField tipoField = new JTextField();
        JTextField sensorIdField = new JTextField();
        JTextField descField = new JTextField();
        formPanel.add(new JLabel("Tipo:"));
        formPanel.add(tipoField);
        formPanel.add(new JLabel("Sensor ID:"));
        formPanel.add(sensorIdField);
        formPanel.add(new JLabel("Descripción:"));
        formPanel.add(descField);

        JButton crearBtn = new JButton("Crear alerta");
        crearBtn.addActionListener(e -> {
            String tipo = tipoField.getText();
            String sensorId = sensorIdField.getText();
            String desc = descField.getText();
            boolean ok = AlertController.getInstance(ctx).crear(tipo, sensorId, desc);
            JOptionPane.showMessageDialog(this, ok ? "Alerta creada" : "Error al crear alerta");
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(crearBtn);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(formPanel, BorderLayout.CENTER);
        centerPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);
    }
}
