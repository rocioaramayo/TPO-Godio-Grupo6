package com.example.demo.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Modelo para representar pagos realizados por usuarios
 * Registra los pagos de facturas y métodos de pago utilizados
 */
public class Pago {
    
    private String idPago;
    private String idFactura;
    private LocalDateTime fechaPago;
    private BigDecimal montoPagado;
    private String metodoPago; // efectivo/transferencia/tarjeta/cheque
    private String estado; // procesado/pendiente/fallido/reembolsado
    private String numeroTransaccion;
    private String observaciones;
    private String datosAdicionales; // JSON con datos específicos del método de pago
    
    // Constructor vacío
    public Pago() {}
    
    // Constructor básico
    public Pago(String idFactura, BigDecimal montoPagado, String metodoPago) {
        this.idFactura = idFactura;
        this.montoPagado = montoPagado;
        this.metodoPago = metodoPago;
        this.fechaPago = LocalDateTime.now();
        this.estado = "procesado";
    }
    
    // Constructor completo
    public Pago(String idFactura, BigDecimal montoPagado, String metodoPago, 
                String numeroTransaccion, String observaciones) {
        this.idFactura = idFactura;
        this.montoPagado = montoPagado;
        this.metodoPago = metodoPago;
        this.numeroTransaccion = numeroTransaccion;
        this.observaciones = observaciones;
        this.fechaPago = LocalDateTime.now();
        this.estado = "procesado";
    }
    
    // Métodos de utilidad
    public boolean estaProcesado() {
        return "procesado".equals(estado);
    }
    
    public boolean estaPendiente() {
        return "pendiente".equals(estado);
    }
    
    public boolean fallo() {
        return "fallido".equals(estado);
    }
    
    public boolean fueReembolsado() {
        return "reembolsado".equals(estado);
    }
    
    public void marcarProcesado() {
        this.estado = "procesado";
    }
    
    public void marcarPendiente() {
        this.estado = "pendiente";
    }
    
    public void marcarFallido(String motivo) {
        this.estado = "fallido";
        this.observaciones = motivo;
    }
    
    public void marcarReembolsado(String motivo) {
        this.estado = "reembolsado";
        this.observaciones = motivo;
    }
    
    public BigDecimal getMontoFormateado() {
        return montoPagado != null ? montoPagado : BigDecimal.ZERO;
    }
    
    public String getMetodoPagoFormateado() {
        if (metodoPago == null) return "No especificado";
        
        switch (metodoPago.toLowerCase()) {
            case "efectivo":
                return "Efectivo";
            case "transferencia":
                return "Transferencia Bancaria";
            case "tarjeta":
                return "Tarjeta de Crédito/Débito";
            case "cheque":
                return "Cheque";
            default:
                return metodoPago;
        }
    }
    
    // Getters y Setters
    public String getIdPago() {
        return idPago;
    }
    
    public void setIdPago(String idPago) {
        this.idPago = idPago;
    }
    
    public String getIdFactura() {
        return idFactura;
    }
    
    public void setIdFactura(String idFactura) {
        this.idFactura = idFactura;
    }
    
    public LocalDateTime getFechaPago() {
        return fechaPago;
    }
    
    public void setFechaPago(LocalDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }
    
    public BigDecimal getMontoPagado() {
        return montoPagado;
    }
    
    public void setMontoPagado(BigDecimal montoPagado) {
        this.montoPagado = montoPagado;
    }
    
    public String getMetodoPago() {
        return metodoPago;
    }
    
    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public String getNumeroTransaccion() {
        return numeroTransaccion;
    }
    
    public void setNumeroTransaccion(String numeroTransaccion) {
        this.numeroTransaccion = numeroTransaccion;
    }
    
    public String getObservaciones() {
        return observaciones;
    }
    
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    
    public String getDatosAdicionales() {
        return datosAdicionales;
    }
    
    public void setDatosAdicionales(String datosAdicionales) {
        this.datosAdicionales = datosAdicionales;
    }
    
    @Override
    public String toString() {
        return "Pago{" +
                "idPago='" + idPago + '\'' +
                ", idFactura='" + idFactura + '\'' +
                ", fechaPago=" + fechaPago +
                ", montoPagado=" + montoPagado +
                ", metodoPago='" + metodoPago + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }
}
