package DAO;

import Entidades.Proveedor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProveedorDAO {

    public boolean registrar(Proveedor p) {
        String sql = "INSERT INTO Proveedor (RUC, RazonSocial, Contacto, Direccion, NumeroContacto, CorreoContacto) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionSQL.probarConexion(); 
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, p.getRuc());
            ps.setString(2, p.getRazonSocial());
            ps.setString(3, p.getContacto());
            ps.setString(4, p.getDireccion());
            ps.setString(5, p.getNumeroContacto());
            ps.setString(6, p.getCorreoContacto());
            
            ps.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al registrar proveedor: " + e.getMessage());
            return false;
        }
    }

    public List<Proveedor> obtenerTodos() {
        List<Proveedor> lista = new ArrayList<>();
        String sql = "SELECT * FROM Proveedor";
        try (Connection con = ConexionSQL.probarConexion(); 
             PreparedStatement ps = con.prepareStatement(sql); 
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Proveedor p = new Proveedor(
                    rs.getString("RUC"),
                    rs.getString("RazonSocial"),
                    rs.getString("Direccion"),
                    rs.getString("Contacto"),
                    rs.getString("NumeroContacto"),
                    rs.getString("CorreoContacto")
                );
                lista.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener proveedores: " + e.getMessage());
        }
        return lista;
    }

    public Proveedor buscarPorRuc(String ruc) {
        String sql = "SELECT * FROM Proveedor WHERE RUC = ?";
        try (Connection con = ConexionSQL.probarConexion(); 
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, ruc);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Proveedor(
                        rs.getString("RUC"),
                        rs.getString("RazonSocial"),
                        rs.getString("Direccion"),
                        rs.getString("Contacto"),
                        rs.getString("NumeroContacto"),
                        rs.getString("CorreoContacto")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar proveedor por RUC: " + e.getMessage());
        }
        return null;
    }

    public List<Proveedor> buscarPorNombre(String nombre) {
        List<Proveedor> lista = new ArrayList<>();
        String sql = "SELECT * FROM Proveedor WHERE LOWER(RazonSocial) LIKE ?";
        try (Connection con = ConexionSQL.probarConexion(); 
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, "%" + nombre.toLowerCase() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Proveedor p = new Proveedor(
                        rs.getString("RUC"),
                        rs.getString("RazonSocial"),
                        rs.getString("Direccion"),
                        rs.getString("Contacto"),
                        rs.getString("NumeroContacto"),
                        rs.getString("CorreoContacto")
                    );
                    lista.add(p);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar proveedor por nombre: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizar(Proveedor p) {
        String sql = "UPDATE Proveedor SET RazonSocial=?, Contacto=?, Direccion=?, NumeroContacto=?, CorreoContacto=? WHERE RUC=?";
        try (Connection con = ConexionSQL.probarConexion(); 
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, p.getRazonSocial());
            ps.setString(2, p.getContacto());
            ps.setString(3, p.getDireccion());
            ps.setString(4, p.getNumeroContacto());
            ps.setString(5, p.getCorreoContacto());
            ps.setString(6, p.getRuc());
            
            ps.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al actualizar proveedor: " + e.getMessage());
            return false;
        }
    }
}
