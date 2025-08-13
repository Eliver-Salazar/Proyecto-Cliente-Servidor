package org.example.DAO;

import org.example.Database.Conexion;
import org.example.Modelo.Entidades.Usuario;

import java.sql.*;

/**
 * DAO de Usuario.
 * - Registrar nuevos usuarios.
 * - Autenticar por contraseña plana o hash (compatibilidad).
 * - Buscar por id.
 */
public class UsuarioDAO {

    /**
     * Inserta un nuevo usuario en BD.
     * La contraseña debe venir YA hasheada si quieres almacenamiento seguro.
     */
    public boolean registrarUsuario(Usuario usuario) {
        String sql = "INSERT INTO Usuario(nombre, correo, contraseña, rol) VALUES (?, ?, ?, ?)";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getCorreo());
            stmt.setString(3, usuario.getContraseña()); // ya viene hasheada
            stmt.setString(4, usuario.getRol());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error registrando usuario: " + e.getMessage());
            return false;
        }
    }

    /**
     * Autenticación por correo + contraseña en claro (compatibilidad).
     * Preferir {@link #autenticarHash(String, String)}.
     */
    public Usuario autenticar(String correo, String passwordPlano) {
        String sql = "SELECT * FROM Usuario WHERE correo=? AND contraseña=?";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, correo);
            stmt.setString(2, passwordPlano);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    /**
     * Autenticación por correo + hash (recomendado).
     */
    public Usuario autenticarHash(String correo, String hash) {
        String sql = "SELECT * FROM Usuario WHERE correo=? AND contraseña=?";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, correo);
            stmt.setString(2, hash);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    /**
     * Busca un usuario por su ID primario.
     */
    public Usuario findById(int id) {
        String sql = "SELECT * FROM Usuario WHERE id_usuario=?";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    /** Mapea una fila del ResultSet a la entidad Usuario. */
    private Usuario map(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getInt("id_usuario"),
                rs.getString("nombre"),
                rs.getString("correo"),
                rs.getString("contraseña"),
                rs.getString("rol")
        );
    }
}
