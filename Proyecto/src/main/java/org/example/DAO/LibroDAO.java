package org.example.DAO;

import org.example.Database.Conexion;
import org.example.Modelo.Entidades.Libro;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibroDAO {

    public boolean registrarLibro(Libro libro) {
        String sql = "INSERT INTO Libro (titulo, autor_id, categoria_id, isbn, disponible) VALUES (?,?,?,?,?)";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, libro.getTitulo());
            ps.setInt(2, libro.getAutorId());
            ps.setInt(3, libro.getCategoriaId());
            ps.setString(4, libro.getIsbn());
            ps.setBoolean(5, libro.isDisponible());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /** Compatibilidad con tu vista: disponibilidad viene como String */
    public List<Libro> buscarLibros(String titulo, int autorId, int categoriaId, String disponibilidad) {
        Boolean disp = null;
        if ("Disponible".equalsIgnoreCase(disponibilidad)) disp = true;
        else if ("No disponible".equalsIgnoreCase(disponibilidad)) disp = false;
        return buscar(titulo, autorId == 0 ? null : autorId, categoriaId == 0 ? null : categoriaId, disp);
    }

    /** Implementación base parametrizable */
    public List<Libro> buscar(String titulo, Integer autorId, Integer categoriaId, Boolean disponible) {
        List<Libro> out = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT id_libro, titulo, autor_id, categoria_id, isbn, disponible FROM Libro WHERE 1=1 ");
        if (titulo != null && !titulo.isEmpty()) sql.append("AND titulo LIKE ? ");
        if (autorId != null) sql.append("AND autor_id = ? ");
        if (categoriaId != null) sql.append("AND categoria_id = ? ");
        if (disponible != null) sql.append("AND disponible = ? ");
        sql.append("ORDER BY titulo");

        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int i = 1;
            if (titulo != null && !titulo.isEmpty()) ps.setString(i++, "%" + titulo + "%");
            if (autorId != null) ps.setInt(i++, autorId);
            if (categoriaId != null) ps.setInt(i++, categoriaId);
            if (disponible != null) ps.setBoolean(i++, disponible);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return out;
    }

    /** Usado por EstudianteView.ReservaPanel */
    public List<Libro> listarLibrosDisponibles() {
        List<Libro> out = new ArrayList<>();
        String sql = "SELECT id_libro, titulo, autor_id, categoria_id, isbn, disponible FROM Libro WHERE disponible=1 ORDER BY titulo";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return out;
    }

    public Libro findById(int id) {
        String sql = "SELECT id_libro, titulo, autor_id, categoria_id, isbn, disponible FROM Libro WHERE id_libro=?";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return map(rs); }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean marcarDisponible(int idLibro) { return updateDisponibilidad(idLibro, true); }
    public boolean marcarNoDisponible(int idLibro) { return updateDisponibilidad(idLibro, false); }

    private boolean updateDisponibilidad(int idLibro, boolean disp) {
        String sql = "UPDATE Libro SET disponible=? WHERE id_libro=?";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, disp);
            ps.setInt(2, idLibro);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    /** Compatibilidad con tu ReportesPanel: devuelve List<Object[]> (ID, Título, Total) */
    public List<Object[]> obtenerReportePrestamos() {
        List<Object[]> out = new ArrayList<>();
        String sql = """
            SELECT l.id_libro, l.titulo, COUNT(p.id_prestamo) AS total
            FROM Libro l
            LEFT JOIN Prestamo p ON p.libro_id = l.id_libro
            GROUP BY l.id_libro, l.titulo
            ORDER BY total DESC, l.titulo ASC
        """;
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(new Object[]{ rs.getInt(1), rs.getString(2), rs.getInt(3) });
        } catch (SQLException e) { e.printStackTrace(); }
        return out;
    }

    private Libro map(ResultSet rs) throws SQLException {
        return new Libro(
                rs.getInt("id_libro"),
                rs.getString("titulo"),
                rs.getInt("autor_id"),
                rs.getInt("categoria_id"),
                rs.getString("isbn"),
                rs.getBoolean("disponible")
        );
    }
}
