package org.example.Net.DTO;

import java.io.Serializable;

/** DTO liviano para resultados de libros (con nombres). */
public class LibroDTO implements Serializable {
    public int id;
    public String titulo;
    public String autor;
    public String categoria;
    public String isbn;
    public boolean disponible;

    public LibroDTO(int id, String titulo, String autor, String categoria, String isbn, boolean disponible) {
        this.id = id; this.titulo = titulo; this.autor = autor; this.categoria = categoria; this.isbn = isbn; this.disponible = disponible;
    }
}

