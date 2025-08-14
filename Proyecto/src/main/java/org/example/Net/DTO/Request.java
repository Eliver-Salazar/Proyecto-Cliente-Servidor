package org.example.Net.DTO;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/** Pedido genérico al servidor. */
public class Request implements Serializable {
    private String op;                  // operación: LOGIN, SEARCH_BOOKS, etc.
    private Map<String, Object> params; // parámetros

    public Request() { this.params = new HashMap<>(); }
    public Request(String op) { this(); this.op = op; }

    public String getOp() { return op; }
    public void setOp(String op) { this.op = op; }

    public Map<String, Object> getParams() { return params; }
    public void put(String k, Object v) { this.params.put(k, v); }
}

