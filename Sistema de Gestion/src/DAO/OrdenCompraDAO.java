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
        String sql = "INSERT INTO OrdenCompra (Codigo, Proveedor_RUC, FechaIngreso, Estado) VALUES (?, ?, ?, ?)";
        try (Connection con = ConexionSQL.probarConexion(); 
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, oc.getCodigo());
            ps.setString(2, oc.getProveedor().getRuc());
            ps.setDate(3, java.sql.Date.valueOf(oc.getFechaIngreso()));
            ps.setString(4, oc.getEstado());
            ps.execute();
            return true;
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
        String sql = "SELECT o.Codigo, o.FechaIngreso, o.Estado, p.RUC, p.RazonSocial FROM OrdenCompra o INNER JOIN Proveedor p ON o.Proveedor_RUC = p.RUC WHERE o.Codigo = ?";
        try (Connection con = ConexionSQL.probarConexion(); 
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Proveedor prov = new Proveedor(rs.getString("RUC"), rs.getString("RazonSocial"), "", "", "", "");
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
        String sql = "SELECT o.Codigo, o.FechaIngreso, o.Estado, p.RUC, p.RazonSocial FROM OrdenCompra o INNER JOIN Proveedor p ON o.Proveedor_RUC = p.RUC";
        try (Connection con = ConexionSQL.probarConexion(); 
             PreparedStatement ps = con.prepareStatement(sql); 
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Proveedor prov = new Proveedor(rs.getString("RUC"), rs.getString("RazonSocial"), "", "", "", "");
                lista.add(new OrdenCompra(rs.getInt("Codigo"), rs.getDate("FechaIngreso").toString(), rs.getString("Estado"), prov));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener historial de órdenes: " + e.getMessage());
        }
        return lista;
    }
    
}
