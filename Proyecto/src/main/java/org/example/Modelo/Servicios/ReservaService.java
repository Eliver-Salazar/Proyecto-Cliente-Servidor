package org.example.Modelo.Servicios;

import org.example.DAO.LibroDAO;
import org.example.DAO.ReservaDAO;
import org.example.Modelo.Entidades.Libro;
import org.example.Modelo.Entidades.Reserva;
import org.example.Modelo.Util.Notificador;


public class ReservaService {
    private final ReservaDAO reservaDAO = new ReservaDAO();
    private final LibroDAO libroDAO = new LibroDAO();
    private final Notificador notificador = new Notificador();

    /**
     * Crea una reserva:
     * - Si el libro está disponible: crea NOTIFICADO y BLOQUEA el libro (no disponible) para ese usuario.
     * - Si NO está disponible: crea PENDIENTE, no cambia disponibilidad.
     */
    public synchronized boolean crearReserva(int usuarioId, int libroId) {
        Libro libro = libroDAO.findById(libroId);
        if (libro == null) throw new IllegalArgumentException("Libro no existe");

        if (libro.isDisponible()) {
            // Reserva inmediata => NOTIFICADO y bloquear el ejemplar
            int id = reservaDAO.insertInmediata(usuarioId, libroId);
            if (id > 0) {
                libroDAO.marcarNoDisponible(libroId); // ← apartar el libro
                notificador.enviarEmail(usuarioId,
                        "Reserva disponible",
                        "Tu libro está apartado por 24 horas. Acércate al mostrador para retirarlo.");
                return true;
            }
            return false;
        } else {
            // En cola
            int id = reservaDAO.insertEnCola(usuarioId, libroId);
            return id > 0;
        }
    }

    /**
     * Al devolver un libro:
     * - Si hay PENDIENTE: notifica al siguiente, marca NOTIFICADO y BLOQUEA el libro.
     * - Si no hay reservas pendientes: libera el libro (disponible = true).
     */
    public synchronized void atenderListaEspera(int libroId) {
        Reserva siguiente = reservaDAO.obtenerSiguientePendiente(libroId);
        if (siguiente != null) {
            notificador.enviarEmail(siguiente.getUsuarioId(),
                    "Libro disponible",
                    "Tu libro está disponible por 24 horas para retirarlo.");
            reservaDAO.marcarNotificado(siguiente.getId());
            // Bloquear el ejemplar para ese usuario
            libroDAO.marcarNoDisponible(libroId);
        } else {
            // Nadie espera: queda disponible
            libroDAO.marcarDisponible(libroId);
        }
    }
}
