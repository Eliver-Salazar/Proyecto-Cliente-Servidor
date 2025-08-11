package org.example.View;

import org.example.DAO.AutorDAO;
import org.example.DAO.CategoriaDAO;
import org.example.DAO.LibroDAO;
import org.example.Modelo.Entidades.Autor;
import org.example.Modelo.Entidades.Categoria;
import org.example.Modelo.Entidades.Libro;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RegistrarLibroPanel extends JPanel {

    private JTextField txtTitulo, txtIsbn;
    private JComboBox<Autor> comboAutor;
    private JComboBox<Categoria> comboCategoria;
    private JCheckBox chkDisponible;
    private JButton btnRegistrar;

    public RegistrarLibroPanel() {
        setLayout(new GridLayout(6, 2, 8, 8));

        txtTitulo = new JTextField();
        txtIsbn = new JTextField();
        comboAutor = new JComboBox<>();
        comboCategoria = new JComboBox<>();
        chkDisponible = new JCheckBox("Disponible", true);
        btnRegistrar = new JButton("Registrar Libro");

        // Cargar datos en los combos
        cargarAutores();
        cargarCategorias();

        add(new JLabel("Título:")); add(txtTitulo);
        add(new JLabel("Autor:")); add(comboAutor);
        add(new JLabel("Categoría:")); add(comboCategoria);
        add(new JLabel("ISBN:")); add(txtIsbn);
        add(new JLabel("Disponible:")); add(chkDisponible);
        add(new JLabel()); add(btnRegistrar);

        btnRegistrar.addActionListener(e -> registrarLibro());
    }

    private void cargarAutores() {
        AutorDAO autorDAO = new AutorDAO();
        java.util.List<Autor> autores = autorDAO.listarAutores();
        comboAutor.removeAllItems();
        for (Autor a : autores) {
            comboAutor.addItem(a);
        }
    }

    private void cargarCategorias() {
        CategoriaDAO categoriaDAO = new CategoriaDAO();
        List<Categoria> categorias = categoriaDAO.listarCategorias();
        comboCategoria.removeAllItems();
        for (Categoria c : categorias) {
            comboCategoria.addItem(c);
        }
    }

    private void registrarLibro() {
        String titulo = txtTitulo.getText().trim();
        String isbn = txtIsbn.getText().trim();
        Autor autorSeleccionado = (Autor) comboAutor.getSelectedItem();
        Categoria categoriaSeleccionada = (Categoria) comboCategoria.getSelectedItem();
        boolean disponible = chkDisponible.isSelected();

        if (titulo.isEmpty() || isbn.isEmpty() || autorSeleccionado == null || categoriaSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Libro libro = new Libro(0, titulo, autorSeleccionado.getId(), categoriaSeleccionada.getId(), isbn, disponible);
        LibroDAO libroDAO = new LibroDAO();

        if (libroDAO.registrarLibro(libro)) {
            JOptionPane.showMessageDialog(this, "Libro registrado con éxito.");
            txtTitulo.setText("");
            txtIsbn.setText("");
            chkDisponible.setSelected(true);
        } else {
            JOptionPane.showMessageDialog(this, "Error al registrar libro.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
