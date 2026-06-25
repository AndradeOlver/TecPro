package DAO;

import Entidades.DetallePedido;
import Entidades.Producto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.ResultSet;

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
    // Recupera todos los productos asociados a una venta
    public List<DetallePedido> obtenerDetallesPorPedido(int codigoPedido) {
        List<DetallePedido> lista = new ArrayList<>();
        String sql = "SELECT * FROM DetallePedido WHERE Pedido_Codigo = ?";
        
        try (Connection con = ConexionSQL.probarConexion(); 
             PreparedStatement ps = con.prepareStatement(sql)) {
             
            ps.setInt(1, codigoPedido);
            
            try (ResultSet rs = ps.executeQuery()) {
                // Reutilizamos el ProductoDAO para ensamblar el producto que se vendió
                ProductoDAO pDAO = new ProductoDAO();
                
                while (rs.next()) {
                    Producto p = pDAO.buscarPorId(rs.getInt("Producto_ID"));
                    
                    if(p != null) {
                        DetallePedido dp = new DetallePedido(
                            rs.getInt("CantidadVendida"), 
                            rs.getDouble("PrecioVentaCongelado"), 
                            p
                        );
                        lista.add(dp);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener los detalles del pedido: " + e.getMessage());
        }
        return lista;
    }
}