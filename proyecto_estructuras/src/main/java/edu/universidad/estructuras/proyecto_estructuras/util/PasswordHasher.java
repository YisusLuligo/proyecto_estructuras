package edu.universidad.estructuras.proyecto_estructuras.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Hasheador de contraseñas con SHA-256 y salt.
 *
 * @author Tu Nombre
 * @version 1.0
 */
public class PasswordHasher {

    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;

    /**
     * Genera un salt aleatorio.
     */
    private static byte[] generarSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }

    /**
     * Hashea una contraseña con salt.
     *
     * @param password Contraseña en texto plano
     * @return String en formato "salt:hash"
     */
    public static String hashPassword(String password) {
        try {
            byte[] salt = generarSalt();
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            byte[] hash = md.digest(password.getBytes());

            String saltBase64 = Base64.getEncoder().encodeToString(salt);
            String hashBase64 = Base64.getEncoder().encodeToString(hash);

            return saltBase64 + ":" + hashBase64;

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al hashear contraseña", e);
        }
    }

    /**
     * Verifica si una contraseña coincide con un hash.
     *
     * @param password Contraseña en texto plano
     * @param storedHash Hash almacenado
     * @return true si coincide
     */
    public static boolean verificarPassword(String password, String storedHash) {
        try {
            String[] partes = storedHash.split(":");
            if (partes.length != 2) return false;

            byte[] salt = Base64.getDecoder().decode(partes[0]);
            byte[] hashAlmacenado = Base64.getDecoder().decode(partes[1]);

            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            byte[] hashCalculado = md.digest(password.getBytes());

            return MessageDigest.isEqual(hashAlmacenado, hashCalculado);

        } catch (Exception e) {
            return false;
        }
    }
}