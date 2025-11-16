package edu.universidad.estructuras.proyecto_estructura.controller;

import edu.universidad.estructuras.proyecto_estructura.model.Cancion;
import edu.universidad.estructuras.proyecto_estructura.model.Usuario;
import edu.universidad.estructuras.proyecto_estructura.service.UsuarioService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Controlador para el reproductor de YouTube con WebView
 */
public class ReproductorYoutubeController {

    @FXML private WebView webView;
    @FXML private Label lblTitulo;
    @FXML private Label lblArtista;
    @FXML private Button btnCerrar;
    @FXML private Button btnAbrirNavegador;
    @FXML private Button btnAgregarFavoritos;

    private Cancion cancionActual;
    private WebEngine webEngine;
    private UsuarioService usuarioService;

    @FXML
    public void initialize() {
        usuarioService = UsuarioService.getInstance();
        webEngine = webView.getEngine();

        // Configurar WebView
        webEngine.setJavaScriptEnabled(true);

        // Opcional: Manejar errores de carga
        webEngine.getLoadWorker().exceptionProperty().addListener((obs, oldEx, newEx) -> {
            if (newEx != null) {
                System.err.println("Error al cargar página: " + newEx.getMessage());
            }
        });
    }

    /**
     * Establece la canción a reproducir
     */
    public void setCancion(Cancion cancion) {
        this.cancionActual = cancion;
        lblTitulo.setText(cancion.getTitulo());
        lblArtista.setText(cancion.getArtista());
        cargarYoutube();
    }

    /**
     * Carga la búsqueda de YouTube en el WebView
     */
    private void cargarYoutube() {
        if (cancionActual == null) {
            return;
        }

        try {
            // Construir query de búsqueda
            String query = cancionActual.getTitulo() + " " + cancionActual.getArtista();
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);

            // URL de búsqueda de YouTube
            String youtubeUrl = "https://www.youtube.com/results?search_query=" + encodedQuery;

            // Cargar en WebView
            webEngine.load(youtubeUrl);

        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al cargar YouTube: " + e.getMessage());
        }
    }

    /**
     * Cierra la ventana
     */
    @FXML
    private void handleCerrar() {
        Stage stage = (Stage) btnCerrar.getScene().getWindow();
        stage.close();
    }

    /**
     * Abre la canción en el navegador predeterminado
     */
    @FXML
    private void handleAbrirNavegador() {
        if (cancionActual == null) {
            return;
        }

        try {
            String query = cancionActual.getTitulo() + " " + cancionActual.getArtista();
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String youtubeUrl = "https://www.youtube.com/results?search_query=" + encodedQuery;

            // Abrir en navegador externo
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(youtubeUrl));
                mostrarExito("Abriendo en navegador externo...");
            } else {
                mostrarAdvertencia("No se puede abrir el navegador en este sistema");
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al abrir navegador: " + e.getMessage());
        }
    }

    /**
     * Agrega la canción a favoritos del usuario actual
     */
    @FXML
    private void handleAgregarFavoritos() {
        if (cancionActual == null) {
            return;
        }

        Usuario usuario = usuarioService.getUsuarioActual();
        if (usuario != null) {
            if (usuario.agregarFavorito(cancionActual)) {
                mostrarExito("Canción agregada a favoritos");
            } else {
                mostrarAdvertencia("La canción ya está en favoritos");
            }
        } else {
            mostrarError("No hay usuario activo");
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