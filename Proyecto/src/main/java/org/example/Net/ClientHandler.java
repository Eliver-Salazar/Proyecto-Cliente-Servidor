package org.example.Net;

import org.example.DAO.*;
import org.example.Modelo.Entidades.Prestamo;
import org.example.Modelo.Entidades.Usuario;
import org.example.Modelo.Servicios.AuthService;
import org.example.Modelo.Servicios.PrestamoService;
import org.example.Modelo.Servicios.ReservaService;
import org.example.Net.DTO.*;

import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Atiende a un cliente: lee Request, ejecuta lógica con DAOs/Services, devuelve Response. */
public class ClientHandler implements Runnable {

    private final Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    // Reutilizamos tu lógica de negocio existente
    private final AuthService authService = new AuthService();
    private final LibroDAO libroDAO = new LibroDAO();
    private final PrestamoDAO prestamoDAO = new PrestamoDAO();
    private final PrestamoService prestamoService = new PrestamoService();
    private final ReservaService reservaService = new ReservaService();
    private final ReporteDAO reporteDAO = new ReporteDAO();

    public ClientHandler(Socket socket) { this.socket = socket; }

    @Override public void run() {
        try (socket) {
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in  = new ObjectInputStream(socket.getInputStream());

            while (true) {
                Object obj = in.readObject();
                if (!(obj instanceof Request req)) {
                    send(Response.error("Objeto no reconocido"));
                    continue;
                }
                Response resp = handle(req);
                send(resp);
            }
        } catch (EOFException eof) {
            System.out.println("[SERVER] Cliente cerró la conexión.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void send(Response r) throws IOException {
        out.writeObject(r);
        out.flush();
    }

    private Response handle(Request req) {
        try {
            String op = req.getOp();
            switch (op) {
                case "LOGIN" -> {
                    String correo = (String) req.getParams().get("correo");
                    String pass   = (String) req.getParams().get("password");
                    Usuario u = authService.login(correo, pass);
                    if (u == null) return Response.error("Credenciales inválidas");
                    return Response.ok()
                            .add("usuarioId", u.getId())
                            .add("rol", u.getRol())
                            .add("nombre", u.getNombre());
                }
                case "SEARCH_BOOKS" -> {
                    String titulo = (String) req.getParams().getOrDefault("titulo", "");
                    Integer autorId = (Integer) req.getParams().get("autorId");
                    Integer catId   = (Integer) req.getParams().get("categoriaId");
                    String disp     = (String) req.getParams().get("disponibilidad");
                    var lista = libroDAO.buscarLibrosVista(
                            titulo,
                            (autorId != null && autorId > 0) ? autorId : null,
                            (catId   != null && catId   > 0) ? catId   : null,
                            disp
                    );
                    List<LibroDTO> dtos = new ArrayList<>();
                    for (var l : lista) dtos.add(new LibroDTO(l.id, l.titulo, l.autor, l.categoria, l.isbn, l.disponible));
                    return Response.ok().add("libros", dtos);
                }
                case "LIST_AVAILABLE" -> {
                    var lista = libroDAO.listarDisponiblesVista();
                    List<LibroDTO> dtos = new ArrayList<>();
                    for (var l : lista) dtos.add(new LibroDTO(l.id, l.titulo, l.autor, l.categoria, l.isbn, l.disponible));
                    return Response.ok().add("libros", dtos);
                }
                case "RESERVE_BOOK" -> {
                    int usuarioId = (Integer) req.getParams().get("usuarioId");
                    int libroId   = (Integer) req.getParams().get("libroId");
                    boolean inmediata = reservaService.crearReserva(usuarioId, libroId);
                    return Response.ok().add("inmediata", inmediata);
                }
                case "LOAN_BOOK" -> {
                    int usuarioId = (Integer) req.getParams().get("usuarioId");
                    int libroId   = (Integer) req.getParams().get("libroId");
                    int dias      = (Integer) req.getParams().getOrDefault("dias", 14);
                    int id = prestamoService.prestar(usuarioId, libroId, dias);
                    return Response.ok().add("prestamoId", id);
                }
                case "RETURN_LOAN" -> {
                    int prestamoId = (Integer) req.getParams().get("prestamoId");
                    var multa = prestamoService.devolver(prestamoId);
                    return Response.ok().add("multa", multa);
                }
                case "LIST_ACTIVE_LOANS" -> {
                    var activos = prestamoDAO.listarActivos();
                    List<PrestamoDTO> out = new ArrayList<>();
                    for (Prestamo p : activos) {
                        out.add(new PrestamoDTO(p.getId(), p.getUsuarioId(), p.getLibroId(),
                                p.getFechaInicio(), p.getFechaVencimiento(), p.getFechaDevolucion(), p.getMulta()));
                    }
                    return Response.ok().add("prestamos", out);
                }
                case "HISTORY_BY_USER" -> {
                    int usuarioId = (Integer) req.getParams().get("usuarioId");
                    var lista = prestamoDAO.historialPorUsuario(usuarioId);
                    List<PrestamoDTO> out = new ArrayList<>();
                    for (Prestamo p : lista) {
                        out.add(new PrestamoDTO(p.getId(), p.getUsuarioId(), p.getLibroId(),
                                p.getFechaInicio(), p.getFechaVencimiento(), p.getFechaDevolucion(), p.getMulta()));
                    }
                    return Response.ok().add("prestamos", out);
                }
                case "REPORT_TOP" -> {
                    int limite = (Integer) req.getParams().getOrDefault("limite", 10);
                    int dias   = (Integer) req.getParams().getOrDefault("dias", 30);
                    var hasta = LocalDate.now();
                    var desde = hasta.minusDays(dias);
                    var items = reporteDAO.topPrestados(limite, desde, hasta);
                    return Response.ok().add("items", items);
                }
                case "REPORT_LEAST" -> {
                    int limite = (Integer) req.getParams().getOrDefault("limite", 10);
                    int dias   = (Integer) req.getParams().getOrDefault("dias", 30);
                    var hasta = LocalDate.now();
                    var desde = hasta.minusDays(dias);
                    var items = reporteDAO.menosUsados(limite, desde, hasta);
                    return Response.ok().add("items", items);
                }
                default -> { return Response.error("Operación no soportada: " + op); }

                case "LIST_AUTHORS" -> {
                    var dao = new AutorDAO();
                    var lista = dao.listar();
                    java.util.List<AutorDTO> out = new java.util.ArrayList<>();
                    for (var a : lista) out.add(new AutorDTO(a.getId(), a.getNombre()));
                    return Response.ok().add("autores", out);
                }
                case "LIST_CATEGORIES" -> {
                    var dao = new CategoriaDAO();
                    var lista = dao.listar();
                    java.util.List<CategoriaDTO> out = new java.util.ArrayList<>();
                    for (var c : lista) out.add(new CategoriaDTO(c.getId(), c.getNombre()));
                    return Response.ok().add("categorias", out);
                }
                case "REGISTER_AUTHOR" -> {
                    String nombre = (String) req.getParams().get("nombre");
                    if (nombre == null || nombre.trim().isEmpty()) return Response.error("Nombre requerido");
                    var dao = new AutorDAO();
                    if (dao.existeAutor(nombre)) return Response.error("El autor ya existe");
                    boolean ok = dao.registrarAutor(new org.example.Modelo.Entidades.Autor(0, nombre.trim()));
                    return ok ? Response.ok() : Response.error("No se pudo registrar autor");
                }
                case "REGISTER_CATEGORY" -> {
                    String nombre = (String) req.getParams().get("nombre");
                    if (nombre == null || nombre.trim().isEmpty()) return Response.error("Nombre requerido");
                    var dao = new CategoriaDAO();
                    if (dao.existeNombre(nombre)) return Response.error("La categoría ya existe");
                    boolean ok = dao.registrarCategoria(new org.example.Modelo.Entidades.Categoria(0, nombre.trim()));
                    return ok ? Response.ok() : Response.error("No se pudo registrar categoría");
                }
                case "REGISTER_BOOK" -> {
                    String titulo = (String) req.getParams().get("titulo");
                    Integer autorId = (Integer) req.getParams().get("autorId");
                    Integer categoriaId = (Integer) req.getParams().get("categoriaId");
                    String isbn = (String) req.getParams().get("isbn");
                    Boolean disponible = (Boolean) req.getParams().get("disponible");
                    if (titulo == null || isbn == null || autorId == null || categoriaId == null)
                        return Response.error("Datos incompletos");
                    var dao = new LibroDAO();
                    var libro = new org.example.Modelo.Entidades.Libro(0, titulo.trim(), autorId, categoriaId, isbn.trim(),
                            disponible != null ? disponible : true);
                    boolean ok = dao.registrarLibro(libro);
                    return ok ? Response.ok() : Response.error("No se pudo registrar libro");
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return Response.error("Error: " + ex.getMessage());
        }
    }
}

