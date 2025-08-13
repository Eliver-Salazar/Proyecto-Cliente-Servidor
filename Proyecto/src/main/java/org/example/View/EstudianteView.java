package org.example.View;

import org.example.DAO.PrestamoDAO;
import org.example.Modelo.Entidades.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class EstudianteView extends JFrame {
    private final Usuario usuario;

    public EstudianteView(Usuario usuario) {
        this.usuario = usuario;
        setTitle("Panel del Estudiante");
        setSize(700, 480);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Top con bienvenida + cerrar sesión
        JPanel top = new JPanel(new BorderLayout());
        top.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
        top.add(new JLabel("Bienvenido(a), " + usuario.getNombre()), BorderLayout.WEST);

        JButton btnCerrar = new JButton("Cerrar sesión"); // <-- NUEVO
        btnCerrar.addActionListener(e -> {
            dispose();
            new LoginView().setVisible(true);
        });
        top.add(btnCerrar, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        // Tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Buscar libros", new BuscarLibroPanel());
        tabs.addTab("Reservar", new ReservaPanel(usuario.getId()));
        tabs.addTab("Historial", new HistorialPanel());
        add(tabs, BorderLayout.CENTER);
    }

    class HistorialPanel extends JPanel {
        private JTable tabla;
        private DefaultTableModel modelo;

        public HistorialPanel() {
            setLayout(new BorderLayout(8,8));
            modelo = new DefaultTableModel(new Object[]{"ID","Libro","Inicio","Vence","Devolución","Multa"}, 0);
            tabla = new JTable(modelo);
            add(new JScrollPane(tabla), BorderLayout.CENTER);

            JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton btnCargar = new JButton("Cargar historial");
            bottom.add(btnCargar);
            add(bottom, BorderLayout.SOUTH);

            btnCargar.addActionListener(e -> cargar());
            cargar();
        }

        private void cargar() {
            modelo.setRowCount(0);
            var dao = new PrestamoDAO();
            var lista = dao.historialPorUsuario(usuario.getId());
            for (var p : lista) {
                modelo.addRow(new Object[]{
                        p.getId(), p.getLibroId(), p.getFechaInicio(),
                        p.getFechaVencimiento(), p.getFechaDevolucion(), p.getMulta()
                });
            }
        }
    }
}
