package org.example.Net.DTO;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/** Respuesta genérica del servidor. */
public class Response implements Serializable {
    private boolean ok;
    private String message;
    private Map<String, Object> data;

    public Response() { this.data = new HashMap<>(); }

    public static Response ok() { Response r = new Response(); r.ok = true; return r; }
    public static Response error(String msg) { Response r = new Response(); r.ok = false; r.message = msg; return r; }

    public boolean isOk() { return ok; }
    public String getMessage() { return message; }
    public Map<String, Object> getData() { return data; }

    public Response add(String key, Object val) { this.data.put(key, val); return this; }
}

