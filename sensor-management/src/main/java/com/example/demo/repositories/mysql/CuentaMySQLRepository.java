package com.example.demo.repositories.mysql;

import com.example.demo.connections.MySQLPool;
import com.example.demo.exceptions.ErrorConectionMySQLException;
import com.example.demo.modelo.CuentaCorriente;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CuentaCorrienteMySQLRepository {
    private static CuentaCorrienteMySQLRepository instance;
    
    private CuentaCorrienteMySQLRepository() {}
    
    public static CuentaCorrienteMySQLRepository getInstance() {
        if (instance == null) instance = new CuentaCorrienteMySQLRepository();
        return instance;
    }
    
    public void crear(CuentaCorriente cuenta) throws ErrorConectionMySQLException {
        String sql = "INSERT INTO cuentas_corrientes (id_cuenta, id_usuario, saldo_actual, limite_credito, estado, fecha_creacion) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String idCuenta = cuenta.getIdCuenta();
            if (idCuenta == null || idCuenta.isEmpty()) {
                idCuenta = "CC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                cuenta.setIdCuenta(idCuenta);
            }
            
            stmt.setString(1, cuenta.getIdCuenta());
            stmt.setInt(2, cuenta.getIdUsuario());
            stmt.setBigDecimal(3, cuenta.getSaldoActual());
            stmt.setBigDecimal(4, cuenta.getLimiteCredito());
            stmt.setString(5, cuenta.getEstado());
            stmt.setTimestamp(6, Timestamp.valueOf(cuenta.getFechaCreacion()));
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error creando cuenta corriente: " + e.getMessage());
        }
    }
    
    public CuentaCorriente findByUsuario(Integer idUsuario) throws ErrorConectionMySQLException {
        String sql = "SELECT * FROM cuentas_corrientes WHERE id_usuario = ?";
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idUsuario);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCuenta(rs);
                }
            }
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error buscando cuenta por usuario: " + e.getMessage());
        }
        
        return null;
    }
    
    public CuentaCorriente findById(String idCuenta) throws ErrorConectionMySQLException {
        String sql = "SELECT * FROM cuentas_corrientes WHERE id_cuenta = ?";
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, idCuenta);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCuenta(rs);
                }
            }
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error buscando cuenta por ID: " + e.getMessage());
        }
        
        return null;
    }
    
    public List<CuentaCorriente> findByEstado(String estado) throws ErrorConectionMySQLException {
        String sql = "SELECT * FROM cuentas_corrientes WHERE estado = ? ORDER BY fecha_creacion DESC";
        List<CuentaCorriente> cuentas = new ArrayList<>();
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, estado);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    cuentas.add(mapResultSetToCuenta(rs));
                }
            }
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error buscando cuentas por estado: " + e.getMessage());
        }
        
        return cuentas;
    }
    
    public void actualizarSaldo(String idCuenta, BigDecimal nuevoSaldo) throws ErrorConectionMySQLException {
        String sql = "UPDATE cuentas_corrientes SET saldo_actual = ?, fecha_ultimo_movimiento = NOW() WHERE id_cuenta = ?";
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBigDecimal(1, nuevoSaldo);
            stmt.setString(2, idCuenta);
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error actualizando saldo: " + e.getMessage());
        }
    }
    
    public void actualizarEstado(String idCuenta, String estado) throws ErrorConectionMySQLException {
        String sql = "UPDATE cuentas_corrientes SET estado = ? WHERE id_cuenta = ?";
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, estado);
            stmt.setString(2, idCuenta);
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error actualizando estado de cuenta: " + e.getMessage());
        }
    }
    
    public void agregarMovimiento(String idCuenta, String tipo, BigDecimal monto, String descripcion) throws ErrorConectionMySQLException {
        String sql = "INSERT INTO movimientos_cuenta (id_cuenta, tipo, monto, descripcion) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, idCuenta);
            stmt.setString(2, tipo);
            stmt.setBigDecimal(3, monto);
            stmt.setString(4, descripcion);
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error agregando movimiento: " + e.getMessage());
        }
    }
    
    public List<CuentaCorriente.MovimientoCuenta> obtenerMovimientos(String idCuenta) throws ErrorConectionMySQLException {
        String sql = "SELECT * FROM movimientos_cuenta WHERE id_cuenta = ? ORDER BY fecha DESC LIMIT 50";
        List<CuentaCorriente.MovimientoCuenta> movimientos = new ArrayList<>();
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, idCuenta);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    movimientos.add(mapResultSetToMovimiento(rs));
                }
            }
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error obteniendo movimientos: " + e.getMessage());
        }
        
        return movimientos;
    }
    
    public BigDecimal getSaldoTotalSistema() throws ErrorConectionMySQLException {
        String sql = "SELECT SUM(saldo_actual) as total FROM cuentas_corrientes WHERE estado = 'activa'";
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                BigDecimal total = rs.getBigDecimal("total");
                return total != null ? total : BigDecimal.ZERO;
            }
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error calculando saldo total del sistema: " + e.getMessage());
        }
        
        return BigDecimal.ZERO;
    }
    
    public List<CuentaCorriente> findCuentasEnRojo() throws ErrorConectionMySQLException {
        String sql = "SELECT * FROM cuentas_corrientes WHERE saldo_actual < 0 AND estado = 'activa' ORDER BY saldo_actual ASC";
        List<CuentaCorriente> cuentas = new ArrayList<>();
        
        try (Connection conn = MySQLPool.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                cuentas.add(mapResultSetToCuenta(rs));
            }
            
        } catch (SQLException e) {
            throw new ErrorConectionMySQLException("Error buscando cuentas en rojo: " + e.getMessage());
        }
        
        return cuentas;
    }
    
    private CuentaCorriente mapResultSetToCuenta(ResultSet rs) throws SQLException {
        CuentaCorriente cuenta = new CuentaCorriente();
        cuenta.setIdCuenta(rs.getString("id_cuenta"));
        cuenta.setIdUsuario(rs.getInt("id_usuario"));
        cuenta.setSaldoActual(rs.getBigDecimal("saldo_actual"));
        cuenta.setLimiteCredito(rs.getBigDecimal("limite_credito"));
        cuenta.setEstado(rs.getString("estado"));
        cuenta.setFechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime());
        
        Timestamp fechaUltimoMovimiento = rs.getTimestamp("fecha_ultimo_movimiento");
        if (fechaUltimoMovimiento != null) {
            cuenta.setFechaUltimoMovimiento(fechaUltimoMovimiento.toLocalDateTime());
        }
        
        return cuenta;
    }
    
    private CuentaCorriente.MovimientoCuenta mapResultSetToMovimiento(ResultSet rs) throws SQLException {
        CuentaCorriente.MovimientoCuenta movimiento = new CuentaCorriente.MovimientoCuenta();
        movimiento.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
        movimiento.setTipo(rs.getString("tipo"));
        movimiento.setMonto(rs.getBigDecimal("monto"));
        movimiento.setDescripcion(rs.getString("descripcion"));
        
        BigDecimal saldoAnterior = rs.getBigDecimal("saldo_anterior");
        if (saldoAnterior != null) {
            movimiento.setSaldoAnterior(saldoAnterior);
        }
        
        BigDecimal saldoPosterior = rs.getBigDecimal("saldo_posterior");
        if (saldoPosterior != null) {
            movimiento.setSaldoPosterior(saldoPosterior);
        }
        
        return movimiento;
    }
}
