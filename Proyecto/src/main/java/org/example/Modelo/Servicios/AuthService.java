package org.example.Modelo.Servicios;

import org.example.DAO.UsuarioDAO;
import org.example.Modelo.Entidades.Usuario;
import org.example.Modelo.Util.HashUtil;

public class AuthService {
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public boolean registrar(String nombre, String correo, String passwordPlano, String rol) {
        String hash = HashUtil.sha256(passwordPlano);
        Usuario u = new Usuario(0, nombre, correo, hash, rol);
        return usuarioDAO.registrarUsuario(u);
    }

    public Usuario login(String correo, String passwordPlano) {
        String hash = HashUtil.sha256(passwordPlano);
        Usuario u = usuarioDAO.autenticarHash(correo, hash);
        if (u == null) { // compatibilidad si hay contraseñas sin hash
            u = usuarioDAO.autenticar(correo, passwordPlano);
        }
        return u;
    }
}
