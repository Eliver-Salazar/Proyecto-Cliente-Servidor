package org.example.Modelo.Servicios;

import org.example.DAO.LibroDAO;
import org.example.DAO.PrestamoDAO;
import org.example.Modelo.Entidades.Libro;
import org.example.Modelo.Entidades.Prestamo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Reglas de negocio de préstamos:
 * - Valida inputs y estado del libro
 * - Crea préstamo y ajusta disponibilidad
 * - Cierra préstamo, calcula multa y atiende lista de espera
 */
public class PrestamoService {
    private final PrestamoDAO prestamoDAO = new PrestamoDAO();
    private final LibroDAO libroDAO = new LibroDAO();
    private final ReservaService reservaService = new ReservaService();

    /** Conveniencia: presta 14 días por defecto. */
    public int prestar(int usuarioId, int libroId) { return prestar(usuarioId, libroId, 14); }

    /** Crea el préstamo con validaciones defensivas. */
    public int prestar(int usuarioId, int libroId, int dias) {
        if (usuarioId <= 0) throw new IllegalArgumentException("Usuario inválido.");
        if (libroId <= 0) throw new IllegalArgumentException("Libro inválido.");
        if (dias <= 0) dias = 14;

        // Verifica existencia del libro
        Libro libro = libroDAO.findById(libroId);
        if (libro == null) throw new IllegalArgumentException("El libro (id=" + libroId + ") no existe.");

        // No permitir prestar si ya está activo
        if (prestamoDAO.existePrestamoActivo(libroId))
            throw new IllegalStateException("El libro ya está prestado actualmente.");

        // Inserción del préstamo
        LocalDate hoy = LocalDate.now();
        LocalDate vto = hoy.plusDays(dias);
        Prestamo p = new Prestamo(0, usuarioId, libroId, hoy, vto);
        int id = prestamoDAO.insertar(p);
        if (id <= 0) throw new RuntimeException("No se pudo registrar el préstamo (ID no generado).");

        // Cambio de disponibilidad
        if (!libroDAO.marcarNoDisponible(libroId))
            throw new RuntimeException("Préstamo creado, pero no se pudo marcar el libro como no disponible.");

        return id;
    }

    /** Cierra un préstamo, calcula multa y atiende reservas. */
    public BigDecimal devolver(int idPrestamo) {
        if (idPrestamo <= 0) throw new IllegalArgumentException("ID de préstamo inválido.");
        Prestamo p = prestamoDAO.findById(idPrestamo);
        if (p == null) throw new IllegalArgumentException("Préstamo no existe.");

        LocalDate hoy = LocalDate.now();
        p.setFechaDevolucion(hoy);

        // Recalcula multa (₡200/día de atraso)
        LocalDate vto = (p.getFechaVencimiento() != null) ? p.getFechaVencimiento() : p.getFechaInicio().plusDays(14);
        BigDecimal multa = calcularMulta(vto, hoy);
        p.setMulta(multa);

        if (!prestamoDAO.cerrar(p))
            throw new RuntimeException("No se pudo cerrar el préstamo (actualizar devolución/multa).");

        // Atiende lista de espera o libera el libro
        reservaService.atenderListaEspera(p.getLibroId());
        return multa;
    }

    /** Multa = 0 si no hay atraso; si lo hay, ₡200 x día. */
    private BigDecimal calcularMulta(LocalDate vto, LocalDate devol) {
        long diasAtraso = ChronoUnit.DAYS.between(vto, devol);
        return (diasAtraso <= 0) ? BigDecimal.ZERO : new BigDecimal("200").multiply(BigDecimal.valueOf(diasAtraso));
    }
}
