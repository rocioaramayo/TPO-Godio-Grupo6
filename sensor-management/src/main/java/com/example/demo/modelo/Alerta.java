package com.example.demo.modelo;

import java.time.Instant;

/**
 * Modelo para representar alertas del sistema
 * Pueden ser alertas de sensor o alertas climáticas
 */
public class Alerta {
    
    private String idAlerta;
    private String tipo; // sensor/climática
    private String sensorId; // Puede ser null si es climática
    private Instant fechaHora;
    private String descripcion;
    private String estado; // activa/resuelta
    
    // Constructor vacío
    public Alerta() {}
    
    // Constructor completo
    public Alerta(String tipo, String sensorId, String descripcion) {
        this.tipo = tipo;
        this.sensorId = sensorId;
        this.descripcion = descripcion;
        this.fechaHora = Instant.now();
        this.estado = "activa";
    }
    
    // Getters y Setters
    public String getIdAlerta() {
        return idAlerta;
    }
    
    public void setIdAlerta(String idAlerta) {
        this.idAlerta = idAlerta;
    }
    
    public String getTipo() {
        return tipo;
    }
    
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    public String getSensorId() {
        return sensorId;
    }
    
    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }
    
    public Instant getFechaHora() {
        return fechaHora;
    }
    
    public void setFechaHora(Instant fechaHora) {
        this.fechaHora = fechaHora;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    @Override
    public String toString() {
        return "Alerta{" +
                "idAlerta='" + idAlerta + '\'' +
                ", tipo='" + tipo + '\'' +
                ", sensorId='" + sensorId + '\'' +
                ", fechaHora=" + fechaHora +
                ", descripcion='" + descripcion + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }
}
