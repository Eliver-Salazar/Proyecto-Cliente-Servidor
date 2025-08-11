package org.example.Modelo.Servicios;

import org.example.DAO.LibroDAO;
import org.example.DAO.PrestamoDAO;
import org.example.Modelo.Entidades.Prestamo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class PrestamoService {
    private final PrestamoDAO prestamoDAO = new PrestamoDAO();
    private final LibroDAO libroDAO = new LibroDAO();
    private final ReservaService reservaService = new ReservaService();

    public int prestar(int usuarioId, int libroId, int dias) {
        if (prestamoDAO.existePrestamoActivo(libroId)) throw new IllegalStateException("Libro actualmente prestado");
        LocalDate hoy = LocalDate.now();
        LocalDate vto = hoy.plusDays(dias);
        Prestamo p = new Prestamo(0, usuarioId, libroId, hoy, vto);
        int id = prestamoDAO.insertar(p);
        libroDAO.marcarNoDisponible(libroId);
        return id;
    }

    public BigDecimal devolver(int idPrestamo) {
        Prestamo p = prestamoDAO.findById(idPrestamo);
        if (p == null) throw new IllegalArgumentException("Préstamo no existe");

        LocalDate hoy = LocalDate.now();
        p.setFechaDevolucion(hoy);
        BigDecimal multa = calcularMulta(p.getFechaVencimiento(), hoy);
        p.setMulta(multa);
        prestamoDAO.cerrar(p);

        reservaService.atenderListaEspera(p.getLibroId());
        return multa;
    }

    private BigDecimal calcularMulta(LocalDate vto, LocalDate devol) {
        long atras = ChronoUnit.DAYS.between(vto, devol);
        if (atras <= 0) return BigDecimal.ZERO;
        return new BigDecimal("200").multiply(BigDecimal.valueOf(atras)); // ₡200 por día
    }
}
