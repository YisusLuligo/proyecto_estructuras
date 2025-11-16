package edu.universidad.estructuras.proyecto_estructura.utils;

/**
 * Clase de utilidades para validaciones del sistema
 *
 * @author SyncUp Team
 * @version 1.0
 */
public class Validaciones {

    /**
     * Valida que un String no sea nulo ni vacío
     *
     * @param texto Texto a validar
     * @return true si es válido, false en caso contrario
     */
    public static boolean esTextoValido(String texto) {
        return texto != null && !texto.trim().isEmpty();
    }

    /**
     * Valida el formato de un username
     * Debe tener al menos 3 caracteres y solo letras, números y guión bajo
     *
     * @param username Username a validar
     * @return true si es válido, false en caso contrario
     */
    public static boolean esUsernameValido(String username) {
        if (!esTextoValido(username)) return false;
        return username.matches("^[a-zA-Z0-9_]{3,20}$");
    }

    /**
     * Valida el formato de una contraseña
     * Debe tener al menos 6 caracteres
     *
     * @param password Contraseña a validar
     * @return true si es válida, false en caso contrario
     */
    public static boolean esPasswordValido(String password) {
        return esTextoValido(password) && password.length() >= 6;
    }

    /**
     * Valida que un año esté en un rango razonable
     *
     * @param anio Año a validar
     * @return true si es válido (1900-2025), false en caso contrario
     */
    public static boolean esAnioValido(int anio) {
        return anio >= 1900 && anio <= 2025;
    }

    /**
     * Valida que una duración sea positiva
     *
     * @param duracion Duración en minutos
     * @return true si es válida, false en caso contrario
     */
    public static boolean esDuracionValida(double duracion) {
        return duracion > 0 && duracion < 60; // Asumimos máximo 60 minutos
    }
}