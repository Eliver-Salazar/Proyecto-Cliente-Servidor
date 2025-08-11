package org.example.Modelo.Servicios;

import org.example.DAO.LibroDAO;
import org.example.DAO.ReservaDAO;
import org.example.Modelo.Entidades.Libro;
import org.example.Modelo.Entidades.Reserva;
import org.example.Modelo.Util.Notificador;
import org.example.Database.Conexion;

import java.sql.*;

public class ReservaService {
    private final ReservaDAO reservaDAO = new ReservaDAO();
    private final LibroDAO libroDAO = new LibroDAO();
    private final Notificador notificador = new Notificador();

    public synchronized boolean crearReserva(int usuarioId, int libroId) {
        Libro libro = libroDAO.findById(libroId);
        if (libro == null) throw new IllegalArgumentException("Libro no existe");

        if (libro.isDisponible()) {
            reservaDAO.insertInmediata(usuarioId, libroId);
            libroDAO.marcarNoDisponible(libroId);
            return true; // inmediata
        } else {
            reservaDAO.insertEnCola(usuarioId, libroId);
            return false; // queda en cola
        }
    }

    public synchronized void atenderListaEspera(int libroId) {
        Reserva siguiente = reservaDAO.obtenerSiguientePendiente(libroId);
        if (siguiente != null) {
            notificador.enviarEmail(siguiente.getUsuarioId(),
                    "Libro disponible",
                    "Tu libro ya está disponible por 24 horas para retirarlo.");
            reservaDAO.marcarNotificado(siguiente.getId());
        } else {
            libroDAO.marcarDisponible(libroId);
        }
    }

    // Obtiene la siguiente reserva en estado PENDIENTE por orden de fecha
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

    // Marca la reserva como NOTIFICADO (compatibilidad con tu service)
    public boolean marcarNotificado(int reservaId) {
        String sql = "UPDATE Reserva SET estado='NOTIFICADO' WHERE id_reserva=?";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reservaId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // --- si no lo tienes aún, agrega este helper privado al final de la clase ---
    private Reserva map(ResultSet rs) throws SQLException {
        Reserva r = new Reserva();
        r.setId(rs.getInt("id_reserva"));
        r.setUsuarioId(rs.getInt("usuario_id"));
        r.setLibroId(rs.getInt("libro_id"));
        Timestamp t = rs.getTimestamp("fecha_reserva");
        if (t != null) r.setFechaReserva(t.toLocalDateTime());
        r.setEstado(rs.getString("estado"));
        return r;
    }

}
