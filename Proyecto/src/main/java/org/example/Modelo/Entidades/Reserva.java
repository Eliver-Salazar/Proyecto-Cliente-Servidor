package org.example.Modelo.Entidades;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * POJO Reserva:
 * - Estados previstos: PENDIENTE, NOTIFICADO, CUMPLIDA.
 * - fechaReserva: cuándo se solicitó/creó.
 */
public class Reserva {
    private int id;
    private int usuarioId;
    private int libroId;
    private LocalDateTime fechaReserva;
    private String estado; // PENDIENTE, NOTIFICADO, CUMPLIDA

    public Reserva(int id, int usuarioId, int libroId, LocalDateTime fechaReserva, String estado) {
        this.id = id; this.usuarioId = usuarioId; this.libroId = libroId;
        this.fechaReserva = fechaReserva; this.estado = estado;
    }
    /**
     * Constructor de conveniencia usado por la UI:
     * (id, libroId, usuarioId, LocalDate) => asume estado PENDIENTE y hora 00:00.
     */
    public Reserva(int id, int libroId, int usuarioId, LocalDate fecha) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.libroId = libroId;
        this.fechaReserva = (fecha != null) ? fecha.atStartOfDay() : LocalDateTime.now();
        this.estado = "PENDIENTE";
    }

    public int getId() { return id; }
    public int getUsuarioId() { return usuarioId; }
    public int getLibroId() { return libroId; }
    public LocalDateTime getFechaReserva() { return fechaReserva; }
    public String getEstado() { return estado; }

    public void setId(int id) { this.id = id; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }
    public void setLibroId(int libroId) { this.libroId = libroId; }
    public void setFechaReserva(LocalDateTime fechaReserva) { this.fechaReserva = fechaReserva; }
    public void setEstado(String estado) { this.estado = estado; }
}
