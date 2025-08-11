package org.example.Modelo.Entidades;

public class Libro {
    private int id;
    private String titulo;
    private int autorId;
    private int categoriaId;
    private String isbn;
    private boolean disponible;

    public Libro() {}

    public Libro(int id, String titulo, int autorId, int categoriaId, String isbn, boolean disponible) {
        this.id = id; this.titulo = titulo; this.autorId = autorId;
        this.categoriaId = categoriaId; this.isbn = isbn; this.disponible = disponible;
    }

    public int getId() { return id; }
    public String getTitulo() { return titulo; }
    public int getAutorId() { return autorId; }
    public int getCategoriaId() { return categoriaId; }
    public String getIsbn() { return isbn; }
    public boolean isDisponible() { return disponible; }

    public void setId(int id) { this.id = id; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setAutorId(int autorId) { this.autorId = autorId; }
    public void setCategoriaId(int categoriaId) { this.categoriaId = categoriaId; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
}
