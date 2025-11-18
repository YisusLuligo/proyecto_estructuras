package edu.universidad.estructuras.proyecto_estructura.controller;


import edu.universidad.estructuras.proyecto_estructura.model.Cancion;
import edu.universidad.estructuras.proyecto_estructura.model.Usuario;
import edu.universidad.estructuras.proyecto_estructura.service.CancionService;
import edu.universidad.estructuras.proyecto_estructura.service.UsuarioService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controlador para ver el perfil público de otros usuarios
 * Muestra información básica y canciones favoritas
 *
 */
public class PerfilPublicoController {

    @FXML private Button btnVolver;
    @FXML private Button btnSeguir;
    @FXML private Button btnDejarDeSeguir;
    @FXML private Label lblUsername;
    @FXML private Label lblNombre;
    @FXML private Label lblCantidadFavoritos;
    @FXML private Label lblCantidadSeguidos;
    @FXML private Label lblCantidadSeguidores;
    @FXML private Label lblGeneroFavorito;
    @FXML private Label lblRelacion;

    // Tabla de favoritos públicos
    @FXML private TableView<Cancion> tableFavoritos;
    @FXML private TableColumn<Cancion, String> colTitulo;
    @FXML private TableColumn<Cancion, String> colArtista;
    @FXML private TableColumn<Cancion, String> colGenero;
    @FXML private TableColumn<Cancion, Integer> colAnio;
    @FXML private TableColumn<Cancion, Void> colAcciones;

    // Recomendaciones basadas en sus gustos
    @FXML private VBox panelRecomendaciones;
    @FXML private ListView<Cancion> listRecomendacionesComunes;
    @FXML private Label lblCancionesComunes;

    private UsuarioService usuarioService;
    private CancionService cancionService;
    private Usuario usuarioActual;
    private Usuario usuarioPublico; // El usuario cuyo perfil estamos viendo
    private ObservableList<Cancion> favoritosObservable;

    @FXML
    public void initialize() {
        usuarioService = UsuarioService.getInstance();
        cancionService = CancionService.getInstance();
        usuarioActual = usuarioService.getUsuarioActual();

        configurarTabla();
        configurarListView();
    }

    /**
     * Establece el usuario cuyo perfil público se va a mostrar
     *
     * @param usuario Usuario a mostrar
     */
    public void setUsuarioPublico(Usuario usuario) {
        this.usuarioPublico = usuario;
        cargarDatosUsuario();
        cargarFavoritos();
        cargarRecomendaciones();
        actualizarBotonesSeguimiento();
    }

