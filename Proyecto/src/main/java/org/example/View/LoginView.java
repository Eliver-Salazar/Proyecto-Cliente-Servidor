package org.example.View;

import org.example.Net.BibliotecaClient;
import org.example.Net.Remote.Remotes;
import org.example.Net.Remote.Session;

import javax.swing.*;
import java.awt.*;

/**
 * Pantalla de Login:
 * - Permite autenticación.
 * - Redirige a EstudianteView o BibliotecarioView según el rol.
 * - Abre RegistroView para alta de usuarios.
 */
public class LoginView extends JFrame {
    private JTextField txtCorreo;
    private JPasswordField txtPassword;

    private final Remotes remotes;

    public LoginView(Remotes remotes) {
        this.remotes = remotes;
        setTitle("Login - Biblioteca Digital");
        setSize(400, 220);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(new JLabel("Correo:"));
        txtCorreo = new JTextField();
        panel.add(txtCorreo);
        panel.add(new JLabel("Contraseña:"));
        txtPassword = new JPasswordField();
        panel.add(txtPassword);

        JButton btnLogin = new JButton("Ingresar");
        JButton btnRegistro = new JButton("Crear Cuenta");
        panel.add(btnLogin);
        panel.add(btnRegistro);
        add(panel, BorderLayout.CENTER);

        btnLogin.addActionListener(e -> autenticar());
        btnRegistro.addActionListener(e -> new RegistroView(remotes).setVisible(true));
    }

    private void autenticar() {
        String correo = txtCorreo.getText().trim();
        String pass = new String(txtPassword.getPassword());
        try {
            Session s = remotes.auth.login(correo, pass);
            JOptionPane.showMessageDialog(this, "Bienvenido " + s.getNombre());
            if ("ESTUDIANTE".equalsIgnoreCase(s.getRol())) {
                new EstudianteView(s, remotes).setVisible(true);
            } else {
                new BibliotecarioView(s, remotes).setVisible(true);
            }
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Credenciales inválidas: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            // Cliente socket a localhost:5555
            BibliotecaClient client = new BibliotecaClient("127.0.0.1", 5555);
            Remotes remotes = new Remotes(client);
            SwingUtilities.invokeLater(() -> new LoginView(remotes).setVisible(true));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
