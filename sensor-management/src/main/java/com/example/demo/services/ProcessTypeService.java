package com.example.demo.services;

import com.example.demo.modelo.Proceso;
import com.example.demo.repositories.mysql.ProcesoMySQLRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

/**
 * Servicio para gestionar los tipos de procesos requeridos por la consigna
 * Implementa los 6 tipos de procesos específicos del TP
 */
public class ProcessTypeService {
    
    private static ProcessTypeService instance;
    private ProcesoMySQLRepository procesoRepository;
    
    private ProcessTypeService() {
        this.procesoRepository = ProcesoMySQLRepository.getInstance();
    }
    
    public static ProcessTypeService getInstance() {
        if (instance == null) {
            instance = new ProcessTypeService();
        }
        return instance;
    }
    
    /**
     * Inicializa los tipos de procesos requeridos por la consigna
     */
    public void inicializarProcesosRequeridos() {
        try {
            List<Proceso> procesosRequeridos = crearProcesosRequeridos();
            
            for (Proceso proceso : procesosRequeridos) {
                procesoRepository.crear(proceso);
            }
            
            System.out.println("Procesos requeridos inicializados: " + procesosRequeridos.size());
            
        } catch (Exception e) {
            System.err.println("Error inicializando procesos requeridos: " + e.getMessage());
        }
    }
    
    /**
     * Crea los 6 tipos de procesos requeridos por la consigna
     */
    private List<Proceso> crearProcesosRequeridos() {
        List<Proceso> procesos = new ArrayList<>();
        
        // 1. Informe de humedad y temperaturas máximas y mínimas por ciudades, zonas, países
        Proceso informeMaxMin = new Proceso(
            "Informe Temperaturas Máximas y Mínimas",
            "Genera informe de humedad y temperaturas máximas y mínimas por ciudades, zonas, países en un rango de fechas anualizadas, mensualizadas, etc.",
            "informe_temp_max_min",
            new BigDecimal("50.00"),
            "{\"ciudad\":\"string\",\"pais\":\"string\",\"fecha_desde\":\"date\",\"fecha_hasta\":\"date\",\"agrupacion\":\"anual|mensual|diario\"}",
            10
        );
        procesos.add(informeMaxMin);
        
        // 2. Informe de humedad y temperaturas promedio por ciudades, zonas, países
        Proceso informePromedio = new Proceso(
            "Informe Temperaturas Promedio",
            "Genera informe de humedad y temperaturas promedio por ciudades, zonas, países en un rango de fechas anualizadas, mensualizadas, etc.",
            "informe_temp_promedio",
            new BigDecimal("45.00"),
            "{\"ciudad\":\"string\",\"pais\":\"string\",\"fecha_desde\":\"date\",\"fecha_hasta\":\"date\",\"agrupacion\":\"anual|mensual|diario\"}",
            8
        );
        procesos.add(informePromedio);
        
        // 3. Alertas de temperaturas y humedad en un rango determinado
        Proceso alertasClimaticas = new Proceso(
            "Alertas Climáticas",
            "Genera alertas de temperaturas y humedad en un rango determinado en una ciudad, zona, país en un rango de fechas",
            "alertas_climaticas",
            new BigDecimal("30.00"),
            "{\"ciudad\":\"string\",\"pais\":\"string\",\"temp_min\":\"double\",\"temp_max\":\"double\",\"humedad_min\":\"double\",\"humedad_max\":\"double\",\"fecha_desde\":\"date\",\"fecha_hasta\":\"date\"}",
            5
        );
        procesos.add(alertasClimaticas);
        
        // 4. Servicios de consultas en línea de la información de los sensores
        Proceso consultasOnline = new Proceso(
            "Consultas en Línea",
            "Servicios de consultas en línea de la información de los sensores por ciudad, zona, país en un rango de fechas",
            "consultas_online",
            new BigDecimal("20.00"),
            "{\"ciudad\":\"string\",\"pais\":\"string\",\"fecha_desde\":\"date\",\"fecha_hasta\":\"date\",\"tipo_sensor\":\"temperatura|humedad|ambos\"}",
            2
        );
        procesos.add(consultasOnline);
        
        // 5. Procesos periódicos de consultas sobre humedad y temperaturas
        Proceso procesosPeriodicos = new Proceso(
            "Procesos Periódicos",
            "Procesos periódicos de consultas sobre humedad y temperaturas por ciudades, zonas, países anualizados, mensualizadas, etc.",
            "procesos_periodicos",
            new BigDecimal("100.00"),
            "{\"ciudad\":\"string\",\"pais\":\"string\",\"frecuencia\":\"diario|semanal|mensual|anual\",\"tipo_proceso\":\"max_min|promedio|alertas\"}",
            15
        );
        procesos.add(procesosPeriodicos);
        
        // 6. Facturación a usuarios, control de pagos y acreditación en cuenta corriente
        Proceso facturacion = new Proceso(
            "Facturación y Control de Pagos",
            "Facturación a usuarios, control de pagos y acreditación en cuenta corriente",
            "facturacion",
            new BigDecimal("0.00"), // Sin costo adicional, es parte del sistema
            "{\"id_usuario\":\"integer\",\"periodo_desde\":\"date\",\"periodo_hasta\":\"date\",\"incluir_pendientes\":\"boolean\"}",
            3
        );
        procesos.add(facturacion);
        
        return procesos;
    }
    
