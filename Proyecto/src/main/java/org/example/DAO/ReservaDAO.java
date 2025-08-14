package org.example.DAO;

import org.example.Database.Conexion;
import org.example.Modelo.Entidades.Reserva;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;

public class ReservaDAO {

    /** Inserta una reserva “en cola”: libro prestado actualmente. */
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

    /**
     * Inserta una “reserva inmediata”: libro disponible ahora.
     * IMPORTANTE: deja estado = NOTIFICADO (el usuario fue avisado para retirar).
     * NO se marca CUMPLIDA hasta que el bibliotecario convierta a préstamo.
     */
    public int insertInmediata(int usuarioId, int libroId) {
        String sql = "INSERT INTO Reserva(usuario_id, libro_id, fecha_reserva, estado) VALUES (?,?,?,?)";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, usuarioId);
            ps.setInt(2, libroId);
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(4, "NOTIFICADO"); // ← cambio clave
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) return rs.getInt(1); }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    /** ¿Ya hay alguna reserva activa (PENDIENTE o NOTIFICADO) para este libro? */
    public boolean libroReservado(int libroId) {
        String sql = "SELECT 1 FROM Reserva WHERE libro_id=? AND estado IN ('PENDIENTE','NOTIFICADO') LIMIT 1";
        try (var conn = Conexion.getConexion();
             var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, libroId);
            try (var rs = ps.executeQuery()) { return rs.next(); }
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    /** Siguiente en cola (PENDIENTE) para ese libro, por fecha. */
    public Reserva obtenerSiguientePendiente(int libroId) {
        String sql = "SELECT id_reserva, usuario_id, libro_id, fecha_reserva, estado " +
                "FROM Reserva WHERE libro_id=? AND estado='PENDIENTE' " +
                "ORDER BY fecha_reserva ASC LIMIT 1";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, libroId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    /** Reserva “activa” para entrega: NOTIFICADO (el libro está apartado para ese usuario). */
    public Optional<Reserva> obtenerReservaNotificada(int libroId) {
        String sql = "SELECT id_reserva, usuario_id, libro_id, fecha_reserva, estado " +
                "FROM Reserva WHERE libro_id=? AND estado='NOTIFICADO' ORDER BY fecha_reserva ASC LIMIT 1";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, libroId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return Optional.empty();
    }

    /** Cambia el estado de una reserva. */
    public boolean actualizarEstado(int idReserva, String nuevoEstado) {
        String sql = "UPDATE Reserva SET estado=? WHERE id_reserva=?";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, idReserva);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    /** Búsqueda por id. */
    public Reserva findById(int id) {
        String sql = "SELECT id_reserva, usuario_id, libro_id, fecha_reserva, estado FROM Reserva WHERE id_reserva=?";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return map(rs); }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    /** Registrar genérico usado por la vista de Estudiante. */
    public boolean registrarReserva(Reserva r) {
        String sql = "INSERT INTO Reserva(usuario_id, libro_id, fecha_reserva, estado) VALUES (?,?,?,?)";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, r.getUsuarioId());
            ps.setInt(2, r.getLibroId());
            ps.setTimestamp(3, Timestamp.valueOf(r.getFechaReserva()));
            ps.setString(4, r.getEstado());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /** Marcar NOTIFICADO (alias). */
    public boolean marcarNotificado(int reservaId) {
        return actualizarEstado(reservaId, "NOTIFICADO");
    }

    // ==== mapeo interno ====
    private Reserva map(ResultSet rs) throws SQLException {
        return new Reserva(
                rs.getInt("id_reserva"),
                rs.getInt("usuario_id"),
                rs.getInt("libro_id"),
                rs.getTimestamp("fecha_reserva").toLocalDateTime(),
                rs.getString("estado")
        );
    }
}
