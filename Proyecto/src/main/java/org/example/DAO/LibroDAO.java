package org.example.DAO;

import org.example.Database.Conexion;
import org.example.Modelo.Entidades.Libro;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de Libro.
 * Importante: la UI usa los métodos *Vista* para mostrar nombres de autor/categoría.
 */
public class LibroDAO {

    /** DTO liviano para la tabla de resultados (incluye nombres). */
    public static class LibroVista {
        public final int id;
        public final String titulo;
        public final String autor;
        public final String categoria;
        public final String isbn;
        public final boolean disponible;
        public LibroVista(int id, String titulo, String autor, String categoria, String isbn, boolean disponible) {
            this.id = id; this.titulo = titulo; this.autor = autor; this.categoria = categoria; this.isbn = isbn; this.disponible = disponible;
        }
    }

    /** Inserta un libro. */
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

    /** Devuelve sólo libros disponibles (modelo simple). */
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

    /** Busca por ID. */
    public Libro findById(int id) {
        String sql = "SELECT id_libro, titulo, autor_id, categoria_id, isbn, disponible FROM Libro WHERE id_libro=?";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return map(rs); }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    /** Marca disponibilidad del libro. */
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

    /** Reporte simple (ID, título, cantidad de préstamos). */
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

    /** Búsqueda para UI con JOINs para traer nombres de autor/categoría. */
    public List<LibroVista> buscarLibrosVista(String titulo, Integer autorId, Integer categoriaId, String disponibilidad) {
        List<LibroVista> out = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT l.id_libro, l.titulo, a.nombre AS autor, c.nombre AS categoria, l.isbn, l.disponible " +
                        "FROM Libro l JOIN Autor a ON a.id_autor = l.autor_id " +
                        "JOIN Categoria c ON c.id_categoria = l.categoria_id WHERE 1=1 "
        );

        if (titulo != null && !titulo.isEmpty()) sql.append("AND l.titulo LIKE ? ");
        if (autorId != null && autorId > 0)      sql.append("AND l.autor_id = ? ");
        if (categoriaId != null && categoriaId > 0) sql.append("AND l.categoria_id = ? ");
        if (disponibilidad != null && !"Todos".equalsIgnoreCase(disponibilidad)) sql.append("AND l.disponible = ? ");
        sql.append("ORDER BY l.titulo");

        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int i = 1;
            if (titulo != null && !titulo.isEmpty()) ps.setString(i++, "%" + titulo + "%");
            if (autorId != null && autorId > 0) ps.setInt(i++, autorId);
            if (categoriaId != null && categoriaId > 0) ps.setInt(i++, categoriaId);
            if (disponibilidad != null && !"Todos".equalsIgnoreCase(disponibilidad))
                ps.setBoolean(i++, "Disponible".equalsIgnoreCase(disponibilidad));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new LibroVista(
                            rs.getInt("id_libro"),
                            rs.getString("titulo"),
                            rs.getString("autor"),
                            rs.getString("categoria"),
                            rs.getString("isbn"),
                            rs.getBoolean("disponible")
                    ));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return out;
    }

    /** Sólo disponibles (con nombres) para vistas que lo necesiten. */
    public List<LibroVista> listarDisponiblesVista() {
        List<LibroVista> out = new ArrayList<>();
        String sql = "SELECT l.id_libro, l.titulo, a.nombre AS autor, c.nombre AS categoria, l.isbn, l.disponible " +
                "FROM Libro l JOIN Autor a ON a.id_autor = l.autor_id " +
                "JOIN Categoria c ON c.id_categoria = l.categoria_id " +
                "WHERE l.disponible = 1 ORDER BY l.titulo";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(new LibroVista(
                        rs.getInt("id_libro"),
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getString("categoria"),
                        rs.getString("isbn"),
                        rs.getBoolean("disponible")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return out;
    }

    /** Mapeo ResultSet -> Entidad. */
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
