package org.example.Modelo.Util;

import org.example.DAO.UsuarioDAO;
import org.example.Modelo.Entidades.Usuario;

/**
 * Simula envío de correos en consola.
 * Incluye helper que resuelve el correo por usuarioId (usado por ReservaService).
 */
public class Notificador {

    /** Envío "crudo": imprime a consola. */
    public static void enviarCorreo(String destinatario, String asunto, String mensaje) {
        System.out.printf("[EMAIL] -> %s | %s%n%s%n", destinatario, asunto, mensaje);
    }

    /** Envío con saludo opcional por nombre. */
    public static void enviarCorreo(String nombre, String destinatario, String asunto, String mensaje) {
        enviarCorreo(destinatario, asunto, (nombre != null ? ("Hola " + nombre + ",\n") : "") + mensaje);
    }

    /**
     * Método de instancia con la firma que llama ReservaService:
     * Resuelve correo del usuario (si existe) y llama a enviarCorreo.
     */
    public void enviarEmail(int usuarioId, String asunto, String mensaje) {
        String correo = "usuario-" + usuarioId + "@example.com"; // fallback
        try {
            UsuarioDAO dao = new UsuarioDAO();
            Usuario u = dao.findById(usuarioId);
            if (u != null && u.getCorreo() != null && !u.getCorreo().isEmpty()) {
                correo = u.getCorreo();
            }
        } catch (Exception ignored) {}
        enviarCorreo(correo, asunto, mensaje);
    }
}
