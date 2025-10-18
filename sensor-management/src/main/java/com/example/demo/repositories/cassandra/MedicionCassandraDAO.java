package com.example.demo.repositories.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.example.demo.connections.CassandraPool;
import com.example.demo.exceptions.ErrorConectionCassandraException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para operaciones con mediciones en Cassandra
 * Optimizado para consultas por sensor y rango temporal
 */
public class MedicionCassandraDAO {
    
    private static MedicionCassandraDAO instance;
    private PreparedStatement insertStatement;
    private PreparedStatement listBySensorAndRangeStatement;
    private PreparedStatement insertByFechaStatement;
    
    private MedicionCassandraDAO() {
        initializePreparedStatements();
    }
    
    public static MedicionCassandraDAO getInstance() {
        if (instance == null) {
            instance = new MedicionCassandraDAO();
        }
        return instance;
    }
    
    /**
     * Inicializa los PreparedStatements para optimizar las consultas
     */
    private void initializePreparedStatements() {
        try {
            CqlSession session = CassandraPool.getInstancia().getConnection();
            
            // PreparedStatement para INSERT en tabla principal
            String insertCql = "INSERT INTO sensor_data.mediciones " +
                              "(sensor_id, timestamp, temperatura, humedad) " +
                              "VALUES (?, ?, ?, ?)";
            insertStatement = session.prepare(insertCql);
            
            // PreparedStatement para SELECT por sensor y rango
            String listByRangeCql = "SELECT * FROM sensor_data.mediciones " +
                                   "WHERE sensor_id = ? AND timestamp >= ? AND timestamp <= ?";
            listBySensorAndRangeStatement = session.prepare(listByRangeCql);
            
            // PreparedStatement para INSERT en tabla por fecha
            String insertByFechaCql = "INSERT INTO sensor_data.mediciones_por_fecha " +
                                     "(sensor_id, fecha, timestamp, temperatura, humedad) " +
                                     "VALUES (?, ?, ?, ?, ?)";
            insertByFechaStatement = session.prepare(insertByFechaCql);
            
        } catch (ErrorConectionCassandraException e) {
            System.err.println("Error inicializando PreparedStatements: " + e.getMessage());
        }
    }
    
    /**
     * Inserta una nueva medición en Cassandra
     * @param sensorId ID del sensor
     * @param timestamp Timestamp de la medición
     * @param temperatura Valor de temperatura
     * @param humedad Valor de humedad
     * @throws ErrorConectionCassandraException si hay error de conexión
     */
    public void insert(Integer sensorId, Instant timestamp, double temperatura, double humedad) 
            throws ErrorConectionCassandraException {
        try {
            CqlSession session = CassandraPool.getInstancia().getConnection();
            
            // Insertar en tabla principal
            session.execute(insertStatement.bind(
                sensorId,
                timestamp,
                temperatura,
                humedad
            ));
            
            // Insertar también en tabla por fecha para consultas optimizadas
            LocalDate fecha = timestamp.atOffset(ZoneOffset.UTC).toLocalDate();
            session.execute(insertByFechaStatement.bind(
                sensorId,
                fecha,
                timestamp,
                temperatura,
                humedad
            ));
            
            System.out.println("Medición insertada: Sensor " + sensorId + " - " + timestamp + 
                             " - Temp: " + temperatura + " - Hum: " + humedad);
            
        } catch (Exception e) {
            throw new ErrorConectionCassandraException("Error insertando medición: " + e.getMessage());
        }
    }
    
    /**
     * Obtiene mediciones de un sensor en un rango de tiempo específico
     * @param sensorId ID del sensor
     * @param desde Timestamp de inicio
     * @param hasta Timestamp de fin
     * @return Lista de mediciones en el rango especificado
     * @throws ErrorConectionCassandraException si hay error de conexión
     */
    public List<MedicionData> listBySensorAndRange(Integer sensorId, Instant desde, Instant hasta) 
            throws ErrorConectionCassandraException {
        try {
            CqlSession session = CassandraPool.getInstancia().getConnection();
            
            ResultSet resultSet = session.execute(listBySensorAndRangeStatement.bind(
                sensorId, desde, hasta));
            
            List<MedicionData> mediciones = new ArrayList<>();
            
            for (Row row : resultSet) {
                MedicionData medicion = new MedicionData();
                medicion.setSensorId(row.getInt("sensor_id"));
                medicion.setTimestamp(row.getInstant("timestamp"));
                medicion.setTemperatura(row.getDouble("temperatura"));
                medicion.setHumedad(row.getDouble("humedad"));
                mediciones.add(medicion);
            }
            
            System.out.println("Consultadas " + mediciones.size() + " mediciones para sensor " + sensorId);
            return mediciones;
            
        } catch (Exception e) {
            throw new ErrorConectionCassandraException("Error consultando mediciones por rango: " + e.getMessage());
        }
    }
    
