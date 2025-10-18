package connections;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import exceptions.ErrorConectionMySQLException;

public class MySQLPool {
    
    private static MySQLPool instancia;
    private static final String URL = "jdbc:mysql://localhost:3306/sensor_management";
    private static final String USER = "root";
    private static final String PASSWORD = "tu_password";
    
    private MySQLPool() {
        try {
            // Cargar el driver de MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Error al cargar driver MySQL: " + e.getMessage());
        }
    }
    
    public static MySQLPool getInstancia() {
        if (instancia == null) {
            instancia = new MySQLPool();
        }
        return instancia;
    }
    
    public Connection getConnection() throws ErrorConectionMySQLException {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            return conn;
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error al conectar con MySQL: " + e.getMessage());
        }
    }
}