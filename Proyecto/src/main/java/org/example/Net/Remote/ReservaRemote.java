package org.example.Net.Remote;


import org.example.Net.BibliotecaClient;
import org.example.Net.DTO.Request;

public class ReservaRemote {
    private final BibliotecaClient client;
    public ReservaRemote(BibliotecaClient c) { this.client = c; }

    public boolean reservar(int usuarioId, int libroId) throws Exception {
        var req = new Request("RESERVE_BOOK");
        req.put("usuarioId", usuarioId);
        req.put("libroId", libroId);
        var resp = client.send(req);
        if (!resp.isOk()) throw new RuntimeException(resp.getMessage());
        return (Boolean) resp.getData().get("inmediata");
    }
}

