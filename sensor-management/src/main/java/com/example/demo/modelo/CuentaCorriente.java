package com.example.demo.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

/**
 * Modelo para representar cuentas corrientes de usuarios
 * Registra el saldo y movimientos financieros de cada usuario
 */
public class CuentaCorriente {
    
    private String idCuenta;
    private Integer idUsuario;
    private BigDecimal saldoActual;
    private BigDecimal limiteCredito;
    private String estado; // activa/suspendida/cancelada
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaUltimoMovimiento;
    private List<MovimientoCuenta> historialMovimientos;
    
    // Constructor vacío
    public CuentaCorriente() {
        this.historialMovimientos = new ArrayList<>();
        this.saldoActual = BigDecimal.ZERO;
        this.limiteCredito = BigDecimal.valueOf(1000); // Límite por defecto
    }
    
    // Constructor básico
    public CuentaCorriente(Integer idUsuario) {
        this.idUsuario = idUsuario;
        this.saldoActual = BigDecimal.ZERO;
        this.limiteCredito = BigDecimal.valueOf(1000);
        this.estado = "activa";
        this.fechaCreacion = LocalDateTime.now();
        this.historialMovimientos = new ArrayList<>();
    }
    
    // Constructor completo
    public CuentaCorriente(Integer idUsuario, BigDecimal limiteCredito) {
        this.idUsuario = idUsuario;
        this.saldoActual = BigDecimal.ZERO;
        this.limiteCredito = limiteCredito;
        this.estado = "activa";
        this.fechaCreacion = LocalDateTime.now();
        this.historialMovimientos = new ArrayList<>();
    }
    
    // Métodos de gestión de movimientos
    public void agregarMovimiento(String tipo, BigDecimal monto, String descripcion) {
        MovimientoCuenta movimiento = new MovimientoCuenta(tipo, monto, descripcion);
        historialMovimientos.add(movimiento);
        
        // Actualizar saldo
        if ("debito".equals(tipo) || "cargo".equals(tipo)) {
            this.saldoActual = this.saldoActual.subtract(monto);
        } else if ("credito".equals(tipo) || "abono".equals(tipo)) {
            this.saldoActual = this.saldoActual.add(monto);
        }
        
        this.fechaUltimoMovimiento = LocalDateTime.now();
    }
    
    public void cargarProceso(BigDecimal monto, String descripcion) {
        agregarMovimiento("cargo", monto, descripcion);
    }
    
    public void abonarPago(BigDecimal monto, String descripcion) {
        agregarMovimiento("abono", monto, descripcion);
    }
    
    public boolean puedeRealizarCargo(BigDecimal monto) {
        BigDecimal saldoDisponible = saldoActual.add(limiteCredito);
        return saldoDisponible.compareTo(monto) >= 0 && "activa".equals(estado);
    }
    
    public BigDecimal getSaldoDisponible() {
        return saldoActual.add(limiteCredito);
    }
    
    public boolean estaEnRojo() {
        return saldoActual.compareTo(BigDecimal.ZERO) < 0;
    }
    
    public boolean estaEnLimite() {
        return saldoActual.compareTo(limiteCredito.negate()) <= 0;
    }
    
    public void suspender(String motivo) {
        this.estado = "suspendida";
        agregarMovimiento("sistema", BigDecimal.ZERO, "Cuenta suspendida: " + motivo);
    }
    
    public void reactivar() {
        this.estado = "activa";
        agregarMovimiento("sistema", BigDecimal.ZERO, "Cuenta reactivada");
    }
    
    public void cancelar(String motivo) {
        this.estado = "cancelada";
        agregarMovimiento("sistema", BigDecimal.ZERO, "Cuenta cancelada: " + motivo);
    }
    
    // Getters y Setters
    public String getIdCuenta() {
        return idCuenta;
    }
    
    public void setIdCuenta(String idCuenta) {
        this.idCuenta = idCuenta;
    }
    
    public Integer getIdUsuario() {
        return idUsuario;
    }
    
    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }
    
    public BigDecimal getSaldoActual() {
        return saldoActual;
    }
    
    public void setSaldoActual(BigDecimal saldoActual) {
        this.saldoActual = saldoActual;
    }
    
    public BigDecimal getLimiteCredito() {
        return limiteCredito;
    }
    
    public void setLimiteCredito(BigDecimal limiteCredito) {
        this.limiteCredito = limiteCredito;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    
    public LocalDateTime getFechaUltimoMovimiento() {
        return fechaUltimoMovimiento;
    }
    
    public void setFechaUltimoMovimiento(LocalDateTime fechaUltimoMovimiento) {
        this.fechaUltimoMovimiento = fechaUltimoMovimiento;
    }
    
    public List<MovimientoCuenta> getHistorialMovimientos() {
        return historialMovimientos;
    }
    
    public void setHistorialMovimientos(List<MovimientoCuenta> historialMovimientos) {
        this.historialMovimientos = historialMovimientos;
    }
    
    @Override
    public String toString() {
        return "CuentaCorriente{" +
                "idCuenta='" + idCuenta + '\'' +
                ", idUsuario=" + idUsuario +
                ", saldoActual=" + saldoActual +
                ", limiteCredito=" + limiteCredito +
                ", estado='" + estado + '\'' +
                ", cantidadMovimientos=" + historialMovimientos.size() +
                '}';
    }
    
    /**
     * Clase interna para representar movimientos de cuenta
     */
    public static class MovimientoCuenta {
        private LocalDateTime fecha;
        private String tipo; // cargo/abono/debito/credito/sistema
        private BigDecimal monto;
        private String descripcion;
        private BigDecimal saldoAnterior;
        private BigDecimal saldoPosterior;
        
        public MovimientoCuenta() {}
        
        public MovimientoCuenta(String tipo, BigDecimal monto, String descripcion) {
            this.fecha = LocalDateTime.now();
            this.tipo = tipo;
            this.monto = monto;
            this.descripcion = descripcion;
        }
        
        // Getters y Setters
        public LocalDateTime getFecha() { return fecha; }
        public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
        
        public String getTipo() { return tipo; }
        public void setTipo(String tipo) { this.tipo = tipo; }
        
        public BigDecimal getMonto() { return monto; }
        public void setMonto(BigDecimal monto) { this.monto = monto; }
        
        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
        
        public BigDecimal getSaldoAnterior() { return saldoAnterior; }
        public void setSaldoAnterior(BigDecimal saldoAnterior) { this.saldoAnterior = saldoAnterior; }
        
        public BigDecimal getSaldoPosterior() { return saldoPosterior; }
        public void setSaldoPosterior(BigDecimal saldoPosterior) { this.saldoPosterior = saldoPosterior; }
        
        @Override
        public String toString() {
            return "MovimientoCuenta{" +
                    "fecha=" + fecha +
                    ", tipo='" + tipo + '\'' +
                    ", monto=" + monto +
                    ", descripcion='" + descripcion + '\'' +
                    '}';
        }
    }
}
