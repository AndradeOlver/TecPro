/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 *
 * @author equipo
 */
public class ConexionSQL {
    

    public static Connection probarConexion() {
        Connection conexion = null;
        Properties propiedades = new Properties();

        // Leemos el archivo de configuración externo
        try (InputStream entrada = new FileInputStream("config.properties")) {
            propiedades.load(entrada);

            String servidor = propiedades.getProperty("db.servidor");
            String puerto = propiedades.getProperty("db.puerto");
            String baseDatos = propiedades.getProperty("db.nombre");
            String usuario = propiedades.getProperty("db.usuario");
            String clave = propiedades.getProperty("db.clave");

            // URL formateada para el driver de Microsoft
            String url = "jdbc:sqlserver://" + servidor + ":" + puerto + 
                         ";databaseName=" + baseDatos + 
                         ";encrypt=true;trustServerCertificate=true;";

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conexion = DriverManager.getConnection(url, usuario, clave);

            if (conexion != null) {
                System.out.println("¡Conexión establecida con éxito a SQL Server!");
            }

        } catch (java.io.FileNotFoundException e) {
            System.err.println("Error crítico: No se encontró el archivo config.properties en la raíz del proyecto.");
        } catch (ClassNotFoundException e) {
            System.err.println("Error crítico: No se encontró el Driver de SQL Server.");
            System.err.println("Detalle: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Error de conexión: Verifica que el servidor esté encendido y las credenciales sean correctas.");
            System.err.println("Detalle: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error inesperado al leer la configuración.");
            System.err.println("Detalle: " + e.getMessage());
        }
        
        return conexion;
    }

    public static void cerrarConexion(Connection conexion) {
        if (conexion != null) {
            try {
                conexion.close();
                System.out.println("Conexión cerrada correctamente.");
            } catch (SQLException e) {
                System.err.println("Error al intentar cerrar la conexión: " + e.getMessage());
            }
        }
    }
}