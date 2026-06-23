/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;
import Entidades.OrdenCompra;
import Entidades.Proveedor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author equipo
 */
public class OrdenCompraDAO {
    public boolean registrar(OrdenCompra oc) {
        // 1. Obtenemos el último código real de SQL Server para evitar duplicados
        String sqlMax = "SELECT ISNULL(MAX(Codigo), 300000) + 1 AS NuevoCodigo FROM OrdenCompra";
        String sqlInsert = "INSERT INTO OrdenCompra (Codigo, Proveedor_RUC, FechaIngreso, Estado) VALUES (?, ?, ?, ?)";
        
        try (Connection con = ConexionSQL.probarConexion()) {
            
            // 2. Asignamos el nuevo código a la entidad antes de insertarla
            try (PreparedStatement psMax = con.prepareStatement(sqlMax);
                 ResultSet rs = psMax.executeQuery()) {
                if (rs.next()) {
                    oc.setCodigo(rs.getInt("NuevoCodigo")); 
                }
            }
            
            // 3. Ejecutamos el registro con el código corregido
            try (PreparedStatement psInsert = con.prepareStatement(sqlInsert)) {
                psInsert.setInt(1, oc.getCodigo());
                psInsert.setString(2, oc.getProveedor().getRuc());
                psInsert.setDate(3, java.sql.Date.valueOf(oc.getFechaIngreso()));
                psInsert.setString(4, oc.getEstado());
                
                psInsert.execute();
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al registrar orden de compra: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarEstado(int codigo, String nuevoEstado) {
        String sql = "UPDATE OrdenCompra SET Estado = ? WHERE Codigo = ?";
        try (Connection con = ConexionSQL.probarConexion(); 
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, codigo);
            ps.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al actualizar estado de orden: " + e.getMessage());
            return false;
        }
    }

    public OrdenCompra buscarPorCodigo(int codigo) {
       // Se agregaron los campos faltantes a la consulta SQL
        String sql = "SELECT o.Codigo, o.FechaIngreso, o.Estado, p.RUC, p.RazonSocial, p.Contacto, p.Direccion, p.NumeroContacto, p.CorreoContacto " +
                     "FROM OrdenCompra o INNER JOIN Proveedor p ON o.Proveedor_RUC = p.RUC WHERE o.Codigo = ?";
                     
        try (Connection con = ConexionSQL.probarConexion(); 
             PreparedStatement ps = con.prepareStatement(sql)) {
             
            ps.setInt(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Ahora instanciamos el Proveedor con sus datos reales para pasar las validaciones
                    Proveedor prov = new Proveedor(
                        rs.getString("RUC"), 
                        rs.getString("RazonSocial"), 
                        rs.getString("Direccion"), 
                        rs.getString("Contacto"), 
                        rs.getString("NumeroContacto"), 
                        rs.getString("CorreoContacto")
                    );
                    return new OrdenCompra(rs.getInt("Codigo"), rs.getDate("FechaIngreso").toString(), rs.getString("Estado"), prov);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar orden: " + e.getMessage());
        }
        return null;
    }

    public List<OrdenCompra> obtenerTodos() {
        List<OrdenCompra> lista = new ArrayList<>();
        // Se agregaron los campos faltantes a la consulta SQL
        String sql = "SELECT o.Codigo, o.FechaIngreso, o.Estado, p.RUC, p.RazonSocial, p.Contacto, p.Direccion, p.NumeroContacto, p.CorreoContacto " +
                     "FROM OrdenCompra o INNER JOIN Proveedor p ON o.Proveedor_RUC = p.RUC";
                     
        try (Connection con = ConexionSQL.probarConexion(); 
             PreparedStatement ps = con.prepareStatement(sql); 
             ResultSet rs = ps.executeQuery()) {
             
            while (rs.next()) {
                // Instanciamos el Proveedor con sus datos reales para pasar las validaciones
                Proveedor prov = new Proveedor(
                    rs.getString("RUC"), 
                    rs.getString("RazonSocial"), 
                    rs.getString("Direccion"), 
                    rs.getString("Contacto"), 
                    rs.getString("NumeroContacto"), 
                    rs.getString("CorreoContacto")
                );
                lista.add(new OrdenCompra(rs.getInt("Codigo"), rs.getDate("FechaIngreso").toString(), rs.getString("Estado"), prov));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener historial de órdenes: " + e.getMessage());
        }
        return lista;
    }
    public boolean eliminarCanceladas() {
        String sqlLotes = "DELETE FROM LoteProducto WHERE OrdenCompra_Codigo IN (SELECT Codigo FROM OrdenCompra WHERE Estado = 'Cancelada')";
        String sqlOrdenes = "DELETE FROM OrdenCompra WHERE Estado = 'Cancelada'";
        
        try (Connection con = ConexionSQL.probarConexion()) {
            con.setAutoCommit(false);
            
            try (PreparedStatement psLotes = con.prepareStatement(sqlLotes);
                 PreparedStatement psOrdenes = con.prepareStatement(sqlOrdenes)) {
                
                psLotes.executeUpdate();
                psOrdenes.executeUpdate();
                
                con.commit();
                return true;
            } catch (SQLException e) {
                con.rollback();
                System.err.println("Error en el rollback al eliminar órdenes: " + e.getMessage());
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error al eliminar órdenes canceladas: " + e.getMessage());
            return false;
        }
    }
    
}
