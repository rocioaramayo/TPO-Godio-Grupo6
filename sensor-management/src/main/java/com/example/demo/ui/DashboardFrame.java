package com.example.demo.ui;

import com.example.demo.ui.panels.GeneratorPanel;

import javax.swing.*;
import java.awt.*;

public class DashboardFrame extends JFrame {
    private GeneratorPanel generatorPanel;
    
    public DashboardFrame() {
        setTitle("Dashboard - Sistema de Gestión de Sensores");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Crear instancia del panel generador
        generatorPanel = new GeneratorPanel();
        
        // Agregar pestañas
        tabbedPane.addTab("Sensores", new JPanel());
        tabbedPane.addTab("Mediciones", new JPanel());
        tabbedPane.addTab("Procesos", new JPanel());
        tabbedPane.addTab("Facturación", new JPanel());
        tabbedPane.addTab("Mensajería", new JPanel());
        tabbedPane.addTab("Alertas", new JPanel());
        tabbedPane.addTab("Generador", generatorPanel); // Nueva pestaña

        add(tabbedPane, BorderLayout.CENTER);
        
        // Configurar cierre de ventana para limpiar recursos
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (generatorPanel != null) {
                    generatorPanel.cleanup();
                }
                System.exit(0);
            }
        });
    }
}
