package org.example.View;

import org.example.DAO.CategoriaDAO;
import org.example.Modelo.Entidades.Categoria;

import javax.swing.*;
import java.awt.*;

public class RegistrarCategoriaPanel extends JPanel {

    private JTextField txtNombre;
    private JButton btnGuardar;

    public RegistrarCategoriaPanel() {
        setLayout(new GridLayout(2, 2, 10, 10));

        add(new JLabel("Nombre de la Categoría:"));
        txtNombre = new JTextField();
        add(txtNombre);

        btnGuardar = new JButton("Guardar");
        add(new JLabel()); // Espacio vacío
        add(btnGuardar);

        btnGuardar.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre no puede estar vacío", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            CategoriaDAO dao = new CategoriaDAO();
            if (dao.registrarCategoria(new Categoria(0, nombre))) {
                JOptionPane.showMessageDialog(this, "Categoría registrada correctamente");
                txtNombre.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Error al registrar categoría", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}

