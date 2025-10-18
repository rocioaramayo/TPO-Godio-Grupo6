package com.example.demo.services;

import com.example.demo.exceptions.ErrorConectionCassandraException;
import com.example.demo.repositories.cassandra.MedicionCassandraDAO;

import javax.swing.*;
import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Servicio para generar mediciones aleatorias de sensores
 * Implementa patrón Singleton con SwingWorker para operaciones en background
 */
public class MeasurementGeneratorService {
    
    private static MeasurementGeneratorService instance;
    private MeasurementWorker worker;
    private AtomicBoolean isRunning = new AtomicBoolean(false);
    
    private MeasurementGeneratorService() {}
    
    public static MeasurementGeneratorService getInstance() {
        if (instance == null) {
            instance = new MeasurementGeneratorService();
        }
        return instance;
    }
    
    /**
     * Inicia la generación de mediciones aleatorias
     * @param sensorIds Lista de IDs de sensores
     * @param intervalSeconds Intervalo entre mediciones en segundos
     * @param baseTemp Temperatura base
     * @param baseHum Humedad base
     */
    public void start(List<String> sensorIds, int intervalSeconds, double baseTemp, double baseHum) {
        if (isRunning.get()) {
            System.out.println("El generador ya está en ejecución");
            return;
        }
        
        // Cancelar worker anterior si existe
        if (worker != null && !worker.isDone()) {
            worker.cancel(true);
        }
        
        // Crear nuevo worker
        worker = new MeasurementWorker(sensorIds, intervalSeconds, baseTemp, baseHum);
        isRunning.set(true);
        
        // Ejecutar en background
        worker.execute();
        
        System.out.println("Generador de mediciones iniciado para " + sensorIds.size() + " sensores");
    }
    
    /**
     * Pausa la generación de mediciones
     */
    public void pause() {
        if (worker != null && !worker.isDone()) {
            worker.cancel(true);
            isRunning.set(false);
            System.out.println("Generador de mediciones pausado");
        }
    }
    
    /**
     * Verifica si el generador está ejecutándose
     * @return true si está ejecutándose, false en caso contrario
     */
    public boolean isRunning() {
        return isRunning.get() && worker != null && !worker.isDone();
    }
    
    /**
     * Obtiene el progreso actual del worker
     * @return Progreso entre 0 y 100, o -1 si no hay worker activo
     */
    public int getProgress() {
        if (worker != null && !worker.isDone()) {
            return worker.getProgress();
        }
        return -1;
    }
    
    /**
     * SwingWorker para generar mediciones en background
     */
    private class MeasurementWorker extends SwingWorker<Void, String> {
        
        private final List<String> sensorIds;
        private final int intervalSeconds;
        private final double baseTemp;
        private final double baseHum;
        private final Random random;
        private int measurementCount = 0;
        
        public MeasurementWorker(List<String> sensorIds, int intervalSeconds, double baseTemp, double baseHum) {
            this.sensorIds = sensorIds;
            this.intervalSeconds = intervalSeconds;
            this.baseTemp = baseTemp;
            this.baseHum = baseHum;
            this.random = new Random();
        }
        
        @Override
        protected Void doInBackground() throws Exception {
            try {
                MedicionCassandraDAO medicionDAO = MedicionCassandraDAO.getInstance();
                
                while (!isCancelled()) {
                    // Generar medición para cada sensor
                    for (String sensorIdStr : sensorIds) {
                        if (isCancelled()) break;
                        
                        try {
                            Integer sensorId = Integer.parseInt(sensorIdStr);
                            Instant timestamp = Instant.now();
                            
                            // Generar valores aleatorios alrededor de la base (±2)
                            double temperatura = baseTemp + (random.nextGaussian() * 2.0);
                            double humedad = baseHum + (random.nextGaussian() * 2.0);
                            
                            // Asegurar que los valores estén en rangos válidos
                            temperatura = Math.max(-50, Math.min(60, temperatura));
                            humedad = Math.max(0, Math.min(100, humedad));
                            
                            // Insertar medición
                            medicionDAO.insert(sensorId, timestamp, temperatura, humedad);
                            
                            measurementCount++;
                            
                            // Publicar progreso
                            publish("Medición " + measurementCount + " insertada para sensor " + sensorId);
                            
                        } catch (NumberFormatException e) {
                            System.err.println("ID de sensor inválido: " + sensorIdStr);
                        } catch (ErrorConectionCassandraException e) {
                            System.err.println("Error insertando medición: " + e.getMessage());
                        }
                    }
                    
                    // Esperar intervalo antes de la siguiente ronda
                    if (!isCancelled()) {
                        Thread.sleep(intervalSeconds * 1000L);
                    }
                }
                
            } catch (InterruptedException e) {
                System.out.println("Generador de mediciones interrumpido");
            } catch (Exception e) {
                System.err.println("Error en generador de mediciones: " + e.getMessage());
            }
            
            return null;
        }
        
        @Override
        protected void process(List<String> chunks) {
            // Actualizar UI con mensajes de progreso
            for (String message : chunks) {
                System.out.println(message);
            }
        }
        
        @Override
        protected void done() {
            isRunning.set(false);
            System.out.println("Generador de mediciones finalizado. Total de mediciones: " + measurementCount);
        }
    }
    
    /**
     * Clase para representar estadísticas del generador
     */
    public static class GeneratorStats {
        private int totalMeasurements;
        private long startTime;
        private List<String> activeSensors;
        
        public GeneratorStats(int totalMeasurements, long startTime, List<String> activeSensors) {
            this.totalMeasurements = totalMeasurements;
            this.startTime = startTime;
            this.activeSensors = activeSensors;
        }
        
        public int getTotalMeasurements() { return totalMeasurements; }
        public long getStartTime() { return startTime; }
        public List<String> getActiveSensors() { return activeSensors; }
        
        public long getElapsedTime() {
            return System.currentTimeMillis() - startTime;
        }
        
        public double getMeasurementsPerMinute() {
            long elapsedMinutes = getElapsedTime() / (1000 * 60);
            return elapsedMinutes > 0 ? (double) totalMeasurements / elapsedMinutes : 0;
        }
    }
}
