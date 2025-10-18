package com.example.demo.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

/**
 * Modelo para representar facturas del sistema
 * Registra los procesos facturados a usuarios
 */
public class Factura {
    
    private String idFactura;
    private Integer idUsuario;
    private LocalDateTime fechaEmision;
    private LocalDateTime fechaVencimiento;
    private BigDecimal montoTotal;
    private String estado; // pendiente/pagada/vencida/cancelada
    private List<String> procesosFacturados; // IDs de procesos incluidos
    private String observaciones;
    private String metodoPago; // efectivo/transferencia/tarjeta
    private LocalDateTime fechaPago;
    
    // Constructor vacío
    public Factura() {
        this.procesosFacturados = new ArrayList<>();
    }
    
    // Constructor básico
    public Factura(Integer idUsuario, BigDecimal montoTotal) {
        this.idUsuario = idUsuario;
        this.montoTotal = montoTotal;
        this.fechaEmision = LocalDateTime.now();
        this.fechaVencimiento = LocalDateTime.now().plusDays(30); // 30 días para pagar
        this.estado = "pendiente";
        this.procesosFacturados = new ArrayList<>();
    }
    
    // Constructor completo
    public Factura(Integer idUsuario, BigDecimal montoTotal, List<String> procesosFacturados, String observaciones) {
        this.idUsuario = idUsuario;
        this.montoTotal = montoTotal;
        this.procesosFacturados = procesosFacturados != null ? procesosFacturados : new ArrayList<>();
        this.observaciones = observaciones;
        this.fechaEmision = LocalDateTime.now();
        this.fechaVencimiento = LocalDateTime.now().plusDays(30);
        this.estado = "pendiente";
    }
    
    // Métodos de utilidad
    public boolean estaPendiente() {
        return "pendiente".equals(estado);
    }
    
    public boolean estaPagada() {
        return "pagada".equals(estado);
    }
    
    public boolean estaVencida() {
        return "vencida".equals(estado);
    }
    
    public boolean estaCancelada() {
        return "cancelada".equals(estado);
    }
    
    public boolean estaVencidaPorFecha() {
        return LocalDateTime.now().isAfter(fechaVencimiento) && estaPendiente();
    }
    
    public void agregarProceso(String idProceso) {
        if (!procesosFacturados.contains(idProceso)) {
            procesosFacturados.add(idProceso);
        }
    }
    
    public void removerProceso(String idProceso) {
        procesosFacturados.remove(idProceso);
    }
    
    public int getCantidadProcesos() {
        return procesosFacturados.size();
    }
    
    public void marcarPagada(String metodoPago) {
        this.estado = "pagada";
        this.metodoPago = metodoPago;
        this.fechaPago = LocalDateTime.now();
    }
    
    public void marcarVencida() {
        this.estado = "vencida";
    }
    
    public void cancelar(String motivo) {
        this.estado = "cancelada";
        this.observaciones = motivo;
    }
    
    public BigDecimal getMontoFormateado() {
        return montoTotal != null ? montoTotal : BigDecimal.ZERO;
    }
    
    // Getters y Setters
    public String getIdFactura() {
        return idFactura;
    }
    
    public void setIdFactura(String idFactura) {
        this.idFactura = idFactura;
    }
    
    public Integer getIdUsuario() {
        return idUsuario;
    }
    
    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }
    
    public LocalDateTime getFechaEmision() {
        return fechaEmision;
    }
    
    public void setFechaEmision(LocalDateTime fechaEmision) {
        this.fechaEmision = fechaEmision;
    }
    
    public LocalDateTime getFechaVencimiento() {
        return fechaVencimiento;
    }
    
    public void setFechaVencimiento(LocalDateTime fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }
    
    public BigDecimal getMontoTotal() {
        return montoTotal;
    }
    
    public void setMontoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public List<String> getProcesosFacturados() {
        return procesosFacturados;
    }
    
    public void setProcesosFacturados(List<String> procesosFacturados) {
        this.procesosFacturados = procesosFacturados;
    }
    
    public String getObservaciones() {
        return observaciones;
    }
    
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    
    public String getMetodoPago() {
        return metodoPago;
    }
    
    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }
    
    public LocalDateTime getFechaPago() {
        return fechaPago;
    }
    
    public void setFechaPago(LocalDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }
    
    @Override
    public String toString() {
        return "Factura{" +
                "idFactura='" + idFactura + '\'' +
                ", idUsuario=" + idUsuario +
                ", fechaEmision=" + fechaEmision +
                ", montoTotal=" + montoTotal +
                ", estado='" + estado + '\'' +
                ", cantidadProcesos=" + getCantidadProcesos() +
                '}';
    }
}
