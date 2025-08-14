package org.example.View;

import org.example.Net.Remote.PrestamoRemote;
import org.example.Net.Remote.Remotes;
import org.example.Net.Remote.Session;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class EstudianteView extends JFrame {
    private final Session session;
    private final Remotes remotes;

    public EstudianteView(Session session, Remotes remotes) {
        this.session = session; this.remotes = remotes;

        setTitle("Panel del Estudiante");
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel top = new JPanel(new BorderLayout());
        top.add(new JLabel("Bienvenido(a) " + session.getNombre()), BorderLayout.WEST);
        JButton btnCerrar = new JButton("Cerrar sesión");
        top.add(btnCerrar, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Buscar libros", new BuscarLibroPanel(remotes));
        tabs.addTab("Reservar", new ReservaPanel(session.getUsuarioId(), remotes));
        tabs.addTab("Historial", new HistorialPanel());
        add(tabs, BorderLayout.CENTER);

        btnCerrar.addActionListener(e -> {
            dispose();
            new LoginView(remotes).setVisible(true);
        });
    }

    class HistorialPanel extends JPanel {
        private JTable tabla;
        private DefaultTableModel modelo;

        public HistorialPanel() {
            setLayout(new BorderLayout(8,8));
            modelo = new DefaultTableModel(new Object[]{"ID","Libro","Inicio","Vence","Devolución","Multa"}, 0);
            tabla = new JTable(modelo);
            add(new JScrollPane(tabla), BorderLayout.CENTER);

            JButton btnCargar = new JButton("Cargar historial");
            add(btnCargar, BorderLayout.SOUTH);
            btnCargar.addActionListener(e -> cargar());
            cargar();
        }

        private void cargar() {
            try {
                modelo.setRowCount(0);
                PrestamoRemote remote = remotes.prestamos;
                var lista = remote.historialPorUsuario(session.getUsuarioId());
                for (var p : lista) {
                    modelo.addRow(new Object[]{
                            p.id, p.libroId, p.fechaInicio, p.fechaVencimiento, p.fechaDevolucion, p.multa
                    });
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }
}
