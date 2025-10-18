package com.example.demo.ui.panels;

import org.springframework.context.ApplicationContext;
import javax.swing.*;
import java.awt.*;

public class MeasurementsPanel extends JPanel {
    public MeasurementsPanel(ApplicationContext ctx) {
        setLayout(new BorderLayout());
        JLabel title = new JLabel("Mediciones", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        JButton mainButton = new JButton("Insertar");
        mainButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Botón Insertar funciona!"));
        add(title, BorderLayout.NORTH);
        add(mainButton, BorderLayout.CENTER);
    }
}