    private void configurarTabla() {
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colArtista.setCellValueFactory(new PropertyValueFactory<>("artista"));
        colGenero.setCellValueFactory(new PropertyValueFactory<>("genero"));
        colAnio.setCellValueFactory(new PropertyValueFactory<>("anio"));

        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnReproducir = new Button("▶️");
            private final Button btnFavorito = new Button("❤️");

            {
                btnReproducir.setStyle("-fx-background-color: #FF0000; -fx-text-fill: white; -fx-cursor: hand;");
                btnFavorito.setStyle("-fx-background-color: #E91E63; -fx-text-fill: white; -fx-cursor: hand;");

                btnReproducir.setOnAction(event -> {
                    Cancion cancion = getTableView().getItems().get(getIndex());
                    abrirReproductor(cancion);
                });

                btnFavorito.setOnAction(event -> {
                    Cancion cancion = getTableView().getItems().get(getIndex());
                    agregarAFavoritos(cancion);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(5, btnReproducir, btnFavorito);
                    hbox.setAlignment(Pos.CENTER);
                    setGraphic(hbox);
                }
            }
        });
    }

    private void configurarListView() {
        listRecomendacionesComunes.setCellFactory(param -> new ListCell<Cancion>() {
            @Override
            protected void updateItem(Cancion cancion, boolean empty) {
                super.updateItem(cancion, empty);
                if (empty || cancion == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(cancion.toString());
                    setStyle("-fx-padding: 10; -fx-font-size: 12;");
                }
            }
        });
    }

    private void cargarDatosUsuario() {
        if (usuarioPublico == null) return;

        lblUsername.setText(usuarioPublico.getUsername());
        lblNombre.setText(usuarioPublico.getNombre());
        lblCantidadFavoritos.setText(String.valueOf(usuarioPublico.getCantidadFavoritos()));

        // Estadísticas sociales
        int seguidos = usuarioService.getCantidadSeguidos(usuarioPublico.getUsername());
        int seguidores = usuarioService.getCantidadSeguidores(usuarioPublico.getUsername());
        lblCantidadSeguidos.setText(String.valueOf(seguidos));
        lblCantidadSeguidores.setText(String.valueOf(seguidores));

        // Género favorito
        Map<String, Long> conteoPorGenero = usuarioPublico.getListaFavoritos().stream()
                .collect(Collectors.groupingBy(Cancion::getGenero, Collectors.counting()));

        String generoFavorito = conteoPorGenero.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");
        lblGeneroFavorito.setText(generoFavorito);

        // Determinar relación
        actualizarRelacion();
    }

    private void actualizarRelacion() {
        if (usuarioService.estaSiguiendo(usuarioActual.getUsername(), usuarioPublico.getUsername())) {
            if (usuarioService.estaSiguiendo(usuarioPublico.getUsername(), usuarioActual.getUsername())) {
                lblRelacion.setText("👥 Se siguen mutuamente");
                lblRelacion.setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
            } else {
                lblRelacion.setText("➡️ Lo sigues");
                lblRelacion.setStyle("-fx-text-fill: #2196F3; -fx-font-weight: bold;");
            }
        } else {
            if (usuarioService.estaSiguiendo(usuarioPublico.getUsername(), usuarioActual.getUsername())) {
                lblRelacion.setText("⬅️ Te sigue");
                lblRelacion.setStyle("-fx-text-fill: #FF9800; -fx-font-weight: bold;");
            } else {
                lblRelacion.setText("Sin conexión");
                lblRelacion.setStyle("-fx-text-fill: #999;");
            }
        }
    }

    private void cargarFavoritos() {
        List<Cancion> favoritos = usuarioPublico.getListaFavoritos();
        favoritosObservable = FXCollections.observableArrayList(favoritos);
        tableFavoritos.setItems(favoritosObservable);
    }

    /**
     * Carga recomendaciones basadas en gustos comunes
     * Encuentra canciones que ambos usuarios tienen en favoritos
     */
    private void cargarRecomendaciones() {
        List<Cancion> favoritosUsuarioActual = usuarioActual.getListaFavoritos();
        List<Cancion> favoritosUsuarioPublico = usuarioPublico.getListaFavoritos();

        // Encontrar canciones en común
        Set<Cancion> cancionesComunes = new HashSet<>(favoritosUsuarioActual);
        cancionesComunes.retainAll(favoritosUsuarioPublico);

        if (!cancionesComunes.isEmpty()) {
            listRecomendacionesComunes.setItems(FXCollections.observableArrayList(cancionesComunes));
            lblCancionesComunes.setText(cancionesComunes.size() + " canciones en común");
            panelRecomendaciones.setVisible(true);
            panelRecomendaciones.setManaged(true);
        } else {
            // Buscar canciones similares basadas en sus favoritos
            List<Cancion> recomendacionesBasadasEnEllos = obtenerRecomendacionesBasadasEnSusGustos();
            if (!recomendacionesBasadasEnEllos.isEmpty()) {
                listRecomendacionesComunes.setItems(FXCollections.observableArrayList(recomendacionesBasadasEnEllos));
                lblCancionesComunes.setText("Recomendaciones basadas en sus gustos");
                panelRecomendaciones.setVisible(true);
                panelRecomendaciones.setManaged(true);
            } else {
                panelRecomendaciones.setVisible(false);
                panelRecomendaciones.setManaged(false);
            }
        }
    }

    /**
     * Genera recomendaciones basadas en los gustos del usuario público
     * que el usuario actual podría disfrutar
     */
    private List<Cancion> obtenerRecomendacionesBasadasEnSusGustos() {
        List<Cancion> recomendaciones = new ArrayList<>();
        Set<Cancion> favoritosActual = new HashSet<>(usuarioActual.getListaFavoritos());

        // Por cada favorito del usuario público, buscar similares
        for (Cancion favoritoPublico : usuarioPublico.getListaFavoritos()) {
            List<Cancion> similares = cancionService.obtenerCancionesSimilares(favoritoPublico, 5);

            for (Cancion similar : similares) {
                // Agregar solo si el usuario actual no la tiene en favoritos
                if (!favoritosActual.contains(similar) && !recomendaciones.contains(similar)) {
                    recomendaciones.add(similar);
                }
            }

            // Limitar a 10 recomendaciones
            if (recomendaciones.size() >= 10) {
                break;
            }
        }

        return recomendaciones;
    }

    private void actualizarBotonesSeguimiento() {
        boolean estaSiguiendo = usuarioService.estaSiguiendo(
                usuarioActual.getUsername(),
                usuarioPublico.getUsername()
        );

        btnSeguir.setVisible(!estaSiguiendo);
        btnSeguir.setManaged(!estaSiguiendo);
        btnDejarDeSeguir.setVisible(estaSiguiendo);
        btnDejarDeSeguir.setManaged(estaSiguiendo);
    }

    @FXML
    private void handleSeguir() {
        if (usuarioService.seguirUsuario(usuarioActual.getUsername(), usuarioPublico.getUsername())) {
            mostrarExito("Ahora sigues a " + usuarioPublico.getUsername());
            actualizarBotonesSeguimiento();
            actualizarRelacion();
            cargarDatosUsuario();
        }
    }

    @FXML
    private void handleDejarDeSeguir() {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar");
        confirmacion.setHeaderText("¿Dejar de seguir a " + usuarioPublico.getUsername() + "?");

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (usuarioService.dejarDeSeguirUsuario(usuarioActual.getUsername(), usuarioPublico.getUsername())) {
                    mostrarExito("Dejaste de seguir a " + usuarioPublico.getUsername());
                    actualizarBotonesSeguimiento();
                    actualizarRelacion();
                    cargarDatosUsuario();
                }
            }
        });
    }

    @FXML
    private void handleVolver() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/edu/universidad/estructuras/proyecto_estructura/red-social.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnVolver.getScene().getWindow();
            Scene scene = new Scene(root, 1200, 900);
            stage.setScene(scene);
            stage.setTitle("MusicApp - Red Social");
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("Error al volver: " + e.getMessage());
        }
    }

    private void abrirReproductor(Cancion cancion) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/edu/universidad/estructuras/proyecto_estructura/reproductor-youtube.fxml"));
            Parent root = loader.load();

            ReproductorYoutubeController controller = loader.getController();
            controller.setCancion(cancion);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Reproducir - " + cancion.getTitulo());
            stage.setScene(new Scene(root, 900, 700));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("Error al abrir el reproductor: " + e.getMessage());
        }
    }

    private void agregarAFavoritos(Cancion cancion) {
        if (usuarioActual.agregarFavorito(cancion)) {
            mostrarExito("Canción agregada a tus favoritos");
            cargarRecomendaciones(); // Actualizar recomendaciones
        } else {
            mostrarAdvertencia("La canción ya está en tus favoritos");
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