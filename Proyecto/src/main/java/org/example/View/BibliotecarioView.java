package org.example.View;

import org.example.Net.Remote.Remotes;
import org.example.Net.Remote.Session;

import javax.swing.*;
import java.awt.*;

public class BibliotecarioView extends JFrame {
    private JPanel panelContenido;
    private final Remotes remotes;
    private final Session session;

    public BibliotecarioView(Session session, Remotes remotes) {
        this.session = session; this.remotes = remotes;

        setTitle("Panel Bibliotecario");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Menú superior
        JMenuBar menuBar = new JMenuBar();
        JMenu menuGestion = new JMenu("Gestión");
        JMenuItem itemAutores = new JMenuItem("Registrar Autor");
        JMenuItem itemCategorias = new JMenuItem("Registrar Categoría");
        itemAutores.addActionListener(e -> mostrarPanel(new RegistrarAutorPanel(remotes)));
        itemCategorias.addActionListener(e -> mostrarPanel(new RegistrarCategoriaPanel(remotes)));
        menuGestion.add(itemAutores);
        menuGestion.add(itemCategorias);
        menuBar.add(menuGestion);
        setJMenuBar(menuBar);

        // Menú lateral
        JPanel menuPanel = new JPanel(new GridLayout(12, 1, 5, 5));
        JButton btnRegistrarAutor = new JButton("Registrar Autor");
        JButton btnRegistrarCategoria = new JButton("Registrar Categoría");
        JButton btnRegistrarLibro = new JButton("Registrar Libro");
        JButton btnBuscarLibros = new JButton("Buscar Libros");
        JButton btnPrestamos = new JButton("Préstamos/Devoluciones");
        JButton btnReportes = new JButton("Reportes");
        JButton btnAvisos = new JButton("Avisos (vencimientos)");
        JButton btnCerrar = new JButton("Cerrar Sesión");

        menuPanel.add(btnRegistrarAutor);
        menuPanel.add(btnRegistrarCategoria);
        menuPanel.add(btnRegistrarLibro);
        menuPanel.add(btnBuscarLibros);
        menuPanel.add(btnPrestamos);
        menuPanel.add(btnReportes);
        menuPanel.add(btnAvisos);
        menuPanel.add(btnCerrar);

        panelContenido = new JPanel(new BorderLayout());
        add(menuPanel, BorderLayout.WEST);
        add(panelContenido, BorderLayout.CENTER);

        // Acciones
        btnRegistrarAutor.addActionListener(e -> mostrarPanel(new RegistrarAutorPanel(remotes)));
        btnRegistrarCategoria.addActionListener(e -> mostrarPanel(new RegistrarCategoriaPanel(remotes)));
        btnRegistrarLibro.addActionListener(e -> mostrarPanel(new RegistrarLibroPanel(remotes)));
        btnBuscarLibros.addActionListener(e -> mostrarPanel(new BuscarLibroPanel(remotes)));
        btnPrestamos.addActionListener(e -> mostrarPanel(new PrestamosPanel(remotes)));
        btnAvisos.addActionListener(e -> mostrarPanel(new AvisosPanel()));
        btnReportes.addActionListener(e -> mostrarPanel(new ReportesPanel(remotes)));
        btnCerrar.addActionListener(e -> {
            dispose();
            new LoginView(remotes).setVisible(true);
        });
    }

    private void mostrarPanel(JPanel panel) {
        panelContenido.removeAll();
        panelContenido.add(panel, BorderLayout.CENTER);
        panelContenido.revalidate();
        panelContenido.repaint();
    }
}
