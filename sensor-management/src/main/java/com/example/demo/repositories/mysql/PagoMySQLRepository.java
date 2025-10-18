package com.example.demo.repositories.mysql;

import com.example.demo.connections.MySQLPool;
import com.example.demo.exceptions.ErrorConectionMySQLException;
import com.example.demo.modelo.Pago;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PagoMySQLRepository {
    private static PagoMySQLRepository instance;
    
    private PagoMySQLRepository() {}
    
    public static PagoMySQLRepository getInstance() {
        if (instance == null) instance = new PagoMySQLRepository();
        return instance;
    }
    
    public void crear(Pago pago) throws ErrorConectionMySQLException {
        String sql = "INSERT INTO pagos (id_pago, id_factura, fecha_pago, monto_pagado, metodo_pago, estado, numero_transaccion, observaciones, datos_adicionales) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, pago.getIdPago());
            stmt.setString(2, pago.getIdFactura());
            stmt.setTimestamp(3, Timestamp.valueOf(pago.getFechaPago()));
            stmt.setBigDecimal(4, pago.getMontoPagado());
            stmt.setString(5, pago.getMetodoPago());
            stmt.setString(6, pago.getEstado());
            stmt.setString(7, pago.getNumeroTransaccion());
            stmt.setString(8, pago.getObservaciones());
            stmt.setString(9, pago.getDatosAdicionales());
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error creando pago: " + e.getMessage());
        }
    }
    
    public Pago findById(String idPago) throws ErrorConectionMySQLException {
        String sql = "SELECT * FROM pagos WHERE id_pago = ?";
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, idPago);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPago(rs);
                }
            }
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error buscando pago por ID: " + e.getMessage());
        }
        
        return null;
    }
    
    public List<Pago> findByFactura(String idFactura) throws ErrorConectionMySQLException {
        String sql = "SELECT * FROM pagos WHERE id_factura = ? ORDER BY fecha_pago DESC";
        List<Pago> pagos = new ArrayList<>();
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, idFactura);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pagos.add(mapResultSetToPago(rs));
                }
            }
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error buscando pagos por factura: " + e.getMessage());
        }
        
        return pagos;
    }
    
    public List<Pago> findByEstado(String estado) throws ErrorConectionMySQLException {
        String sql = "SELECT * FROM pagos WHERE estado = ? ORDER BY fecha_pago DESC";
        List<Pago> pagos = new ArrayList<>();
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, estado);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pagos.add(mapResultSetToPago(rs));
                }
            }
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error buscando pagos por estado: " + e.getMessage());
        }
        
        return pagos;
    }
    
    public List<Pago> findByMetodoPago(String metodoPago) throws ErrorConectionMySQLException {
        String sql = "SELECT * FROM pagos WHERE metodo_pago = ? ORDER BY fecha_pago DESC";
        List<Pago> pagos = new ArrayList<>();
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, metodoPago);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pagos.add(mapResultSetToPago(rs));
                }
            }
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error buscando pagos por método: " + e.getMessage());
        }
        
        return pagos;
    }
    
    public void actualizarEstado(String idPago, String estado) throws ErrorConectionMySQLException {
        String sql = "UPDATE pagos SET estado = ? WHERE id_pago = ?";
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, estado);
            stmt.setString(2, idPago);
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error actualizando estado de pago: " + e.getMessage());
        }
    }
    
    public BigDecimal getTotalPagadoPorFactura(String idFactura) throws ErrorConectionMySQLException {
        String sql = "SELECT SUM(monto_pagado) as total FROM pagos WHERE id_factura = ? AND estado = 'procesado'";
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, idFactura);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal total = rs.getBigDecimal("total");
                    return total != null ? total : BigDecimal.ZERO;
                }
            }
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error calculando total pagado: " + e.getMessage());
        }
        
        return BigDecimal.ZERO;
    }
    
    public BigDecimal getTotalPagadoPorPeriodo(String fechaDesde, String fechaHasta) throws ErrorConectionMySQLException {
        String sql = "SELECT SUM(monto_pagado) as total FROM pagos WHERE fecha_pago BETWEEN ? AND ? AND estado = 'procesado'";
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, fechaDesde);
            stmt.setString(2, fechaHasta);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal total = rs.getBigDecimal("total");
                    return total != null ? total : BigDecimal.ZERO;
                }
            }
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error calculando total pagado por período: " + e.getMessage());
        }
        
        return BigDecimal.ZERO;
    }
    
    private Pago mapResultSetToPago(ResultSet rs) throws SQLException {
        Pago pago = new Pago();
        pago.setIdPago(rs.getString("id_pago"));
        pago.setIdFactura(rs.getString("id_factura"));
        pago.setFechaPago(rs.getTimestamp("fecha_pago").toLocalDateTime());
        pago.setMontoPagado(rs.getBigDecimal("monto_pagado"));
        pago.setMetodoPago(rs.getString("metodo_pago"));
        pago.setEstado(rs.getString("estado"));
        pago.setNumeroTransaccion(rs.getString("numero_transaccion"));
        pago.setObservaciones(rs.getString("observaciones"));
        pago.setDatosAdicionales(rs.getString("datos_adicionales"));
        return pago;
    }
}