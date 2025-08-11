package org.example.View;

import org.example.DAO.PrestamoDAO;
import org.example.DAO.LibroDAO;
import org.example.Modelo.Entidades.Prestamo;
import org.example.Modelo.Servicios.PrestamoService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class PrestamosPanel extends JPanel {
    private final PrestamoDAO prestamoDAO = new PrestamoDAO();
    private final PrestamoService service = new PrestamoService();

    private JTextField txtUsuarioId, txtLibroId, txtDias;
    private JTable tblActivos;
    private DefaultTableModel modelo;

    public PrestamosPanel() {
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
        modelo.setRowCount(0);
        List<Prestamo> activos = prestamoDAO.listarActivos();
        for (Prestamo p : activos) {
            modelo.addRow(new Object[]{
                    p.getId(), p.getUsuarioId(), p.getLibroId(),
                    p.getFechaInicio(), p.getFechaVencimiento()
            });
        }
    }

    private void prestar() {
        try {
            int usuarioId = Integer.parseInt(txtUsuarioId.getText().trim());
            int libroId = Integer.parseInt(txtLibroId.getText().trim());
            int dias = Integer.parseInt(txtDias.getText().trim());
            int id = service.prestar(usuarioId, libroId, dias);
            if (id > 0) {
                JOptionPane.showMessageDialog(this, "Préstamo registrado. ID=" + id);
                cargarActivos();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo registrar el préstamo", "Error", JOptionPane.ERROR_MESSAGE);
            }
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
            BigDecimal multa = service.devolver(idPrestamo);
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

