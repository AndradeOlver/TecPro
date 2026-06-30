/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author equipo
 */
public class ConexionSQL {
    // 1. Los Parámetros de Ruta (Cadena de Conexión)
    // Reemplaza "NombreDeTuBD", "tu_usuario" y "tu_contraseña" con tus datos reales.
private static final String SERVIDOR = "localhost";
    private static final String PUERTO = "1433";
    private static final String BASE_DATOS = "GESTOR";
    private static final String USUARIO = "sa";
    private static final String CLAVE = "olverandradesql";
    
    // URL formateada para el driver de Microsoft
    private static final String URL = "jdbc:sqlserver://" + SERVIDOR + ":" + PUERTO + 
                                      ";databaseName=" + BASE_DATOS + 
                                      ";encrypt=true;trustServerCertificate=true;";

    public static Connection probarConexion() {
        Connection conexion = null;
        
        
        try {
            
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            
            
            conexion = DriverManager.getConnection(URL, USUARIO, CLAVE);
            
           
            if (conexion != null) {
                System.out.println("¡Conexión establecida con éxito a SQL Server!");
            }
            
        } catch (ClassNotFoundException e) {
            System.err.println("Error crítico: No se encontró el Driver de SQL Server.");
            System.err.println("Detalle: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Error de conexión: Verifica que el servidor esté encendido y las credenciales sean correctas.");
            System.err.println("Detalle: " + e.getMessage());
        }
        
        return conexion;
    }

    // 6. La Limpieza (Método auxiliar para cerrar la conexión cuando termines de usarla)
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
    

