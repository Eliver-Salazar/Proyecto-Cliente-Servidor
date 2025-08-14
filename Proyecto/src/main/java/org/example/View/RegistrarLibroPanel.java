package org.example.View;

import org.example.Modelo.Entidades.Autor;
import org.example.Modelo.Entidades.Categoria;
import org.example.Net.DTO.AutorDTO;
import org.example.Net.DTO.CategoriaDTO;
import org.example.Net.Remote.Remotes;

import javax.swing.*;
import java.awt.*;

public class RegistrarLibroPanel extends JPanel {

    private final Remotes remotes;

    private JTextField txtTitulo, txtIsbn;
    private JComboBox<Autor> comboAutor;
    private JComboBox<Categoria> comboCategoria;
    private JCheckBox chkDisponible;

    public RegistrarLibroPanel(Remotes remotes) {
        this.remotes = remotes;
        setLayout(new GridLayout(6, 2, 8, 8));

        txtTitulo = new JTextField();
        txtIsbn = new JTextField();
        comboAutor = new JComboBox<org.example.Modelo.Entidades.Autor>();
        comboCategoria = new JComboBox<org.example.Modelo.Entidades.Categoria>();
        chkDisponible = new JCheckBox("Disponible", true);
        JButton btnRegistrar = new JButton("Registrar Libro");

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
        try {
            comboAutor.removeAllItems();
            comboAutor.addItem(new org.example.Modelo.Entidades.Autor(0,"Todos"));
            for (var a : remotes.maestro.listarAutores()) {
                comboAutor.addItem(new org.example.Modelo.Entidades.Autor(a.id, a.nombre));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error cargando autores: " + ex.getMessage());
        }
    }
    private void cargarCategorias() {
        try {
            comboCategoria.removeAllItems();
            comboCategoria.addItem(new org.example.Modelo.Entidades.Categoria(0,"Todos"));
            for (var c : remotes.maestro.listarCategorias()) {
                comboCategoria.addItem(new org.example.Modelo.Entidades.Categoria(c.id, c.nombre));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error cargando categorías: " + ex.getMessage());
        }
    }


    private void registrarLibro() {
        try {
            String titulo = txtTitulo.getText().trim();
            String isbn = txtIsbn.getText().trim();
            AutorDTO a = (AutorDTO) comboAutor.getSelectedItem();
            CategoriaDTO c = (CategoriaDTO) comboCategoria.getSelectedItem();
            boolean disponible = chkDisponible.isSelected();

            if (titulo.isEmpty() || isbn.isEmpty() || a == null || c == null) {
                JOptionPane.showMessageDialog(this, "Complete todos los campos", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            remotes.maestro.registrarLibro(titulo, a.id, c.id, isbn, disponible);
            JOptionPane.showMessageDialog(this, "Libro registrado con éxito.");
            txtTitulo.setText(""); txtIsbn.setText(""); chkDisponible.setSelected(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al registrar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
