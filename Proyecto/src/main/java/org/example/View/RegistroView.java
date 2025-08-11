package org.example.View;

import org.example.DAO.UsuarioDAO;
import org.example.Modelo.Entidades.Usuario;
import org.example.Modelo.Util.HashUtil;

import javax.swing.*;
import java.awt.*;


class RegistroView extends JFrame {
    private JTextField txtNombre, txtCorreo;
    private JPasswordField txtPassword;
    private JComboBox<String> comboRol;
    private JButton btnRegistrar;

    public RegistroView() {
        setTitle("Registro de Usuario");
        setSize(400, 300);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(6, 2, 10, 10));

        add(new JLabel("Nombre:")); txtNombre = new JTextField(); add(txtNombre);
        add(new JLabel("Correo:")); txtCorreo = new JTextField(); add(txtCorreo);
        add(new JLabel("Contraseña:")); txtPassword = new JPasswordField(); add(txtPassword);
        add(new JLabel("Rol:")); comboRol = new JComboBox<>(new String[]{"Estudiante", "Bibliotecario"}); add(comboRol);

        btnRegistrar = new JButton("Registrar");
        add(new JLabel()); add(btnRegistrar);

        btnRegistrar.addActionListener(e -> registrarUsuario());
    }

    private void registrarUsuario() {
        String nombre = txtNombre.getText();
        String correo = txtCorreo.getText();
        String contraseniaSinEncriptar = new String(txtPassword.getPassword());
        String contraseniaEncriptada = HashUtil.sha256(contraseniaSinEncriptar);

        Usuario usuario = new Usuario(0, nombre, correo, contraseniaEncriptada, (String) comboRol.getSelectedItem());
        UsuarioDAO dao = new UsuarioDAO();

        if (dao.registrarUsuario(usuario)) {
            JOptionPane.showMessageDialog(this, "Usuario registrado exitosamente");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error al registrar", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
