package org.example.Net.Remote;

import org.example.Net.BibliotecaClient;
import org.example.Net.DTO.PrestamoDTO;
import org.example.Net.DTO.Request;

import java.math.BigDecimal;
import java.util.List;

@SuppressWarnings("unchecked")
public class PrestamoRemote {
    private final BibliotecaClient client;
    public PrestamoRemote(BibliotecaClient c) { this.client = c; }

    public int prestar(int usuarioId, int libroId, int dias) throws Exception {
        var req = new Request("LOAN_BOOK");
        req.put("usuarioId", usuarioId);
        req.put("libroId", libroId);
        req.put("dias", dias);
        var resp = client.send(req);
        if (!resp.isOk()) throw new RuntimeException(resp.getMessage());
        return (Integer) resp.getData().get("prestamoId");
    }

    public BigDecimal devolver(int prestamoId) throws Exception {
        var req = new Request("RETURN_LOAN");
        req.put("prestamoId", prestamoId);
        var resp = client.send(req);
        if (!resp.isOk()) throw new RuntimeException(resp.getMessage());
        return (BigDecimal) resp.getData().get("multa");
    }

    public List<PrestamoDTO> listarActivos() throws Exception {
        var req = new Request("LIST_ACTIVE_LOANS");
        var resp = client.send(req);
        if (!resp.isOk()) throw new RuntimeException(resp.getMessage());
        return (List<PrestamoDTO>) resp.getData().get("prestamos");
    }

    public List<PrestamoDTO> historialPorUsuario(int usuarioId) throws Exception {
        var req = new Request("HISTORY_BY_USER");
        req.put("usuarioId", usuarioId);
        var resp = client.send(req);
        if (!resp.isOk()) throw new RuntimeException(resp.getMessage());
        return (List<PrestamoDTO>) resp.getData().get("prestamos");
    }
}

