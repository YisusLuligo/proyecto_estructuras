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

/**
 * Controlador para la vista principal (Actualizado Fase 3)
 */
public class MainController {

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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/universidad/estructuras/proyecto_estructura/perfil.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnIrPerfil.getScene().getWindow();
            Scene scene = new Scene(root, 900, 700);
            stage.setScene(scene);
            stage.setTitle("SyncUp - Mi Perfil");
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("Error al cargar la pantalla de perfil: " + e.getMessage());
        }
    }

    @FXML
    private void handleMisFavoritos() {
        mostrarInformacion("Próximamente", "La funcionalidad de Mis Favoritos estará disponible pronto");
    }

    @FXML
    private void handleBuscar() {
        mostrarInformacion("Próximamente", "La funcionalidad de Búsqueda estará disponible pronto");
    }

    @FXML
    private void handleDescubrir() {
        mostrarInformacion("Próximamente", "La funcionalidad de Descubrimiento estará disponible pronto");
    }

    @FXML
    private void handleGestionCanciones() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/universidad/estructuras/proyecto_estructura/gestion-canciones.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnGestionCanciones.getScene().getWindow();
            Scene scene = new Scene(root, 1100, 700);
            stage.setScene(scene);
            stage.setTitle("SyncUp - Gestión de Canciones");
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("Error al cargar gestión de canciones: " + e.getMessage());
        }
    }

    @FXML
    private void handleGestionUsuarios() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/universidad/estructuras/proyecto_estructura/gestion-usuarios.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnGestionUsuarios.getScene().getWindow();
            Scene scene = new Scene(root, 1000, 600);
            stage.setScene(scene);
            stage.setTitle("SyncUp - Gestión de Usuarios");
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("Error al cargar gestión de usuarios: " + e.getMessage());
        }
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/universidad/estructuras/proyecto_estructura/login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnCerrarSesion.getScene().getWindow();
            Scene scene = new Scene(root, 800, 600);
            stage.setScene(scene);
            stage.setTitle("SyncUp - Inicio de Sesión");
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("Error al cargar la ventana de login: " + e.getMessage());
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

    private void mostrarInformacion(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}