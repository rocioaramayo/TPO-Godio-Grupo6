package com.example.demo.ui.panels;

import org.springframework.context.ApplicationContext;
import javax.swing.*;
import java.awt.*;
import java.time.Instant;

import com.example.demo.controllers.SensorController;

public class SensorsPanel extends JPanel {
    public SensorsPanel(ApplicationContext ctx) {
        setLayout(new BorderLayout());
        JLabel title = new JLabel("Sensores", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        // Sensor fields
        JTextField idField = new JTextField();
        JTextField nombreField = new JTextField();
        JTextField ciudadField = new JTextField();
        formPanel.add(new JLabel("ID:"));
        formPanel.add(idField);
        formPanel.add(new JLabel("Nombre:"));
        formPanel.add(nombreField);
        formPanel.add(new JLabel("Ciudad:"));
        formPanel.add(ciudadField);

        JButton guardarBtn = new JButton("Guardar sensor");
        guardarBtn.addActionListener(e -> {
            String id = idField.getText();
            String nombre = nombreField.getText();
            String ciudad = ciudadField.getText();
            boolean ok = SensorController.getInstance(ctx).altaSensor(id, nombre, ciudad);
            JOptionPane.showMessageDialog(this, ok ? "Sensor guardado" : "Error al guardar sensor");
        });

        // Medición fields
        JTextField sensorIdField = new JTextField();
        JTextField tempField = new JTextField();
        JTextField humField = new JTextField();
        formPanel.add(new JLabel("Sensor ID (medición):"));
        formPanel.add(sensorIdField);
        formPanel.add(new JLabel("Temperatura:"));
        formPanel.add(tempField);
        formPanel.add(new JLabel("Humedad:"));
        formPanel.add(humField);

        JButton medicionBtn = new JButton("Insertar medición");
        medicionBtn.addActionListener(e -> {
            String sensorId = sensorIdField.getText();
            double temp = 0, hum = 0;
            try {
                temp = Double.parseDouble(tempField.getText());
                hum = Double.parseDouble(humField.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Temperatura y humedad deben ser números", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            boolean ok = SensorController.getInstance(ctx).insertarMedicion(sensorId, Instant.now().toString(), temp, hum);
            JOptionPane.showMessageDialog(this, ok ? "Medición insertada" : "Error al insertar medición");
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(guardarBtn);
        buttonPanel.add(medicionBtn);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(formPanel, BorderLayout.CENTER);
        centerPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);
    }
}
