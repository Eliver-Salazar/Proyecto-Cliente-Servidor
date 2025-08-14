package org.example.Net.DTO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/** DTO liviano para préstamos. */
public class PrestamoDTO implements Serializable {
    public int id;
    public int usuarioId;
    public int libroId;
    public LocalDate fechaInicio;
    public LocalDate fechaVencimiento;
    public LocalDate fechaDevolucion;
    public BigDecimal multa;

    public PrestamoDTO(int id, int usuarioId, int libroId, LocalDate ini, LocalDate vto, LocalDate dev, BigDecimal multa) {
        this.id = id; this.usuarioId = usuarioId; this.libroId = libroId;
        this.fechaInicio = ini; this.fechaVencimiento = vto; this.fechaDevolucion = dev; this.multa = multa;
    }
}

