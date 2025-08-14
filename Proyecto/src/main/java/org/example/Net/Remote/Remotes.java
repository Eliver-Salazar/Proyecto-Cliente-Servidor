package org.example.Net.Remote;

import org.example.Net.BibliotecaClient;

public class Remotes {
    public final BibliotecaClient client;
    public final AuthRemote auth;
    public final LibroRemote libros;
    public final ReservaRemote reservas;
    public final PrestamoRemote prestamos;
    public final ReporteRemote reportes;
    public final MaestroRemote maestro;

    public Remotes(BibliotecaClient client) {
        this.client = client;
        this.auth = new AuthRemote(client);
        this.libros = new LibroRemote(client);
        this.reservas = new ReservaRemote(client);
        this.prestamos = new PrestamoRemote(client);
        this.reportes = new ReporteRemote(client);
        this.maestro = new MaestroRemote(client);
    }
}

