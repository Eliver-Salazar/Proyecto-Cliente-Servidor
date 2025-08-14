package org.example.Modelo.Servicios;

import org.example.DAO.LibroDAO;
import org.example.DAO.PrestamoDAO;
import org.example.DAO.ReservaDAO;
import org.example.DAO.UsuarioDAO;
import org.example.Modelo.Entidades.Libro;
import org.example.Modelo.Entidades.Prestamo;
import org.example.Modelo.Entidades.Reserva;
import org.example.Modelo.Entidades.Usuario;
import org.example.Modelo.Util.Notificador;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

public class PrestamoService {
    private final PrestamoDAO prestamoDAO = new PrestamoDAO();
    private final LibroDAO libroDAO = new LibroDAO();
    private final ReservaDAO reservaDAO = new ReservaDAO();
    private final ReservaService reservaService = new ReservaService();

    /**
     * Presta un libro cumpliendo reglas:
     * - No prestar si hay un préstamo activo.
     * - Si existe una reserva NOTIFICADO de otra persona, BLOQUEA el préstamo (solo ese usuario puede retirarlo).
     * - Si existe una reserva NOTIFICADO del mismo usuario, se permite prestar y se marca la reserva CUMPLIDA.
     */
    public int prestar(int usuarioId, int libroId, int dias) {
        if (usuarioId <= 0) throw new IllegalArgumentException("Usuario inválido.");
        if (libroId <= 0) throw new IllegalArgumentException("Libro inválido.");
        if (dias <= 0) dias = 14;

        Libro libro = libroDAO.findById(libroId);
        if (libro == null) throw new IllegalArgumentException("El libro (id=" + libroId + ") no existe.");

        if (prestamoDAO.existePrestamoActivo(libroId))
            throw new IllegalStateException("El libro ya está prestado actualmente.");

        // ¿Hay una reserva NOTIFICADO para este libro?
        Optional<Reserva> reservaNotificada = reservaDAO.obtenerReservaNotificada(libroId);
        if (reservaNotificada.isPresent()) {
            Reserva r = reservaNotificada.get();
            if (r.getUsuarioId() != usuarioId) {
                // Está apartado para otra persona
                throw new IllegalStateException("Este libro está reservado (apartado) para otro usuario.");
            }
            // Es del mismo usuario: se permitirá y se marcará CUMPLIDA tras prestar
        }

        // Crear préstamo
        LocalDate hoy = LocalDate.now();
        LocalDate vto = hoy.plusDays(dias);
        Prestamo p = new Prestamo(0, usuarioId, libroId, hoy, vto);

        int id = prestamoDAO.insertar(p);
        if (id <= 0) throw new RuntimeException("No se pudo registrar el préstamo.");

        // Asegurar disponibilidad a NO (debe estarlo si había reserva)
        if (!libroDAO.marcarNoDisponible(libroId)) {
            throw new RuntimeException("Préstamo creado, pero no se pudo actualizar la disponibilidad del libro.");
        }

        // Si había reserva NOTIFICADO de este mismo usuario, la damos por CUMPLIDA
        reservaNotificada.ifPresent(r -> {
            if (r.getUsuarioId() == usuarioId) {
                reservaDAO.actualizarEstado(r.getId(), "CUMPLIDA");
            }
        });

        return id;
    }

    public BigDecimal devolver(int idPrestamo) {
        if (idPrestamo <= 0) throw new IllegalArgumentException("ID de préstamo inválido.");

        Prestamo p = prestamoDAO.findById(idPrestamo);
        if (p == null) throw new IllegalArgumentException("Préstamo no existe.");

        LocalDate hoy = LocalDate.now();
        p.setFechaDevolucion(hoy);

        LocalDate vto = p.getFechaVencimiento() != null ? p.getFechaVencimiento() : p.getFechaInicio().plusDays(14);
        BigDecimal multa = calcularMulta(vto, hoy);
        p.setMulta(multa);

        if (!prestamoDAO.cerrar(p)) throw new RuntimeException("No se pudo cerrar el préstamo.");

        // Atender lista de espera (esto ya BLOQUEA si notifica, o libera si no hay reservas)
        reservaService.atenderListaEspera(p.getLibroId());
        return multa;
    }

    private BigDecimal calcularMulta(LocalDate vto, LocalDate devol) {
        long diasAtraso = ChronoUnit.DAYS.between(vto, devol);
        if (diasAtraso <= 0) return BigDecimal.ZERO;
        return new BigDecimal("200").multiply(BigDecimal.valueOf(diasAtraso)); // ₡200 por día
    }

    /**
     * Lista los préstamos que vencen mañana
     */
    public List<Prestamo> listarVencenManiana() {
        return prestamoDAO.vencenManiana();
    }

    /**
     * Envia avisos por correo (simulado) a todos los usuarios con préstamos que vencen mañana.
     * Retorna cuántos correos/envíos se intentaron.
     */
    public int enviarAvisosVencimiento() {
        List<Prestamo> lista = prestamoDAO.vencenManiana();
        if (lista == null || lista.isEmpty()) return 0;

        UsuarioDAO usuarioDAO = new UsuarioDAO();
        Notificador notificador = new Notificador();

        int enviados = 0;
        for (Prestamo p : lista) {
            Usuario u = usuarioDAO.findById(p.getUsuarioId());
            Libro l = libroDAO.findById(p.getLibroId());

            String correo = (u != null && u.getCorreo() != null && !u.getCorreo().isEmpty())
                    ? u.getCorreo()
                    : ("usuario-" + p.getUsuarioId() + "@example.com");

            String nombre = (u != null ? u.getNombre() : null);
            String tituloLibro = (l != null ? l.getTitulo() : ("ID " + p.getLibroId()));

            String asunto = "Recordatorio: tu préstamo vence mañana";
            String mensaje = "Este es un recordatorio de que tu préstamo del libro \"" + tituloLibro + "\" " +
                    "vence el " + p.getFechaVencimiento() + ". Por favor realiza la devolución a tiempo para evitar multas.";

            // Usa el simulador (imprime en consola)
            Notificador.enviarCorreo(nombre, correo, asunto, mensaje);
            enviados++;
        }
        return enviados;
    }
}
