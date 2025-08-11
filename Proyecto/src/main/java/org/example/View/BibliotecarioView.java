package org.example.View;

import org.example.DAO.LibroDAO;
import org.example.Modelo.Entidades.Usuario;
import org.example.View.EstudianteView.BuscarLibroPanel;
import org.example.View.ReportesPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BibliotecarioView extends JFrame {

    private JPanel panelContenido;

    public BibliotecarioView(Usuario usuario) {
        setTitle("Panel Bibliotecario");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Crear barra de menú
        JMenuBar menuBar = new JMenuBar();
        JMenu menuGestion = new JMenu("Gestión");
        JMenuItem itemAutores = new JMenuItem("Registrar Autor");
        JMenuItem itemCategorias = new JMenuItem("Registrar Categoría");

        itemAutores.addActionListener(e -> mostrarPanel(new RegistrarAutorPanel()));
        itemCategorias.addActionListener(e -> mostrarPanel(new RegistrarCategoriaPanel()));

        menuGestion.add(itemAutores);
        menuGestion.add(itemCategorias);
        menuBar.add(menuGestion);

        // Asignar barra de menú al JFrame
        setJMenuBar(menuBar);

        // Panel de botones de menú lateral
        JPanel menuPanel = new JPanel(new GridLayout(10, 1, 5, 5));
        JButton btnRegistrarLibro = new JButton("Registrar Libro");
        JButton btnBuscarLibros = new JButton("Buscar Libros");
        JButton btnCerrar = new JButton("Cerrar Sesión");
        JButton btnPrestamos = new JButton("Préstamos/Devoluciones");
        menuPanel.add(btnPrestamos);
        JButton btnReportes = new JButton("Reportes");
        menuPanel.add(btnReportes);


        menuPanel.add(btnRegistrarLibro);
        menuPanel.add(btnBuscarLibros);
        menuPanel.add(btnCerrar);

        panelContenido = new JPanel(new BorderLayout());
        add(menuPanel, BorderLayout.WEST);
        add(panelContenido, BorderLayout.CENTER);

        btnPrestamos.addActionListener(e -> mostrarPanel(new PrestamosPanel()));
        btnReportes.addActionListener(e -> mostrarPanel(new ReportesPanel()));
        btnRegistrarLibro.addActionListener(e -> mostrarPanel(new RegistrarLibroPanel()));
        btnBuscarLibros.addActionListener(e -> mostrarPanel(new BuscarLibroPanel()));
        btnCerrar.addActionListener(e -> {
            dispose();
            new LoginView().setVisible(true);
        });
    }

    // Método para mostrar un panel en el área central
    private void mostrarPanel(JPanel panel) {
        panelContenido.removeAll();
        panelContenido.add(panel, BorderLayout.CENTER);
        panelContenido.revalidate();
        panelContenido.repaint();
    }

    class GestionPrestamosPanel extends JPanel {
        public GestionPrestamosPanel() {
            setLayout(new BorderLayout());
            add(new JLabel("Aquí irá la tabla de préstamos con opción de devolución"), BorderLayout.CENTER);// se esta trabajando aún
        }
    }
}