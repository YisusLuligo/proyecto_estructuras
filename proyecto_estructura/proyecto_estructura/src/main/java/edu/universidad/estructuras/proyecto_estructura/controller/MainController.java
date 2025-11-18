package edu.universidad.estructuras.proyecto_estructura.controller;

import edu.universidad.estructuras.proyecto_estructura.model.Cancion;
import edu.universidad.estructuras.proyecto_estructura.model.Usuario;
import edu.universidad.estructuras.proyecto_estructura.service.CancionService;
import edu.universidad.estructuras.proyecto_estructura.service.UsuarioService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class MainController {
    @FXML private Button btnRedSocial;
    @FXML private Label lblBienvenida;
    @FXML private Label lblTipoUsuario;
    @FXML private Label lblUsername;
    @FXML private Label lblNombre;
    @FXML private Label lblCantidadFavoritos;
    @FXML private Label lblTipoCuenta;
    @FXML private Label lblTotalUsuarios;
    @FXML private Label lblTotalCanciones;
    @FXML private Label lblSubtitulo;
    @FXML private Button btnCerrarSesion;
    @FXML private Button btnProbarFavoritos;
    @FXML private Button btnIrPerfil;
    @FXML private Button btnMiPerfil;
    @FXML private Button btnMisFavoritos;
    @FXML private Button btnBuscar;
    @FXML private Button btnDescubrir;
    @FXML private Button btnGestionCanciones;
    @FXML private Button btnGestionUsuarios;
    @FXML private VBox menuUsuario;
    @FXML private VBox menuAdmin;
    @FXML private Separator separadorAdmin;
    @FXML private Button btnPlaylists;
    @FXML private Button btnDescubrimientoSemanal;

    private UsuarioService usuarioService;
    private CancionService cancionService;
    private Usuario usuarioActual;

    @FXML
    public void initialize() {
        usuarioService = UsuarioService.getInstance();
        cancionService = CancionService.getInstance();
        usuarioActual = usuarioService.getUsuarioActual();

        if (usuarioActual != null) {
            cargarDatosUsuario();
            cargarEstadisticas();
            configurarMenuSegunTipoUsuario();
        }
    }

    private void cargarDatosUsuario() {
        lblBienvenida.setText("Bienvenido, " + usuarioActual.getNombre());
        lblTipoUsuario.setText("[" + usuarioActual.getTipoUsuario() + "]");
        lblUsername.setText(usuarioActual.getUsername());
        lblNombre.setText(usuarioActual.getNombre());
        lblCantidadFavoritos.setText(String.valueOf(usuarioActual.getCantidadFavoritos()));
        lblTipoCuenta.setText(usuarioActual.getTipoUsuario().toString());

        if (usuarioActual.esAdministrador()) {
            lblSubtitulo.setText("Panel de Administración");
        }
    }

    private void cargarEstadisticas() {
        lblTotalUsuarios.setText(String.valueOf(usuarioService.getCantidadUsuarios()));
        lblTotalCanciones.setText(String.valueOf(cancionService.getCantidadCanciones()));
    }

    private void configurarMenuSegunTipoUsuario() {
        if (usuarioActual.esAdministrador()) {
            menuAdmin.setVisible(true);
            menuAdmin.setManaged(true);
            separadorAdmin.setVisible(true);
            separadorAdmin.setManaged(true);
        } else {
            menuAdmin.setVisible(false);
            menuAdmin.setManaged(false);
            separadorAdmin.setVisible(false);
            separadorAdmin.setManaged(false);
        }
    }

    @FXML
    private void handleIrPerfil() {
        abrirVentana("/edu/universidad/estructuras/proyecto_estructura/perfil.fxml",
                "MusicApp - Mi Perfil", 1000, 800);
    }

    @FXML
    private void handleGestionCanciones() {
        abrirVentana("/edu/universidad/estructuras/proyecto_estructura/gestion-canciones.fxml",
                "MusicApp - Gestión de Canciones", 1200, 800);
    }

    @FXML
    private void handleGestionUsuarios() {
        abrirVentana("/edu/universidad/estructuras/proyecto_estructura/gestion-usuarios.fxml",
                "MusicApp - Gestión de Usuarios", 1100, 800);
    }

    @FXML
    private void handleAgregarCancionPrueba() {
        String id = "TEST" + String.format("%03d", usuarioActual.getCantidadFavoritos() + 1);
        String[] generos = {"Pop", "Rock", "Jazz", "Electrónica", "Reggaeton", "Salsa", "Blues", "Country"};
        String[] artistas = {"Artista Demo", "The Beatles", "Queen", "Pink Floyd", "Led Zeppelin",
                "Michael Jackson", "Madonna", "Coldplay"};
        String genero = generos[(int) (Math.random() * generos.length)];
        String artista = artistas[(int) (Math.random() * artistas.length)];

        Cancion cancionPrueba = new Cancion(
                id,
                "Canción de Prueba " + (usuarioActual.getCantidadFavoritos() + 1),
                artista,
                genero,
                2015 + (int) (Math.random() * 10),
                2.0 + (Math.random() * 4)
        );

        if (usuarioActual.agregarFavorito(cancionPrueba)) {
            lblCantidadFavoritos.setText(String.valueOf(usuarioActual.getCantidadFavoritos()));
            mostrarExito("Canción agregada a favoritos.\nAhora puedes verla en 'Mi Perfil'");
        } else {
            mostrarAdvertencia("La canción ya está en favoritos");
        }
    }

    @FXML
    private void handleCerrarSesion() {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Cerrar Sesión");
        confirmacion.setHeaderText("¿Está seguro que desea cerrar sesión?");
        confirmacion.setContentText("Será redirigido a la pantalla de inicio de sesión.");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            usuarioService.cerrarSesion();
            volverALogin();
        }
    }

    private void volverALogin() {
        abrirVentana("/edu/universidad/estructuras/proyecto_estructura/login.fxml",
                "MusicApp - Inicio de Sesión", 800, 800);
    }

    @FXML
    private void handleBuscar() {
        abrirVentana("/edu/universidad/estructuras/proyecto_estructura/busqueda.fxml",
                "MusicApp - Búsqueda Avanzada", 1100, 800);
    }

    @FXML
    private void handleDescubrir() {
        abrirVentana("/edu/universidad/estructuras/proyecto_estructura/descubrimiento.fxml",
                "MusicApp - Descubrir Música", 1100, 800);
    }

    @FXML
    private void handleMisFavoritos() {
        abrirVentana("/edu/universidad/estructuras/proyecto_estructura/favoritos.fxml",
                "MusicApp - Mis Favoritos", 1100, 800);
    }

    @FXML
    private void handlePlaylists() {
        abrirVentana("/edu/universidad/estructuras/proyecto_estructura/playlist.fxml",
                "MusicApp - Mis Playlists", 1100, 800);
    }

    @FXML
    private void handleRedSocial() {
        abrirVentana("/edu/universidad/estructuras/proyecto_estructura/red-social.fxml",
                "MusicApp - Red Social", 1100, 800);
    }

    @FXML
    private void handleDescubrimientoSemanal() {
        abrirVentana("/edu/universidad/estructuras/proyecto_estructura/descubrimiento-semanal.fxml",
                "MusicApp - Descubrimiento Semanal", 1100, 800);
    }

    private void abrirVentana(String fxmlPath, String titulo, int ancho, int alto) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = (Stage) btnCerrarSesion.getScene().getWindow();

            // Forzar el tamaño exacto
            stage.setWidth(ancho);
            stage.setHeight(alto);
            stage.setMinWidth(ancho);
            stage.setMinHeight(alto);
            stage.setMaxWidth(ancho);
            stage.setMaxHeight(alto);

            Scene scene = new Scene(root, ancho, alto);
            stage.setScene(scene);
            stage.setTitle(titulo);

            // Centrar ventana
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("Error al cargar la ventana: " + e.getMessage());
        }
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarExito(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarAdvertencia(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advertencia");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}