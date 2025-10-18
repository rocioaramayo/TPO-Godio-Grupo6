package com.example.demo.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.example.demo.connections.MySQLPool;           // <-- asegúrate que tu clase esté en este paquete
import com.example.demo.exceptions.ErrorConectionMySQLException;
import com.example.demo.modelo.Usuario;                  // <-- si tu clase está en "modelo" a secas, cambia este import a "modelo.Usuario"

public class UsuarioRepository {

    private static UsuarioRepository instance;

    private UsuarioRepository() {}

    public static synchronized UsuarioRepository getInstance() {
        if (instance == null) {
            instance = new UsuarioRepository();
        }
        return instance;
    }

    // CREAR USUARIO
    public void guardarUsuario(Usuario usuario) throws ErrorConectionMySQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = MySQLPool.getInstancia().getConnection();
            String sql = "INSERT INTO usuarios (nombre_completo, email, password_hash, estado) VALUES (?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, usuario.getNombreCompleto());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getPasswordHash());
            stmt.setString(4, usuario.getEstado());

            stmt.executeUpdate();

            // Obtener el ID generado
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                usuario.setIdUsuario(rs.getInt(1));
            }

            System.out.println("Usuario guardado con ID: " + usuario.getIdUsuario());

        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error al guardar usuario: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException ignored) {}
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
            try { if (conn != null) conn.close(); } catch (SQLException ignored) {}
        }
    }

    // BUSCAR USUARIO POR EMAIL
    public Usuario buscarPorEmail(String email) throws ErrorConectionMySQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Usuario usuario = null;

        try {
            conn = MySQLPool.getInstancia().getConnection();
            String sql = "SELECT * FROM usuarios WHERE email = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);

            rs = stmt.executeQuery();

            if (rs.next()) {
                usuario = new Usuario();
                usuario.setIdUsuario(rs.getInt("id_usuario"));
                usuario.setNombreCompleto(rs.getString("nombre_completo"));
                usuario.setEmail(rs.getString("email"));
                usuario.setPasswordHash(rs.getString("password_hash"));
                usuario.setEstado(rs.getString("estado"));
                usuario.setFechaRegistro(rs.getTimestamp("fecha_registro"));
            }

        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error al buscar usuario: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException ignored) {}
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
            try { if (conn != null) conn.close(); } catch (SQLException ignored) {}
        }

        return usuario;
    }

    // ASIGNAR ROL A USUARIO
    public void asignarRol(Integer idUsuario, Integer idRol) throws ErrorConectionMySQLException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = MySQLPool.getInstancia().getConnection();
            String sql = "INSERT INTO usuario_roles (id_usuario, id_rol) VALUES (?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idRol);

            stmt.executeUpdate();
            System.out.println("Rol asignado correctamente");

        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error al asignar rol: " + e.getMessage());
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
            try { if (conn != null) conn.close(); } catch (SQLException ignored) {}
        }
    }
}
