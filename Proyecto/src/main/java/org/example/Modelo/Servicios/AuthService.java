package org.example.Modelo.Servicios;

import org.example.DAO.UsuarioDAO;
import org.example.Modelo.Entidades.Usuario;
import org.example.Modelo.Util.HashUtil;

/**
 * Servicio de Autenticación:
 * - Registro (hashea la contraseña antes de persistir).
 * - Login (intenta por hash y como compatibilidad por texto plano).
 */
public class AuthService {
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    /** Registra un usuario con la contraseña hasheada (SHA-256). */
    public boolean registrar(String nombre, String correo, String passwordPlano, String rol) {
        String hash = HashUtil.sha256(passwordPlano);
        Usuario u = new Usuario(0, nombre, correo, hash, rol);
        return usuarioDAO.registrarUsuario(u);
    }

    /** Autentica: primero por hash; si no, intenta por texto plano (compatibilidad). */
    public Usuario login(String correo, String passwordPlano) {
        String hash = HashUtil.sha256(passwordPlano);
        Usuario u = usuarioDAO.autenticarHash(correo, hash);
        if (u == null) {
            u = usuarioDAO.autenticar(correo, passwordPlano);
        }
        return u;
    }
}
