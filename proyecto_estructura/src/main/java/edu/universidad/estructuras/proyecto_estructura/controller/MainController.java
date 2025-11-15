package edu.universidad.estructuras.proyecto_estructura.controller;


import edu.universidad.estructuras.proyecto_estructura.model.Cancion;
import edu.universidad.estructuras.proyecto_estructura.model.Usuario;
import edu.universidad.estructuras.proyecto_estructura.service.UsuarioService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controlador para la vista principal
 *
 * @author SyncUp Team
 * @version 1.0
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
    @FXML private Button btnCerrarSesion;
    @FXML private Button btnProbarFavoritos;

    private UsuarioService usuarioService;
    private Usuario usuarioActual;

    /**
     * Inicializa el controlador
     */
    @FXML
    public void initialize() {
        usuarioService = UsuarioService.getInstance();
        usuarioActual = usuarioService.getUsuarioActual();

        cargarDatosUsuario();
        cargarEstadisticas();
    }

    /**
     * Carga los datos del usuario actual
     */
    private void cargarDatosUsuario() {
        if (usuarioActual != null) {
            lblBienvenida.setText("Bienvenido, " + usuarioActual.getNombre());
            lblTipoUsuario.setText("[" + usuarioActual.getTipoUsuario() + "]");
            lblUsername.setText(usuarioActual.getUsername());
            lblNombre.setText(usuarioActual.getNombre());
            lblCantidadFavoritos.setText(String.valueOf(usuarioActual.getCantidadFavoritos()));
            lblTipoCuenta.setText(usuarioActual.getTipoUsuario().toString());
        }
    }

    /**
     * Carga las estadísticas del sistema
     */
    private void cargarEstadisticas() {
        lblTotalUsuarios.setText(String.valueOf(usuarioService.getCantidadUsuarios()));
        lblTotalCanciones.setText("0"); // Por ahora, hasta implementar el catálogo
    }

    /**
     * Maneja el evento de cerrar sesión
     */
    @FXML
    private void handleCerrarSesion() {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Cerrar Sesión");
        confirmacion.setHeaderText("¿Está seguro que desea cerrar sesión?");
        confirmacion.setContentText("Será redirigido a la pantalla de inicio de sesión.");

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                usuarioService.cerrarSesion();
                volverALogin();
            }
        });
    }

    /**
     * Maneja el botón de prueba para agregar canción a favoritos
     */
    @FXML
    private void handleAgregarCancionPrueba() {
        // Crear una canción de prueba
        Cancion cancionPrueba = new Cancion(
                "TEST001",
                "Canción de Prueba",
                "Artista Demo",
                "Pop",
                2024,
                3.5
        );

        if (usuarioActual.agregarFavorito(cancionPrueba)) {
            lblCantidadFavoritos.setText(String.valueOf(usuarioActual.getCantidadFavoritos()));

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Éxito");
            alert.setHeaderText(null);
            alert.setContentText("Canción agregada a favoritos:\n" + cancionPrueba.toString());
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Advertencia");
            alert.setHeaderText(null);
            alert.setContentText("La canción ya está en favoritos");
            alert.showAndWait();
        }
    }

    /**
     * Vuelve a la pantalla de login
     */
    private void volverALogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnCerrarSesion.getScene().getWindow();
            Scene scene = new Scene(root, 800, 600);
            stage.setScene(scene);
            stage.setTitle("SyncUp - Inicio de Sesión");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}