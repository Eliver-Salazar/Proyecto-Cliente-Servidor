package org.example.Modelo.Util;

import org.example.DAO.UsuarioDAO;
import org.example.Modelo.Entidades.Usuario;

public class Notificador {

    // Ya existente
    public static void enviarCorreo(String destinatario, String asunto, String mensaje) {
        System.out.printf("[EMAIL] -> %s | %s%n%s%n", destinatario, asunto, mensaje);
    }

    public static void enviarCorreo(String nombre, String destinatario, String asunto, String mensaje) {
        enviarCorreo(destinatario, asunto, (nombre != null ? ("Hola " + nombre + ",\n") : "") + mensaje);
    }

    // === NUEVO: método de instancia con firma EXACTA que usa ReservaService ===
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
