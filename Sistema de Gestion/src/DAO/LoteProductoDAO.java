/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import Entidades.LoteProducto;
import Entidades.Producto;
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
public class LoteProductoDAO {
    public boolean registrarLotes(int codigoOrden, List<LoteProducto> lotes) {
        String sql = "INSERT INTO LoteProducto (OrdenCompra_Codigo, Producto_ID, CantidadIngresada, PrecioCompraIndividual) VALUES (?, ?, ?, ?)";
        try (Connection con = ConexionSQL.probarConexion(); 
             PreparedStatement ps = con.prepareStatement(sql)) {
            con.setAutoCommit(false);
            for (LoteProducto lote : lotes) {
                ps.setInt(1, codigoOrden);
                ps.setInt(2, lote.getProducto().getId());
                ps.setInt(3, lote.getCantidadIngresada());
                ps.setDouble(4, lote.getPrecioCompraIndividual());
                ps.addBatch();
            }
            ps.executeBatch();
            con.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al registrar lotes: " + e.getMessage());
            return false;
        }
    }

    public List<LoteProducto> obtenerLotesPorOrden(int codigoOrden) {
        List<LoteProducto> lista = new ArrayList<>();
        String sql = "SELECT * FROM LoteProducto WHERE OrdenCompra_Codigo = ?";
        try (Connection con = ConexionSQL.probarConexion(); 
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, codigoOrden);
            try (ResultSet rs = ps.executeQuery()) {
                ProductoDAO pDAO = new ProductoDAO(); // Reutilizamos tu traductor de productos
                while (rs.next()) {
                    Producto p = pDAO.buscarPorId(rs.getInt("Producto_ID"));
                    lista.add(new LoteProducto(rs.getInt("CantidadIngresada"), rs.getDouble("PrecioCompraIndividual"), p));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener lotes: " + e.getMessage());
        }
        return lista;
    }
}
