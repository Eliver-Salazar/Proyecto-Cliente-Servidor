package org.example.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Proveedor de conexiones JDBC a MySQL.
 */
public class Conexion {
    private static final String URL = "jdbc:mysql://localhost:3306/BibliotecaDigital";
    private static final String USER = "root";
    private static final String PASSWORD = "Parras992013";

    /**
     * Obtiene una conexión nueva a la BD.
     * El llamador es responsable de cerrarla (try-with-resources).
     */
    public static Connection getConexion() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // asegura carga del driver
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