    /**
     * Obtiene mediciones de un sensor para una fecha específica
     * @param sensorId ID del sensor
     * @param fecha Fecha específica
     * @return Lista de mediciones para esa fecha
     * @throws ErrorConectionCassandraException si hay error de conexión
     */
    public List<MedicionData> listBySensorAndDate(Integer sensorId, LocalDate fecha) 
            throws ErrorConectionCassandraException {
        try {
            CqlSession session = CassandraPool.getInstancia().getConnection();
            
            String cql = "SELECT * FROM sensor_data.mediciones_por_fecha " +
                        "WHERE sensor_id = ? AND fecha = ?";
            PreparedStatement statement = session.prepare(cql);
            
            ResultSet resultSet = session.execute(statement.bind(sensorId, fecha));
            
            List<MedicionData> mediciones = new ArrayList<>();
            
            for (Row row : resultSet) {
                MedicionData medicion = new MedicionData();
                medicion.setSensorId(row.getInt("sensor_id"));
                medicion.setTimestamp(row.getInstant("timestamp"));
                medicion.setTemperatura(row.getDouble("temperatura"));
                medicion.setHumedad(row.getDouble("humedad"));
                mediciones.add(medicion);
            }
            
            return mediciones;
            
        } catch (Exception e) {
            throw new ErrorConectionCassandraException("Error consultando mediciones por fecha: " + e.getMessage());
        }
    }
    
    /**
     * Obtiene mediciones por ciudad y fecha
     * @param ciudad Nombre de la ciudad
     * @param fecha Fecha específica
     * @return Lista de mediciones para esa ciudad y fecha
     * @throws ErrorConectionCassandraException si hay error de conexión
     */
    public List<MedicionData> listByCiudadAndDate(String ciudad, LocalDate fecha) 
            throws ErrorConectionCassandraException {
        try {
            CqlSession session = CassandraPool.getInstancia().getConnection();
            
            String cql = "SELECT * FROM sensor_data.mediciones_por_ciudad " +
                        "WHERE ciudad = ? AND fecha = ?";
            PreparedStatement statement = session.prepare(cql);
            
            ResultSet resultSet = session.execute(statement.bind(ciudad, fecha));
            
            List<MedicionData> mediciones = new ArrayList<>();
            
            for (Row row : resultSet) {
                MedicionData medicion = new MedicionData();
                medicion.setSensorId(row.getInt("sensor_id"));
                medicion.setTimestamp(row.getInstant("timestamp"));
                medicion.setTemperatura(row.getDouble("temperatura"));
                medicion.setHumedad(row.getDouble("humedad"));
                mediciones.add(medicion);
            }
            
            return mediciones;
            
        } catch (Exception e) {
            throw new ErrorConectionCassandraException("Error consultando mediciones por ciudad: " + e.getMessage());
        }
    }
    
    /**
     * Obtiene mediciones por país y fecha
     * @param pais Nombre del país
     * @param fecha Fecha específica
     * @return Lista de mediciones para ese país y fecha
     * @throws ErrorConectionCassandraException si hay error de conexión
     */
    public List<MedicionData> listByPaisAndDate(String pais, LocalDate fecha) 
            throws ErrorConectionCassandraException {
        try {
            CqlSession session = CassandraPool.getInstancia().getConnection();
            
            String cql = "SELECT * FROM sensor_data.mediciones_por_pais " +
                        "WHERE pais = ? AND fecha = ?";
            PreparedStatement statement = session.prepare(cql);
            
            ResultSet resultSet = session.execute(statement.bind(pais, fecha));
            
            List<MedicionData> mediciones = new ArrayList<>();
            
            for (Row row : resultSet) {
                MedicionData medicion = new MedicionData();
                medicion.setSensorId(row.getInt("sensor_id"));
                medicion.setTimestamp(row.getInstant("timestamp"));
                medicion.setTemperatura(row.getDouble("temperatura"));
                medicion.setHumedad(row.getDouble("humedad"));
                mediciones.add(medicion);
            }
            
            return mediciones;
            
        } catch (Exception e) {
            throw new ErrorConectionCassandraException("Error consultando mediciones por país: " + e.getMessage());
        }
    }
    
    /**
     * Clase interna para representar datos de medición
     */
    public static class MedicionData {
        private Integer sensorId;
        private Instant timestamp;
        private Double temperatura;
        private Double humedad;
        
        // Constructores
        public MedicionData() {}
        
        public MedicionData(Integer sensorId, Instant timestamp, Double temperatura, Double humedad) {
            this.sensorId = sensorId;
            this.timestamp = timestamp;
            this.temperatura = temperatura;
            this.humedad = humedad;
        }
        
        // Getters y Setters
        public Integer getSensorId() { return sensorId; }
        public void setSensorId(Integer sensorId) { this.sensorId = sensorId; }
        
        public Instant getTimestamp() { return timestamp; }
        public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
        
        public Double getTemperatura() { return temperatura; }
        public void setTemperatura(Double temperatura) { this.temperatura = temperatura; }
        
        public Double getHumedad() { return humedad; }
        public void setHumedad(Double humedad) { this.humedad = humedad; }
        
        @Override
        public String toString() {
            return "MedicionData{" +
                    "sensorId=" + sensorId +
                    ", timestamp=" + timestamp +
                    ", temperatura=" + temperatura +
                    ", humedad=" + humedad +
                    '}';
        }
    }
}
