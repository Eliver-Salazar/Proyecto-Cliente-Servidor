package org.example.DAO;

import org.example.Database.Conexion;
import org.example.Modelo.Entidades.Reserva;

import java.sql.*;
import java.time.LocalDateTime;

public class ReservaDAO {

    /** Tu vista llama registrarReserva(Reserva): lo implementamos aquí */
    public boolean registrarReserva(Reserva r) {
        String sql = "INSERT INTO Reserva(usuario_id, libro_id, fecha_reserva, estado) VALUES (?,?,?,?)";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, r.getUsuarioId());
            ps.setInt(2, r.getLibroId());
            ps.setTimestamp(3, Timestamp.valueOf(
                    r.getFechaReserva() != null ? r.getFechaReserva() : LocalDateTime.now()));
            ps.setString(4, r.getEstado() != null ? r.getEstado() : "PENDIENTE");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /** ¿Ya hay reserva activa del libro? (pendiente o notificado) */
    public boolean libroReservado(int libroId) {
        String sql = "SELECT 1 FROM Reserva WHERE libro_id=? AND estado IN ('PENDIENTE','NOTIFICADO') ORDER BY fecha_reserva ASC LIMIT 1";
        try (var conn = Conexion.getConexion();
             var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, libroId);
            try (var rs = ps.executeQuery()) { return rs.next(); }
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    // Métodos de servicio que ya te dejé antes (por si los usas en otras partes)
    public int insertInmediata(int usuarioId, int libroId) {
        String sql = "INSERT INTO Reserva(usuario_id, libro_id, fecha_reserva, estado) VALUES (?,?,?,?)";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, usuarioId);
            ps.setInt(2, libroId);
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(4, "CUMPLIDA");
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) return rs.getInt(1); }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public int insertEnCola(int usuarioId, int libroId) {
        String sql = "INSERT INTO Reserva(usuario_id, libro_id, fecha_reserva, estado) VALUES (?,?,?,?)";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, usuarioId);
            ps.setInt(2, libroId);
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(4, "PENDIENTE");
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) return rs.getInt(1); }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    // Devuelve la siguiente reserva en cola para ese libro (la más antigua)
    public Reserva obtenerSiguientePendiente(int libroId) {
        String sql = "SELECT id_reserva, usuario_id, libro_id, fecha_reserva, estado " +
                "FROM Reserva WHERE libro_id=? AND estado='PENDIENTE' " +
                "ORDER BY fecha_reserva ASC LIMIT 1";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, libroId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapReserva(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // Marca como notificado al usuario de esa reserva
    public boolean marcarNotificado(int reservaId) {
        // si NO tienes columna fecha_notificacion, elimina esa parte
        String sql = "UPDATE Reserva SET estado='NOTIFICADO' WHERE id_reserva=?";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reservaId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // --- ayudante para mapear ResultSet -> Reserva (ajusta al constructor que tengas)
    private Reserva mapReserva(ResultSet rs) throws SQLException {
        return new Reserva(
                rs.getInt("id_reserva"),
                rs.getInt("usuario_id"),
                rs.getInt("libro_id"),
                rs.getTimestamp("fecha_reserva").toLocalDateTime(),
                rs.getString("estado")
        );
    }
}
