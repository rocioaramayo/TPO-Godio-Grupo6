package com.example.demo.repositories.mysql;

import com.example.demo.connections.MySQLPool;
import com.example.demo.exceptions.ErrorConectionMySQLException;
import com.example.demo.modelo.Rol;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RolMySQLRepository {
    private static RolMySQLRepository instance;
    
    private RolMySQLRepository() {}
    
    public static RolMySQLRepository getInstance() {
        if (instance == null) instance = new RolMySQLRepository();
        return instance;
    }
    
    public void crear(Rol rol) throws ErrorConectionMySQLException {
        String sql = "INSERT INTO roles (descripcion, permisos, estado) VALUES (?, ?, ?)";
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, rol.getDescripcion());
            stmt.setString(2, rol.getPermisos());
            stmt.setString(3, rol.getEstado());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        rol.setIdRol(generatedKeys.getInt(1));
                        System.out.println("Rol creado con ID: " + rol.getIdRol());
                    }
                }
            }
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error creando rol: " + e.getMessage());
        }
    }
    
    public Rol findById(Integer idRol) throws ErrorConectionMySQLException {
        String sql = "SELECT * FROM roles WHERE id_rol = ?";
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idRol);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRol(rs);
                }
            }
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error buscando rol por ID: " + e.getMessage());
        }
        
        return null;
    }
    
    public Rol findByDescripcion(String descripcion) throws ErrorConectionMySQLException {
        String sql = "SELECT * FROM roles WHERE descripcion = ?";
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, descripcion);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRol(rs);
                }
            }
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error buscando rol por descripción: " + e.getMessage());
        }
        
        return null;
    }
    
    public Rol findByUsuario(Integer idUsuario) throws ErrorConectionMySQLException {
        String sql = "SELECT r.* FROM roles r " +
                    "INNER JOIN usuario_roles ur ON r.id_rol = ur.id_rol " +
                    "WHERE ur.id_usuario = ?";
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idUsuario);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRol(rs);
                }
            }
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error buscando rol por usuario: " + e.getMessage());
        }
        
        return null;
    }
    
    public List<Rol> findAll() throws ErrorConectionMySQLException {
        String sql = "SELECT * FROM roles ORDER BY descripcion ASC";
        List<Rol> roles = new ArrayList<>();
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                roles.add(mapResultSetToRol(rs));
            }
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error listando roles: " + e.getMessage());
        }
        
        return roles;
    }
    
    public List<Rol> findByEstado(String estado) throws ErrorConectionMySQLException {
        String sql = "SELECT * FROM roles WHERE estado = ? ORDER BY descripcion ASC";
        List<Rol> roles = new ArrayList<>();
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, estado);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    roles.add(mapResultSetToRol(rs));
                }
            }
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error buscando roles por estado: " + e.getMessage());
        }
        
        return roles;
    }
    
    public void actualizarPermisos(Integer idRol, String nuevosPermisos) throws ErrorConectionMySQLException {
        String sql = "UPDATE roles SET permisos = ? WHERE id_rol = ?";
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nuevosPermisos);
            stmt.setInt(2, idRol);
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error actualizando permisos de rol: " + e.getMessage());
        }
    }
    
    public void actualizarEstado(Integer idRol, String estado) throws ErrorConectionMySQLException {
        String sql = "UPDATE roles SET estado = ? WHERE id_rol = ?";
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, estado);
            stmt.setInt(2, idRol);
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error actualizando estado de rol: " + e.getMessage());
        }
    }
    
    public int getCantidadUsuariosPorRol(Integer idRol) throws ErrorConectionMySQLException {
        String sql = "SELECT COUNT(*) as total FROM usuario_roles WHERE id_rol = ?";
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idRol);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error contando usuarios por rol: " + e.getMessage());
        }
        
        return 0;
    }
    
    public boolean existeRol(String descripcion) throws ErrorConectionMySQLException {
        String sql = "SELECT COUNT(*) as total FROM roles WHERE descripcion = ?";
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, descripcion);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total") > 0;
                }
            }
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error verificando existencia de rol: " + e.getMessage());
        }
        
        return false;
    }
    
    private Rol mapResultSetToRol(ResultSet rs) throws SQLException {
        Rol rol = new Rol();
        rol.setIdRol(rs.getInt("id_rol"));
        rol.setDescripcion(rs.getString("descripcion"));
        rol.setPermisos(rs.getString("permisos"));
        rol.setEstado(rs.getString("estado"));
        return rol;
    }
}
