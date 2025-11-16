package edu.universidad.estructuras.proyecto_estructura.controller;

import edu.universidad.estructuras.proyecto_estructura.model.Cancion;
import edu.universidad.estructuras.proyecto_estructura.model.Usuario;
import edu.universidad.estructuras.proyecto_estructura.service.CancionService;
import edu.universidad.estructuras.proyecto_estructura.service.PlaylistService;
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
 * Controlador para "Descubrimiento Semanal" (RF-005)
 * Genera recomendaciones personalizadas basadas en los gustos del usuario
 * usando el Grafo de Similitud con algoritmo de Dijkstra
 *
 * @author SyncUp Team
 * @version 1.0
 */
public class DescubrimientoSemanalController {

    @FXML private Button btnVolver;
    @FXML private Button btnGenerar;
    @FXML private Button btnGuardarPlaylist;
    @FXML private Button btnReproducirTodo;
    @FXML private Button btnAgregarTodasFavoritos;
    @FXML private ProgressIndicator progressIndicator;
    @FXML private VBox panelRecomendaciones;
    @FXML private Label lblCantidadRecomendaciones;
    @FXML private Label lblBasadoEn;
    @FXML private Slider sliderCantidad;
    @FXML private Label lblCantidadSlider;

    // Tabla de recomendaciones
    @FXML private TableView<Cancion> tableRecomendaciones;
    @FXML private TableColumn<Cancion, Integer> colNumero;
    @FXML private TableColumn<Cancion, String> colTitulo;
    @FXML private TableColumn<Cancion, String> colArtista;
    @FXML private TableColumn<Cancion, String> colGenero;
    @FXML private TableColumn<Cancion, Integer> colAnio;
    @FXML private TableColumn<Cancion, String> colDuracion;
    @FXML private TableColumn<Cancion, Void> colAcciones;

    private CancionService cancionService;
    private UsuarioService usuarioService;
    private PlaylistService playlistService;
    private Usuario usuarioActual;
    private ObservableList<Cancion> recomendacionesObservable;

    @FXML
    public void initialize() {
        cancionService = CancionService.getInstance();
        usuarioService = UsuarioService.getInstance();
        playlistService = PlaylistService.getInstance();
        usuarioActual = usuarioService.getUsuarioActual();

        configurarTabla();
        configurarSlider();

        // Ocultar panel de recomendaciones inicialmente
        panelRecomendaciones.setVisible(false);
        panelRecomendaciones.setManaged(false);
        progressIndicator.setVisible(false);
    }

