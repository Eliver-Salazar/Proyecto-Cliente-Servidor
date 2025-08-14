package org.example.View;

import org.example.Net.Remote.Remotes;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ReservaPanel extends JPanel {

    private JTable tablaLibros;
    private final int usuarioId;
    private final Remotes remotes;

    public ReservaPanel(int usuarioId, Remotes remotes) {
        this.usuarioId = usuarioId;
        this.remotes = remotes;

        setLayout(new BorderLayout(8, 8));

        tablaLibros = new JTable(new DefaultTableModel(new Object[]{"ID", "Título", "Disponible"}, 0));
        add(new JScrollPane(tablaLibros), BorderLayout.CENTER);

        JPanel panelBotones = new JPanel();
        JButton btnCargar = new JButton("Cargar libros disponibles");
        JButton btnReservar = new JButton("Reservar libro");
        panelBotones.add(btnCargar); panelBotones.add(btnReservar);
        add(panelBotones, BorderLayout.SOUTH);

        btnCargar.addActionListener(e -> cargarLibrosDisponibles());
        btnReservar.addActionListener(e -> reservarLibro());

        cargarLibrosDisponibles();
    }

    private void cargarLibrosDisponibles() {
        try {
            var libros = remotes.libros.listarDisponibles();
            DefaultTableModel modelo = (DefaultTableModel) tablaLibros.getModel();
            modelo.setRowCount(0);
            for (var l : libros) {
                modelo.addRow(new Object[]{l.id, l.titulo, l.disponible ? "Sí" : "No"});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
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
            boolean inmediata = remotes.reservas.reservar(usuarioId, libroId);
            if (inmediata) {
                JOptionPane.showMessageDialog(this, "Reserva inmediata: el libro quedó apartado para retiro.");
            } else {
                JOptionPane.showMessageDialog(this, "Agregado a la lista de espera. Recibirá notificación.");
            }
            cargarLibrosDisponibles();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al reservar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
