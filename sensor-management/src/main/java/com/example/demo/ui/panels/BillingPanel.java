package com.example.demo.ui.panels;

import org.springframework.context.ApplicationContext;
import javax.swing.*;
import java.awt.*;

import com.example.demo.controllers.BillingController;

public class BillingPanel extends JPanel {
    public BillingPanel(ApplicationContext ctx) {
        setLayout(new BorderLayout());
        JLabel title = new JLabel("Facturación", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        JTextField emailField = new JTextField();
        JTextField montoField = new JTextField();
        JTextField metodoField = new JTextField();
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Monto:"));
        formPanel.add(montoField);
        formPanel.add(new JLabel("Método de pago:"));
        formPanel.add(metodoField);

        JButton consultarBtn = new JButton("Consultar");
        consultarBtn.addActionListener(e -> {
            String email = emailField.getText();
            String cuenta = BillingController.getInstance(ctx).mostrarCuenta(email);
            JOptionPane.showMessageDialog(this, cuenta);
        });

        JButton registrarPagoBtn = new JButton("Registrar pago");
        registrarPagoBtn.addActionListener(e -> {
            String email = emailField.getText();
            double monto = 0;
            try {
                monto = Double.parseDouble(montoField.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Monto debe ser un número", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String metodo = metodoField.getText();
            boolean ok = BillingController.getInstance(ctx).registrarPago(email, monto, metodo);
            JOptionPane.showMessageDialog(this, ok ? "Pago registrado" : "Error al registrar pago");
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(consultarBtn);
        buttonPanel.add(registrarPagoBtn);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(formPanel, BorderLayout.CENTER);
        centerPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);
    }
}
