package DAO;

import Entidades.Cliente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public boolean registrar(Cliente c) {
        String sql = "INSERT INTO Cliente (ID, Nombre, Direccion, Correo, NumeroContacto) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = ConexionSQL.probarConexion(); 
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, c.getCodigo());
            ps.setString(2, c.getNombre());
            ps.setString(3, c.getDireccion());
            ps.setString(4, c.getCorreo());
            ps.setString(5, c.getNumeroContacto());
            
            ps.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al registrar cliente: " + e.getMessage());
            return false;
        }
    }

    public List<Cliente> obtenerTodos() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM Cliente";
        try (Connection con = ConexionSQL.probarConexion(); 
             PreparedStatement ps = con.prepareStatement(sql); 
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Cliente c = new Cliente(
                    rs.getInt("ID"),
                    rs.getString("Nombre"),
                    rs.getString("Direccion"),
                    rs.getString("Correo"),
                    rs.getString("NumeroContacto")
                );
                lista.add(c);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener clientes: " + e.getMessage());
        }
        return lista;
    }

    public Cliente buscarPorCodigo(int codigo) {
        String sql = "SELECT * FROM Cliente WHERE ID = ?";
        try (Connection con = ConexionSQL.probarConexion(); 
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Cliente(
                        rs.getInt("ID"),
                        rs.getString("Nombre"),
                        rs.getString("Direccion"),
                        rs.getString("Correo"),
                        rs.getString("NumeroContacto")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar cliente por código: " + e.getMessage());
        }
        return null; // Retorna null si no lo encuentra
    }

    public List<Cliente> buscarPorNombre(String nombre) {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM Cliente WHERE LOWER(Nombre) LIKE ?";
        try (Connection con = ConexionSQL.probarConexion(); 
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, "%" + nombre.toLowerCase() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Cliente c = new Cliente(
                        rs.getInt("ID"),
                        rs.getString("Nombre"),
                        rs.getString("Direccion"),
                        rs.getString("Correo"),
                        rs.getString("NumeroContacto")
                    );
                    lista.add(c);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar cliente por nombre: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizar(Cliente c) {
        String sql = "UPDATE Cliente SET Nombre=?, Direccion=?, Correo=?, NumeroContacto=? WHERE ID=?";
        try (Connection con = ConexionSQL.probarConexion(); 
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getDireccion());
            ps.setString(3, c.getCorreo());
            ps.setString(4, c.getNumeroContacto());
            ps.setInt(5, c.getCodigo());
            
            ps.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al actualizar cliente: " + e.getMessage());
            return false;
        }
    }
    public boolean eliminar(int id) {
    String sql = "DELETE FROM Cliente WHERE ID = ?";
    try (Connection con = ConexionSQL.probarConexion();
         PreparedStatement ps = con.prepareStatement(sql)) {
        
        ps.setInt(1, id);
        ps.execute();
        return true;
        
    } catch (SQLException e) {
        System.err.println("Error de integridad referencial: " + e.getMessage());
        return false;
    }
}
}