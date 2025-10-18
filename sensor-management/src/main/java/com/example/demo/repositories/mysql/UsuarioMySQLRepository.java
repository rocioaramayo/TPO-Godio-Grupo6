package com.example.demo.repositories.mysql;

import com.example.demo.connections.MySQLPool;
import com.example.demo.exceptions.ErrorConectionMySQLException;
import com.example.demo.modelo.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioMySQLRepository {
    private static UsuarioMySQLRepository instance;
    
    private UsuarioMySQLRepository() {}
    
    public static UsuarioMySQLRepository getInstance() {
        if (instance == null) instance = new UsuarioMySQLRepository();
        return instance;
    }
    
    public void crear(String nombre, String email, String passHash) throws ErrorConectionMySQLException {
        String sql = "INSERT INTO usuarios (nombre_completo, email, password_hash, estado, fecha_registro) VALUES (?, ?, ?, 'activo', NOW())";
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, nombre);
            stmt.setString(2, email);
            stmt.setString(3, passHash);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        System.out.println("Usuario creado con ID: " + generatedKeys.getInt(1));
                    }
                }
            }
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error creando usuario: " + e.getMessage());
        }
    }
    
    public Usuario validarLogin(String email, String passHash) throws ErrorConectionMySQLException {
        String sql = "SELECT * FROM usuarios WHERE email = ? AND password_hash = ? AND estado = 'activo'";
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            stmt.setString(2, passHash);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUsuario(rs);
                }
            }
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error validando login: " + e.getMessage());
        }
        
        return null;
    }
    
    public Usuario findByEmail(String email) throws ErrorConectionMySQLException {
        String sql = "SELECT * FROM usuarios WHERE email = ?";
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUsuario(rs);
                }
            }
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error buscando usuario por email: " + e.getMessage());
        }
        
        return null;
    }
    
    public Usuario findById(Integer idUsuario) throws ErrorConectionMySQLException {
        String sql = "SELECT * FROM usuarios WHERE id_usuario = ?";
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idUsuario);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUsuario(rs);
                }
            }
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error buscando usuario por ID: " + e.getMessage());
        }
        
        return null;
    }
    
    public List<Usuario> findAll() throws ErrorConectionMySQLException {
        String sql = "SELECT * FROM usuarios ORDER BY fecha_registro DESC";
        List<Usuario> usuarios = new ArrayList<>();
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                usuarios.add(mapResultSetToUsuario(rs));
            }
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error listando usuarios: " + e.getMessage());
        }
        
        return usuarios;
    }
    
    public void actualizarEstado(Integer idUsuario, String estado) throws ErrorConectionMySQLException {
        String sql = "UPDATE usuarios SET estado = ? WHERE id_usuario = ?";
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, estado);
            stmt.setInt(2, idUsuario);
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error actualizando estado de usuario: " + e.getMessage());
        }
    }
    
    public void asignarRol(Integer idUsuario, Integer idRol) throws ErrorConectionMySQLException {
        String sql = "INSERT INTO usuario_roles (id_usuario, id_rol) VALUES (?, ?) ON DUPLICATE KEY UPDATE id_rol = ?";
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idRol);
            stmt.setInt(3, idRol);
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error asignando rol: " + e.getMessage());
        }
    }
    
    public List<Usuario> findByRol(String descripcionRol) throws ErrorConectionMySQLException {
        String sql = "SELECT u.* FROM usuarios u " +
                    "INNER JOIN usuario_roles ur ON u.id_usuario = ur.id_usuario " +
                    "INNER JOIN roles r ON ur.id_rol = r.id_rol " +
                    "WHERE r.descripcion = ?";
        List<Usuario> usuarios = new ArrayList<>();
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, descripcionRol);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    usuarios.add(mapResultSetToUsuario(rs));
                }
            }
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error buscando usuarios por rol: " + e.getMessage());
        }
        
        return usuarios;
    }
}