    private void configurarTabla() {
        // Columna de número
        colNumero.setCellFactory(column -> new TableCell<Cancion, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(String.valueOf(getIndex() + 1));
                }
            }
        });

        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colArtista.setCellValueFactory(new PropertyValueFactory<>("artista"));
        colGenero.setCellValueFactory(new PropertyValueFactory<>("genero"));
        colAnio.setCellValueFactory(new PropertyValueFactory<>("anio"));

        colDuracion.setCellValueFactory(cellData -> {
            double duracion = cellData.getValue().getDuracion();
            int minutos = (int) duracion;
            int segundos = (int) ((duracion - minutos) * 100);
            return new javafx.beans.property.SimpleStringProperty(
                    String.format("%d:%02d", minutos, segundos)
            );
        });

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

    private void configurarSlider() {
        sliderCantidad.setMin(10);
        sliderCantidad.setMax(50);
        sliderCantidad.setValue(20);
        sliderCantidad.setShowTickLabels(true);
        sliderCantidad.setShowTickMarks(true);
        sliderCantidad.setMajorTickUnit(10);

        sliderCantidad.valueProperty().addListener((obs, oldVal, newVal) -> {
            lblCantidadSlider.setText(String.valueOf(newVal.intValue()));
        });

        lblCantidadSlider.setText("20");
    }

    /**
     * Genera el Descubrimiento Semanal usando el Grafo de Similitud
     * Algoritmo:
     * 1. Analiza todas las canciones favoritas del usuario
     * 2. Por cada favorita, encuentra N canciones similares usando Dijkstra
     * 3. Puntúa las canciones según cuántas veces aparecen como similares
     * 4. Retorna las top N canciones con mayor score
     */
    @FXML
    private void handleGenerar() {
        if (usuarioActual == null) {
            mostrarError("No hay usuario activo");
            return;
        }

        if (usuarioActual.getCantidadFavoritos() == 0) {
            mostrarAdvertencia("Necesitas tener canciones en favoritos para generar recomendaciones");
            return;
        }

        int cantidadRecomendaciones = (int) sliderCantidad.getValue();

        // Mostrar indicador de progreso
        progressIndicator.setVisible(true);
        btnGenerar.setDisable(true);

        // Generar en hilo separado para no bloquear UI
        new Thread(() -> {
            try {
                List<Cancion> favoritos = usuarioActual.getListaFavoritos();

                // Map para contar apariciones (score de similitud)
                Map<Cancion, Integer> scoreSimilitud = new HashMap<>();

                // Por cada canción favorita, obtener similares
                for (Cancion favorita : favoritos) {
                    // Usar Dijkstra para encontrar canciones similares
                    List<Cancion> similares = cancionService.obtenerCancionesSimilares(
                            favorita, 15); // Top 15 similares por cada favorita

                    // Incrementar score por cada aparición
                    for (Cancion similar : similares) {
                        // No recomendar canciones ya en favoritos
                        if (!favoritos.contains(similar)) {
                            scoreSimilitud.put(similar,
                                    scoreSimilitud.getOrDefault(similar, 0) + 1);
                        }
                    }
                }

                // Ordenar por score (mayor score = más recomendada)
                List<Cancion> recomendaciones = scoreSimilitud.entrySet().stream()
                        .sorted(Map.Entry.<Cancion, Integer>comparingByValue().reversed())
                        .limit(cantidadRecomendaciones)
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList());

                // Actualizar UI en hilo de JavaFX
                javafx.application.Platform.runLater(() -> {
                    recomendacionesObservable = FXCollections.observableArrayList(recomendaciones);
                    tableRecomendaciones.setItems(recomendacionesObservable);

                    lblCantidadRecomendaciones.setText(recomendaciones.size() + " recomendaciones");
                    lblBasadoEn.setText("Basado en " + favoritos.size() + " canciones favoritas");

                    // Mostrar panel de recomendaciones
                    panelRecomendaciones.setVisible(true);
                    panelRecomendaciones.setManaged(true);

                    // Ocultar indicador de progreso
                    progressIndicator.setVisible(false);
                    btnGenerar.setDisable(false);

                    if (recomendaciones.isEmpty()) {
                        mostrarAdvertencia("No se encontraron recomendaciones. Agrega más canciones a favoritos.");
                    } else {
                        mostrarExito("¡Descubrimiento Semanal generado con " + recomendaciones.size() + " canciones!");
                    }
                });

            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    progressIndicator.setVisible(false);
                    btnGenerar.setDisable(false);
                    mostrarError("Error al generar recomendaciones: " + e.getMessage());
                    e.printStackTrace();
                });
            }
        }).start();
    }

    @FXML
    private void handleGuardarPlaylist() {
        if (recomendacionesObservable == null || recomendacionesObservable.isEmpty()) {
            mostrarAdvertencia("Primero genera el descubrimiento semanal");
            return;
        }

        // Crear playlist automáticamente
        String nombrePlaylist = "Descubrimiento Semanal - " +
                java.time.LocalDate.now().toString();

        var playlist = playlistService.crearPlaylist(
                nombrePlaylist,
                "Recomendaciones personalizadas generadas automáticamente",
                usuarioActual.getUsername()
        );

        // Agregar todas las canciones
        int agregadas = 0;
        for (Cancion cancion : recomendacionesObservable) {
            if (playlistService.agregarCancionAPlaylist(
                    usuarioActual.getUsername(),
                    playlist.getId(),
                    cancion)) {
                agregadas++;
            }
        }

        mostrarExito("Playlist creada con " + agregadas + " canciones: " + nombrePlaylist);
    }

    @FXML
    private void handleReproducirTodo() {
        if (recomendacionesObservable == null || recomendacionesObservable.isEmpty()) {
            mostrarAdvertencia("Primero genera el descubrimiento semanal");
            return;
        }

        mostrarInformacion("Reproducción",
                "Reproduciendo Descubrimiento Semanal:\n" +
                        recomendacionesObservable.size() + " canciones recomendadas");
    }

    @FXML
    private void handleAgregarTodasFavoritos() {
        if (recomendacionesObservable == null || recomendacionesObservable.isEmpty()) {
            mostrarAdvertencia("Primero genera el descubrimiento semanal");
            return;
        }

        int agregadas = 0;
        for (Cancion cancion : recomendacionesObservable) {
            if (usuarioActual.agregarFavorito(cancion)) {
                agregadas++;
            }
        }

        if (agregadas > 0) {
            mostrarExito(agregadas + " canciones agregadas a favoritos");
        } else {
            mostrarAdvertencia("Todas las canciones ya estaban en favoritos");
        }
    }

    @FXML
    private void handleVolver() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/edu/universidad/estructuras/proyecto_estructura/main.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnVolver.getScene().getWindow();
            Scene scene = new Scene(root, 900, 600);
            stage.setScene(scene);
            stage.setTitle("SyncUp - Panel Principal");
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("Error al cargar la ventana principal: " + e.getMessage());
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
            mostrarExito("Canción agregada a favoritos");
        } else {
            mostrarAdvertencia("La canción ya está en favoritos");
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