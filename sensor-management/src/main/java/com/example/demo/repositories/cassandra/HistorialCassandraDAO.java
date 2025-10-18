package com.example.demo.repositories.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.example.demo.connections.CassandraPool;
import com.example.demo.exceptions.ErrorConectionCassandraException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para operaciones con historial de ejecución de procesos en Cassandra
 * Registra el historial de ejecución de procesos solicitados por usuarios
 */
public class HistorialCassandraDAO {
    
    private static HistorialCassandraDAO instance;
    private PreparedStatement registrarStatement;
    private PreparedStatement listBySolicitudStatement;
    private PreparedStatement listByEstadoStatement;
    
    private HistorialCassandraDAO() {
        initializePreparedStatements();
    }
    
    public static HistorialCassandraDAO getInstance() {
        if (instance == null) {
            instance = new HistorialCassandraDAO();
        }
        return instance;
    }
    
    /**
     * Inicializa los PreparedStatements para optimizar las consultas
     */
    private void initializePreparedStatements() {
        try {
            CqlSession session = CassandraPool.getInstancia().getConnection();
            
            // PreparedStatement para INSERT de registro de ejecución
            String registrarCql = "INSERT INTO sensor_data.historial_ejecucion " +
                                 "(solicitud_id, timestamp_ejecucion, resultado, estado) " +
                                 "VALUES (?, ?, ?, ?)";
            registrarStatement = session.prepare(registrarCql);
            
            // PreparedStatement para SELECT por solicitud
            String listBySolicitudCql = "SELECT * FROM sensor_data.historial_ejecucion " +
                                       "WHERE solicitud_id = ?";
            listBySolicitudStatement = session.prepare(listBySolicitudCql);
            
            // PreparedStatement para SELECT por estado (requiere ALLOW FILTERING)
            String listByEstadoCql = "SELECT * FROM sensor_data.historial_ejecucion " +
                                    "WHERE estado = ? ALLOW FILTERING";
            listByEstadoStatement = session.prepare(listByEstadoCql);
            
        } catch (ErrorConectionCassandraException e) {
            System.err.println("Error inicializando PreparedStatements: " + e.getMessage());
        }
    }
    
    /**
     * Registra una nueva ejecución en el historial
     * @param solicitudId ID de la solicitud de proceso
     * @param timestamp Timestamp de la ejecución
     * @param resultado Resultado de la ejecución
     * @param estado Estado de la ejecución (completado, error, etc.)
     * @throws ErrorConectionCassandraException si hay error de conexión
     */
    public void registrar(String solicitudId, Instant timestamp, String resultado, String estado) 
            throws ErrorConectionCassandraException {
        try {
            CqlSession session = CassandraPool.getInstancia().getConnection();
            
            session.execute(registrarStatement.bind(
                solicitudId,
                timestamp,
                resultado,
                estado
            ));
            
            System.out.println("Historial registrado: Solicitud " + solicitudId + 
                             " - Estado: " + estado + " - " + timestamp);
            
        } catch (Exception e) {
            throw new ErrorConectionCassandraException("Error registrando historial: " + e.getMessage());
        }
    }
    
    /**
     * Obtiene el historial de ejecución de una solicitud específica
     * @param solicitudId ID de la solicitud
     * @return Lista de ejecuciones para esa solicitud
     * @throws ErrorConectionCassandraException si hay error de conexión
     */
    public List<HistorialData> listBySolicitud(String solicitudId) 
            throws ErrorConectionCassandraException {
        try {
            CqlSession session = CassandraPool.getInstancia().getConnection();
            
            ResultSet resultSet = session.execute(listBySolicitudStatement.bind(solicitudId));
            
            List<HistorialData> historial = new ArrayList<>();
            
            for (Row row : resultSet) {
                HistorialData data = new HistorialData();
                data.setSolicitudId(row.getString("solicitud_id"));
                data.setTimestampEjecucion(row.getInstant("timestamp_ejecucion"));
                data.setResultado(row.getString("resultado"));
                data.setEstado(row.getString("estado"));
                historial.add(data);
            }
            
            System.out.println("Consultadas " + historial.size() + " ejecuciones para solicitud " + solicitudId);
            return historial;
            
        } catch (Exception e) {
            throw new ErrorConectionCassandraException("Error consultando historial por solicitud: " + e.getMessage());
        }
    }
    
