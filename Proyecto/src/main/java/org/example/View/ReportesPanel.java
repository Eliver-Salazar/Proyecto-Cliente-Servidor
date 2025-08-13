package org.example.View;

import org.example.DAO.ReporteDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;

/**
 * Panel de reportes:
 * - Más prestados y Menos usados, en un rango elegido.
 * - Consulta ReporteDAO.
 */
public class ReportesPanel extends JPanel {
    private JTable tabla;
    private DefaultTableModel modelo;
    private JComboBox<String> comboRango;
    private JSpinner spLimite;

    public ReportesPanel() {
        setLayout(new BorderLayout(8,8));

        // Filtros de rango y límite
        JPanel top = new JPanel();
        comboRango = new JComboBox<>(new String[]{"30 días","90 días","180 días","365 días"});
        spLimite = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));
        JButton btnMas = new JButton("Más prestados");
        JButton btnMenos = new JButton("Menos usados");
        top.add(new JLabel("Rango:")); top.add(comboRango);
        top.add(new JLabel("Límite:")); top.add(spLimite);
        top.add(btnMas); top.add(btnMenos);
        add(top, BorderLayout.NORTH);

        // Tabla
        modelo = new DefaultTableModel(new Object[]{"ID Libro","Título","Conteo"}, 0);
        tabla = new JTable(modelo);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // Acciones
        btnMas.addActionListener(e -> cargar(true));
        btnMenos.addActionListener(e -> cargar(false));
    }

    /** Ejecuta el reporte seleccionado y llena la tabla. */
    private void cargar(boolean mas) {
        modelo.setRowCount(0);
        int dias = switch ((String) comboRango.getSelectedItem()) {
            case "30 días" -> 30;
            case "90 días" -> 90;
            case "180 días" -> 180;
            default -> 365;
        };
        int limite = (Integer) spLimite.getValue();
        LocalDate hasta = LocalDate.now();
        LocalDate desde = hasta.minusDays(dias);

        var dao = new ReporteDAO();
        var datos = mas ? dao.topPrestados(limite, desde, hasta)
                : dao.menosUsados(limite, desde, hasta);

        for (var it : datos) {
            modelo.addRow(new Object[]{ it.idLibro, it.titulo, it.conteo });
        }
    }
}
