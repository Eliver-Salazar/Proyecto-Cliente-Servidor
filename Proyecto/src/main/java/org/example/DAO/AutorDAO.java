package org.example.DAO;

import org.example.Database.Conexion;
import org.example.Modelo.Entidades.Autor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AutorDAO {

    public boolean existeAutor(String nombre) {
        String sql = "SELECT COUNT(*) FROM Autor WHERE nombre = ?";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // true si ya existe
            }
        } catch (SQLException e) {
            System.err.println("Error verificando autor: " + e.getMessage());
        }
        return false;
    }

    public boolean registrarAutor(Autor autor) {
        String sql = "INSERT INTO Autor(nombre) VALUES (?)";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, autor.getNombre());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Autor> listarAutores() {
        return listar(); // alias
    }

    public List<Autor> listar() {
        List<Autor> out = new ArrayList<>();
        String sql = "SELECT id_autor, nombre FROM Autor ORDER BY nombre";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(new Autor(rs.getInt("id_autor"), rs.getString("nombre")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return out;
    }
}
