package com.example.demo.ui.panels;

import org.springframework.context.ApplicationContext;
import javax.swing.*;
import java.awt.*;

import com.example.demo.controllers.MessagingController;

public class MessagingPanel extends JPanel {
    public MessagingPanel(ApplicationContext ctx) {
        setLayout(new BorderLayout());
        JLabel title = new JLabel("Mensajería", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        JTextField deField = new JTextField();
        JTextField aField = new JTextField();
        JTextField textoField = new JTextField();
        formPanel.add(new JLabel("De:"));
        formPanel.add(deField);
        formPanel.add(new JLabel("A:"));
        formPanel.add(aField);
        formPanel.add(new JLabel("Texto:"));
        formPanel.add(textoField);

        JButton enviarBtn = new JButton("Enviar");
        enviarBtn.addActionListener(e -> {
            String de = deField.getText();
            String a = aField.getText();
            String texto = textoField.getText();
            boolean ok = MessagingController.getInstance(ctx).enviarPrivado(de, a, texto);
            JOptionPane.showMessageDialog(this, ok ? "Mensaje enviado" : "Error al enviar mensaje");
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(enviarBtn);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(formPanel, BorderLayout.CENTER);
        centerPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);
    }
}
