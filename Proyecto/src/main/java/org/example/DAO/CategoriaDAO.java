package org.example.DAO;

import org.example.Database.Conexion;
import org.example.Modelo.Entidades.Categoria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    public boolean existeNombre(String nombre) {
        String sql = "SELECT 1 FROM Categoria WHERE LOWER(nombre) = LOWER(?) LIMIT 1";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre.trim());
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean registrarCategoria(Categoria c) {
        String nombre = normalizar(c.getNombre());
        if (existeNombre(nombre)) return false; // ya existe

        String sql = "INSERT INTO Categoria(nombre) VALUES (?)";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            return ps.executeUpdate() > 0;
        } catch (java.sql.SQLIntegrityConstraintViolationException dup) {
            // respaldo si solo confías en la BD
            return false;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private String normalizar(String s) {
        if (s == null) return "";
        // quita espacios extra y estandariza capitalización básica
        String t = s.trim().replaceAll("\\s+", " ");
        return t;
    }

    public List<Categoria> listarCategorias() {
        return listar(); // alias
    }

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
