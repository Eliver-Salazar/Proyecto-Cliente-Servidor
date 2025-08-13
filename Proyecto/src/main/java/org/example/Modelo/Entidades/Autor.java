package org.example.Modelo.Entidades;

/**
 * POJO Autor: id + nombre.
 * toString() devuelve el nombre (útil para mostrar en JComboBox).
 */
public class Autor {
    private int id;
    private String nombre;

    public Autor() {}
    public Autor(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }

    public void setId(int id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    @Override public String toString() { return nombre; }
}
