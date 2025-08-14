package org.example.View;

import org.example.Modelo.Entidades.Prestamo;
import org.example.Modelo.Servicios.PrestamoService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AvisosPanel extends JPanel {
    private final PrestamoService service = new PrestamoService();

    private JTable tabla;
    private DefaultTableModel modelo;

    public AvisosPanel() {
        setLayout(new BorderLayout(8,8));

        modelo = new DefaultTableModel(new Object[]{"ID Préstamo","Usuario","Libro","Inicio","Vence"}, 0);
        tabla = new JTable(modelo);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel abajo = new JPanel();
        JButton btnRefrescar = new JButton("Ver que vencen mañana");
        JButton btnEnviar = new JButton("Enviar correos de aviso");
        abajo.add(btnRefrescar);
        abajo.add(btnEnviar);
        add(abajo, BorderLayout.SOUTH);

        btnRefrescar.addActionListener(e -> cargar());
        btnEnviar.addActionListener(e -> enviar());

        cargar();
    }

    private void cargar() {
        modelo.setRowCount(0);
        List<Prestamo> lista = service.listarVencenManiana();
        for (Prestamo p : lista) {
            modelo.addRow(new Object[]{
                    p.getId(), p.getUsuarioId(), p.getLibroId(),
                    p.getFechaInicio(), p.getFechaVencimiento()
            });
        }
        if (lista.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay préstamos que venzan mañana.");
        }
    }

    private void enviar() {
        int enviados = service.enviarAvisosVencimiento();
        JOptionPane.showMessageDialog(this,
                (enviados == 0)
                        ? "No había préstamos próximos a vencer."
                        : ("Se enviaron " + enviados + " correos."));
    }
}

