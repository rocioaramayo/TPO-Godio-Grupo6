package com.example.demo.repositories.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.example.demo.connections.CassandraPool;
import com.example.demo.exceptions.ErrorConectionCassandraException;
import com.example.demo.modelo.Sensor;

import java.time.Instant;
import java.util.Date;

/**
 * DAO para operaciones con sensores en Cassandra
 * Implementa patrón Singleton con PreparedStatement para optimización
 */
public class SensorCassandraDAO {
    
    private static SensorCassandraDAO instance;
    private PreparedStatement upsertStatement;
    private PreparedStatement getByIdStatement;
    
    private SensorCassandraDAO() {
        initializePreparedStatements();
    }
    
    public static SensorCassandraDAO getInstance() {
        if (instance == null) {
            instance = new SensorCassandraDAO();
        }
        return instance;
    }
    
    /**
     * Inicializa los PreparedStatements para optimizar las consultas
     */
    private void initializePreparedStatements() {
        try {
            CqlSession session = CassandraPool.getInstancia().getConnection();
            
            // PreparedStatement para UPSERT
            String upsertCql = "INSERT INTO sensor_data.sensores " +
                              "(id_sensor, nombre, tipo_sensor, latitud, longitud, ciudad, pais, estado_sensor, fecha_inicio_emision) " +
                              "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            upsertStatement = session.prepare(upsertCql);
            
            // PreparedStatement para SELECT por ID
            String getByIdCql = "SELECT * FROM sensor_data.sensores WHERE id_sensor = ?";
            getByIdStatement = session.prepare(getByIdCql);
            
        } catch (ErrorConectionCassandraException e) {
            System.err.println("Error inicializando PreparedStatements: " + e.getMessage());
        }
    }
    
    /**
     * Inserta o actualiza un sensor en Cassandra
     * @param sensor El sensor a insertar/actualizar
     * @throws ErrorConectionCassandraException si hay error de conexión
     */
    public void upsert(Sensor sensor) throws ErrorConectionCassandraException {
        try {
            CqlSession session = CassandraPool.getInstancia().getConnection();
            
            // Convertir Date a Instant para Cassandra
            Instant fechaInicio = sensor.getFechaInicioEmision() != null ? 
                sensor.getFechaInicioEmision().toInstant() : Instant.now();
            
            session.execute(upsertStatement.bind(
                sensor.getIdSensor(),
                sensor.getNombre(),
                sensor.getTipoSensor(),
                sensor.getLatitud(),
                sensor.getLongitud(),
                sensor.getCiudad(),
                sensor.getPais(),
                sensor.getEstadoSensor(),
                fechaInicio
            ));
            
            System.out.println("Sensor upserted: " + sensor.getIdSensor() + " - " + sensor.getNombre());
            
        } catch (Exception e) {
            throw new ErrorConectionCassandraException("Error en upsert de sensor: " + e.getMessage());
        }
    }
    
    /**
     * Obtiene un sensor por su ID
     * @param sensorId ID del sensor a buscar
     * @return Sensor encontrado o null si no existe
     * @throws ErrorConectionCassandraException si hay error de conexión
     */
    public Sensor getById(Integer sensorId) throws ErrorConectionCassandraException {
        try {
            CqlSession session = CassandraPool.getInstancia().getConnection();
            
            ResultSet resultSet = session.execute(getByIdStatement.bind(sensorId));
            Row row = resultSet.one();
            
            if (row == null) {
                return null;
            }
            
            // Mapear Row a objeto Sensor
            Sensor sensor = new Sensor();
            sensor.setIdSensor(row.getInt("id_sensor"));
            sensor.setNombre(row.getString("nombre"));
            sensor.setTipoSensor(row.getString("tipo_sensor"));
            sensor.setLatitud(row.getDouble("latitud"));
            sensor.setLongitud(row.getDouble("longitud"));
            sensor.setCiudad(row.getString("ciudad"));
            sensor.setPais(row.getString("pais"));
            sensor.setEstadoSensor(row.getString("estado_sensor"));
            
            // Convertir Instant a Date
            Instant fechaInicio = row.getInstant("fecha_inicio_emision");
            if (fechaInicio != null) {
                sensor.setFechaInicioEmision(Date.from(fechaInicio));
            }
            
            return sensor;
            
        } catch (Exception e) {
            throw new ErrorConectionCassandraException("Error obteniendo sensor por ID: " + e.getMessage());
        }
    }
    
    /**
     * Obtiene todos los sensores de una ciudad específica
     * @param ciudad Nombre de la ciudad
     * @return Lista de sensores en esa ciudad
     * @throws ErrorConectionCassandraException si hay error de conexión
     */
    public java.util.List<Sensor> getByCiudad(String ciudad) throws ErrorConectionCassandraException {
        try {
            CqlSession session = CassandraPool.getInstancia().getConnection();
            
            String cql = "SELECT * FROM sensor_data.sensores WHERE ciudad = ? ALLOW FILTERING";
            PreparedStatement statement = session.prepare(cql);
            
            ResultSet resultSet = session.execute(statement.bind(ciudad));
            java.util.List<Sensor> sensores = new java.util.ArrayList<>();
            
            for (Row row : resultSet) {
                Sensor sensor = mapRowToSensor(row);
                sensores.add(sensor);
            }
            
            return sensores;
            
        } catch (Exception e) {
            throw new ErrorConectionCassandraException("Error obteniendo sensores por ciudad: " + e.getMessage());
        }
    }
    
    /**
     * Obtiene todos los sensores de un país específico
     * @param pais Nombre del país
     * @return Lista de sensores en ese país
     * @throws ErrorConectionCassandraException si hay error de conexión
     */
    public java.util.List<Sensor> getByPais(String pais) throws ErrorConectionCassandraException {
        try {
            CqlSession session = CassandraPool.getInstancia().getConnection();
            
            String cql = "SELECT * FROM sensor_data.sensores WHERE pais = ? ALLOW FILTERING";
            PreparedStatement statement = session.prepare(cql);
            
            ResultSet resultSet = session.execute(statement.bind(pais));
            java.util.List<Sensor> sensores = new java.util.ArrayList<>();
            
            for (Row row : resultSet) {
                Sensor sensor = mapRowToSensor(row);
                sensores.add(sensor);
            }
            
            return sensores;
            
        } catch (Exception e) {
            throw new ErrorConectionCassandraException("Error obteniendo sensores por país: " + e.getMessage());
        }
    }
    
    /**
     * Mapea una Row de Cassandra a un objeto Sensor
     * @param row Row de Cassandra
     * @return Objeto Sensor mapeado
     */
    private Sensor mapRowToSensor(Row row) {
        Sensor sensor = new Sensor();
        sensor.setIdSensor(row.getInt("id_sensor"));
        sensor.setNombre(row.getString("nombre"));
        sensor.setTipoSensor(row.getString("tipo_sensor"));
        sensor.setLatitud(row.getDouble("latitud"));
        sensor.setLongitud(row.getDouble("longitud"));
        sensor.setCiudad(row.getString("ciudad"));
        sensor.setPais(row.getString("pais"));
        sensor.setEstadoSensor(row.getString("estado_sensor"));
        
        // Convertir Instant a Date
        Instant fechaInicio = row.getInstant("fecha_inicio_emision");
        if (fechaInicio != null) {
            sensor.setFechaInicioEmision(Date.from(fechaInicio));
        }
        
        return sensor;
    }
}
