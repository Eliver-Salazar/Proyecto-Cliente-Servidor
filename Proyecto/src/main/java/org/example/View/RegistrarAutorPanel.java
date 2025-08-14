package org.example.View;

import org.example.Net.Remote.Remotes;

import javax.swing.*;
import java.awt.*;

public class RegistrarAutorPanel extends JPanel {
    private final Remotes remotes;
    private JTextField txtNombre;

    public RegistrarAutorPanel(Remotes remotes) {
        this.remotes = remotes;
        setLayout(new GridLayout(2, 2, 10, 10));

        add(new JLabel("Nombre del Autor:"));
        txtNombre = new JTextField(); add(txtNombre);
        JButton btnGuardar = new JButton("Guardar");
        add(new JLabel()); add(btnGuardar);

        btnGuardar.addActionListener(e -> guardar());
    }

    private void guardar() {
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre no puede estar vacío", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            remotes.maestro.registrarAutor(nombre);
            JOptionPane.showMessageDialog(this, "Autor registrado correctamente");
            txtNombre.setText("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "No se pudo registrar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
