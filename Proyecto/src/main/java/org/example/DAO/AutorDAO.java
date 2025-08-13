package org.example.DAO;

import org.example.Database.Conexion;
import org.example.Modelo.Entidades.Autor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de Autor: inserción, validación de duplicados y listado.
 */
public class AutorDAO {

    /** Normaliza el nombre para comparar: trim y lower-case */
    private String norm(String s) { return (s == null) ? "" : s.trim().replaceAll("\\s+"," ").toLowerCase(); }

    /**
     * Verifica si ya existe un autor con el mismo nombre (insensible a mayúsculas/minúsculas y espacios).
     */
    public boolean existeAutor(String nombre) {
        String sql = "SELECT 1 FROM Autor WHERE LOWER(TRIM(nombre)) = ? LIMIT 1";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, norm(nombre));
            try (ResultSet rs = stmt.executeQuery()) { return rs.next(); }
        } catch (SQLException e) {
            System.err.println("Error verificando autor: " + e.getMessage());
            return false;
        }
    }

    /**
     * Inserta un autor si no existe. Devuelve true si se insertó.
     */
    public boolean registrarAutor(Autor autor) {
        String nombre = autor.getNombre();
        if (existeAutor(nombre)) return false;

        String sql = "INSERT INTO Autor(nombre) VALUES (?)";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre.trim().replaceAll("\\s+"," "));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Alias de compatibilidad con vistas */
    public List<Autor> listarAutores() { return listar(); }

    /** Lista todos los autores ordenados por nombre. */
    public List<Autor> listar() {
        List<Autor> out = new ArrayList<>();
        String sql = "SELECT id_autor, nombre FROM Autor ORDER BY nombre";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(new Autor(rs.getInt("id_autor"), rs.getString("nombre")));
        } catch (SQLException e) { e.printStackTrace(); }
        return out;
    }
}
