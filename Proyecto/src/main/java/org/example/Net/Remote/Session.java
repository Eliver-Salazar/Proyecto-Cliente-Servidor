package org.example.Net.Remote;

/** Sesión simple del lado cliente. */
public class Session {
    private int usuarioId;
    private String nombre;
    private String rol;

    public Session(int usuarioId, String nombre, String rol) {
        this.usuarioId = usuarioId; this.nombre = nombre; this.rol = rol;
    }

    public int getUsuarioId() { return usuarioId; }
    public String getNombre() { return nombre; }
    public String getRol() { return rol; }
}

