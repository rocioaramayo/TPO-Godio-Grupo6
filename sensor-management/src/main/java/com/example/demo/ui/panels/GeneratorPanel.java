package com.example.demo.ui.panels;

import com.example.demo.services.MeasurementGeneratorService;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Panel para configurar y controlar el generador de mediciones
 * Permite seleccionar sensores, configurar parámetros y controlar la generación
 */
public class GeneratorPanel extends JPanel {
    
    private JList<String> sensorList;
    private DefaultListModel<String> sensorListModel;
    private JTextField baseTempField;
    private JTextField baseHumField;
    private JSpinner intervalSpinner;
    private JButton startButton;
    private JButton pauseButton;
    private JProgressBar progressBar;
    private JLabel statusLabel;
    private JTextArea logArea;
    private JScrollPane logScrollPane;
    
    private MeasurementGeneratorService generatorService;
    private Timer statusTimer;
    
    public GeneratorPanel() {
        generatorService = MeasurementGeneratorService.getInstance();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadSensors();
        startStatusTimer();
    }
    
    /**
     * Inicializa todos los componentes de la UI
     */
    private void initializeComponents() {
        // Lista de sensores
        sensorListModel = new DefaultListModel<>();
        sensorList = new JList<>(sensorListModel);
        sensorList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        sensorList.setVisibleRowCount(8);
        
        // Campos de configuración
        baseTempField = new JTextField("20.0", 10);
        baseHumField = new JTextField("50.0", 10);
        
        // Spinner para intervalo
        SpinnerModel intervalModel = new SpinnerNumberModel(5, 1, 60, 1);
        intervalSpinner = new JSpinner(intervalModel);
        
        // Botones de control
        startButton = new JButton("Iniciar Generación");
        pauseButton = new JButton("Pausar");
        pauseButton.setEnabled(false);
        
        // Barra de progreso
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setString("Inactivo");
        
        // Label de estado
        statusLabel = new JLabel("Estado: Detenido");
        
        // Área de log
        logArea = new JTextArea(10, 40);
        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        logScrollPane = new JScrollPane(logArea);
        logScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    }
    
    /**
     * Configura el layout del panel
     */
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel izquierdo - Configuración
        JPanel configPanel = createConfigPanel();
        
        // Panel central - Control
        JPanel controlPanel = createControlPanel();
        
        // Panel derecho - Log
        JPanel logPanel = createLogPanel();
        
