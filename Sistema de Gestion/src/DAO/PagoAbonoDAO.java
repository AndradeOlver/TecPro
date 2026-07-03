/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import Entidades.PagoAbono;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author equipo
 */
public class PagoAbonoDAO {
    public boolean registrar(PagoAbono abono) {
        String sql = "INSERT INTO PagoAbono (Pedido_Codigo, FechaAbono, MontoAbonado) VALUES (?, ?, ?)";
        
        try (Connection con = ConexionSQL.probarConexion(); 
             PreparedStatement ps = con.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, abono.getPedido().getCodigo());
            ps.setDate(2, java.sql.Date.valueOf(abono.getFechaAbono()));
            ps.setDouble(3, abono.getMontoAbonado());
            
            ps.execute();
            
            // Recuperamos el código del recibo que acaba de generar SQL Server
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    abono.setCodigoRecibo(rs.getInt(1));
                }
            }
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error al registrar Pago/Abono: " + e.getMessage());
            return false;
        }
    }
    
}
