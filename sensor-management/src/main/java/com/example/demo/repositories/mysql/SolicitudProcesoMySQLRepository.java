package com.example.demo.repositories.mysql;

import com.example.demo.connections.MySQLPool;
import com.example.demo.exceptions.ErrorConectionMySQLException;
import com.example.demo.modelo.SolicitudProceso;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SolicitudProcesoMySQLRepository {
    private static SolicitudProcesoMySQLRepository instance;
    
    private SolicitudProcesoMySQLRepository() {}
    
    public static SolicitudProcesoMySQLRepository getInstance() {
        if (instance == null) instance = new SolicitudProcesoMySQLRepository();
        return instance;
    }
    
    public void crear(SolicitudProceso solicitud) throws ErrorConectionMySQLException {
        String sql = "INSERT INTO solicitudes_proceso (id_solicitud, id_usuario, id_proceso, fecha_solicitud, estado, parametros, resultado, fecha_completado, observaciones) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String idSolicitud = solicitud.getIdSolicitud();
            if (idSolicitud == null || idSolicitud.isEmpty()) {
                idSolicitud = "SOL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                solicitud.setIdSolicitud(idSolicitud);
            }
            
            stmt.setString(1, solicitud.getIdSolicitud());
            stmt.setInt(2, solicitud.getIdUsuario());
            stmt.setInt(3, solicitud.getIdProceso());
            stmt.setTimestamp(4, Timestamp.valueOf(solicitud.getFechaSolicitud()));
            stmt.setString(5, solicitud.getEstado());
            stmt.setString(6, solicitud.getParametros());
            stmt.setString(7, solicitud.getResultado());
            
            LocalDateTime fechaCompletado = solicitud.getFechaCompletado();
            if (fechaCompletado != null) {
                stmt.setTimestamp(8, Timestamp.valueOf(fechaCompletado));
            } else {
                stmt.setNull(8, Types.TIMESTAMP);
            }
            
            stmt.setString(9, solicitud.getObservaciones());
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error creando solicitud: " + e.getMessage());
        }
    }
    
    public SolicitudProceso findById(String idSolicitud) throws ErrorConectionMySQLException {
        String sql = "SELECT * FROM solicitudes_proceso WHERE id_solicitud = ?";
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, idSolicitud);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSolicitud(rs);
                }
            }
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error buscando solicitud por ID: " + e.getMessage());
        }
        
        return null;
    }
    
    public List<SolicitudProceso> findByUsuario(Integer idUsuario) throws ErrorConectionMySQLException {
        String sql = "SELECT * FROM solicitudes_proceso WHERE id_usuario = ? ORDER BY fecha_solicitud DESC";
        List<SolicitudProceso> solicitudes = new ArrayList<>();
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idUsuario);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    solicitudes.add(mapResultSetToSolicitud(rs));
                }
            }
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error buscando solicitudes por usuario: " + e.getMessage());
        }
        
        return solicitudes;
    }
    
    public List<SolicitudProceso> findByUsuarioYPeriodo(Integer idUsuario, String fechaDesde, String fechaHasta, String estado) throws ErrorConectionMySQLException {
        String sql = "SELECT * FROM solicitudes_proceso WHERE id_usuario = ? AND fecha_solicitud BETWEEN ? AND ? AND estado = ? ORDER BY fecha_solicitud DESC";
        List<SolicitudProceso> solicitudes = new ArrayList<>();
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idUsuario);
            stmt.setString(2, fechaDesde);
            stmt.setString(3, fechaHasta);
            stmt.setString(4, estado);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    solicitudes.add(mapResultSetToSolicitud(rs));
                }
            }
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error buscando solicitudes por período: " + e.getMessage());
        }
        
        return solicitudes;
    }
    
    public List<SolicitudProceso> findByEstado(String estado) throws ErrorConectionMySQLException {
        String sql = "SELECT * FROM solicitudes_proceso WHERE estado = ? ORDER BY fecha_solicitud DESC";
        List<SolicitudProceso> solicitudes = new ArrayList<>();
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, estado);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    solicitudes.add(mapResultSetToSolicitud(rs));
                }
            }
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error buscando solicitudes por estado: " + e.getMessage());
        }
        
        return solicitudes;
    }
    
    public List<SolicitudProceso> findByProceso(Integer idProceso) throws ErrorConectionMySQLException {
        String sql = "SELECT * FROM solicitudes_proceso WHERE id_proceso = ? ORDER BY fecha_solicitud DESC";
        List<SolicitudProceso> solicitudes = new ArrayList<>();
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idProceso);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    solicitudes.add(mapResultSetToSolicitud(rs));
                }
            }
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error buscando solicitudes por proceso: " + e.getMessage());
        }
        
        return solicitudes;
    }
    
    public void actualizarEstado(String idSolicitud, String estado) throws ErrorConectionMySQLException {
        String sql = "UPDATE solicitudes_proceso SET estado = ? WHERE id_solicitud = ?";
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, estado);
            stmt.setString(2, idSolicitud);
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error actualizando estado de solicitud: " + e.getMessage());
        }
    }
    
    public void marcarCompletado(String idSolicitud, String resultado) throws ErrorConectionMySQLException {
        String sql = "UPDATE solicitudes_proceso SET estado = 'completado', resultado = ?, fecha_completado = NOW() WHERE id_solicitud = ?";
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, resultado);
            stmt.setString(2, idSolicitud);
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error marcando solicitud como completada: " + e.getMessage());
        }
    }
    
    public void marcarError(String idSolicitud, String observaciones) throws ErrorConectionMySQLException {
        String sql = "UPDATE solicitudes_proceso SET estado = 'error', observaciones = ?, fecha_completado = NOW() WHERE id_solicitud = ?";
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, observaciones);
            stmt.setString(2, idSolicitud);
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error marcando solicitud con error: " + e.getMessage());
        }
    }
    
    public int getCantidadSolicitudesPendientes() throws ErrorConectionMySQLException {
        String sql = "SELECT COUNT(*) as total FROM solicitudes_proceso WHERE estado = 'pendiente'";
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt("total");
            }
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error contando solicitudes pendientes: " + e.getMessage());
        }
        
        return 0;
    }
    
    public int getCantidadSolicitudesPorUsuario(Integer idUsuario) throws ErrorConectionMySQLException {
        String sql = "SELECT COUNT(*) as total FROM solicitudes_proceso WHERE id_usuario = ?";
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idUsuario);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error contando solicitudes por usuario: " + e.getMessage());
        }
        
        return 0;
    }
    
    private SolicitudProceso mapResultSetToSolicitud(ResultSet rs) throws SQLException {
        SolicitudProceso solicitud = new SolicitudProceso();
        solicitud.setIdSolicitud(rs.getString("id_solicitud"));
        solicitud.setIdUsuario(rs.getInt("id_usuario"));
        solicitud.setIdProceso(rs.getInt("id_proceso"));
        solicitud.setFechaSolicitud(rs.getTimestamp("fecha_solicitud").toLocalDateTime());
        solicitud.setEstado(rs.getString("estado"));
        solicitud.setParametros(rs.getString("parametros"));
        solicitud.setResultado(rs.getString("resultado"));
        solicitud.setObservaciones(rs.getString("observaciones"));
        
        Timestamp fechaCompletado = rs.getTimestamp("fecha_completado");
        if (fechaCompletado != null) {
            solicitud.setFechaCompletado(fechaCompletado.toLocalDateTime());
        }
        
        return solicitud;
    }
}
