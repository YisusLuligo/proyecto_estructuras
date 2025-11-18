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
import java.net.URI;
import java.nio.charset.StandardCharsets;

/**
 * Controlador para el reproductor de YouTube
 * Carga directamente el video usando la URL de la canción
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
        webEngine.setJavaScriptEnabled(true);
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
     * Carga el video de YouTube
     * Si tiene URL, la usa directamente
     * Si no, crea un enlace de búsqueda
     */
    private void cargarYoutube() {
        if (cancionActual == null) {
            return;
        }

        try {
            String videoUrl;

            // ✅ Si tiene URL, usarla directamente
            if (cancionActual.tieneUrlYoutube()) {
                videoUrl = cancionActual.getUrlYoutube();
                System.out.println("✓ Cargando video directo: " + videoUrl);

                // Convertir URL normal a URL embebida
                String videoId = extraerVideoId(videoUrl);
                if (videoId != null) {
                    cargarVideoEmbebido(videoId);
                } else {
                    // Si no se puede extraer ID, abrir en navegador
                    abrirEnNavegadorExterno(videoUrl);
                }
            } else {
                // Si no tiene URL, buscar en YouTube
                System.out.println("⚠️ Canción sin URL, buscando en YouTube...");
                cargarBusquedaYoutube();
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al cargar YouTube: " + e.getMessage());
        }
    }

    /**
     * Extrae el ID del video de una URL de YouTube
     */
    private String extraerVideoId(String url) {
        try {
            if (url.contains("youtube.com/watch?v=")) {
                return url.split("v=")[1].split("&")[0];
            } else if (url.contains("youtu.be/")) {
                return url.split("youtu.be/")[1].split("\\?")[0];
            }
        } catch (Exception e) {
            System.err.println("Error al extraer video ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Carga el video embebido en el WebView
     */
    private void cargarVideoEmbebido(String videoId) {
        String htmlContent = String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body {
                        margin: 0;
                        padding: 0;
                        background-color: #000;
                        display: flex;
                        justify-content: center;
                        align-items: center;
                        height: 100vh;
                    }
                    iframe {
                        border: none;
                    }
                </style>
            </head>
            <body>
                <iframe 
                    width="100%%" 
                    height="100%%" 
                    src="https://www.youtube.com/embed/%s?autoplay=1" 
                    frameborder="0" 
                    allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" 
                    allowfullscreen>
                </iframe>
            </body>
            </html>
            """, videoId);

        webEngine.loadContent(htmlContent);
    }

    /**
     * Carga una búsqueda de YouTube si no hay URL
     */
    private void cargarBusquedaYoutube() {
        String query = cancionActual.getTitulo() + " " + cancionActual.getArtista();
        String htmlContent = String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body {
                        margin: 0;
                        padding: 20px;
                        background-color: #181818;
                        color: white;
                        font-family: Arial;
                        text-align: center;
                    }
                    .info {
                        margin-top: 50px;
                    }
                    a {
                        color: #3ea6ff;
                        text-decoration: none;
                        font-size: 18px;
                    }
                    a:hover {
                        text-decoration: underline;
                    }
                </style>
            </head>
            <body>
                <div class="info">
                    <h2>🎵 %s</h2>
                    <p>%s</p>
                    <p style="margin-top: 30px;">
                        Esta canción no tiene URL configurada.<br>
                        Haz clic en "Abrir en Navegador" para buscarla en YouTube.
                    </p>
                </div>
            </body>
            </html>
            """, cancionActual.getTitulo(), cancionActual.getArtista());

        webEngine.loadContent(htmlContent);
    }

    /**
     * Abre la URL directamente en el navegador externo
     */
    private void abrirEnNavegadorExterno(String url) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Detiene el audio del WebView
     */
    private void detenerAudio() {
        if (webEngine != null) {
            // Cargar contenido vacío para detener el video/audio
            webEngine.loadContent("<html><body></body></html>");
        }
    }

    /**
     * Cierra la ventana y detiene el audio
     */
    @FXML
    private void handleCerrar() {
        detenerAudio();
        Stage stage = (Stage) btnCerrar.getScene().getWindow();
        stage.close();
    }

    /**
     * Abre la canción en el navegador externo
     */
    @FXML
    private void handleAbrirNavegador() {
        if (cancionActual == null) {
            return;
        }

        try {
            String url;

            if (cancionActual.tieneUrlYoutube()) {
                // Usar URL directa
                url = cancionActual.getUrlYoutube();
            } else {
                // Crear búsqueda
                String query = cancionActual.getTitulo() + " " + cancionActual.getArtista() + " official";
                url = "https://www.youtube.com/results?search_query=" +
                        java.net.URLEncoder.encode(query, StandardCharsets.UTF_8);
            }

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
                mostrarExito("Abriendo en navegador...");
            } else {
                mostrarAdvertencia("No se puede abrir el navegador en este sistema");
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al abrir navegador: " + e.getMessage());
        }
    }

    /**
     * Agrega la canción a favoritos
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