    /**
     * Obtiene todos los procesos disponibles
     */
    public List<Proceso> obtenerProcesosDisponibles() {
        try {
            return procesoRepository.findAll();
        } catch (Exception e) {
            System.err.println("Error obteniendo procesos disponibles: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtiene procesos por tipo
     */
    public List<Proceso> obtenerProcesosPorTipo(String tipoProceso) {
        try {
            return procesoRepository.findByTipo(tipoProceso);
        } catch (Exception e) {
            System.err.println("Error obteniendo procesos por tipo: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtiene un proceso por ID
     */
    public Proceso obtenerProcesoPorId(Integer idProceso) {
        try {
            return procesoRepository.findById(idProceso);
        } catch (Exception e) {
            System.err.println("Error obteniendo proceso por ID: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Calcula el costo total de una lista de procesos
     */
    public BigDecimal calcularCostoTotal(List<Proceso> procesos) {
        return procesos.stream()
                .map(Proceso::getCostoFormateado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * Valida si un proceso está disponible para solicitar
     */
    public boolean esProcesoDisponible(Integer idProceso) {
        Proceso proceso = obtenerProcesoPorId(idProceso);
        return proceso != null && proceso.esProcesoActivo();
    }
    
    /**
     * Obtiene estadísticas de procesos
     */
    public ProcessStats obtenerEstadisticas() {
        try {
            List<Proceso> todosProcesos = obtenerProcesosDisponibles();
            
            long totalProcesos = todosProcesos.size();
            long procesosActivos = todosProcesos.stream().filter(Proceso::esProcesoActivo).count();
            BigDecimal costoPromedio = todosProcesos.stream()
                    .map(Proceso::getCostoFormateado)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(totalProcesos), 2, java.math.RoundingMode.HALF_UP);
            
            return new ProcessStats(totalProcesos, procesosActivos, costoPromedio);
            
        } catch (Exception e) {
            System.err.println("Error obteniendo estadísticas: " + e.getMessage());
            return new ProcessStats(0, 0, BigDecimal.ZERO);
        }
    }
    
    /**
     * Clase para estadísticas de procesos
     */
    public static class ProcessStats {
        private long totalProcesos;
        private long procesosActivos;
        private BigDecimal costoPromedio;
        
        public ProcessStats(long totalProcesos, long procesosActivos, BigDecimal costoPromedio) {
            this.totalProcesos = totalProcesos;
            this.procesosActivos = procesosActivos;
            this.costoPromedio = costoPromedio;
        }
        
        public long getTotalProcesos() { return totalProcesos; }
        public long getProcesosActivos() { return procesosActivos; }
        public BigDecimal getCostoPromedio() { return costoPromedio; }
        
        @Override
        public String toString() {
            return "ProcessStats{" +
                    "totalProcesos=" + totalProcesos +
                    ", procesosActivos=" + procesosActivos +
                    ", costoPromedio=" + costoPromedio +
                    '}';
        }
    }
}
