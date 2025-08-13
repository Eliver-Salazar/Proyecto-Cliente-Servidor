package org.example.DAO;

import org.example.Database.Conexion;
import org.example.Modelo.Entidades.Categoria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de Categoría: evita duplicados, inserta y lista categorías.
 */
public class CategoriaDAO {

    /** Normaliza el nombre para comparar: trim y lower-case */
    private String norm(String s) { return (s == null) ? "" : s.trim().replaceAll("\\s+"," ").toLowerCase(); }

    /** Devuelve true si ya existe una categoría con el mismo nombre (insensible a mayúsculas/minúsculas). */
    public boolean existeNombre(String nombre) {
        String sql = "SELECT 1 FROM Categoria WHERE LOWER(TRIM(nombre)) = ? LIMIT 1";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, norm(nombre));
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /** Inserta una categoría si no existe (devuelve true si se insertó). */
    public boolean registrarCategoria(Categoria c) {
        String nombre = c.getNombre();
        if (existeNombre(nombre)) return false;

        String sql = "INSERT INTO Categoria(nombre) VALUES (?)";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre.trim().replaceAll("\\s+"," "));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /** Alias de compatibilidad con vistas */
    public List<Categoria> listarCategorias() { return listar(); }

    /** Lista categorías ordenadas por nombre. */
    public List<Categoria> listar() {
        List<Categoria> out = new ArrayList<>();
        String sql = "SELECT id_categoria, nombre FROM Categoria ORDER BY nombre";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(new Categoria(rs.getInt(1), rs.getString(2)));
        } catch (SQLException e) { e.printStackTrace(); }
        return out;
    }
}
