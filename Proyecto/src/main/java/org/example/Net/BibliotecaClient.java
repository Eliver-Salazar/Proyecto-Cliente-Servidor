package org.example.Net;

import org.example.Net.DTO.Request;
import org.example.Net.DTO.Response;

import java.io.*;
import java.net.Socket;

/** Cliente socket: envía Request y recibe Response. */
public class BibliotecaClient implements Closeable {

    private final String host;
    private final int port;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public BibliotecaClient(String host, int port) throws IOException {
        this.host = host; this.port = port;
        connect();
    }

    private void connect() throws IOException {
        this.socket = new Socket(host, port);
        this.out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        this.in  = new ObjectInputStream(socket.getInputStream());
    }

    public synchronized Response send(Request req) throws IOException, ClassNotFoundException {
        out.writeObject(req);
        out.flush();
        Object obj = in.readObject();
        return (Response) obj;
    }

    @Override public void close() throws IOException {
        if (socket != null && !socket.isClosed()) socket.close();
    }
}

