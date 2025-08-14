package org.example.View;

import org.example.Net.Remote.Remotes;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;

public class PrestamosPanel extends JPanel {
    private final Remotes remotes;

    private JTextField txtUsuarioId, txtLibroId, txtDias;
    private JTable tblActivos;
    private DefaultTableModel modelo;

    public PrestamosPanel(Remotes remotes) {
        this.remotes = remotes;

        setLayout(new BorderLayout(8,8));

        JPanel arriba = new JPanel(new GridLayout(2,6,6,6));
        arriba.add(new JLabel("Usuario ID:"));
        txtUsuarioId = new JTextField(); arriba.add(txtUsuarioId);
        arriba.add(new JLabel("Libro ID:"));
        txtLibroId = new JTextField(); arriba.add(txtLibroId);
        arriba.add(new JLabel("Días:"));
        txtDias = new JTextField("14"); arriba.add(txtDias);

        JButton btnPrestar = new JButton("Prestar");
        JButton btnRefrescar = new JButton("Refrescar");
        arriba.add(new JLabel()); arriba.add(btnPrestar); arriba.add(btnRefrescar);
        add(arriba, BorderLayout.NORTH);

        modelo = new DefaultTableModel(new Object[]{"ID Préstamo","Usuario","Libro","Inicio","Vence"}, 0);
        tblActivos = new JTable(modelo);
        add(new JScrollPane(tblActivos), BorderLayout.CENTER);

        JPanel abajo = new JPanel();
        JButton btnDevolver = new JButton("Registrar devolución");
        abajo.add(btnDevolver);
        add(abajo, BorderLayout.SOUTH);

        btnPrestar.addActionListener(e -> prestar());
        btnRefrescar.addActionListener(e -> cargarActivos());
        btnDevolver.addActionListener(e -> devolverSeleccionado());

        cargarActivos();
    }

    private void cargarActivos() {
        try {
            modelo.setRowCount(0);
            var activos = remotes.prestamos.listarActivos();
            for (var p : activos) {
                modelo.addRow(new Object[]{p.id, p.usuarioId, p.libroId, p.fechaInicio, p.fechaVencimiento});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void prestar() {
        try {
            int usuarioId = Integer.parseInt(txtUsuarioId.getText().trim());
            int libroId = Integer.parseInt(txtLibroId.getText().trim());
            int dias = Integer.parseInt(txtDias.getText().trim());
            int id = remotes.prestamos.prestar(usuarioId, libroId, dias);
            JOptionPane.showMessageDialog(this, "Préstamo registrado. ID=" + id);
            cargarActivos();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void devolverSeleccionado() {
        int row = tblActivos.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un préstamo activo en la tabla");
            return;
        }
        int idPrestamo = (int) modelo.getValueAt(row, 0);
        try {
            BigDecimal multa = remotes.prestamos.devolver(idPrestamo);
            String msg = (multa == null || multa.signum()==0)
                    ? "Devolución registrada. Sin multa."
                    : "Devolución registrada. Multa: ₡" + multa;
            JOptionPane.showMessageDialog(this, msg);
            cargarActivos();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
