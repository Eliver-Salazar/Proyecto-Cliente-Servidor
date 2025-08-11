package org.example.View;

import org.example.DAO.*;

import org.example.Modelo.Entidades.*;
import org.example.Modelo.Util.Notificador;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class EstudianteView extends JFrame {
    private final Usuario usuario;

    public EstudianteView(Usuario usuario) {
        this.usuario = usuario;
        setTitle("Panel del Estudiante");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel superior
        JPanel top = new JPanel();
        top.add(new JLabel("Bienvenido(a) Estudiante"));
        add(top, BorderLayout.NORTH);

        // Panel central con tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Buscar libros", new BuscarLibroPanel());
        tabs.addTab("Historial", new HistorialPanel());
        tabs.addTab("Buscar libros", new BuscarLibroPanel());
        tabs.addTab("Reservar", new ReservaPanel(usuario.getId()));
        tabs.addTab("Historial", new HistorialPanel());
        add(tabs, BorderLayout.CENTER);
    }

    // Panel de búsqueda de libros
    public static class BuscarLibroPanel extends JPanel {

        private JTextField txtTitulo;
        private JComboBox<Autor> comboAutor;
        private JComboBox<Categoria> comboCategoria;
        private JComboBox<String> comboDisponibilidad;
        private JTable tablaResultados;

        public BuscarLibroPanel() {
            setLayout(new BorderLayout(10, 10));

            // Panel superior de filtros
            JPanel panelFiltros = new JPanel(new GridLayout(2, 4, 5, 5));

            txtTitulo = new JTextField();
            comboAutor = new JComboBox<>();
            comboCategoria = new JComboBox<>();
            comboDisponibilidad = new JComboBox<>(new String[]{"Todos", "Disponible", "No disponible"});

            cargarAutores();
            cargarCategorias();

            panelFiltros.add(new JLabel("Título:"));
            panelFiltros.add(txtTitulo);
            panelFiltros.add(new JLabel("Autor:"));
            panelFiltros.add(comboAutor);
            panelFiltros.add(new JLabel("Categoría:"));
            panelFiltros.add(comboCategoria);
            panelFiltros.add(new JLabel("Disponibilidad:"));
            panelFiltros.add(comboDisponibilidad);

            // Botón de búsqueda
            JButton btnBuscar = new JButton("Buscar");
            btnBuscar.addActionListener(e -> buscarLibros());

            // Tabla de resultados
            tablaResultados = new JTable(new DefaultTableModel(
                    new Object[]{"ID", "Título", "Autor", "Categoría", "ISBN", "Disponible"}, 0
            ));

            add(panelFiltros, BorderLayout.NORTH);
            add(new JScrollPane(tablaResultados), BorderLayout.CENTER);
            add(btnBuscar, BorderLayout.SOUTH);
        }

        private void cargarAutores() {
            AutorDAO autorDAO = new AutorDAO();
            List<Autor> autores = autorDAO.listarAutores();
            comboAutor.addItem(new Autor(0, "Todos"));
            for (Autor a : autores) {
                comboAutor.addItem(a);
            }
        }

        private void cargarCategorias() {
            CategoriaDAO categoriaDAO = new CategoriaDAO();
            List<Categoria> categorias = categoriaDAO.listarCategorias();
            comboCategoria.addItem(new Categoria(0, "Todos"));
            for (Categoria c : categorias) {
                comboCategoria.addItem(c);
            }
        }

        private void buscarLibros() {
            String titulo = txtTitulo.getText().trim();
            Autor autorSeleccionado = (Autor) comboAutor.getSelectedItem();
            Categoria categoriaSeleccionada = (Categoria) comboCategoria.getSelectedItem();
            String disponibilidad = (String) comboDisponibilidad.getSelectedItem();

            LibroDAO libroDAO = new LibroDAO();
            List<Libro> resultados = libroDAO.buscarLibros(
                    titulo,
                    (autorSeleccionado != null) ? autorSeleccionado.getId() : 0,
                    (categoriaSeleccionada != null) ? categoriaSeleccionada.getId() : 0,
                    disponibilidad
            );

            DefaultTableModel modelo = (DefaultTableModel) tablaResultados.getModel();
            modelo.setRowCount(0);
            for (Libro libro : resultados) {
                modelo.addRow(new Object[]{
                        libro.getId(),
                        libro.getTitulo(),
                        libro.getAutorId(),
                        libro.getCategoriaId(),
                        libro.getIsbn(),
                        libro.isDisponible() ? "Sí" : "No"
                });
            }
        }
    }

    public class ReservaPanel extends JPanel {

        private JTable tablaLibros;
        private int usuarioId;

        public ReservaPanel(int usuarioId) {
            this.usuarioId = usuarioId;
            setLayout(new BorderLayout(8, 8));

            tablaLibros = new JTable(new DefaultTableModel(new Object[]{"ID", "Título", "Disponible"}, 0));
            JScrollPane scroll = new JScrollPane(tablaLibros);
            add(scroll, BorderLayout.CENTER);

            JPanel panelBotones = new JPanel();
            JButton btnCargar = new JButton("Cargar libros disponibles");
            JButton btnReservar = new JButton("Reservar libro");
            panelBotones.add(btnCargar);
            panelBotones.add(btnReservar);
            add(panelBotones, BorderLayout.SOUTH);

            btnCargar.addActionListener(e -> cargarLibrosDisponibles());
            btnReservar.addActionListener(e -> reservarLibro());

            // Carga inicial
            cargarLibrosDisponibles();
        }

        private void cargarLibrosDisponibles() {
            LibroDAO dao = new LibroDAO();
            List<Libro> libros = dao.listarLibrosDisponibles(); // asegúrate de tener este método
            DefaultTableModel modelo = (DefaultTableModel) tablaLibros.getModel();
            modelo.setRowCount(0);
            for (Libro l : libros) {
                modelo.addRow(new Object[]{l.getId(), l.getTitulo(), l.isDisponible() ? "Sí" : "No"});
            }
        }

        private void reservarLibro() {
            int fila = tablaLibros.getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione un libro para reservar.");
                return;
            }

            int libroId = (int) tablaLibros.getValueAt(fila, 0);

            try {
                PrestamoDAO prestamoDAO = new PrestamoDAO();
                ReservaDAO reservaDAO = new ReservaDAO();

                // 1) ¿Está prestado ahora mismo?
                if (prestamoDAO.estaPrestadoActivo(libroId)) {
                    String fecha = prestamoDAO.obtenerFechaDevolucionActiva(libroId);
                    JOptionPane.showMessageDialog(this, "El libro está prestado. Disponible después de: " + fecha);
                    return;
                }

                // 2) ¿Ya existe una reserva activa?
                if (reservaDAO.libroReservado(libroId)) {
                    JOptionPane.showMessageDialog(this, "Este libro ya está reservado (registro activo).");
                    return;
                }

                // 3) Crear objeto Reserva y registrar
                Reserva reserva = new Reserva(0, libroId, usuarioId, LocalDate.now());
                boolean ok = reservaDAO.registrarReserva(reserva);

                if (ok) {
                    JOptionPane.showMessageDialog(this, "Reserva realizada con éxito.");
                    // Notificación simulada (usa tu Notificador)
                    Notificador.enviarCorreo(
                            /*destinatario*/ obtenerCorreoUsuario(usuarioId),
                            /*asunto*/ "Reserva confirmada",
                            /*mensaje*/ "Su reserva del libro (ID " + libroId + ") ha sido registrada. Fecha: " + LocalDate.now()
                    );
                    cargarLibrosDisponibles(); // refrescar lista
                } else {
                    JOptionPane.showMessageDialog(this, "Error al registrar la reserva.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ocurrió un error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }

        private String obtenerCorreoUsuario(int usuarioId) {
            // Aquí puedes llamar a UsuarioDAO.usuarioPorId(usuarioId).getCorreo();
            // Si aún no tienes ese método, puedes devolver un correo de prueba o adaptarlo.
            return "usuario@example.com";
        }
    }

    // Panel de historial
    class HistorialPanel extends JPanel {
        private JTable tabla;
        private DefaultTableModel modelo;

        public HistorialPanel() {
            setLayout(new BorderLayout(8,8));
            modelo = new DefaultTableModel(new Object[]{"ID","Libro","Inicio","Vence","Devolución","Multa"}, 0);
            tabla = new JTable(modelo);
            add(new JScrollPane(tabla), BorderLayout.CENTER);

            JButton btnCargar = new JButton("Cargar historial");
            add(btnCargar, BorderLayout.SOUTH);
            btnCargar.addActionListener(e -> cargar());
            cargar();
        }

        private void cargar() {
            modelo.setRowCount(0);
            var dao = new org.example.DAO.PrestamoDAO();
            var lista = dao.historialPorUsuario(usuario.getId());
            for (var p : lista) {
                modelo.addRow(new Object[]{
                        p.getId(), p.getLibroId(), p.getFechaInicio(),
                        p.getFechaVencimiento(), p.getFechaDevolucion(), p.getMulta()
                });
            }
        }
    }
}
