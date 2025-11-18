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
import java.util.List;

/**
 * Controlador para la función "Radio" - genera estaciones basadas en similitud
 * Utiliza el Grafo de Similitud con algoritmo de Dijkstra
 *
 */
public class RadioController {

    @FXML private Button btnVolver;
    @FXML private Label lblCancionSemilla;
    @FXML private Label lblArtistaSemilla;
    @FXML private Label lblGeneroSemilla;
    @FXML private Label lblCantidadCanciones;
    @FXML private Slider sliderCantidad;
    @FXML private Button btnGenerar;
    @FXML private Button btnReproducirTodo;
    @FXML private Button btnAgregarTodasFavoritos;
    @FXML private TableView<Cancion> tableRadio;
    @FXML private TableColumn<Cancion, Integer> colNumero;
    @FXML private TableColumn<Cancion, String> colTitulo;
    @FXML private TableColumn<Cancion, String> colArtista;
    @FXML private TableColumn<Cancion, String> colGenero;
    @FXML private TableColumn<Cancion, Integer> colAnio;
    @FXML private TableColumn<Cancion, String> colDuracion;
    @FXML private TableColumn<Cancion, Void> colAcciones;
    @FXML private ProgressIndicator progressIndicator;
    @FXML private VBox panelResultados;

    private CancionService cancionService;
    private UsuarioService usuarioService;
    private Cancion cancionSemilla;
    private ObservableList<Cancion> cancionesRadio;

    @FXML
    public void initialize() {
        cancionService = CancionService.getInstance();
        usuarioService = UsuarioService.getInstance();

        configurarTabla();
        configurarSlider();

        // Ocultar panel de resultados inicialmente
        panelResultados.setVisible(false);
        panelResultados.setManaged(false);
        progressIndicator.setVisible(false);
    }

    /**
     * Establece la canción semilla para generar la radio
     *
     * @param cancion Canción base
     */
    public void setCancionSemilla(Cancion cancion) {
        this.cancionSemilla = cancion;
        if (cancion != null) {
            lblCancionSemilla.setText(cancion.getTitulo());
            lblArtistaSemilla.setText(cancion.getArtista());
            lblGeneroSemilla.setText(cancion.getGenero());
        }
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
        sliderCantidad.setMin(5);
        sliderCantidad.setMax(50);
        sliderCantidad.setValue(20);
        sliderCantidad.setShowTickLabels(true);
        sliderCantidad.setShowTickMarks(true);
        sliderCantidad.setMajorTickUnit(10);
        sliderCantidad.setMinorTickCount(5);
        sliderCantidad.setBlockIncrement(5);

        // Actualizar label cuando cambia el slider
        sliderCantidad.valueProperty().addListener((obs, oldVal, newVal) -> {
            lblCantidadCanciones.setText(String.valueOf(newVal.intValue()));
        });

        lblCantidadCanciones.setText("20");
    }

    @FXML
    private void handleGenerar() {
        if (cancionSemilla == null) {
            mostrarError("No se ha seleccionado una canción semilla");
            return;
        }

        int cantidad = (int) sliderCantidad.getValue();

        // Mostrar indicador de progreso
        progressIndicator.setVisible(true);
        btnGenerar.setDisable(true);

        // Ejecutar generación en un hilo separado para no bloquear la UI
        new Thread(() -> {
            try {
                // Generar radio usando Dijkstra
                List<Cancion> similares = cancionService.generarRadio(cancionSemilla, cantidad);

                // Actualizar UI en el hilo de JavaFX
                javafx.application.Platform.runLater(() -> {
                    cancionesRadio = FXCollections.observableArrayList(similares);
                    tableRadio.setItems(cancionesRadio);

                    // Mostrar panel de resultados
                    panelResultados.setVisible(true);
                    panelResultados.setManaged(true);

                    // Ocultar indicador de progreso
                    progressIndicator.setVisible(false);
                    btnGenerar.setDisable(false);

                    if (similares.isEmpty()) {
                        mostrarAdvertencia("No se encontraron canciones similares");
                    } else {
                        mostrarExito("Radio generada con " + similares.size() + " canciones similares");
                    }
                });

            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    progressIndicator.setVisible(false);
                    btnGenerar.setDisable(false);
                    mostrarError("Error al generar radio: " + e.getMessage());
                    e.printStackTrace();
                });
            }
        }).start();
    }

    @FXML
    private void handleReproducirTodo() {
        if (cancionesRadio == null || cancionesRadio.isEmpty()) {
            mostrarAdvertencia("Primero genera una radio");
            return;
        }

        mostrarInformacion("Reproducción",
                "Reproduciendo radio completa:\n" +
                        cancionesRadio.size() + " canciones similares a \"" +
                        cancionSemilla.getTitulo() + "\"");
    }

    @FXML
    private void handleAgregarTodasFavoritos() {
        if (cancionesRadio == null || cancionesRadio.isEmpty()) {
            mostrarAdvertencia("Primero genera una radio");
            return;
        }

        Usuario usuario = usuarioService.getUsuarioActual();
        if (usuario == null) {
            return;
        }

        int agregadas = 0;
        for (Cancion cancion : cancionesRadio) {
            if (usuario.agregarFavorito(cancion)) {
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

            stage.setWidth(900);
            stage.setHeight(600);
            stage.setMinWidth(900);
            stage.setMinHeight(600);
            stage.setMaxWidth(Double.MAX_VALUE);
            stage.setMaxHeight(Double.MAX_VALUE);

            Scene scene = new Scene(root, 900, 800);
            stage.setScene(scene);
            stage.setTitle("MusicApp - Panel Principal");
            stage.centerOnScreen();
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
        Usuario usuario = usuarioService.getUsuarioActual();
        if (usuario != null) {
            if (usuario.agregarFavorito(cancion)) {
                mostrarExito("Canción agregada a favoritos");
            } else {
                mostrarAdvertencia("La canción ya está en favoritos");
            }
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
