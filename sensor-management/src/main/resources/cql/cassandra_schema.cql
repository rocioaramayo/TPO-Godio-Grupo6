-- Esquema de Cassandra para Sistema de Gestión de Sensores
-- Ingeniería de Datos II - TP Persistencia Poliglota

-- Crear keyspace si no existe
CREATE KEYSPACE IF NOT EXISTS sensor_data 
WITH REPLICATION = {
    'class': 'SimpleStrategy',
    'replication_factor': 1
};

-- Usar el keyspace
USE sensor_data;

-- Tabla de sensores
-- Optimizada para consultas por ID de sensor
CREATE TABLE IF NOT EXISTS sensores (
    id_sensor INT PRIMARY KEY,
    nombre TEXT,
    tipo_sensor TEXT,
    latitud DOUBLE,
    longitud DOUBLE,
    ciudad TEXT,
    pais TEXT,
    estado_sensor TEXT,
    fecha_inicio_emision TIMESTAMP
);

-- Tabla de mediciones
-- Particionada por sensor_id para distribuir datos
-- Clustering por timestamp para consultas temporales eficientes
CREATE TABLE IF NOT EXISTS mediciones (
    sensor_id INT,
    timestamp TIMESTAMP,
    temperatura DOUBLE,
    humedad DOUBLE,
    PRIMARY KEY (sensor_id, timestamp)
) WITH CLUSTERING ORDER BY (timestamp DESC);

-- Tabla de historial de ejecución de procesos
-- Particionada por solicitud_id para agrupar ejecuciones
CREATE TABLE IF NOT EXISTS historial_ejecucion (
    solicitud_id TEXT,
    timestamp_ejecucion TIMESTAMP,
    resultado TEXT,
    estado TEXT,
    PRIMARY KEY (solicitud_id, timestamp_ejecucion)
) WITH CLUSTERING ORDER BY (timestamp_ejecucion DESC);

-- Índices secundarios para consultas adicionales
CREATE INDEX IF NOT EXISTS idx_sensores_ciudad ON sensores (ciudad);
CREATE INDEX IF NOT EXISTS idx_sensores_pais ON sensores (pais);
CREATE INDEX IF NOT EXISTS idx_sensores_estado ON sensores (estado_sensor);

-- Tabla para consultas por rango de fechas en mediciones
-- Permite consultas eficientes por sensor y rango temporal
CREATE TABLE IF NOT EXISTS mediciones_por_fecha (
    sensor_id INT,
    fecha DATE,
    timestamp TIMESTAMP,
    temperatura DOUBLE,
    humedad DOUBLE,
    PRIMARY KEY ((sensor_id, fecha), timestamp)
) WITH CLUSTERING ORDER BY (timestamp DESC);

-- Tabla para agregaciones por ciudad
-- Optimizada para reportes por ciudad
CREATE TABLE IF NOT EXISTS mediciones_por_ciudad (
    ciudad TEXT,
    fecha DATE,
    timestamp TIMESTAMP,
    sensor_id INT,
    temperatura DOUBLE,
    humedad DOUBLE,
    PRIMARY KEY ((ciudad, fecha), timestamp, sensor_id)
) WITH CLUSTERING ORDER BY (timestamp DESC, sensor_id ASC);

-- Tabla para agregaciones por país
-- Optimizada para reportes por país
CREATE TABLE IF NOT EXISTS mediciones_por_pais (
    pais TEXT,
    fecha DATE,
    timestamp TIMESTAMP,
    sensor_id INT,
    temperatura DOUBLE,
    humedad DOUBLE,
    PRIMARY KEY ((pais, fecha), timestamp, sensor_id)
) WITH CLUSTERING ORDER BY (timestamp DESC, sensor_id ASC);
