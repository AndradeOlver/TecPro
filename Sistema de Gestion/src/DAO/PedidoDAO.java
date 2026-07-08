package DAO;

import Entidades.Cliente;
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
        String sql = "INSERT INTO Pedido (Cliente_ID, FechaEmision, FechaRecepcion, Estado, TipoVenta, FechaLimitePago, DeudaPendiente) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionSQL.probarConexion(); 
             PreparedStatement ps = con.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS);) {
            
            
            ps.setInt(1, p.getCliente().getCodigo()); // Asumiendo que Pedido tiene un objeto Cliente
            
            // Convertir fechas de Java a SQL
            ps.setDate(2, java.sql.Date.valueOf(p.getFechaEmision()));
            
            if (p.getFechaRecepcion() != null) {
                ps.setDate(3, java.sql.Date.valueOf(p.getFechaRecepcion()));
            } else {
                ps.setNull(3, java.sql.Types.DATE);
            }
            
            ps.setString(4, p.getEstado());
            ps.setString(5, p.getTipoVenta());
            
            if (p.getFechaLimitePago() != null) {
                ps.setDate(6, java.sql.Date.valueOf(p.getFechaLimitePago()));
            } else {
                ps.setNull(6, java.sql.Types.DATE);
            }
            
            ps.setDouble(7, p.getDeudaPendiente());
            
            ps.execute();
            try (ResultSet rs = ps.getGeneratedKeys()) {
            if (rs.next()) {
                p.setCodigo(rs.getInt(1));
            }
}
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
    // 1. Obtener el historial completo (Hace un INNER JOIN con Cliente para armar la tabla)
    public List<Pedido> obtenerTodos() {
        List<Pedido> lista = new ArrayList<>();
        String sql = "SELECT p.Codigo, p.Cliente_ID, p.FechaEmision, p.FechaRecepcion, p.Estado, p.TipoVenta, p.FechaLimitePago, p.DeudaPendiente, " +
                     "c.Nombre, c.Direccion, c.Correo, c.NumeroContacto " +
                     "FROM Pedido p INNER JOIN Cliente c ON p.Cliente_ID = c.ID";
                     
        try (Connection con = ConexionSQL.probarConexion(); 
             PreparedStatement ps = con.prepareStatement(sql); 
             ResultSet rs = ps.executeQuery()) {
             
            while (rs.next()) {
                // Instanciamos el cliente con los datos del JOIN
                Cliente cli = new Cliente(
                    rs.getInt("Cliente_ID"), 
                    rs.getString("Nombre"), 
                    rs.getString("Direccion"), 
                    rs.getString("Correo"), 
                    rs.getString("NumeroContacto")
                );
                
                // Usamos el NUEVO constructor de base de datos
                Pedido pedido = new Pedido(
                    rs.getInt("Codigo"),
                    rs.getString("FechaEmision"),
                    rs.getString("FechaRecepcion"),
                    rs.getString("Estado"),
                    rs.getString("TipoVenta"),
                    rs.getString("FechaLimitePago"),
                    rs.getDouble("DeudaPendiente"),
                    cli
                );
                lista.add(pedido);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener historial de pedidos: " + e.getMessage());
        }
        return lista;
    }

    // 2. Buscar un pedido específico por su código primario
    public Pedido buscarPorCodigo(int codigo) {
        String sql = "SELECT p.Codigo, p.Cliente_ID, p.FechaEmision, p.FechaRecepcion, p.Estado, p.TipoVenta, p.FechaLimitePago, p.DeudaPendiente, " +
                     "c.Nombre, c.Direccion, c.Correo, c.NumeroContacto " +
                     "FROM Pedido p INNER JOIN Cliente c ON p.Cliente_ID = c.ID WHERE p.Codigo = ?";
                     
        try (Connection con = ConexionSQL.probarConexion(); 
             PreparedStatement ps = con.prepareStatement(sql)) {
             
            ps.setInt(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Cliente cli = new Cliente(
                        rs.getInt("Cliente_ID"), 
                        rs.getString("Nombre"), 
                        rs.getString("Direccion"), 
                        rs.getString("Correo"), 
                        rs.getString("NumeroContacto")
                    );
                    
                    return new Pedido(
                        rs.getInt("Codigo"),
                        rs.getString("FechaEmision"),
                        rs.getString("FechaRecepcion"),
                        rs.getString("Estado"),
                        rs.getString("TipoVenta"),
                        rs.getString("FechaLimitePago"),
                        rs.getDouble("DeudaPendiente"),
                        cli
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar pedido: " + e.getMessage());
        }
        return null;
    }

    // 3. Actualización Rápida de Estado (Para botones de Cancelar o Avanzar)
    public boolean actualizarEstado(int codigo, String nuevoEstado) {
        String sql = "UPDATE Pedido SET Estado = ? WHERE Codigo = ?";
        try (Connection con = ConexionSQL.probarConexion(); 
             PreparedStatement ps = con.prepareStatement(sql)) {
             
            ps.setString(1, nuevoEstado);
            ps.setInt(2, codigo);
            ps.execute();
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar estado del pedido: " + e.getMessage());
            return false;
        }
    }

    // 4. Limpieza: Elimina primero los detalles (efecto cascada manual) y luego la cabecera
    public boolean eliminarCancelados() {
        String sqlDetalles = "DELETE FROM DetallePedido WHERE Pedido_Codigo IN (SELECT Codigo FROM Pedido WHERE Estado = 'Cancelado')";
        String sqlPedidos = "DELETE FROM Pedido WHERE Estado = 'Cancelado'";
        
        try (Connection con = ConexionSQL.probarConexion()) {
            con.setAutoCommit(false); // Transacción segura
            
            try (PreparedStatement psDetalles = con.prepareStatement(sqlDetalles);
                 PreparedStatement psPedidos = con.prepareStatement(sqlPedidos)) {
                
                psDetalles.executeUpdate();
                psPedidos.executeUpdate();
                
                con.commit();
                return true;
                
            } catch (SQLException e) {
                con.rollback();
                System.err.println("Error en el rollback al eliminar pedidos: " + e.getMessage());
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error crítico al eliminar pedidos cancelados: " + e.getMessage());
            return false;
        }
    }
    public boolean registrarTransaccional(Connection con, Pedido p) throws SQLException {
    String sql = "INSERT INTO Pedido (Cliente_ID, FechaEmision, FechaRecepcion, Estado, TipoVenta, FechaLimitePago, DeudaPendiente) VALUES (?, ?, ?, ?, ?, ?, ?)";
    
    // Solo cerramos el PreparedStatement, la conexión sigue abierta para los detalles
    try (PreparedStatement ps = con.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
        ps.setInt(1, p.getCliente().getCodigo());
        ps.setDate(2, java.sql.Date.valueOf(p.getFechaEmision()));
        if (p.getFechaRecepcion() != null) ps.setDate(3, java.sql.Date.valueOf(p.getFechaRecepcion()));
        else ps.setNull(3, java.sql.Types.DATE);
        
        ps.setString(4, p.getEstado());
        ps.setString(5, p.getTipoVenta());
        
        if (p.getFechaLimitePago() != null) ps.setDate(6, java.sql.Date.valueOf(p.getFechaLimitePago()));
        else ps.setNull(6, java.sql.Types.DATE);
        
        ps.setDouble(7, p.getDeudaPendiente());
        ps.execute();
        
        try (ResultSet rs = ps.getGeneratedKeys()) {
            if (rs.next()) {
                p.setCodigo(rs.getInt(1));
            }
        }
        return true;
    }
}
    public boolean actualizarEstadoYDeudaTransaccional(Connection con, int codigoPedido, String nuevoEstado, double nuevaDeuda) throws SQLException {
    String sql = "UPDATE Pedido SET Estado = ?, DeudaPendiente = ? WHERE Codigo = ?";
    try (PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, nuevoEstado);
        ps.setDouble(2, nuevaDeuda);
        ps.setInt(3, codigoPedido);
        ps.execute();
        return true;
    }
}
    
}