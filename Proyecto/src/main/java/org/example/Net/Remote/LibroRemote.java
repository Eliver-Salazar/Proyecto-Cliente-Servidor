package org.example.Net.Remote;


import org.example.Net.BibliotecaClient;
import org.example.Net.DTO.LibroDTO;
import org.example.Net.DTO.Request;

import java.util.List;

@SuppressWarnings("unchecked")
public class LibroRemote {
    private final BibliotecaClient client;
    public LibroRemote(BibliotecaClient c) { this.client = c; }

    public List<LibroDTO> buscar(String titulo, Integer autorId, Integer categoriaId, String disponibilidad) throws Exception {
        var req = new Request("SEARCH_BOOKS");
        if (titulo != null) req.put("titulo", titulo);
        if (autorId != null) req.put("autorId", autorId);
        if (categoriaId != null) req.put("categoriaId", categoriaId);
        if (disponibilidad != null) req.put("disponibilidad", disponibilidad);
        var resp = client.send(req);
        if (!resp.isOk()) throw new RuntimeException(resp.getMessage());
        return (List<LibroDTO>) resp.getData().get("libros");
    }

    public List<LibroDTO> listarDisponibles() throws Exception {
        var req = new Request("LIST_AVAILABLE");
        var resp = client.send(req);
        if (!resp.isOk()) throw new RuntimeException(resp.getMessage());
        return (List<LibroDTO>) resp.getData().get("libros");
    }
}

