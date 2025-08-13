package org.example.Modelo.Util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Utilidad de hashing SHA-256 (hex lower-case).
 * Nota: para producción, considerar sal y algoritmos de password hashing (e.g., bcrypt/argon2).
 */
public class HashUtil {
    public static String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] h = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : h) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) { throw new RuntimeException(e); }
    }
}
