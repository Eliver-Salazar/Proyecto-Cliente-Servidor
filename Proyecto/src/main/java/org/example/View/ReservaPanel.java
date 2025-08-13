package org.example.View;

import org.example.DAO.LibroDAO;
import org.example.DAO.PrestamoDAO;
import org.example.DAO.ReservaDAO;
import org.example.Modelo.Entidades.Libro;
import org.example.Modelo.Entidades.Reserva;
import org.example.Modelo.Servicios.ReservaService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Panel para que el estudiante:
 * - Vea libros disponibles y los reserve de forma inmediata (se marcan no disponibles).
 * - Si el libro está prestado, pueda entrar en cola (reserva pendiente) para ser notificado al devolver.
 */
public class ReservaPanel extends JPanel {

    private JTable tablaLibros;
    private final int usuarioId;

    public ReservaPanel(int usuarioId) {
        this.usuarioId = usuarioId;
        setLayout(new BorderLayout(8, 8));

        tablaLibros = new JTable(new DefaultTableModel(new Object[]{"ID", "Título", "Disponible"}, 0));
        JScrollPane scroll = new JScrollPane(tablaLibros);
        add(scroll, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel();
        JButton btnCargar = new JButton("Cargar libros disponibles");
        JButton btnReservar = new JButton("Reservar / Apartar");
        panelBotones.add(btnCargar);
        panelBotones.add(btnReservar);
        add(panelBotones, BorderLayout.SOUTH);

        btnCargar.addActionListener(e -> cargarLibrosDisponibles());
        btnReservar.addActionListener(e -> reservarLibro());

        // Carga inicial
        cargarLibrosDisponibles();
    }

    private void cargarLibrosDisponibles() {
        LibroDAO dao = new LibroDAO();
        List<Libro> libros = dao.listarLibrosDisponibles();
        DefaultTableModel modelo = (DefaultTableModel) tablaLibros.getModel();
        modelo.setRowCount(0);
        for (Libro l : libros) {
            modelo.addRow(new Object[]{l.getId(), l.getTitulo(), l.isDisponible() ? "Sí" : "No"});
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
            PrestamoDAO prestamoDAO = new PrestamoDAO();
            ReservaDAO reservaDAO = new ReservaDAO();

            // ¿Está prestado ahora mismo?
            if (prestamoDAO.estaPrestadoActivo(libroId)) {
                String fecha = prestamoDAO.obtenerFechaDevolucionActiva(libroId);
                int r = JOptionPane.showConfirmDialog(
                        this,
                        "El libro está prestado (vuelve aprox. " + fecha + ").\n¿Desea reservarlo en cola y ser notificado cuando esté disponible?",
                        "Libro prestado",
                        JOptionPane.YES_NO_OPTION
                );
                if (r == JOptionPane.YES_OPTION) {
                    int idReserva = reservaDAO.insertEnCola(usuarioId, libroId);
                    if (idReserva > 0) {
                        JOptionPane.showMessageDialog(this, "Reserva en cola creada. Serás notificado al quedar disponible.");
                    } else {
                        JOptionPane.showMessageDialog(this, "No se pudo crear la reserva en cola.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                return;
            }

            // ¿Ya existe una reserva activa?
            if (reservaDAO.libroReservado(libroId)) {
                JOptionPane.showMessageDialog(this, "Este libro ya está reservado (registro activo).");
                return;
            }

            // Libro no prestado y sin reserva activa: usar ReservaService para coherencia (marca no disponible si aplica)
            boolean inmediata = new ReservaService().crearReserva(usuarioId, libroId);
            if (inmediata) {
                JOptionPane.showMessageDialog(this, "Reserva inmediata creada. El libro quedó apartado para ti (no disponible para otros).");
            } else {
                // Por si en algún escenario la reserva cae en cola (no debería con disponibles)
                JOptionPane.showMessageDialog(this, "Quedaste en cola. Te avisaremos cuando esté disponible.");
            }
            cargarLibrosDisponibles(); // refrescar

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ocurrió un error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
