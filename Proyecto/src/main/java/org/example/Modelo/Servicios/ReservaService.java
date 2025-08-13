package org.example.Modelo.Servicios;

import org.example.DAO.LibroDAO;
import org.example.DAO.ReservaDAO;
import org.example.Modelo.Entidades.Libro;
import org.example.Modelo.Entidades.Reserva;
import org.example.Modelo.Util.Notificador;

/**
 * Service de reservas:
 * - Si el libro está disponible, crea reserva inmediata y bloquea el libro
 * - Si no, encola la reserva (PENDIENTE)
 * - Al devolverse un libro, notifica al siguiente o lo libera
 */
public class ReservaService {
    private final ReservaDAO reservaDAO = new ReservaDAO();
    private final LibroDAO libroDAO = new LibroDAO();
    private final Notificador notificador = new Notificador();

    /** Crea reserva: inmediata si el libro está disponible; en cola si no. */
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

    /** Atiende la lista de espera: notifica al siguiente o libera el libro si no hay pendientes. */
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
}
