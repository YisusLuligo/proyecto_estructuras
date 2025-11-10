package edu.universidad.estructuras.proyecto_estructuras.util;

import java.util.regex.Pattern;

/**
 * Validador de datos del sistema.
 *
 * @author Tu Nombre
 * @version 1.0
 */
public class Validator {

    private static final Pattern USERNAME_PATTERN =
            Pattern.compile("^[a-zA-Z0-9_]{3,20}$");

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    /**
     * Valida un nombre de usuario.
     * Debe tener entre 3 y 20 caracteres alfanuméricos o guión bajo.
     */
    public static boolean validarUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return USERNAME_PATTERN.matcher(username).matches();
    }

    /**
     * Valida una contraseña.
     * Debe tener al menos 6 caracteres.
     */
    public static boolean validarPassword(String password) {
        if (password == null) return false;
        return password.length() >= Constants.MIN_PASSWORD_LENGTH &&
                password.length() <= Constants.MAX_PASSWORD_LENGTH;
    }

    /**
     * Valida un correo electrónico.
     */
    public static boolean validarEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Valida un nombre completo.
     */
    public static boolean validarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return false;
        }
        int length = nombre.trim().length();
        return length >= Constants.MIN_NOMBRE_LENGTH &&
                length <= Constants.MAX_NOMBRE_LENGTH;
    }

    /**
     * Valida un año.
     */
    public static boolean validarAnio(int anio) {
        int anioActual = java.time.Year.now().getValue();
        return anio >= 1900 && anio <= anioActual;
    }

    /**
     * Valida una duración en segundos.
     */
    public static boolean validarDuracion(int duracionSegundos) {
        return duracionSegundos > 0 && duracionSegundos <= 7200; // Máximo 2 horas
    }

    /**
     * Verifica que un string no sea nulo ni vacío.
     */
    public static boolean noVacio(String texto) {
        return texto != null && !texto.trim().isEmpty();
    }

    /**
     * Sanitiza un string eliminando caracteres peligrosos.
     */
    public static String sanitizar(String input) {
        if (input == null) return "";
        return input.replaceAll("[<>\"']", "").trim();
    }
}