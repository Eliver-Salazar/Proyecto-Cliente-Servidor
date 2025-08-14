package org.example.View;

import org.example.DAO.AutorDAO;
import org.example.DAO.CategoriaDAO;
import org.example.Modelo.Entidades.Autor;
import org.example.Modelo.Entidades.Categoria;
import org.example.Net.Remote.Remotes;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BuscarLibroPanel extends JPanel {

    private final Remotes remotes;

    private JTextField txtTitulo;
    private JComboBox<Autor> comboAutor;
    private JComboBox<Categoria> comboCategoria;
    private JComboBox<String> comboDisponibilidad;
    private JTable tablaResultados;

    public BuscarLibroPanel(Remotes remotes) {
        this.remotes = remotes;
        setLayout(new BorderLayout(10, 10));

        JPanel panelFiltros = new JPanel(new GridLayout(2, 4, 5, 5));
        txtTitulo = new JTextField();
        comboAutor = new JComboBox<>();
        comboCategoria = new JComboBox<>();
        comboDisponibilidad = new JComboBox<>(new String[]{"Todos", "Disponible", "No disponible"});

        cargarAutores();
        cargarCategorias();

        panelFiltros.add(new JLabel("Título:")); panelFiltros.add(txtTitulo);
        panelFiltros.add(new JLabel("Autor:")); panelFiltros.add(comboAutor);
        panelFiltros.add(new JLabel("Categoría:")); panelFiltros.add(comboCategoria);
        panelFiltros.add(new JLabel("Disponibilidad:")); panelFiltros.add(comboDisponibilidad);

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> buscarLibros());

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
        for (Autor a : autores) comboAutor.addItem(a);
    }

    private void cargarCategorias() {
        CategoriaDAO categoriaDAO = new CategoriaDAO();
        List<Categoria> categorias = categoriaDAO.listarCategorias();
        comboCategoria.addItem(new Categoria(0, "Todos"));
        for (Categoria c : categorias) comboCategoria.addItem(c);
    }

    private void buscarLibros() {
        try {
            String titulo = txtTitulo.getText().trim();
            Autor autorSeleccionado = (Autor) comboAutor.getSelectedItem();
            Categoria categoriaSeleccionada = (Categoria) comboCategoria.getSelectedItem();
            String disponibilidad = (String) comboDisponibilidad.getSelectedItem();

            Integer autorId = (autorSeleccionado != null && autorSeleccionado.getId() > 0) ? autorSeleccionado.getId() : null;
            Integer categoriaId = (categoriaSeleccionada != null && categoriaSeleccionada.getId() > 0) ? categoriaSeleccionada.getId() : null;

            var resultados = remotes.libros.buscar(titulo, autorId, categoriaId, disponibilidad);

            DefaultTableModel modelo = (DefaultTableModel) tablaResultados.getModel();
            modelo.setRowCount(0);
            for (var l : resultados) {
                modelo.addRow(new Object[]{
                        l.id, l.titulo, l.autor, l.categoria, l.isbn, l.disponible ? "Sí" : "No"
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al buscar: " + ex.getMessage());
        }
    }
}