    /**
     * Obtiene el historial de ejecuciones por estado
     * @param estado Estado a filtrar (completado, error, pendiente, etc.)
     * @return Lista de ejecuciones con ese estado
     * @throws ErrorConectionCassandraException si hay error de conexión
     */
    public List<HistorialData> listByEstado(String estado) 
            throws ErrorConectionCassandraException {
        try {
            CqlSession session = CassandraPool.getInstancia().getConnection();
            
            ResultSet resultSet = session.execute(listByEstadoStatement.bind(estado));
            
            List<HistorialData> historial = new ArrayList<>();
            
            for (Row row : resultSet) {
                HistorialData data = new HistorialData();
                data.setSolicitudId(row.getString("solicitud_id"));
                data.setTimestampEjecucion(row.getInstant("timestamp_ejecucion"));
                data.setResultado(row.getString("resultado"));
                data.setEstado(row.getString("estado"));
                historial.add(data);
            }
            
            System.out.println("Consultadas " + historial.size() + " ejecuciones con estado " + estado);
            return historial;
            
        } catch (Exception e) {
            throw new ErrorConectionCassandraException("Error consultando historial por estado: " + e.getMessage());
        }
    }
    
    /**
     * Obtiene el historial de ejecuciones en un rango de tiempo
     * @param desde Timestamp de inicio
     * @param hasta Timestamp de fin
     * @return Lista de ejecuciones en el rango especificado
     * @throws ErrorConectionCassandraException si hay error de conexión
     */
    public List<HistorialData> listByRangoTiempo(Instant desde, Instant hasta) 
            throws ErrorConectionCassandraException {
        try {
            CqlSession session = CassandraPool.getInstancia().getConnection();
            
            String cql = "SELECT * FROM sensor_data.historial_ejecucion " +
                        "WHERE timestamp_ejecucion >= ? AND timestamp_ejecucion <= ? ALLOW FILTERING";
            PreparedStatement statement = session.prepare(cql);
            
            ResultSet resultSet = session.execute(statement.bind(desde, hasta));
            
            List<HistorialData> historial = new ArrayList<>();
            
            for (Row row : resultSet) {
                HistorialData data = new HistorialData();
                data.setSolicitudId(row.getString("solicitud_id"));
                data.setTimestampEjecucion(row.getInstant("timestamp_ejecucion"));
                data.setResultado(row.getString("resultado"));
                data.setEstado(row.getString("estado"));
                historial.add(data);
            }
            
            System.out.println("Consultadas " + historial.size() + " ejecuciones en rango temporal");
            return historial;
            
        } catch (Exception e) {
            throw new ErrorConectionCassandraException("Error consultando historial por rango temporal: " + e.getMessage());
        }
    }
    
    /**
     * Obtiene estadísticas de ejecución por estado
     * @return Mapa con conteos por estado
     * @throws ErrorConectionCassandraException si hay error de conexión
     */
    public java.util.Map<String, Integer> getEstadisticasPorEstado() 
            throws ErrorConectionCassandraException {
        try {
            CqlSession session = CassandraPool.getInstancia().getConnection();
            
            String cql = "SELECT estado, COUNT(*) as total FROM sensor_data.historial_ejecucion " +
                        "GROUP BY estado ALLOW FILTERING";
            PreparedStatement statement = session.prepare(cql);
            
            ResultSet resultSet = session.execute(statement.bind());
            
            java.util.Map<String, Integer> estadisticas = new java.util.HashMap<>();
            
            for (Row row : resultSet) {
                String estado = row.getString("estado");
                Long total = row.getLong("total");
                estadisticas.put(estado, total.intValue());
            }
            
            return estadisticas;
            
        } catch (Exception e) {
            throw new ErrorConectionCassandraException("Error obteniendo estadísticas por estado: " + e.getMessage());
        }
    }
    
    /**
     * Clase interna para representar datos de historial de ejecución
     */
    public static class HistorialData {
        private String solicitudId;
        private Instant timestampEjecucion;
        private String resultado;
        private String estado;
        
        // Constructores
        public HistorialData() {}
        
        public HistorialData(String solicitudId, Instant timestampEjecucion, String resultado, String estado) {
            this.solicitudId = solicitudId;
            this.timestampEjecucion = timestampEjecucion;
            this.resultado = resultado;
            this.estado = estado;
        }
        
        // Getters y Setters
        public String getSolicitudId() { return solicitudId; }
        public void setSolicitudId(String solicitudId) { this.solicitudId = solicitudId; }
        
        public Instant getTimestampEjecucion() { return timestampEjecucion; }
        public void setTimestampEjecucion(Instant timestampEjecucion) { this.timestampEjecucion = timestampEjecucion; }
        
        public String getResultado() { return resultado; }
        public void setResultado(String resultado) { this.resultado = resultado; }
        
        public String getEstado() { return estado; }
        public void setEstado(String estado) { this.estado = estado; }
        
        @Override
        public String toString() {
            return "HistorialData{" +
                    "solicitudId='" + solicitudId + '\'' +
                    ", timestampEjecucion=" + timestampEjecucion +
                    ", resultado='" + resultado + '\'' +
                    ", estado='" + estado + '\'' +
                    '}';
        }
    }
}
