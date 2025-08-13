package org.example.Modelo.Entidades;

/**
 * POJO Categoria: id + nombre.
 * toString() devuelve el nombre (útil para combos).
 */
public class Categoria {
    private int id;
    private String nombre;

    public Categoria() {}
    public Categoria(int id, String nombre) {
        this.id = id; this.nombre = nombre;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public void setId(int id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    @Override public String toString() { return nombre; }
}
