package org.example.DAO;

import org.example.Database.Conexion;
import org.example.Modelo.Entidades.Categoria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    public boolean registrarCategoria(Categoria c) {
        String sql = "INSERT INTO Categoria(nombre) VALUES (?)";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /** Tu vista llama listarCategorias(): lo dejamos como alias */
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
