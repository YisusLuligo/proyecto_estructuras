package edu.universidad.estructuras.proyecto_estructuras.util;

/**
 * Constantes del sistema SyncUp.
 *
 * @author Tu Nombre
 * @version 1.0
 */
public class Constants {

    // Aplicación
    public static final String APP_NAME = "SyncUp";
    public static final String APP_VERSION = "1.0.0";

    // Límites
    public static final int MAX_FAVORITOS = 1000;
    public static final int MAX_RESULTADOS_BUSQUEDA = 50;
    public static final int MAX_SUGERENCIAS_PARCEROS = 10;
    public static final int CANCIONES_DESCUBRIMIENTO = 20;
    public static final int CANCIONES_RADIO = 30;

    // Validaciones
    public static final int MIN_USERNAME_LENGTH = 3;
    public static final int MAX_USERNAME_LENGTH = 20;
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final int MAX_PASSWORD_LENGTH = 50;
    public static final int MIN_NOMBRE_LENGTH = 2;
    public static final int MAX_NOMBRE_LENGTH = 100;

    // Grafo de Similitud
    public static final double UMBRAL_SIMILITUD = 0.3;
    public static final double PESO_GENERO = 0.4;
    public static final double PESO_ARTISTA = 0.3;
    public static final double PESO_ANIO = 0.15;
    public static final double PESO_DURACION = 0.15;

    // Mensajes
    public static final String MSG_LOGIN_EXITOSO = "¡Bienvenido parce! 🎵";
    public static final String MSG_LOGIN_FALLIDO = "Usuario o contraseña incorrectos, mi llave";
    public static final String MSG_REGISTRO_EXITOSO = "¡Cuenta creada exitosamente, parcero!";
    public static final String MSG_USUARIO_EXISTE = "Ese usuario ya está pillado, busca otro";
    public static final String MSG_CAMPOS_VACIOS = "Llena todos los campos, hermano";
    public static final String MSG_PASSWORDS_NO_COINCIDEN = "Las contraseñas no coinciden, revisa bien";
    public static final String MSG_USERNAME_INVALIDO = "El usuario debe tener entre 3 y 20 caracteres";
    public static final String MSG_PASSWORD_CORTA = "La contraseña debe tener mínimo 6 caracteres";
    public static final String MSG_CANCION_AGREGADA = "¡Canción agregada a favoritos!";
    public static final String MSG_CANCION_ELIMINADA = "Canción eliminada de favoritos";
    public static final String MSG_YA_ES_FAVORITA = "Esa canción ya está en tus favoritos, parce";
    public static final String MSG_PARCERO_AGREGADO = "¡Ahora sigues a ese parcero!";
    public static final String MSG_PARCERO_ELIMINADO = "Dejaste de seguir a ese man";

    // Títulos de ventanas
    public static final String TITULO_LOGIN = "Iniciar Sesión - SyncUp";
    public static final String TITULO_REGISTRO = "Crear Cuenta - SyncUp";
    public static final String TITULO_PRINCIPAL = "SyncUp - Tu Música";
    public static final String TITULO_ADMIN = "SyncUp - Panel de Administración";
    public static final String TITULO_BUSQUEDA = "Buscar Música - SyncUp";
    public static final String TITULO_PERFIL = "Mi Perfil - SyncUp";
    public static final String TITULO_PARCEROS = "Mis Parceros - SyncUp";

    // Constructor privado
    private Constants() {
        throw new AssertionError("No se puede instanciar Constants");
    }
}