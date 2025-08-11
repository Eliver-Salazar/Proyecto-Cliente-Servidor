package org.example.View;

import org.example.DAO.AutorDAO;
import org.example.Modelo.Entidades.Autor;

import javax.swing.*;
import java.awt.*;

public class RegistrarAutorPanel extends JPanel {

    private JTextField txtNombre;
    private JButton btnGuardar;

    public RegistrarAutorPanel() {
        setLayout(new GridLayout(2, 2, 10, 10));

        add(new JLabel("Nombre del Autor:"));
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

            AutorDAO dao = new AutorDAO();
            if (dao.registrarAutor(new Autor(0, nombre))) {
                JOptionPane.showMessageDialog(this, "Autor registrado correctamente");
                txtNombre.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Error al registrar autor", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}