        // Panel superior con configuración y control
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.add(configPanel, BorderLayout.WEST);
        topPanel.add(controlPanel, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);
        add(logPanel, BorderLayout.CENTER);
    }
    
    /**
     * Crea el panel de configuración
     */
    private JPanel createConfigPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder("Configuración"));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        // Selección de sensores
        JPanel sensorPanel = new JPanel(new BorderLayout());
        sensorPanel.add(new JLabel("Sensores:"), BorderLayout.NORTH);
        sensorPanel.add(new JScrollPane(sensorList), BorderLayout.CENTER);
        
        // Parámetros base
        JPanel paramsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 0;
        paramsPanel.add(new JLabel("Temperatura Base (°C):"), gbc);
        gbc.gridx = 1;
        paramsPanel.add(baseTempField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        paramsPanel.add(new JLabel("Humedad Base (%):"), gbc);
        gbc.gridx = 1;
        paramsPanel.add(baseHumField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        paramsPanel.add(new JLabel("Intervalo (seg):"), gbc);
        gbc.gridx = 1;
        paramsPanel.add(intervalSpinner, gbc);
        
        panel.add(sensorPanel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(paramsPanel);
        
        return panel;
    }
    
    /**
     * Crea el panel de control
     */
    private JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder("Control"));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        // Botones
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(startButton);
        buttonPanel.add(pauseButton);
        
        // Barra de progreso
        JPanel progressPanel = new JPanel(new BorderLayout());
        progressPanel.add(new JLabel("Progreso:"), BorderLayout.WEST);
        progressPanel.add(progressBar, BorderLayout.CENTER);
        
        // Estado
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.add(statusLabel);
        
        panel.add(buttonPanel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(progressPanel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(statusPanel);
        
        return panel;
    }
    
    /**
     * Crea el panel de log
     */
    private JPanel createLogPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Log de Actividad"));
        panel.add(logScrollPane, BorderLayout.CENTER);
        
        // Botón para limpiar log
        JButton clearLogButton = new JButton("Limpiar Log");
        clearLogButton.addActionListener(e -> logArea.setText(""));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(clearLogButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Configura los event handlers
     */
    private void setupEventHandlers() {
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGeneration();
            }
        });
        
        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pauseGeneration();
            }
        });
    }
    
    /**
     * Inicia la generación de mediciones
     */
    private void startGeneration() {
        try {
            // Validar selección de sensores
            List<String> selectedSensors = sensorList.getSelectedValuesList();
            if (selectedSensors.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Seleccione al menos un sensor", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validar parámetros
            double baseTemp = Double.parseDouble(baseTempField.getText());
            double baseHum = Double.parseDouble(baseHumField.getText());
            int interval = (Integer) intervalSpinner.getValue();
            
            if (baseTemp < -50 || baseTemp > 60) {
                JOptionPane.showMessageDialog(this, "Temperatura base debe estar entre -50°C y 60°C", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (baseHum < 0 || baseHum > 100) {
                JOptionPane.showMessageDialog(this, "Humedad base debe estar entre 0% y 100%", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Iniciar generación
            generatorService.start(selectedSensors, interval, baseTemp, baseHum);
            
            // Actualizar UI
            startButton.setEnabled(false);
            pauseButton.setEnabled(true);
            statusLabel.setText("Estado: Generando mediciones...");
            progressBar.setString("Generando...");
            
            logMessage("Generación iniciada para " + selectedSensors.size() + " sensores");
            logMessage("Parámetros: Temp=" + baseTemp + "°C, Hum=" + baseHum + "%, Intervalo=" + interval + "s");
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Valores numéricos inválidos", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error iniciando generación: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Pausa la generación de mediciones
     */
    private void pauseGeneration() {
        generatorService.pause();
        
        // Actualizar UI
        startButton.setEnabled(true);
        pauseButton.setEnabled(false);
        statusLabel.setText("Estado: Pausado");
        progressBar.setString("Pausado");
        
        logMessage("Generación pausada");
    }
    
    /**
     * Carga la lista de sensores disponibles
     */
    private void loadSensors() {
        // Simular carga de sensores (en una implementación real vendría de la BD)
        sensorListModel.clear();
        sensorListModel.addElement("1 - Sensor Buenos Aires Centro");
        sensorListModel.addElement("2 - Sensor Buenos Aires Norte");
        sensorListModel.addElement("3 - Sensor Córdoba Centro");
        sensorListModel.addElement("4 - Sensor Rosario Centro");
        sensorListModel.addElement("5 - Sensor Mendoza Centro");
        sensorListModel.addElement("6 - Sensor La Plata Centro");
        sensorListModel.addElement("7 - Sensor Mar del Plata Centro");
        sensorListModel.addElement("8 - Sensor San Miguel de Tucumán Centro");
        
        logMessage("Cargados " + sensorListModel.getSize() + " sensores disponibles");
    }
    
    /**
     * Inicia el timer para actualizar el estado
     */
    private void startStatusTimer() {
        statusTimer = new Timer(1000, e -> updateStatus());
        statusTimer.start();
    }
    
    /**
     * Actualiza el estado de la UI
     */
    private void updateStatus() {
        boolean running = generatorService.isRunning();
        int progress = generatorService.getProgress();
        
        if (running) {
            if (progress >= 0) {
                progressBar.setValue(progress);
                progressBar.setString("Generando... " + progress + "%");
            }
            statusLabel.setText("Estado: Generando mediciones...");
        } else {
            progressBar.setValue(0);
            progressBar.setString("Inactivo");
            statusLabel.setText("Estado: Detenido");
            
            if (startButton.isEnabled() == false) {
                startButton.setEnabled(true);
                pauseButton.setEnabled(false);
            }
        }
    }
    
    /**
     * Agrega un mensaje al log
     */
    private void logMessage(String message) {
        String timestamp = java.time.LocalTime.now().toString().substring(0, 8);
        logArea.append("[" + timestamp + "] " + message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
    
    /**
     * Limpia los recursos al cerrar el panel
     */
    public void cleanup() {
        if (statusTimer != null) {
            statusTimer.stop();
        }
        if (generatorService.isRunning()) {
            generatorService.pause();
        }
    }
}
