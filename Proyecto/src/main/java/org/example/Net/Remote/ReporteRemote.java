package org.example.Net.Remote;

import org.example.DAO.ReporteDAO.ItemConteo;
import org.example.Net.BibliotecaClient;
import org.example.Net.DTO.Request;

import java.util.List;

@SuppressWarnings("unchecked")
public class ReporteRemote {
    private final BibliotecaClient client;
    public ReporteRemote(BibliotecaClient c) { this.client = c; }

    public List<ItemConteo> topPrestados(int limite, int dias) throws Exception {
        var req = new Request("REPORT_TOP");
        req.put("limite", limite);
        req.put("dias", dias);
        var resp = client.send(req);
        if (!resp.isOk()) throw new RuntimeException(resp.getMessage());
        return (List<ItemConteo>) resp.getData().get("items");
    }

    public List<ItemConteo> menosUsados(int limite, int dias) throws Exception {
        var req = new Request("REPORT_LEAST");
        req.put("limite", limite);
        req.put("dias", dias);
        var resp = client.send(req);
        if (!resp.isOk()) throw new RuntimeException(resp.getMessage());
        return (List<ItemConteo>) resp.getData().get("items");
    }
}

