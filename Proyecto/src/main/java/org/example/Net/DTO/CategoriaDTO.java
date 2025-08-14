package org.example.Net.DTO;

import java.io.Serializable;

public class CategoriaDTO implements Serializable {
    public int id;
    public String nombre;
    public CategoriaDTO(int id, String nombre) { this.id = id; this.nombre = nombre; }

    @Override public String toString(){ return nombre; }

}

