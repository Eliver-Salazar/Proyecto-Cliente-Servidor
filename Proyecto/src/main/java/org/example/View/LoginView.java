package org.example.View;

import org.example.Modelo.Entidades.Usuario;
import org.example.Modelo.Servicios.AuthService;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JFrame {
    private JTextField txtCorreo;
    private JPasswordField txtPassword;

    public LoginView() {
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
        btnRegistro.addActionListener(e -> new RegistroView().setVisible(true));
    }

    private void autenticar() {
        String correo = txtCorreo.getText().trim();
        String pass = new String(txtPassword.getPassword());
        AuthService auth = new AuthService();
        Usuario u = auth.login(correo, pass);
        if (u != null) {
            JOptionPane.showMessageDialog(this, "Bienvenido " + u.getNombre());
            if ("ESTUDIANTE".equalsIgnoreCase(u.getRol())) {
                new EstudianteView(u).setVisible(true);
            } else {
                new BibliotecarioView(u).setVisible(true);
            }
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Credenciales inválidas");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginView().setVisible(true));
    }
}
