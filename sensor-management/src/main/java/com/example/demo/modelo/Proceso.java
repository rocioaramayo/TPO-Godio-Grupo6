package com.example.demo.modelo;

import java.math.BigDecimal;

/**
 * Modelo para representar procesos que pueden ejecutarse en el sistema
 * Define los diferentes tipos de análisis y reportes disponibles
 */
public class Proceso {
    
    private Integer idProceso;
    private String nombre;
    private String descripcion;
    private String tipoProceso; // informe_temp_max_min, informe_temp_promedio, alertas_climaticas, consultas_online, procesos_periodicos
    private BigDecimal costo;
    private String parametrosRequeridos; // JSON con parámetros necesarios
    private String estado; // activo/inactivo
    private Integer tiempoEstimadoMinutos;
    
    // Constructor vacío
    public Proceso() {}
    
    // Constructor básico
    public Proceso(String nombre, String descripcion, String tipoProceso, BigDecimal costo) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.tipoProceso = tipoProceso;
        this.costo = costo;
        this.estado = "activo";
        this.tiempoEstimadoMinutos = 5; // Por defecto 5 minutos
    }
    
    // Constructor completo
    public Proceso(String nombre, String descripcion, String tipoProceso, BigDecimal costo, 
                   String parametrosRequeridos, Integer tiempoEstimadoMinutos) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.tipoProceso = tipoProceso;
        this.costo = costo;
        this.parametrosRequeridos = parametrosRequeridos;
        this.tiempoEstimadoMinutos = tiempoEstimadoMinutos;
        this.estado = "activo";
    }
    
    // Métodos de utilidad
    public boolean esProcesoActivo() {
        return "activo".equals(estado);
    }
    
    public boolean requiereParametros() {
        return parametrosRequeridos != null && !parametrosRequeridos.trim().isEmpty();
    }
    
    public BigDecimal getCostoFormateado() {
        return costo != null ? costo : BigDecimal.ZERO;
    }
    
    // Getters y Setters
    public Integer getIdProceso() {
        return idProceso;
    }
    
    public void setIdProceso(Integer idProceso) {
        this.idProceso = idProceso;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getTipoProceso() {
        return tipoProceso;
    }
    
    public void setTipoProceso(String tipoProceso) {
        this.tipoProceso = tipoProceso;
    }
    
    public BigDecimal getCosto() {
        return costo;
    }
    
    public void setCosto(BigDecimal costo) {
        this.costo = costo;
    }
    
    public String getParametrosRequeridos() {
        return parametrosRequeridos;
    }
    
    public void setParametrosRequeridos(String parametrosRequeridos) {
        this.parametrosRequeridos = parametrosRequeridos;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public Integer getTiempoEstimadoMinutos() {
        return tiempoEstimadoMinutos;
    }
    
    public void setTiempoEstimadoMinutos(Integer tiempoEstimadoMinutos) {
        this.tiempoEstimadoMinutos = tiempoEstimadoMinutos;
    }
    
    @Override
    public String toString() {
        return "Proceso{" +
                "idProceso=" + idProceso +
                ", nombre='" + nombre + '\'' +
                ", tipoProceso='" + tipoProceso + '\'' +
                ", costo=" + costo +
                ", tiempoEstimadoMinutos=" + tiempoEstimadoMinutos +
                ", estado='" + estado + '\'' +
                '}';
    }
}
