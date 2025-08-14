package org.example.Net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/** Servidor: acepta conexiones y crea un ClientHandler por cliente. */
public class BibliotecaServer {

    private final int port;

    public BibliotecaServer(int port) { this.port = port; }

    public void start() throws IOException {
        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("[SERVER] Escuchando en puerto " + port + " ...");
            while (true) {
                Socket client = server.accept();
                System.out.println("[SERVER] Cliente: " + client.getRemoteSocketAddress());
                new Thread(new ClientHandler(client)).start();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new BibliotecaServer(5555).start();
    }
}

