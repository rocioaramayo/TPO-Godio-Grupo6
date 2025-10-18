package com.example.demo.services;

import com.example.demo.modelo.*;
import com.example.demo.repositories.cassandra.HistorialCassandraDAO;
import com.example.demo.repositories.mysql.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Servicio para control de procesos según los requerimientos del TP
 * Implementa procesos que pueden ejecutarse sobre los datos, su control y registración
 */
public class ProcessControlService {
    
    private static ProcessControlService instance;
    private SolicitudProcesoMySQLRepository solicitudRepository;
    private ProcesoMySQLRepository procesoRepository;
    private HistorialCassandraDAO historialDAO;
    private ExecutorService executorService;
    
    private ProcessControlService() {
        this.solicitudRepository = SolicitudProcesoMySQLRepository.getInstance();
        this.procesoRepository = ProcesoMySQLRepository.getInstance();
        this.historialDAO = HistorialCassandraDAO.getInstance();
        this.executorService = Executors.newFixedThreadPool(5); // Pool de 5 hilos para procesos
    }
    
    public static ProcessControlService getInstance() {
        if (instance == null) {
            instance = new ProcessControlService();
        }
        return instance;
    }
    
    /**
     * Solicitar ejecución de un proceso
     */
    public SolicitudProceso solicitarProceso(Integer idUsuario, Integer idProceso, String parametros) {
        try {
            // Verificar que el proceso existe y está activo
            Proceso proceso = procesoRepository.findById(idProceso);
            if (proceso == null || !proceso.esProcesoActivo()) {
                System.err.println("Proceso no disponible: " + idProceso);
                return null;
            }
            
            // Crear solicitud
            SolicitudProceso solicitud = new SolicitudProceso(idUsuario, idProceso, parametros);
            solicitudRepository.crear(solicitud);
            
            System.out.println("Solicitud de proceso creada: " + solicitud.getIdSolicitud());
            return solicitud;
            
        } catch (Exception e) {
            System.err.println("Error solicitando proceso: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Ejecutar proceso de forma asíncrona
     */
    public CompletableFuture<String> ejecutarProcesoAsync(String idSolicitud) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return ejecutarProceso(idSolicitud);
            } catch (Exception e) {
                System.err.println("Error ejecutando proceso asíncrono: " + e.getMessage());
                return "Error: " + e.getMessage();
            }
        }, executorService);
    }
    
    /**
     * Ejecutar proceso de forma síncrona
     */
    public String ejecutarProceso(String idSolicitud) {
        try {
            SolicitudProceso solicitud = solicitudRepository.findById(idSolicitud);
            if (solicitud == null) {
                return "Error: Solicitud no encontrada";
            }
            
            if (!solicitud.estaPendiente()) {
                return "Error: La solicitud ya fue procesada";
            }
            
            Proceso proceso = procesoRepository.findById(solicitud.getIdProceso());
            if (proceso == null) {
                return "Error: Proceso no encontrado";
            }
            
            // Registrar inicio de ejecución
            historialDAO.registrar(idSolicitud, java.time.Instant.now(), "Iniciando ejecución", "iniciado");
            
            // Ejecutar proceso según su tipo
            String resultado = ejecutarProcesoPorTipo(proceso, solicitud.getParametros());
            
            // Registrar finalización
            historialDAO.registrar(idSolicitud, java.time.Instant.now(), resultado, "completado");
            
            // Marcar solicitud como completada
            solicitudRepository.marcarCompletado(idSolicitud, resultado);
            
            System.out.println("Proceso ejecutado: " + idSolicitud + " - Resultado: " + resultado.substring(0, Math.min(100, resultado.length())));
            return resultado;
            
        } catch (Exception e) {
            try {
                historialDAO.registrar(idSolicitud, java.time.Instant.now(), "Error: " + e.getMessage(), "error");
                solicitudRepository.marcarError(idSolicitud, e.getMessage());
            } catch (Exception ex) {
                System.err.println("Error registrando fallo: " + ex.getMessage());
            }
            
            System.err.println("Error ejecutando proceso: " + e.getMessage());
            return "Error: " + e.getMessage();
        }
    }
    
    /**
     * Ejecutar proceso según su tipo específico
     */
    private String ejecutarProcesoPorTipo(Proceso proceso, String parametros) {
        String tipoProceso = proceso.getTipoProceso();
        
        switch (tipoProceso) {
            case "informe_temp_max_min":
                return ejecutarInformeTemperaturasMaxMin(parametros);
                
            case "informe_temp_promedio":
                return ejecutarInformeTemperaturasPromedio(parametros);
                
            case "alertas_climaticas":
                return ejecutarAlertasClimaticas(parametros);
                
            case "consultas_online":
                return ejecutarConsultasOnline(parametros);
                
            case "procesos_periodicos":
                return ejecutarProcesosPeriodicos(parametros);
                
            case "facturacion":
                return ejecutarFacturacion(parametros);
                
            default:
                return "Error: Tipo de proceso no reconocido: " + tipoProceso;
        }
    }
    
    /**
     * Ejecutar informe de temperaturas máximas y mínimas
     */
    private String ejecutarInformeTemperaturasMaxMin(String parametros) {
        try {
            // Simular procesamiento de datos
            Thread.sleep(2000); // Simular tiempo de procesamiento
            
            StringBuilder resultado = new StringBuilder();
            resultado.append("INFORME DE TEMPERATURAS MÁXIMAS Y MÍNIMAS\n");
            resultado.append("==========================================\n");
            resultado.append("Parámetros: ").append(parametros).append("\n");
            resultado.append("Fecha de generación: ").append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append("\n\n");
            
            // Simular datos de ejemplo
            resultado.append("CIUDAD: Buenos Aires\n");
            resultado.append("Período: 2024-01-01 a 2024-01-31\n");
            resultado.append("Temperatura Máxima: 35.2°C (2024-01-15 14:30)\n");
            resultado.append("Temperatura Mínima: 8.5°C (2024-01-08 06:15)\n");
            resultado.append("Humedad Máxima: 95% (2024-01-20 08:00)\n");
            resultado.append("Humedad Mínima: 45% (2024-01-25 15:45)\n\n");
            
            resultado.append("CIUDAD: Córdoba\n");
            resultado.append("Período: 2024-01-01 a 2024-01-31\n");
            resultado.append("Temperatura Máxima: 38.1°C (2024-01-18 16:00)\n");
            resultado.append("Temperatura Mínima: 5.2°C (2024-01-12 05:30)\n");
            resultado.append("Humedad Máxima: 92% (2024-01-22 07:30)\n");
            resultado.append("Humedad Mínima: 38% (2024-01-28 14:20)\n");
            
            return resultado.toString();
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Error: Proceso interrumpido";
        } catch (Exception e) {
            return "Error ejecutando informe de temperaturas máximas y mínimas: " + e.getMessage();
        }
    }
    
    /**
     * Ejecutar informe de temperaturas promedio
     */
    private String ejecutarInformeTemperaturasPromedio(String parametros) {
        try {
            Thread.sleep(1500);
            
            StringBuilder resultado = new StringBuilder();
            resultado.append("INFORME DE TEMPERATURAS PROMEDIO\n");
            resultado.append("=================================\n");
            resultado.append("Parámetros: ").append(parametros).append("\n");
            resultado.append("Fecha de generación: ").append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append("\n\n");
            
            resultado.append("CIUDAD: Buenos Aires\n");
            resultado.append("Período: 2024-01-01 a 2024-01-31\n");
            resultado.append("Temperatura Promedio: 22.8°C\n");
            resultado.append("Humedad Promedio: 68.5%\n\n");
            
            resultado.append("CIUDAD: Córdoba\n");
            resultado.append("Período: 2024-01-01 a 2024-01-31\n");
            resultado.append("Temperatura Promedio: 24.3°C\n");
            resultado.append("Humedad Promedio: 65.2%\n");
            
            return resultado.toString();
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Error: Proceso interrumpido";
        } catch (Exception e) {
            return "Error ejecutando informe de temperaturas promedio: " + e.getMessage();
        }
    }
    
    /**
     * Ejecutar alertas climáticas
     */
    private String ejecutarAlertasClimaticas(String parametros) {
        try {
            Thread.sleep(1000);
            
            StringBuilder resultado = new StringBuilder();
            resultado.append("ALERTAS CLIMÁTICAS\n");
            resultado.append("==================\n");
            resultado.append("Parámetros: ").append(parametros).append("\n");
            resultado.append("Fecha de generación: ").append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append("\n\n");
            
            resultado.append("ALERTA 1: Temperatura Alta\n");
            resultado.append("Ciudad: Buenos Aires\n");
            resultado.append("Sensor: SENSOR-001\n");
            resultado.append("Temperatura: 36.5°C (Umbral: 35°C)\n");
            resultado.append("Fecha: 2024-01-15 14:30\n");
            resultado.append("Estado: ACTIVA\n\n");
            
            resultado.append("ALERTA 2: Humedad Baja\n");
            resultado.append("Ciudad: Córdoba\n");
            resultado.append("Sensor: SENSOR-002\n");
            resultado.append("Humedad: 25% (Umbral: 30%)\n");
            resultado.append("Fecha: 2024-01-20 10:15\n");
            resultado.append("Estado: ACTIVA\n");
            
            return resultado.toString();
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Error: Proceso interrumpido";
        } catch (Exception e) {
            return "Error ejecutando alertas climáticas: " + e.getMessage();
        }
    }
    
    /**
     * Ejecutar consultas en línea
     */
    private String ejecutarConsultasOnline(String parametros) {
        try {
            Thread.sleep(500);
            
            StringBuilder resultado = new StringBuilder();
            resultado.append("CONSULTAS EN LÍNEA\n");
            resultado.append("==================\n");
            resultado.append("Parámetros: ").append(parametros).append("\n");
            resultado.append("Fecha de consulta: ").append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append("\n\n");
            
            resultado.append("DATOS EN TIEMPO REAL:\n");
            resultado.append("Sensor ID: 1 - Buenos Aires\n");
            resultado.append("Temperatura Actual: 23.5°C\n");
            resultado.append("Humedad Actual: 72%\n");
            resultado.append("Última actualización: ").append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append("\n\n");
            
            resultado.append("Sensor ID: 2 - Córdoba\n");
            resultado.append("Temperatura Actual: 25.8°C\n");
            resultado.append("Humedad Actual: 68%\n");
            resultado.append("Última actualización: ").append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append("\n");
            
            return resultado.toString();
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Error: Proceso interrumpido";
        } catch (Exception e) {
            return "Error ejecutando consultas en línea: " + e.getMessage();
        }
    }
    
    /**
     * Ejecutar procesos periódicos
     */
    private String ejecutarProcesosPeriodicos(String parametros) {
        try {
            Thread.sleep(3000);
            
            StringBuilder resultado = new StringBuilder();
            resultado.append("PROCESOS PERIÓDICOS\n");
            resultado.append("===================\n");
            resultado.append("Parámetros: ").append(parametros).append("\n");
            resultado.append("Fecha de ejecución: ").append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append("\n\n");
            
            resultado.append("PROCESO 1: Resumen Diario\n");
            resultado.append("Período: Últimas 24 horas\n");
            resultado.append("Sensores procesados: 15\n");
            resultado.append("Mediciones procesadas: 1,440\n");
            resultado.append("Alertas generadas: 3\n\n");
            
            resultado.append("PROCESO 2: Resumen Semanal\n");
            resultado.append("Período: Última semana\n");
            resultado.append("Sensores procesados: 15\n");
            resultado.append("Mediciones procesadas: 10,080\n");
            resultado.append("Alertas generadas: 12\n\n");
            
            resultado.append("PROCESO 3: Resumen Mensual\n");
            resultado.append("Período: Último mes\n");
            resultado.append("Sensores procesados: 15\n");
            resultado.append("Mediciones procesadas: 43,200\n");
            resultado.append("Alertas generadas: 45\n");
            
            return resultado.toString();
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Error: Proceso interrumpido";
        } catch (Exception e) {
            return "Error ejecutando procesos periódicos: " + e.getMessage();
        }
    }
    
    /**
     * Ejecutar proceso de facturación
     */
    private String ejecutarFacturacion(String parametros) {
        try {
            Thread.sleep(1000);
            
            StringBuilder resultado = new StringBuilder();
            resultado.append("PROCESO DE FACTURACIÓN\n");
            resultado.append("======================\n");
            resultado.append("Parámetros: ").append(parametros).append("\n");
            resultado.append("Fecha de ejecución: ").append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append("\n\n");
            
            resultado.append("FACTURAS GENERADAS:\n");
            resultado.append("Usuario ID: 1 - Juan Pérez\n");
            resultado.append("Período: 2024-01-01 a 2024-01-31\n");
            resultado.append("Procesos facturados: 5\n");
            resultado.append("Monto total: $250.00\n");
            resultado.append("Estado: Pendiente\n\n");
            
            resultado.append("Usuario ID: 2 - María García\n");
            resultado.append("Período: 2024-01-01 a 2024-01-31\n");
            resultado.append("Procesos facturados: 3\n");
            resultado.append("Monto total: $150.00\n");
            resultado.append("Estado: Pendiente\n");
            
            return resultado.toString();
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Error: Proceso interrumpido";
        } catch (Exception e) {
            return "Error ejecutando proceso de facturación: " + e.getMessage();
        }
    }
    
    /**
     * Obtener solicitudes pendientes
     */
    public List<SolicitudProceso> obtenerSolicitudesPendientes() {
        try {
            return solicitudRepository.findByEstado("pendiente");
        } catch (Exception e) {
            System.err.println("Error obteniendo solicitudes pendientes: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtener solicitudes de un usuario
     */
    public List<SolicitudProceso> obtenerSolicitudesDeUsuario(Integer idUsuario) {
        try {
            return solicitudRepository.findByUsuario(idUsuario);
        } catch (Exception e) {
            System.err.println("Error obteniendo solicitudes de usuario: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtener historial de ejecución de una solicitud
     */
    public List<HistorialCassandraDAO.HistorialData> obtenerHistorialEjecucion(String idSolicitud) {
        try {
            return historialDAO.listBySolicitud(idSolicitud);
        } catch (Exception e) {
            System.err.println("Error obteniendo historial de ejecución: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Cancelar solicitud
     */
    public boolean cancelarSolicitud(String idSolicitud, String motivo) {
        try {
            solicitudRepository.marcarError(idSolicitud, "Cancelado: " + motivo);
            historialDAO.registrar(idSolicitud, java.time.Instant.now(), "Cancelado: " + motivo, "cancelado");
            return true;
        } catch (Exception e) {
            System.err.println("Error cancelando solicitud: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Obtener estadísticas de procesos
     */
    public ProcessStats obtenerEstadisticas() {
        try {
            int totalSolicitudes = solicitudRepository.getCantidadSolicitudesPendientes();
            int totalProcesos = procesoRepository.getCantidadProcesosActivos();
            
            return new ProcessStats(totalSolicitudes, totalProcesos);
            
        } catch (Exception e) {
            System.err.println("Error obteniendo estadísticas de procesos: " + e.getMessage());
            return new ProcessStats(0, 0);
        }
    }
    
    /**
     * Cerrar el servicio
     */
    public void cerrar() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
    
    /**
     * Clase para estadísticas de procesos
     */
    public static class ProcessStats {
        private int solicitudesPendientes;
        private int procesosActivos;
        
        public ProcessStats(int solicitudesPendientes, int procesosActivos) {
            this.solicitudesPendientes = solicitudesPendientes;
            this.procesosActivos = procesosActivos;
        }
        
        public int getSolicitudesPendientes() { return solicitudesPendientes; }
        public int getProcesosActivos() { return procesosActivos; }
        
        @Override
        public String toString() {
            return "ProcessStats{" +
                    "solicitudesPendientes=" + solicitudesPendientes +
                    ", procesosActivos=" + procesosActivos +
                    '}';
        }
    }
}
