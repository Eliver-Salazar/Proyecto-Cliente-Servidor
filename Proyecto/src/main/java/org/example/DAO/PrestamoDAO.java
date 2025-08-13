package org.example.DAO;

import org.example.Database.Conexion;
import org.example.Modelo.Entidades.Prestamo;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDAO {


    public boolean existePrestamoActivo(int libroId) { return estaPrestadoActivo(libroId); }


    public boolean estaPrestadoActivo(int libroId) {
        String sql = "SELECT 1 FROM Prestamo WHERE libro_id=? AND fecha_devolucion IS NULL";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, libroId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /** Devuelve la fecha de vencimiento del préstamo activo (o cadena vacía) */
    public String obtenerFechaDevolucionActiva(int libroId) {
        String sql = "SELECT fecha_vencimiento FROM Prestamo WHERE libro_id=? AND fecha_devolucion IS NULL ORDER BY id_prestamo DESC LIMIT 1";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, libroId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Date d = rs.getDate(1);
                    return (d != null) ? d.toLocalDate().toString() : "";
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return "";
    }

    public int insertar(Prestamo p) {
        String sql = "INSERT INTO Prestamo(usuario_id, libro_id, fecha_inicio, fecha_vencimiento) VALUES (?,?,?,?)";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, p.getUsuarioId());
            ps.setInt(2, p.getLibroId());
            ps.setDate(3, Date.valueOf(p.getFechaInicio()));
            ps.setDate(4, Date.valueOf(p.getFechaVencimiento()));
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) return rs.getInt(1); }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public Prestamo findById(int id) {
        String sql = "SELECT * FROM Prestamo WHERE id_prestamo=?";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return map(rs); }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean cerrar(Prestamo p) {
        String sql = "UPDATE Prestamo SET fecha_devolucion=?, multa=? WHERE id_prestamo=?";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(p.getFechaDevolucion()));
            ps.setBigDecimal(2, p.getMulta());
            ps.setInt(3, p.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public List<Prestamo> historialPorUsuario(int usuarioId) {
        List<Prestamo> out = new ArrayList<>();
        String sql = "SELECT * FROM Prestamo WHERE usuario_id=? ORDER BY fecha_inicio DESC";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) out.add(map(rs)); }
        } catch (SQLException e) { e.printStackTrace(); }
        return out;
    }

    public List<Prestamo> listarActivos() {
        List<Prestamo> out = new ArrayList<>();
        String sql = "SELECT * FROM Prestamo WHERE fecha_devolucion IS NULL ORDER BY fecha_inicio DESC";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return out;
    }

    private Prestamo map(ResultSet rs) throws SQLException {
        Prestamo p = new Prestamo(
                rs.getInt("id_prestamo"),
                rs.getInt("usuario_id"),
                rs.getInt("libro_id"),
                rs.getDate("fecha_inicio").toLocalDate(),
                rs.getDate("fecha_vencimiento").toLocalDate()
        );
        Date d = rs.getDate("fecha_devolucion");
        if (d != null) p.setFechaDevolucion(d.toLocalDate());
        p.setMulta(rs.getBigDecimal("multa"));
        return p;
    }

    public List<Prestamo> vencenManiana() {
        List<Prestamo> out = new ArrayList<>();
        String sql = "SELECT * FROM Prestamo WHERE fecha_devolucion IS NULL AND fecha_vencimiento = CURDATE() + INTERVAL 1 DAY";
        try (Connection c = Conexion.getConexion();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return out;
    }

}
