package org.example.Net.Remote;

import org.example.Net.BibliotecaClient;
import org.example.Net.DTO.Request;

public class AuthRemote {
    private final BibliotecaClient client;

    public AuthRemote(BibliotecaClient client) { this.client = client; }

    public Session login(String correo, String password) throws Exception {
        var req = new Request("LOGIN");
        req.put("correo", correo);
        req.put("password", password);
        var resp = client.send(req);
        if (!resp.isOk()) throw new RuntimeException(resp.getMessage());
        int uid = (int) resp.getData().get("usuarioId");
        String nombre = (String) resp.getData().get("nombre");
        String rol = (String) resp.getData().get("rol");
        return new Session(uid, nombre, rol);
    }
}

