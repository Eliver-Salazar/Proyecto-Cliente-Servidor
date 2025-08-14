package org.example.Net.Remote;

import org.example.Net.BibliotecaClient;
import org.example.Net.DTO.AutorDTO;
import org.example.Net.DTO.CategoriaDTO;
import org.example.Net.DTO.Request;

import java.util.List;

@SuppressWarnings("unchecked")
public class MaestroRemote {
    private final BibliotecaClient client;
    public MaestroRemote(BibliotecaClient c) { this.client = c; }

    public List<AutorDTO> listarAutores() throws Exception {
        var req = new Request("LIST_AUTHORS");
        var resp = client.send(req);
        if (!resp.isOk()) throw new RuntimeException(resp.getMessage());
        return (List<AutorDTO>) resp.getData().get("autores");
    }

    public List<CategoriaDTO> listarCategorias() throws Exception {
        var req = new Request("LIST_CATEGORIES");
        var resp = client.send(req);
        if (!resp.isOk()) throw new RuntimeException(resp.getMessage());
        return (List<CategoriaDTO>) resp.getData().get("categorias");
    }

    public void registrarAutor(String nombre) throws Exception {
        var req = new Request("REGISTER_AUTHOR");
        req.put("nombre", nombre);
        var resp = client.send(req);
        if (!resp.isOk()) throw new RuntimeException(resp.getMessage());
    }

    public void registrarCategoria(String nombre) throws Exception {
        var req = new Request("REGISTER_CATEGORY");
        req.put("nombre", nombre);
        var resp = client.send(req);
        if (!resp.isOk()) throw new RuntimeException(resp.getMessage());
    }

    public void registrarLibro(String titulo, int autorId, int categoriaId, String isbn, boolean disponible) throws Exception {
        var req = new Request("REGISTER_BOOK");
        req.put("titulo", titulo);
        req.put("autorId", autorId);
        req.put("categoriaId", categoriaId);
        req.put("isbn", isbn);
        req.put("disponible", disponible);
        var resp = client.send(req);
        if (!resp.isOk()) throw new RuntimeException(resp.getMessage());
    }
}

