package org.example.Modelo.Entidades;

public class Usuario {
    private int id;
    private String nombre;
    private String correo;
    private String contraseña; // almacenada hasheada
    private String rol;        // ESTUDIANTE | BIBLIOTECARIO

    public Usuario() {}

    public Usuario(int id, String nombre, String correo, String contraseña, String rol) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.contraseña = contraseña;
        this.rol = rol;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getCorreo() { return correo; }
    public String getContraseña() { return contraseña; }
    public String getRol() { return rol; }

    public void setId(int id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setCorreo(String correo) { this.correo = correo; }
    public void setContraseña(String contraseña) { this.contraseña = contraseña; }
    public void setRol(String rol) { this.rol = rol; }
}
