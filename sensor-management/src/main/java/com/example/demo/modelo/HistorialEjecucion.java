package com.example.demo.modelo;

import java.time.LocalDateTime;

/**
 * Modelo para representar el historial de ejecución de procesos
 * Registra cada ejecución de un proceso con sus resultados
 */
public class HistorialEjecucion {
    
    private String idEjecucion;
    private String idSolicitud;
    private LocalDateTime fechaEjecucion;
    private String resultado;
    private String estado; // iniciado/completado/error/cancelado
    private Integer duracionSegundos;
    private String observaciones;
    private String recursosUtilizados; // JSON con información de recursos
    
    // Constructor vacío
    public HistorialEjecucion() {}
    
    // Constructor básico
    public HistorialEjecucion(String idSolicitud) {
        this.idSolicitud = idSolicitud;
        this.fechaEjecucion = LocalDateTime.now();
        this.estado = "iniciado";
    }
    
    // Constructor completo
    public HistorialEjecucion(String idSolicitud, String observaciones) {
        this.idSolicitud = idSolicitud;
        this.observaciones = observaciones;
        this.fechaEjecucion = LocalDateTime.now();
        this.estado = "iniciado";
    }
    
    // Métodos de utilidad
    public boolean estaIniciado() {
        return "iniciado".equals(estado);
    }
    
    public boolean estaCompletado() {
        return "completado".equals(estado);
    }
    
    public boolean tieneError() {
        return "error".equals(estado);
    }
    
    public boolean estaCancelado() {
        return "cancelado".equals(estado);
    }
    
    public void marcarCompletado(String resultado, Integer duracionSegundos) {
        this.estado = "completado";
        this.resultado = resultado;
        this.duracionSegundos = duracionSegundos;
    }
    
    public void marcarError(String observaciones, Integer duracionSegundos) {
        this.estado = "error";
        this.observaciones = observaciones;
        this.duracionSegundos = duracionSegundos;
    }
    
    public void cancelar(String motivo) {
        this.estado = "cancelado";
        this.observaciones = motivo;
    }
    
    public String getDuracionFormateada() {
        if (duracionSegundos == null) return "N/A";
        
        int horas = duracionSegundos / 3600;
        int minutos = (duracionSegundos % 3600) / 60;
        int segundos = duracionSegundos % 60;
        
        if (horas > 0) {
            return String.format("%dh %dm %ds", horas, minutos, segundos);
        } else if (minutos > 0) {
            return String.format("%dm %ds", minutos, segundos);
        } else {
            return String.format("%ds", segundos);
        }
    }
    
    // Getters y Setters
    public String getIdEjecucion() {
        return idEjecucion;
    }
    
    public void setIdEjecucion(String idEjecucion) {
        this.idEjecucion = idEjecucion;
    }
    
    public String getIdSolicitud() {
        return idSolicitud;
    }
    
    public void setIdSolicitud(String idSolicitud) {
        this.idSolicitud = idSolicitud;
    }
    
    public LocalDateTime getFechaEjecucion() {
        return fechaEjecucion;
    }
    
    public void setFechaEjecucion(LocalDateTime fechaEjecucion) {
        this.fechaEjecucion = fechaEjecucion;
    }
    
    public String getResultado() {
        return resultado;
    }
    
    public void setResultado(String resultado) {
        this.resultado = resultado;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public Integer getDuracionSegundos() {
        return duracionSegundos;
    }
    
    public void setDuracionSegundos(Integer duracionSegundos) {
        this.duracionSegundos = duracionSegundos;
    }
    
    public String getObservaciones() {
        return observaciones;
    }
    
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    
    public String getRecursosUtilizados() {
        return recursosUtilizados;
    }
    
    public void setRecursosUtilizados(String recursosUtilizados) {
        this.recursosUtilizados = recursosUtilizados;
    }
    
    @Override
    public String toString() {
        return "HistorialEjecucion{" +
                "idEjecucion='" + idEjecucion + '\'' +
                ", idSolicitud='" + idSolicitud + '\'' +
                ", fechaEjecucion=" + fechaEjecucion +
                ", estado='" + estado + '\'' +
                ", duracionSegundos=" + duracionSegundos +
                '}';
    }
}
