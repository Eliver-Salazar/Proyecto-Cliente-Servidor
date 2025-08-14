package org.example.View;

import org.example.DAO.ReporteDAO;
import org.example.Net.Remote.Remotes;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ReportesPanel extends JPanel {
    private JTable tabla;
    private DefaultTableModel modelo;
    private JComboBox<String> comboRango;
    private JSpinner spLimite;
    private final Remotes remotes;

    public ReportesPanel(Remotes remotes) {
        this.remotes = remotes;

        setLayout(new BorderLayout(8,8));

        JPanel top = new JPanel();
        comboRango = new JComboBox<>(new String[]{"30 días","90 días","180 días","365 días"});
        spLimite = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));
        JButton btnMas = new JButton("Más prestados");
        JButton btnMenos = new JButton("Menos usados");
        top.add(new JLabel("Rango:")); top.add(comboRango);
        top.add(new JLabel("Límite:")); top.add(spLimite);
        top.add(btnMas); top.add(btnMenos);
        add(top, BorderLayout.NORTH);

        modelo = new DefaultTableModel(new Object[]{"ID Libro","Título","Conteo"}, 0);
        tabla = new JTable(modelo);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        btnMas.addActionListener(e -> cargar(true));
        btnMenos.addActionListener(e -> cargar(false));
    }

    private void cargar(boolean mas) {
        try {
            modelo.setRowCount(0);
            int dias = switch ((String) comboRango.getSelectedItem()) {
                case "30 días" -> 30;
                case "90 días" -> 90;
                case "180 días" -> 180;
                default -> 365;
            };
            int limite = (Integer) spLimite.getValue();

            var datos = mas
                    ? remotes.reportes.topPrestados(limite, dias)
                    : remotes.reportes.menosUsados(limite, dias);

            for (ReporteDAO.ItemConteo it : datos) {
                modelo.addRow(new Object[]{ it.idLibro, it.titulo, it.conteo });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar reportes: " + ex.getMessage());
        }
    }
}
