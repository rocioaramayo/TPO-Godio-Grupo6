package com.example.demo.modelo;

import java.time.Instant;

/**
 * Modelo para representar mediciones de sensores
 * Almacena datos de temperatura y humedad con timestamp
 */
public class Medicion {
    
    private String idMedicion;
    private Integer sensorId;
    private Instant fechaHora;
    private Double temperatura;
    private Double humedad;
    
    // Constructor vacío
    public Medicion() {}
    
    // Constructor completo
    public Medicion(Integer sensorId, Instant fechaHora, Double temperatura, Double humedad) {
        this.sensorId = sensorId;
        this.fechaHora = fechaHora;
        this.temperatura = temperatura;
        this.humedad = humedad;
    }
    
    // Getters y Setters
    public String getIdMedicion() {
        return idMedicion;
    }
    
    public void setIdMedicion(String idMedicion) {
        this.idMedicion = idMedicion;
    }
    
    public Integer getSensorId() {
        return sensorId;
    }
    
    public void setSensorId(Integer sensorId) {
        this.sensorId = sensorId;
    }
    
    public Instant getFechaHora() {
        return fechaHora;
    }
    
    public void setFechaHora(Instant fechaHora) {
        this.fechaHora = fechaHora;
    }
    
    public Double getTemperatura() {
        return temperatura;
    }
    
    public void setTemperatura(Double temperatura) {
        this.temperatura = temperatura;
    }
    
    public Double getHumedad() {
        return humedad;
    }
    
    public void setHumedad(Double humedad) {
        this.humedad = humedad;
    }
    
    @Override
    public String toString() {
        return "Medicion{" +
                "idMedicion='" + idMedicion + '\'' +
                ", sensorId=" + sensorId +
                ", fechaHora=" + fechaHora +
                ", temperatura=" + temperatura +
                ", humedad=" + humedad +
                '}';
    }
}
