package org.example.DAO;

import org.example.Database.Conexion;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReporteDAO {

    public static class ItemConteo {
        public final int idLibro;
        public final String titulo;
        public final int conteo;
        public ItemConteo(int idLibro, String titulo, int conteo) {
            this.idLibro = idLibro; this.titulo = titulo; this.conteo = conteo;
        }
    }

    public List<ItemConteo> topPrestados(int limite, LocalDate desde, LocalDate hasta) {
        List<ItemConteo> out = new ArrayList<>();
        String sql = """
                SELECT l.id_libro, l.titulo, COUNT(*) AS cnt
                FROM Prestamo p JOIN Libro l ON l.id_libro = p.libro_id
                WHERE p.fecha_inicio BETWEEN ? AND ?
                GROUP BY l.id_libro, l.titulo
                ORDER BY cnt DESC
                LIMIT ?
                """;
        try (Connection conn = Conexion.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(desde));
            stmt.setDate(2, Date.valueOf(hasta));
            stmt.setInt(3, limite);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next())
                    out.add(new ItemConteo(rs.getInt(1), rs.getString(2), rs.getInt(3)));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return out;
    }

    public List<ItemConteo> menosUsados(int limite, LocalDate desde, LocalDate hasta) {
        List<ItemConteo> out = new ArrayList<>();
        String sql = """
                SELECT l.id_libro, l.titulo, COALESCE(COUNT(p.id_prestamo),0) AS cnt
                FROM Libro l
                LEFT JOIN Prestamo p ON p.libro_id = l.id_libro AND p.fecha_inicio BETWEEN ? AND ?
                GROUP BY l.id_libro, l.titulo
                ORDER BY cnt ASC, l.titulo ASC
                LIMIT ?
                """;
        try (Connection conn = Conexion.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(desde));
            stmt.setDate(2, Date.valueOf(hasta));
            stmt.setInt(3, limite);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next())
                    out.add(new ItemConteo(rs.getInt(1), rs.getString(2), rs.getInt(3)));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return out;
    }
}
