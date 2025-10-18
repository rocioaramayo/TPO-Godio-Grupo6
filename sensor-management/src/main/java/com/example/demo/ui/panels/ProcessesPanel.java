package com.example.demo.ui.panels;

import org.springframework.context.ApplicationContext;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import com.example.demo.controllers.ProcessController;

public class ProcessesPanel extends JPanel {
    public ProcessesPanel(ApplicationContext ctx) {
        setLayout(new BorderLayout());
        JLabel title = new JLabel("Procesos", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        JTextField emailField = new JTextField();
        JTextField procesoField = new JTextField();
        JTextField paramsField = new JTextField();
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Nombre proceso:"));
        formPanel.add(procesoField);
        formPanel.add(new JLabel("Params (k1=v1,k2=v2):"));
        formPanel.add(paramsField);

        JButton solicitarBtn = new JButton("Solicitar");
        solicitarBtn.addActionListener(e -> {
            String email = emailField.getText();
            String proceso = procesoField.getText();
            String paramsText = paramsField.getText();
            Map<String, String> params = new HashMap<>();
            if (!paramsText.isEmpty()) {
                String[] pairs = paramsText.split(",");
                for (String pair : pairs) {
                    String[] kv = pair.split("=");
                    if (kv.length == 2) params.put(kv[0].trim(), kv[1].trim());
                }
            }
            boolean ok = ProcessController.getInstance(ctx).solicitar(email, proceso, params);
            JOptionPane.showMessageDialog(this, ok ? "Solicitud enviada" : "Error al solicitar proceso");
        });

        JButton ejecutarBtn = new JButton("Ejecutar siguiente");
        ejecutarBtn.addActionListener(e -> {
            boolean ok = ProcessController.getInstance(ctx).ejecutarSiguiente();
            JOptionPane.showMessageDialog(this, ok ? "Proceso ejecutado" : "Error al ejecutar proceso");
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(solicitarBtn);
        buttonPanel.add(ejecutarBtn);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(formPanel, BorderLayout.CENTER);
        centerPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);
    }
}
