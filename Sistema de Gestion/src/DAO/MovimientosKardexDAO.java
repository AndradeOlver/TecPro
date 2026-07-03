/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;
import Entidades.MovimientosKardex;
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
public class MovimientosKardexDAO {
    public boolean registrar(MovimientosKardex mk) {
        String sql = "INSERT INTO MovimientosKardex (Producto_ID, FechaMovimiento, TipoMovimiento, CantidadFisica, ValorUnitario, SaldoCantidadActual, SaldoCostoPromedio) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionSQL.probarConexion(); 
             PreparedStatement ps = con.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS);) {
            
            ps.setInt(1, mk.getProducto().getId());
            ps.setDate(2, java.sql.Date.valueOf(mk.getFechaMovimiento()));
            ps.setString(3, mk.getTipoMovimiento());
            ps.setInt(4, mk.getCantidadFisica());
            ps.setDouble(5, mk.getValorUnitario());
            ps.setInt(6, mk.getSaldoCantidadActual());
            ps.setDouble(7, mk.getSaldoCostoPromedio());
            ps.execute();
            try (ResultSet rs = ps.getGeneratedKeys()) {
            if (rs.next()) {
                mk.setIdMovimiento(rs.getInt(1));
            }
}
            return true;
        } catch (SQLException e) {
            System.err.println("Error al registrar Kardex: " + e.getMessage());
            return false;
        }
    }

    public List<MovimientosKardex> obtenerPorProducto(int idProducto) {
        List<MovimientosKardex> lista = new ArrayList<>();
        String sql = "SELECT * FROM MovimientosKardex WHERE Producto_ID = ? ORDER BY FechaMovimiento ASC, IdMovimiento ASC";
        try (Connection con = ConexionSQL.probarConexion(); 
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new MovimientosKardex(
                        rs.getInt("IdMovimiento"),
                        rs.getDate("FechaMovimiento").toString(),
                        rs.getString("TipoMovimiento"),
                        rs.getInt("CantidadFisica"),
                        rs.getDouble("ValorUnitario"),
                        rs.getInt("SaldoCantidadActual"),
                        rs.getDouble("SaldoCostoPromedio")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al extraer Kardex: " + e.getMessage());
        }
        return lista;
    }
}
