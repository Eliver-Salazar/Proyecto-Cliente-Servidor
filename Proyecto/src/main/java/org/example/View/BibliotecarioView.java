package org.example.View;

import org.example.Modelo.Entidades.Usuario;

import javax.swing.*;
import java.awt.*;

/**
 * Vista del Bibliotecario:
 * - Menú superior (Gestión: Autor/Categoría)
 * - Menú lateral con accesos a: Registrar Autor/Categoría/Libro, Buscar, Préstamos, Reportes, Cerrar sesión
 * - Panel central conmutado
 */
public class BibliotecarioView extends JFrame {

    private JPanel panelContenido;

    public BibliotecarioView(Usuario usuario) {
        setTitle("Panel Bibliotecario");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ===== Menú superior (Gestión) =====
        JMenuBar menuBar = new JMenuBar();
        JMenu menuGestion = new JMenu("Gestión");
        JMenuItem itemAutores = new JMenuItem("Registrar Autor");
        JMenuItem itemCategorias = new JMenuItem("Registrar Categoría");

        itemAutores.addActionListener(e -> mostrarPanel(new RegistrarAutorPanel()));
        itemCategorias.addActionListener(e -> mostrarPanel(new RegistrarCategoriaPanel()));

        menuGestion.add(itemAutores);
        menuGestion.add(itemCategorias);
        menuBar.add(menuGestion);
        setJMenuBar(menuBar);

        // ===== Menú lateral =====
        JPanel menuPanel = new JPanel(new GridLayout(12, 1, 5, 5));
        JButton btnRegistrarAutor = new JButton("Registrar Autor");
        JButton btnRegistrarCategoria = new JButton("Registrar Categoría");
        JButton btnRegistrarLibro = new JButton("Registrar Libro");
        JButton btnBuscarLibros = new JButton("Buscar Libros");
        JButton btnPrestamos = new JButton("Préstamos/Devoluciones");
        JButton btnReportes = new JButton("Reportes");
        JButton btnCerrar = new JButton("Cerrar Sesión");

        menuPanel.add(btnRegistrarAutor);
        menuPanel.add(btnRegistrarCategoria);
        menuPanel.add(btnRegistrarLibro);
        menuPanel.add(btnBuscarLibros);
        menuPanel.add(btnPrestamos);
        menuPanel.add(btnReportes);
        menuPanel.add(btnCerrar);

        panelContenido = new JPanel(new BorderLayout());
        add(menuPanel, BorderLayout.WEST);
        add(panelContenido, BorderLayout.CENTER);

        // ===== Acciones de navegación =====
        btnRegistrarAutor.addActionListener(e -> mostrarPanel(new RegistrarAutorPanel()));
        btnRegistrarCategoria.addActionListener(e -> mostrarPanel(new RegistrarCategoriaPanel()));
        btnRegistrarLibro.addActionListener(e -> mostrarPanel(new RegistrarLibroPanel()));
        btnBuscarLibros.addActionListener(e -> mostrarPanel(new BuscarLibroPanel()));
        btnPrestamos.addActionListener(e -> mostrarPanel(new PrestamosPanel()));
        btnReportes.addActionListener(e -> mostrarPanel(new ReportesPanel()));
        btnCerrar.addActionListener(e -> { dispose(); new LoginView().setVisible(true); });
    }

    /** Cambia el panel central. */
    private void mostrarPanel(JPanel panel) {
        panelContenido.removeAll();
        panelContenido.add(panel, BorderLayout.CENTER);
        panelContenido.revalidate();
        panelContenido.repaint();
    }
}
