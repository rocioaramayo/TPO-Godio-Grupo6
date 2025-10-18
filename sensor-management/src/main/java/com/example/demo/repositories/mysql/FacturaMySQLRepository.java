package com.example.demo.repositories.mysql;

import com.example.demo.connections.MySQLPool;
import com.example.demo.exceptions.ErrorConectionMySQLException;
import com.example.demo.modelo.Factura;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FacturaMySQLRepository {
    private static FacturaMySQLRepository instance;
    
    private FacturaMySQLRepository() {}
    
    public static FacturaMySQLRepository getInstance() {
        if (instance == null) instance = new FacturaMySQLRepository();
        return instance;
    }
    
    public void crear(Factura factura) throws ErrorConectionMySQLException {
        String sql = "INSERT INTO facturas (id_factura, id_usuario, fecha_emision, fecha_vencimiento, monto_total, estado, observaciones) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, factura.getIdFactura());
            stmt.setInt(2, factura.getIdUsuario());
            stmt.setTimestamp(3, Timestamp.valueOf(factura.getFechaEmision()));
            stmt.setTimestamp(4, Timestamp.valueOf(factura.getFechaVencimiento()));
            stmt.setBigDecimal(5, factura.getMontoTotal());
            stmt.setString(6, factura.getEstado());
            stmt.setString(7, factura.getObservaciones());
            
            stmt.executeUpdate();
            
            // Insertar procesos facturados
            insertarProcesosFacturados(factura);
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error creando factura: " + e.getMessage());
        }
    }
    
    private void insertarProcesosFacturados(Factura factura) throws SQLException {
        String sql = "INSERT INTO factura_procesos (id_factura, id_proceso, monto) VALUES (?, ?, ?)";
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            for (String idProceso : factura.getProcesosFacturados()) {
                stmt.setString(1, factura.getIdFactura());
                stmt.setString(2, idProceso);
                stmt.setBigDecimal(3, BigDecimal.ZERO); // Se puede calcular el monto individual
                stmt.addBatch();
            }
            
            stmt.executeBatch();
        }
    }
    
    public Factura findById(String idFactura) throws ErrorConectionMySQLException {
        String sql = "SELECT * FROM facturas WHERE id_factura = ?";
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, idFactura);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToFactura(rs);
                }
            }
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error buscando factura por ID: " + e.getMessage());
        }
        
        return null;
    }
    
    public List<Factura> findByUsuario(Integer idUsuario) throws ErrorConectionMySQLException {
        String sql = "SELECT * FROM facturas WHERE id_usuario = ? ORDER BY fecha_emision DESC";
        List<Factura> facturas = new ArrayList<>();
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idUsuario);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    facturas.add(mapResultSetToFactura(rs));
                }
            }
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error buscando facturas por usuario: " + e.getMessage());
        }
        
        return facturas;
    }
    
    public List<Factura> findByEstado(String estado) throws ErrorConectionMySQLException {
        String sql = "SELECT * FROM facturas WHERE estado = ? ORDER BY fecha_emision DESC";
        List<Factura> facturas = new ArrayList<>();
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, estado);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    facturas.add(mapResultSetToFactura(rs));
                }
            }
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error buscando facturas por estado: " + e.getMessage());
        }
        
        return facturas;
    }
    
    public void actualizarEstado(String idFactura, String estado) throws ErrorConectionMySQLException {
        String sql = "UPDATE facturas SET estado = ? WHERE id_factura = ?";
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, estado);
            stmt.setString(2, idFactura);
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error actualizando estado de factura: " + e.getMessage());
        }
    }
    
    public void marcarPagada(String idFactura, String metodoPago) throws ErrorConectionMySQLException {
        String sql = "UPDATE facturas SET estado = 'pagada', metodo_pago = ?, fecha_pago = NOW() WHERE id_factura = ?";
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, metodoPago);
            stmt.setString(2, idFactura);
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error marcando factura como pagada: " + e.getMessage());
        }
    }
    
    public List<Factura> findVencidas() throws ErrorConectionMySQLException {
        String sql = "SELECT * FROM facturas WHERE estado = 'pendiente' AND fecha_vencimiento < NOW() ORDER BY fecha_vencimiento ASC";
        List<Factura> facturas = new ArrayList<>();
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                facturas.add(mapResultSetToFactura(rs));
            }
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error buscando facturas vencidas: " + e.getMessage());
        }
        
        return facturas;
    }
    
    public BigDecimal getTotalFacturadoPorUsuario(Integer idUsuario) throws ErrorConectionMySQLException {
        String sql = "SELECT SUM(monto_total) as total FROM facturas WHERE id_usuario = ? AND estado IN ('pagada', 'pendiente')";
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idUsuario);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal total = rs.getBigDecimal("total");
                    return total != null ? total : BigDecimal.ZERO;
                }
            }
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error calculando total facturado: " + e.getMessage());
        }
        
        return BigDecimal.ZERO;
    }
    
    private Factura mapResultSetToFactura(ResultSet rs) throws SQLException {
        Factura factura = new Factura();
        factura.setIdFactura(rs.getString("id_factura"));
        factura.setIdUsuario(rs.getInt("id_usuario"));
        factura.setFechaEmision(rs.getTimestamp("fecha_emision").toLocalDateTime());
        factura.setFechaVencimiento(rs.getTimestamp("fecha_vencimiento").toLocalDateTime());
        factura.setMontoTotal(rs.getBigDecimal("monto_total"));
        factura.setEstado(rs.getString("estado"));
        factura.setObservaciones(rs.getString("observaciones"));
        factura.setMetodoPago(rs.getString("metodo_pago"));
        
        Timestamp fechaPago = rs.getTimestamp("fecha_pago");
        if (fechaPago != null) {
            factura.setFechaPago(fechaPago.toLocalDateTime());
        }
        
        // Cargar procesos facturados
        factura.setProcesosFacturados(cargarProcesosFacturados(factura.getIdFactura()));
        
        return factura;
    }
    
    private List<String> cargarProcesosFacturados(String idFactura) throws SQLException {
        List<String> procesos = new ArrayList<>();
        String sql = "SELECT id_proceso FROM factura_procesos WHERE id_factura = ?";
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, idFactura);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    procesos.add(rs.getString("id_proceso"));
                }
            }
        }
        
        return procesos;
    }
}
