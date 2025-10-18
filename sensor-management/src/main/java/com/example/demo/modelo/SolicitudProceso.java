package com.example.demo.modelo;

import java.time.LocalDateTime;

/**
 * Modelo para representar solicitudes de procesos por parte de usuarios
 * Registra las solicitudes y su estado de procesamiento
 */
public class SolicitudProceso {
    
    private String idSolicitud;
    private Integer idUsuario;
    private Integer idProceso;
    private LocalDateTime fechaSolicitud;
    private String estado; // pendiente/completado/error/cancelado
    private String parametros; // JSON con parámetros de la solicitud
    private String resultado; // Resultado del proceso (puede ser null si está pendiente)
    private LocalDateTime fechaCompletado;
    private String observaciones;
    
    // Constructor vacío
    public SolicitudProceso() {}
    
    // Constructor básico
    public SolicitudProceso(Integer idUsuario, Integer idProceso, String parametros) {
        this.idUsuario = idUsuario;
        this.idProceso = idProceso;
        this.parametros = parametros;
        this.fechaSolicitud = LocalDateTime.now();
        this.estado = "pendiente";
    }
    
    // Constructor completo
    public SolicitudProceso(Integer idUsuario, Integer idProceso, String parametros, String observaciones) {
        this.idUsuario = idUsuario;
        this.idProceso = idProceso;
        this.parametros = parametros;
        this.observaciones = observaciones;
        this.fechaSolicitud = LocalDateTime.now();
        this.estado = "pendiente";
    }
    
    // Métodos de utilidad
    public boolean estaPendiente() {
        return "pendiente".equals(estado);
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
    
    public void marcarCompletado(String resultado) {
        this.estado = "completado";
        this.resultado = resultado;
        this.fechaCompletado = LocalDateTime.now();
    }
    
    public void marcarError(String observaciones) {
        this.estado = "error";
        this.observaciones = observaciones;
        this.fechaCompletado = LocalDateTime.now();
    }
    
    public void cancelar(String motivo) {
        this.estado = "cancelado";
        this.observaciones = motivo;
        this.fechaCompletado = LocalDateTime.now();
    }
    
    // Getters y Setters
    public String getIdSolicitud() {
        return idSolicitud;
    }
    
    public void setIdSolicitud(String idSolicitud) {
        this.idSolicitud = idSolicitud;
    }
    
    public Integer getIdUsuario() {
        return idUsuario;
    }
    
    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }
    
    public Integer getIdProceso() {
        return idProceso;
    }
    
    public void setIdProceso(Integer idProceso) {
        this.idProceso = idProceso;
    }
    
    public LocalDateTime getFechaSolicitud() {
        return fechaSolicitud;
    }
    
    public void setFechaSolicitud(LocalDateTime fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public String getParametros() {
        return parametros;
    }
    
    public void setParametros(String parametros) {
        this.parametros = parametros;
    }
    
    public String getResultado() {
        return resultado;
    }
    
    public void setResultado(String resultado) {
        this.resultado = resultado;
    }
    
    public LocalDateTime getFechaCompletado() {
        return fechaCompletado;
    }
    
    public void setFechaCompletado(LocalDateTime fechaCompletado) {
        this.fechaCompletado = fechaCompletado;
    }
    
    public String getObservaciones() {
        return observaciones;
    }
    
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    
    @Override
    public String toString() {
        return "SolicitudProceso{" +
                "idSolicitud='" + idSolicitud + '\'' +
                ", idUsuario=" + idUsuario +
                ", idProceso=" + idProceso +
                ", fechaSolicitud=" + fechaSolicitud +
                ", estado='" + estado + '\'' +
                ", fechaCompletado=" + fechaCompletado +
                '}';
    }
}
