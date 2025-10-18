package com.example.demo.repositories.mysql;

import com.example.demo.connections.MySQLPool;
import com.example.demo.exceptions.ErrorConectionMySQLException;
import com.example.demo.modelo.Proceso;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProcesoMySQLRepository {
    private static ProcesoMySQLRepository instance;
    
    private ProcesoMySQLRepository() {}
    
    public static ProcesoMySQLRepository getInstance() {
        if (instance == null) instance = new ProcesoMySQLRepository();
        return instance;
    }
    
    public void crear(Proceso proceso) throws ErrorConectionMySQLException {
        String sql = "INSERT INTO procesos (nombre, descripcion, tipo_proceso, costo, parametros_requeridos, estado, tiempo_estimado_minutos) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, proceso.getNombre());
            stmt.setString(2, proceso.getDescripcion());
            stmt.setString(3, proceso.getTipoProceso());
            stmt.setBigDecimal(4, proceso.getCosto());
            stmt.setString(5, proceso.getParametrosRequeridos());
            stmt.setString(6, proceso.getEstado());
            stmt.setInt(7, proceso.getTiempoEstimadoMinutos());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        proceso.setIdProceso(generatedKeys.getInt(1));
                        System.out.println("Proceso creado con ID: " + proceso.getIdProceso());
                    }
                }
            }
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error creando proceso: " + e.getMessage());
        }
    }
    
    public Proceso findById(Integer idProceso) throws ErrorConectionMySQLException {
        String sql = "SELECT * FROM procesos WHERE id_proceso = ?";
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idProceso);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProceso(rs);
                }
            }
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error buscando proceso por ID: " + e.getMessage());
        }
        
        return null;
    }
    
    public List<Proceso> findAll() throws ErrorConectionMySQLException {
        String sql = "SELECT * FROM procesos ORDER BY nombre ASC";
        List<Proceso> procesos = new ArrayList<>();
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                procesos.add(mapResultSetToProceso(rs));
            }
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error listando procesos: " + e.getMessage());
        }
        
        return procesos;
    }
    
    public List<Proceso> findByTipo(String tipoProceso) throws ErrorConectionMySQLException {
        String sql = "SELECT * FROM procesos WHERE tipo_proceso = ? ORDER BY nombre ASC";
        List<Proceso> procesos = new ArrayList<>();
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, tipoProceso);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    procesos.add(mapResultSetToProceso(rs));
                }
            }
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error buscando procesos por tipo: " + e.getMessage());
        }
        
        return procesos;
    }
    
    public List<Proceso> findByEstado(String estado) throws ErrorConectionMySQLException {
        String sql = "SELECT * FROM procesos WHERE estado = ? ORDER BY nombre ASC";
        List<Proceso> procesos = new ArrayList<>();
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, estado);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    procesos.add(mapResultSetToProceso(rs));
                }
            }
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error buscando procesos por estado: " + e.getMessage());
        }
        
        return procesos;
    }
    
    public void actualizarEstado(Integer idProceso, String estado) throws ErrorConectionMySQLException {
        String sql = "UPDATE procesos SET estado = ? WHERE id_proceso = ?";
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, estado);
            stmt.setInt(2, idProceso);
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error actualizando estado de proceso: " + e.getMessage());
        }
    }
    
    public void actualizarCosto(Integer idProceso, BigDecimal nuevoCosto) throws ErrorConectionMySQLException {
        String sql = "UPDATE procesos SET costo = ? WHERE id_proceso = ?";
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBigDecimal(1, nuevoCosto);
            stmt.setInt(2, idProceso);
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error actualizando costo de proceso: " + e.getMessage());
        }
    }
    
    public BigDecimal getCostoPromedio() throws ErrorConectionMySQLException {
        String sql = "SELECT AVG(costo) as costo_promedio FROM procesos WHERE estado = 'activo'";
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                BigDecimal promedio = rs.getBigDecimal("costo_promedio");
                return promedio != null ? promedio : BigDecimal.ZERO;
            }
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error calculando costo promedio: " + e.getMessage());
        }
        
        return BigDecimal.ZERO;
    }
    
    public int getCantidadProcesosActivos() throws ErrorConectionMySQLException {
        String sql = "SELECT COUNT(*) as total FROM procesos WHERE estado = 'activo'";
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt("total");
            }
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error contando procesos activos: " + e.getMessage());
        }
        
        return 0;
    }
    
    private Proceso mapResultSetToProceso(ResultSet rs) throws SQLException {
        Proceso proceso = new Proceso();
        proceso.setIdProceso(rs.getInt("id_proceso"));
        proceso.setNombre(rs.getString("nombre"));
        proceso.setDescripcion(rs.getString("descripcion"));
        proceso.setTipoProceso(rs.getString("tipo_proceso"));
        proceso.setCosto(rs.getBigDecimal("costo"));
        proceso.setParametrosRequeridos(rs.getString("parametros_requeridos"));
        proceso.setEstado(rs.getString("estado"));
        proceso.setTiempoEstimadoMinutos(rs.getInt("tiempo_estimado_minutos"));
        return proceso;
    }
}