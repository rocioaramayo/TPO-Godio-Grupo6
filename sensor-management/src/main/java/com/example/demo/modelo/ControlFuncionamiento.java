package com.example.demo.modelo;

import java.time.LocalDateTime;

/**
 * Modelo para representar controles de funcionamiento de sensores
 * Registra las revisiones y mantenimientos realizados a los sensores
 */
public class ControlFuncionamiento {
    
    private String idControl;
    private Integer idSensor;
    private LocalDateTime fechaRevision;
    private String estadoSensor; // funcionando/fallando/mantenimiento/desconectado
    private String observaciones;
    private String tecnicoResponsable;
    private String tipoControl; // preventivo/correctivo/rutina/emergencia
    private String accionesRealizadas;
    private LocalDateTime proximaRevision;
    private String estadoControl; // completado/pendiente/cancelado
    
    // Constructor vacío
    public ControlFuncionamiento() {}
    
    // Constructor básico
    public ControlFuncionamiento(Integer idSensor, String estadoSensor, String observaciones) {
        this.idSensor = idSensor;
        this.estadoSensor = estadoSensor;
        this.observaciones = observaciones;
        this.fechaRevision = LocalDateTime.now();
        this.estadoControl = "completado";
        this.tipoControl = "rutina";
    }
    
    // Constructor completo
    public ControlFuncionamiento(Integer idSensor, String estadoSensor, String observaciones,
                                String tecnicoResponsable, String tipoControl, String accionesRealizadas) {
        this.idSensor = idSensor;
        this.estadoSensor = estadoSensor;
        this.observaciones = observaciones;
        this.tecnicoResponsable = tecnicoResponsable;
        this.tipoControl = tipoControl;
        this.accionesRealizadas = accionesRealizadas;
        this.fechaRevision = LocalDateTime.now();
        this.estadoControl = "completado";
    }
    
    // Métodos de utilidad
    public boolean estaCompletado() {
        return "completado".equals(estadoControl);
    }
    
    public boolean estaPendiente() {
        return "pendiente".equals(estadoControl);
    }
    
    public boolean estaCancelado() {
        return "cancelado".equals(estadoControl);
    }
    
    public boolean sensorFuncionando() {
        return "funcionando".equals(estadoSensor);
    }
    
    public boolean sensorFallando() {
        return "fallando".equals(estadoSensor);
    }
    
    public boolean sensorEnMantenimiento() {
        return "mantenimiento".equals(estadoSensor);
    }
    
    public boolean sensorDesconectado() {
        return "desconectado".equals(estadoSensor);
    }
    
    public boolean esControlPreventivo() {
        return "preventivo".equals(tipoControl);
    }
    
    public boolean esControlCorrectivo() {
        return "correctivo".equals(tipoControl);
    }
    
    public boolean esControlEmergencia() {
        return "emergencia".equals(tipoControl);
    }
    
    public void marcarCompletado() {
        this.estadoControl = "completado";
    }
    
    public void marcarPendiente() {
        this.estadoControl = "pendiente";
    }
    
    public void cancelar(String motivo) {
        this.estadoControl = "cancelado";
        this.observaciones = motivo;
    }
    
    public void programarProximaRevision(int dias) {
        this.proximaRevision = LocalDateTime.now().plusDays(dias);
    }
    
    public boolean necesitaProximaRevision() {
        return proximaRevision != null && LocalDateTime.now().isAfter(proximaRevision);
    }
    
    public String getTipoControlFormateado() {
        if (tipoControl == null) return "No especificado";
        
        switch (tipoControl.toLowerCase()) {
            case "preventivo":
                return "Control Preventivo";
            case "correctivo":
                return "Control Correctivo";
            case "rutina":
                return "Control de Rutina";
            case "emergencia":
                return "Control de Emergencia";
            default:
                return tipoControl;
        }
    }
    
    public String getEstadoSensorFormateado() {
        if (estadoSensor == null) return "Desconocido";
        
        switch (estadoSensor.toLowerCase()) {
            case "funcionando":
                return "Funcionando Correctamente";
            case "fallando":
                return "Con Fallas";
            case "mantenimiento":
                return "En Mantenimiento";
            case "desconectado":
                return "Desconectado";
            default:
                return estadoSensor;
        }
    }
    
    // Getters y Setters
    public String getIdControl() {
        return idControl;
    }
    
    public void setIdControl(String idControl) {
        this.idControl = idControl;
    }
    
    public Integer getIdSensor() {
        return idSensor;
    }
    
    public void setIdSensor(Integer idSensor) {
        this.idSensor = idSensor;
    }
    
    public LocalDateTime getFechaRevision() {
        return fechaRevision;
    }
    
    public void setFechaRevision(LocalDateTime fechaRevision) {
        this.fechaRevision = fechaRevision;
    }
    
    public String getEstadoSensor() {
        return estadoSensor;
    }
    
    public void setEstadoSensor(String estadoSensor) {
        this.estadoSensor = estadoSensor;
    }
    
    public String getObservaciones() {
        return observaciones;
    }
    
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    
    public String getTecnicoResponsable() {
        return tecnicoResponsable;
    }
    
    public void setTecnicoResponsable(String tecnicoResponsable) {
        this.tecnicoResponsable = tecnicoResponsable;
    }
    
    public String getTipoControl() {
        return tipoControl;
    }
    
    public void setTipoControl(String tipoControl) {
        this.tipoControl = tipoControl;
    }
    
    public String getAccionesRealizadas() {
        return accionesRealizadas;
    }
    
    public void setAccionesRealizadas(String accionesRealizadas) {
        this.accionesRealizadas = accionesRealizadas;
    }
    
    public LocalDateTime getProximaRevision() {
        return proximaRevision;
    }
    
    public void setProximaRevision(LocalDateTime proximaRevision) {
        this.proximaRevision = proximaRevision;
    }
    
    public String getEstadoControl() {
        return estadoControl;
    }
    
    public void setEstadoControl(String estadoControl) {
        this.estadoControl = estadoControl;
    }
    
    @Override
    public String toString() {
        return "ControlFuncionamiento{" +
                "idControl='" + idControl + '\'' +
                ", idSensor=" + idSensor +
                ", fechaRevision=" + fechaRevision +
                ", estadoSensor='" + estadoSensor + '\'' +
                ", tipoControl='" + tipoControl + '\'' +
                ", estadoControl='" + estadoControl + '\'' +
                '}';
    }
}
