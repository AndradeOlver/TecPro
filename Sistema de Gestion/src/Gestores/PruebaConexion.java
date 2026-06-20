    /*
     * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
     * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
     */
    package Gestores;

import DAO.ConexionSQL;
    import java.sql.Connection;

    /**
     *
     * @author equipo
     */
    public class PruebaConexion {

        /**
         * @param args the command line arguments
         */
        public static void main(String[] args) {
            // Llamamos al método que intenta conectar
            Connection miConexion = ConexionSQL.probarConexion();

            // Simulamos que hicimos alguna operación...

            // Cerramos la puerta
            ConexionSQL.cerrarConexion(miConexion);
        }

    }
