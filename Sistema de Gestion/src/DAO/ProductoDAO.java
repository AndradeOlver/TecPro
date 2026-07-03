package DAO;

import Entidades.Producto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    public boolean registrar(Producto p) {
        String sql = "INSERT INTO Producto (Descripcion, PrecioVentaBase, PrecioCompra, Stock) VALUES (?, ?, ?, ?)";
        try (Connection con = ConexionSQL.probarConexion(); 
             PreparedStatement ps = con.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS);) {
            
          
            ps.setString(1, p.getDescripcion());
            
            // Validación para Precio de Venta
            if (p.getPrecioVentaBase() != null) {
                ps.setDouble(2, p.getPrecioVentaBase());
            } else {
                ps.setNull(2, java.sql.Types.DECIMAL);
            }
            
            // Validación para Precio de Compra
            if (p.getPrecioCompra() != null) {
                ps.setDouble(3, p.getPrecioCompra());
            } else {
                ps.setNull(3, java.sql.Types.DECIMAL);
            }
            
            // Validación para Stock
            if (p.getStock() != null) {
                ps.setInt(4, p.getStock());
            } else {
                ps.setNull(4, java.sql.Types.INTEGER);
            }
            
            ps.execute();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    p.setId(rs.getInt(1)); 
                }
            }
            return true;
        } catch (SQLException e) {
            System.err.println("Error al registrar producto: " + e.getMessage());
            return false;
        }
    }

    public List<Producto> obtenerTodos() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM Producto";
        try (Connection con = ConexionSQL.probarConexion(); 
             PreparedStatement ps = con.prepareStatement(sql); 
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Producto p = new Producto(
                    rs.getInt("ID"),
                    rs.getString("Descripcion"),
                    rs.getDouble("PrecioVentaBase"),
                    rs.getDouble("PrecioCompra"),
                    rs.getInt("Stock")
                );
                lista.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener productos: " + e.getMessage());
        }
        return lista;
    }

    public Producto buscarPorId(int id) {
        String sql = "SELECT * FROM Producto WHERE ID = ?";
        try (Connection con = ConexionSQL.probarConexion(); 
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Producto(
                        rs.getInt("ID"),
                        rs.getString("Descripcion"),
                        rs.getDouble("PrecioVentaBase"),
                        rs.getDouble("PrecioCompra"),
                        rs.getInt("Stock")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar producto por ID: " + e.getMessage());
        }
        return null;
    }

    public List<Producto> buscarPorDescripcion(String descripcion) {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM Producto WHERE LOWER(Descripcion) LIKE ?";
        try (Connection con = ConexionSQL.probarConexion(); 
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, "%" + descripcion.toLowerCase() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Producto p = new Producto(
                        rs.getInt("ID"),
                        rs.getString("Descripcion"),
                        rs.getDouble("PrecioVentaBase"),
                        rs.getDouble("PrecioCompra"),
                        rs.getInt("Stock")
                    );
                    lista.add(p);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar producto por descripción: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizar(Producto p) {
     String sql = "UPDATE Producto SET Descripcion=?, PrecioVentaBase=?, PrecioCompra=?, Stock=? WHERE ID=?";
        try (Connection con = ConexionSQL.probarConexion(); 
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, p.getDescripcion());
            
            // Validación para Precio de Venta
            if (p.getPrecioVentaBase() != null) {
                ps.setDouble(2, p.getPrecioVentaBase());
            } else {
                ps.setNull(2, java.sql.Types.DECIMAL);
            }
            
            // Validación para Precio de Compra
            if (p.getPrecioCompra() != null) {
                ps.setDouble(3, p.getPrecioCompra());
            } else {
                ps.setNull(3, java.sql.Types.DECIMAL);
            }
            
            // Validación para Stock
            if (p.getStock() != null) {
                ps.setInt(4, p.getStock());
            } else {
                ps.setNull(4, java.sql.Types.INTEGER);
            }
            
            ps.setInt(5, p.getId());
            
            ps.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al actualizar producto: " + e.getMessage());
            return false;
        }
    }
}