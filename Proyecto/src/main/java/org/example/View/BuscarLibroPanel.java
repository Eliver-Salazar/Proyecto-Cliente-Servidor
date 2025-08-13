package org.example.View;

import org.example.DAO.AutorDAO;
import org.example.DAO.CategoriaDAO;
import org.example.DAO.LibroDAO;
import org.example.Modelo.Entidades.Autor;
import org.example.Modelo.Entidades.Categoria;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel de búsqueda de libros para estudiantes/bibliotecarios:
 * - Filtros: título, autor, categoría, disponibilidad.
 * - Muestra nombres (JOIN) gracias a LibroDAO.buscarLibrosVista(...).
 */
public class BuscarLibroPanel extends JPanel {

    private JTextField txtTitulo;
    private JComboBox<Autor> comboAutor;
    private JComboBox<Categoria> comboCategoria;
    private JComboBox<String> comboDisponibilidad;
    private JTable tablaResultados;

    public BuscarLibroPanel() {
        setLayout(new BorderLayout(10, 10));

        // Filtros
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

        // Botón
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> buscarLibros());

        // Tabla
        tablaResultados = new JTable(new DefaultTableModel(
                new Object[]{"ID", "Título", "Autor", "Categoría", "ISBN", "Disponible"}, 0
        ));

        add(panelFiltros, BorderLayout.NORTH);
        add(new JScrollPane(tablaResultados), BorderLayout.CENTER);
        add(btnBuscar, BorderLayout.SOUTH);
    }

    /** Llena combo de autores (incluye opción "Todos"). */
    private void cargarAutores() {
        AutorDAO autorDAO = new AutorDAO();
        List<Autor> autores = autorDAO.listarAutores();
        comboAutor.addItem(new Autor(0, "Todos"));
        for (Autor a : autores) comboAutor.addItem(a);
    }

    /** Llena combo de categorías (incluye opción "Todos"). */
    private void cargarCategorias() {
        CategoriaDAO categoriaDAO = new CategoriaDAO();
        List<Categoria> categorias = categoriaDAO.listarCategorias();
        comboCategoria.addItem(new Categoria(0, "Todos"));
        for (Categoria c : categorias) comboCategoria.addItem(c);
    }

    /** Ejecuta búsqueda con filtros y muestra resultados con nombres (JOIN). */
    private void buscarLibros() {
        String titulo = txtTitulo.getText().trim();
        Autor autorSeleccionado = (Autor) comboAutor.getSelectedItem();
        Categoria categoriaSeleccionada = (Categoria) comboCategoria.getSelectedItem();
        String disponibilidad = (String) comboDisponibilidad.getSelectedItem();

        Integer autorId = (autorSeleccionado != null) ? autorSeleccionado.getId() : 0;
        Integer categoriaId = (categoriaSeleccionada != null) ? categoriaSeleccionada.getId() : 0;

        LibroDAO libroDAO = new LibroDAO();
        var resultados = libroDAO.buscarLibrosVista(
                titulo,
                autorId != null && autorId > 0 ? autorId : null,
                categoriaId != null && categoriaId > 0 ? categoriaId : null,
                disponibilidad
        );

        DefaultTableModel modelo = (DefaultTableModel) tablaResultados.getModel();
        modelo.setRowCount(0);
        for (var l : resultados) {
            modelo.addRow(new Object[]{
                    l.id,
                    l.titulo,
                    l.autor,
                    l.categoria,
                    l.isbn,
                    l.disponible ? "Sí" : "No"
            });
        }
    }
}
