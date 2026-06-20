package DAO;

import Entidades.DetallePedido;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class DetallePedidoDAO {

    // Guarda una lista entera de detalles asociados a un pedido
    public boolean registrarDetalles(int codigoPedido, List<DetallePedido> detalles) {
        String sql = "INSERT INTO DetallePedido (Pedido_Codigo, Producto_ID, CantidadVendida, PrecioVentaCongelado) VALUES (?, ?, ?, ?)";
        
        try (Connection con = ConexionSQL.probarConexion(); 
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            // Desactivamos el auto-commit para asegurar que o se guardan todos los detalles, o no se guarda ninguno
            con.setAutoCommit(false); 
            
            for (DetallePedido dp : detalles) {
                ps.setInt(1, codigoPedido);
                ps.setInt(2, dp.getProducto().getId()); // Asumiendo que DetallePedido tiene un objeto Producto
                ps.setInt(3, dp.getCantidadVendida());
                ps.setDouble(4, dp.getPrecioVentaCongelado());
                
                ps.addBatch(); // Agrega a la cola de inserción
            }
            
            ps.executeBatch(); // Ejecuta todas las inserciones de golpe
            con.commit(); // Confirma los cambios
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error al registrar los detalles del pedido: " + e.getMessage());
            return false;
        }
    }
}