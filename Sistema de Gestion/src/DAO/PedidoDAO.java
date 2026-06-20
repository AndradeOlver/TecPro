package DAO;

import Entidades.Pedido;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO {

    public boolean registrar(Pedido p) {
        String sql = "INSERT INTO Pedido (Codigo, Cliente_ID, FechaEmision, FechaRecepcion, Estado, TipoVenta, FechaLimitePago, DeudaPendiente) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionSQL.probarConexion(); 
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, p.getCodigo());
            ps.setInt(2, p.getCliente().getCodigo()); // Asumiendo que Pedido tiene un objeto Cliente
            
            // Convertir fechas de Java a SQL
            ps.setDate(3, java.sql.Date.valueOf(p.getFechaEmision()));
            
            if (p.getFechaRecepcion() != null) {
                ps.setDate(4, java.sql.Date.valueOf(p.getFechaRecepcion()));
            } else {
                ps.setNull(4, java.sql.Types.DATE);
            }
            
            ps.setString(5, p.getEstado());
            ps.setString(6, p.getTipoVenta());
            
            if (p.getFechaLimitePago() != null) {
                ps.setDate(7, java.sql.Date.valueOf(p.getFechaLimitePago()));
            } else {
                ps.setNull(7, java.sql.Types.DATE);
            }
            
            ps.setDouble(8, p.getDeudaPendiente());
            
            ps.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al registrar el pedido: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarEstadoYDeuda(int codigoPedido, String nuevoEstado, double nuevaDeuda) {
        String sql = "UPDATE Pedido SET Estado = ?, DeudaPendiente = ? WHERE Codigo = ?";
        try (Connection con = ConexionSQL.probarConexion(); 
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, nuevoEstado);
            ps.setDouble(2, nuevaDeuda);
            ps.setInt(3, codigoPedido);
            
            ps.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al actualizar estado del pedido: " + e.getMessage());
            return false;
        }
    }
}