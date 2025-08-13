package org.example.Modelo.Servicios;

import org.example.DAO.PrestamoDAO;
import org.example.Modelo.Util.Notificador;

/**
 * Envía recordatorios por correo para préstamos que vencen mañana.
 * Se dispara manualmente desde la UI (PrestamosPanel) con un botón.
 * Si lo prefieres, puedes cronificarlo con un Timer.
 */
public class RecordatorioService {
    private final PrestamoDAO prestamoDAO = new PrestamoDAO();

    /** Retorna la cantidad de correos enviados */
    public int enviarRecordatoriosVencimiento() {
        var lista = prestamoDAO.vencenManiana();
        int enviados = 0;
        for (var p : lista) {
            // Ideal: buscar el correo real por UsuarioDAO. Aquí un fallback simple:
            String destinatario = "usuario-" + p.getUsuarioId() + "@example.com";
            Notificador.enviarCorreo(
                    destinatario,
                    "Recordatorio: tu préstamo vence mañana",
                    "Tu préstamo del libro ID " + p.getLibroId() +
                            " vence el " + p.getFechaVencimiento() + "."
            );
            enviados++;
        }
        return enviados;
    }
}

