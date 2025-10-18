package com.example.demo.repositories.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.example.demo.connections.CassandraPool;
import com.example.demo.exceptions.ErrorConectionCassandraException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Inicializador del esquema de Cassandra
 * Lee y ejecuta el archivo CQL con las definiciones de tablas
 */
public class SchemaInitializer {
    
    private static SchemaInitializer instance;
    
    private SchemaInitializer() {}
    
    public static SchemaInitializer getInstance() {
        if (instance == null) {
            instance = new SchemaInitializer();
        }
        return instance;
    }
    
    /**
     * Aplica el esquema de Cassandra leyendo el archivo CQL
     * @throws ErrorConectionCassandraException si hay error de conexión o ejecución
     */
    public void apply() throws ErrorConectionCassandraException {
        try {
            CqlSession session = CassandraPool.getInstancia().getConnection();
            
            // Leer el archivo CQL desde resources
            InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("cql/cassandra_schema.cql");
            
            if (inputStream == null) {
                throw new ErrorConectionCassandraException("No se pudo encontrar el archivo cassandra_schema.cql");
            }
            
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            
            StringBuilder cqlScript = new StringBuilder();
            String line;
            
            // Leer todo el archivo
            while ((line = reader.readLine()) != null) {
                cqlScript.append(line).append("\n");
            }
            
            reader.close();
            inputStream.close();
            
            // Dividir por comandos (separados por punto y coma)
            String[] commands = cqlScript.toString().split(";");
            
            // Ejecutar cada comando
            for (String command : commands) {
                command = command.trim();
                if (!command.isEmpty() && !command.startsWith("--")) {
                    try {
                        session.execute(command);
                        System.out.println("Comando ejecutado: " + command.substring(0, Math.min(50, command.length())) + "...");
                    } catch (Exception e) {
                        System.err.println("Error ejecutando comando: " + command);
                        System.err.println("Error: " + e.getMessage());
                        // Continuar con el siguiente comando
                    }
                }
            }
            
            System.out.println("Esquema de Cassandra aplicado exitosamente");
            
        } catch (IOException e) {
            throw new ErrorConectionCassandraException("Error leyendo archivo CQL: " + e.getMessage());
        } catch (Exception e) {
            throw new ErrorConectionCassandraException("Error aplicando esquema: " + e.getMessage());
        }
    }
    
    /**
     * Verifica si el esquema ya está aplicado
     * @return true si las tablas principales existen
     */
    public boolean isSchemaApplied() {
        try {
            CqlSession session = CassandraPool.getInstancia().getConnection();
            
            // Verificar si existe la tabla sensores
            String query = "SELECT table_name FROM system_schema.tables " +
                         "WHERE keyspace_name = 'sensor_data' AND table_name = 'sensores'";
            
            var result = session.execute(query);
            return result.one() != null;
            
        } catch (Exception e) {
            System.err.println("Error verificando esquema: " + e.getMessage());
            return false;
        }
    }
}
