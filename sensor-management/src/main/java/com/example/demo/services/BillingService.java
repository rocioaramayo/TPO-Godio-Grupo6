package com.example.demo.services;

import com.example.demo.modelo.*;
import com.example.demo.repositories.mysql.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Servicio completo de facturación según los requerimientos del TP
 * Implementa facturación a usuarios, control de pagos y acreditación en cuenta corriente
 */
public class BillingService {
    
    private static BillingService instance;
    private FacturaMySQLRepository facturaRepository;
    private PagoMySQLRepository pagoRepository;
    private CuentaCorrienteMySQLRepository cuentaRepository;
    private SolicitudProcesoMySQLRepository solicitudRepository;
    private ProcesoMySQLRepository procesoRepository;
    
    private BillingService() {
        this.facturaRepository = FacturaMySQLRepository.getInstance();
        this.pagoRepository = PagoMySQLRepository.getInstance();
        this.cuentaRepository = CuentaCorrienteMySQLRepository.getInstance();
        this.solicitudRepository = SolicitudProcesoMySQLRepository.getInstance();
        this.procesoRepository = ProcesoMySQLRepository.getInstance();
    }
    
    public static BillingService getInstance() {
        if (instance == null) {
            instance = new BillingService();
        }
        return instance;
    }
    
    /**
     * Genera factura para un usuario basada en sus procesos solicitados
     */
    public Factura generarFactura(Integer idUsuario, String periodoDesde, String periodoHasta) {
        try {
            // Obtener solicitudes completadas del usuario en el período
            List<SolicitudProceso> solicitudes = solicitudRepository.findByUsuarioYPeriodo(
                idUsuario, periodoDesde, periodoHasta, "completado");
            
            if (solicitudes.isEmpty()) {
                System.out.println("No hay procesos completados para facturar para el usuario " + idUsuario);
                return null;
            }
            
            // Calcular monto total
            BigDecimal montoTotal = calcularMontoTotal(solicitudes);
            
            // Crear factura
            String idFactura = "FAC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            Factura factura = new Factura(idUsuario, montoTotal);
            factura.setIdFactura(idFactura);
            factura.setObservaciones("Factura generada automáticamente para período " + periodoDesde + " - " + periodoHasta);
            
            // Agregar procesos facturados
            for (SolicitudProceso solicitud : solicitudes) {
                factura.agregarProceso(solicitud.getIdSolicitud());
            }
            
            // Guardar factura
            facturaRepository.crear(factura);
            
            // Cargar la cuenta corriente del usuario
            CuentaCorriente cuenta = cuentaRepository.findByUsuario(idUsuario);
            if (cuenta != null) {
                cuenta.cargarProceso(montoTotal, "Factura " + idFactura);
                cuentaRepository.actualizarSaldo(cuenta.getIdCuenta(), cuenta.getSaldoActual());
            }
            
            System.out.println("Factura generada: " + idFactura + " - Monto: $" + montoTotal);
            return factura;
            
        } catch (Exception e) {
            System.err.println("Error generando factura: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Procesa un pago de factura
     */
    public Pago procesarPago(String idFactura, BigDecimal montoPagado, String metodoPago, String numeroTransaccion) {
        try {
            // Verificar que la factura existe y está pendiente
            Factura factura = facturaRepository.findById(idFactura);
            if (factura == null) {
                throw new RuntimeException("Factura no encontrada: " + idFactura);
            }
            
            if (!factura.estaPendiente()) {
                throw new RuntimeException("La factura ya fue pagada o está cancelada");
            }
            
            // Crear registro de pago
            String idPago = "PAG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            Pago pago = new Pago(idFactura, montoPagado, metodoPago, numeroTransaccion, "Pago procesado automáticamente");
            pago.setIdPago(idPago);
            
            // Guardar pago
            pagoRepository.crear(pago);
            
            // Marcar factura como pagada
            facturaRepository.marcarPagada(idFactura, metodoPago);
            
            // Acreditar en cuenta corriente
            CuentaCorriente cuenta = cuentaRepository.findByUsuario(factura.getIdUsuario());
            if (cuenta != null) {
                cuenta.abonarPago(montoPagado, "Pago de factura " + idFactura);
                cuentaRepository.actualizarSaldo(cuenta.getIdCuenta(), cuenta.getSaldoActual());
            }
            
            System.out.println("Pago procesado: " + idPago + " - Monto: $" + montoPagado);
            return pago;
            
        } catch (Exception e) {
            System.err.println("Error procesando pago: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Obtiene el estado de cuenta de un usuario
     */
    public AccountStatus obtenerEstadoCuenta(Integer idUsuario) {
        try {
            CuentaCorriente cuenta = cuentaRepository.findByUsuario(idUsuario);
            if (cuenta == null) {
                return new AccountStatus(idUsuario, BigDecimal.ZERO, BigDecimal.ZERO, "Sin cuenta corriente");
            }
            
            List<Factura> facturasPendientes = facturaRepository.findByUsuario(idUsuario).stream()
                    .filter(Factura::estaPendiente)
                    .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
            
            BigDecimal totalPendiente = facturasPendientes.stream()
                    .map(Factura::getMontoFormateado)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            return new AccountStatus(
                idUsuario,
                cuenta.getSaldoActual(),
                totalPendiente,
                cuenta.getEstado()
            );
            
        } catch (Exception e) {
            System.err.println("Error obteniendo estado de cuenta: " + e.getMessage());
            return new AccountStatus(idUsuario, BigDecimal.ZERO, BigDecimal.ZERO, "Error");
        }
    }
    
    /**
     * Marca facturas vencidas automáticamente
     */
    public int marcarFacturasVencidas() {
        try {
            List<Factura> facturasVencidas = facturaRepository.findVencidas();
            
            for (Factura factura : facturasVencidas) {
                facturaRepository.actualizarEstado(factura.getIdFactura(), "vencida");
                System.out.println("Factura marcada como vencida: " + factura.getIdFactura());
            }
            
            return facturasVencidas.size();
            
        } catch (Exception e) {
            System.err.println("Error marcando facturas vencidas: " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * Genera reporte de facturación por período
     */
    public BillingReport generarReporteFacturacion(String fechaDesde, String fechaHasta) {
        try {
            List<Factura> facturas = facturaRepository.findByPeriodo(fechaDesde, fechaHasta);
            
            BigDecimal totalFacturado = facturas.stream()
                    .map(Factura::getMontoFormateado)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal totalPagado = facturas.stream()
                    .filter(Factura::estaPagada)
                    .map(Factura::getMontoFormateado)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            long facturasPendientes = facturas.stream()
                    .filter(Factura::estaPendiente)
                    .count();
            
            long facturasVencidas = facturas.stream()
                    .filter(Factura::estaVencida)
                    .count();
            
            return new BillingReport(
                fechaDesde, fechaHasta,
                facturas.size(),
                totalFacturado,
                totalPagado,
                facturasPendientes,
                facturasVencidas
            );
            
        } catch (Exception e) {
            System.err.println("Error generando reporte de facturación: " + e.getMessage());
            return new BillingReport(fechaDesde, fechaHasta, 0, BigDecimal.ZERO, BigDecimal.ZERO, 0, 0);
        }
    }
    
    /**
     * Calcula el monto total de una lista de solicitudes
     */
    private BigDecimal calcularMontoTotal(List<SolicitudProceso> solicitudes) {
        BigDecimal total = BigDecimal.ZERO;
        
        for (SolicitudProceso solicitud : solicitudes) {
            try {
                Proceso proceso = procesoRepository.findById(solicitud.getIdProceso());
                if (proceso != null) {
                    total = total.add(proceso.getCostoFormateado());
                }
            } catch (Exception e) {
                System.err.println("Error calculando costo de proceso " + solicitud.getIdProceso() + ": " + e.getMessage());
            }
        }
        
        return total;
    }
    
    /**
     * Clase para estado de cuenta
     */
    public static class AccountStatus {
        private Integer idUsuario;
        private BigDecimal saldoActual;
        private BigDecimal totalPendiente;
        private String estadoCuenta;
        
        public AccountStatus(Integer idUsuario, BigDecimal saldoActual, BigDecimal totalPendiente, String estadoCuenta) {
            this.idUsuario = idUsuario;
            this.saldoActual = saldoActual;
            this.totalPendiente = totalPendiente;
            this.estadoCuenta = estadoCuenta;
        }
        
        public Integer getIdUsuario() { return idUsuario; }
        public BigDecimal getSaldoActual() { return saldoActual; }
        public BigDecimal getTotalPendiente() { return totalPendiente; }
        public String getEstadoCuenta() { return estadoCuenta; }
        
        public BigDecimal getSaldoTotal() {
            return saldoActual.add(totalPendiente);
        }
        
        @Override
        public String toString() {
            return "AccountStatus{" +
                    "idUsuario=" + idUsuario +
                    ", saldoActual=" + saldoActual +
                    ", totalPendiente=" + totalPendiente +
                    ", estadoCuenta='" + estadoCuenta + '\'' +
                    '}';
        }
    }
    
    /**
     * Clase para reporte de facturación
     */
    public static class BillingReport {
        private String fechaDesde;
        private String fechaHasta;
        private int totalFacturas;
        private BigDecimal totalFacturado;
        private BigDecimal totalPagado;
        private long facturasPendientes;
        private long facturasVencidas;
        
        public BillingReport(String fechaDesde, String fechaHasta, int totalFacturas, 
                           BigDecimal totalFacturado, BigDecimal totalPagado, 
                           long facturasPendientes, long facturasVencidas) {
            this.fechaDesde = fechaDesde;
            this.fechaHasta = fechaHasta;
            this.totalFacturas = totalFacturas;
            this.totalFacturado = totalFacturado;
            this.totalPagado = totalPagado;
            this.facturasPendientes = facturasPendientes;
            this.facturasVencidas = facturasVencidas;
        }
        
        // Getters
        public String getFechaDesde() { return fechaDesde; }
        public String getFechaHasta() { return fechaHasta; }
        public int getTotalFacturas() { return totalFacturas; }
        public BigDecimal getTotalFacturado() { return totalFacturado; }
        public BigDecimal getTotalPagado() { return totalPagado; }
        public long getFacturasPendientes() { return facturasPendientes; }
        public long getFacturasVencidas() { return facturasVencidas; }
        
        public BigDecimal getTotalPendiente() {
            return totalFacturado.subtract(totalPagado);
        }
        
        public double getPorcentajePagado() {
            if (totalFacturado.compareTo(BigDecimal.ZERO) == 0) return 0.0;
            return totalPagado.divide(totalFacturado, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(BigDecimal.valueOf(100)).doubleValue();
        }
        
        @Override
        public String toString() {
            return "BillingReport{" +
                    "fechaDesde='" + fechaDesde + '\'' +
                    ", fechaHasta='" + fechaHasta + '\'' +
                    ", totalFacturas=" + totalFacturas +
                    ", totalFacturado=" + totalFacturado +
                    ", totalPagado=" + totalPagado +
                    ", facturasPendientes=" + facturasPendientes +
                    ", facturasVencidas=" + facturasVencidas +
                    '}';
        }
    }
}