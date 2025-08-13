package org.example.Modelo.Entidades;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * POJO Prestamo:
 * - Registra inicio, vencimiento y eventualmente devolución y multa.
 * - Si fechaDevolucion es null => préstamo activo.
 */
public class Prestamo {
    private int id;
    private int usuarioId;
    private int libroId;
    private LocalDate fechaInicio;
    private LocalDate fechaVencimiento;
    private LocalDate fechaDevolucion; // null si activo
    private BigDecimal multa;          // null si no aplica

    public Prestamo() {}

    public Prestamo(int id, int usuarioId, int libroId, LocalDate fechaInicio, LocalDate fechaVencimiento) {
        this.id = id; this.usuarioId = usuarioId; this.libroId = libroId;
        this.fechaInicio = fechaInicio; this.fechaVencimiento = fechaVencimiento;
    }

    public int getId() { return id; }
    public int getUsuarioId() { return usuarioId; }
    public int getLibroId() { return libroId; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public LocalDate getFechaDevolucion() { return fechaDevolucion; }
    public BigDecimal getMulta() { return multa; }

    public void setId(int id) { this.id = id; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }
    public void setLibroId(int libroId) { this.libroId = libroId; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
    public void setFechaDevolucion(LocalDate fechaDevolucion) { this.fechaDevolucion = fechaDevolucion; }
    public void setMulta(BigDecimal multa) { this.multa = multa; }
}
