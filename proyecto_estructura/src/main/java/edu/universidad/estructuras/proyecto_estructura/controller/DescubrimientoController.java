package edu.universidad.estructuras.proyecto_estructura.controller;

import edu.universidad.estructuras.proyecto_estructura.model.Cancion;
import edu.universidad.estructuras.proyecto_estructura.model.Usuario;
import edu.universidad.estructuras.proyecto_estructura.service.CancionService;
import edu.universidad.estructuras.proyecto_estructura.service.UsuarioService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
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
 * Controlador para la vista de descubrimiento de música
 */
public class DescubrimientoController {

    @FXML private Button btnVolver;
    @FXML private Button btnRefrescar;

    // Top Populares
    @FXML private ListView<Cancion> listTopPopulares;
    @FXML private Button btnVerMasPopulares;

    // Lanzamientos Recientes
    @FXML private ComboBox<String> comboPeriodo;

    // Explorar por Género
    @FXML private javafx.scene.layout.FlowPane flowGeneros;
    @FXML private VBox panelGeneroSeleccionado;
    @FXML private Label lblGeneroActual;
    @FXML private Label lblCantidadGenero;
    @FXML private TableView<Cancion> tableGenero;
    @FXML private TableColumn<Cancion, String> colGeneroTitulo;
    @FXML private TableColumn<Cancion, String> colGeneroArtista;
    @FXML private TableColumn<Cancion, Integer> colGeneroAnio;
    @FXML private TableColumn<Cancion, String> colGeneroDuracion;
    @FXML private TableColumn<Cancion, Void> colGeneroAcciones;

    // Décadas
    @FXML private Button btn1960s;
    @FXML private Button btn1970s;
    @FXML private Button btn1980s;
    @FXML private Button btn1990s;
    @FXML private Button btn2000s;
    @FXML private Button btn2010s;
    @FXML private Button btn2020s;
    @FXML private VBox panelDecada;
    @FXML private Label lblDecadaActual;
    @FXML private ListView<Cancion> listDecada;

    // Aleatorio
    @FXML private Button btnSorprendeme;
    @FXML private VBox panelAleatorio;

    private CancionService cancionService;
    private UsuarioService usuarioService;

    @FXML
    public void initialize() {
        cancionService = CancionService.getInstance();
        usuarioService = UsuarioService.getInstance();

        configurarTablaGenero();
        cargarTopPopulares();
        cargarGeneros();
        configurarComboBox();
        configurarListViews();

        // Ocultar paneles por defecto
        panelGeneroSeleccionado.setVisible(false);
        panelGeneroSeleccionado.setManaged(false);
        panelDecada.setVisible(false);
        panelDecada.setManaged(false);
    }

    private void configurarTablaGenero() {
        colGeneroTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colGeneroArtista.setCellValueFactory(new PropertyValueFactory<>("artista"));
        colGeneroAnio.setCellValueFactory(new PropertyValueFactory<>("anio"));

        colGeneroDuracion.setCellValueFactory(cellData -> {
            double duracion = cellData.getValue().getDuracion();
            int minutos = (int) duracion;
            int segundos = (int) ((duracion - minutos) * 100);
            return new javafx.beans.property.SimpleStringProperty(
                    String.format("%d:%02d", minutos, segundos)
            );
        });

        colGeneroAcciones.setCellFactory(param -> new TableCell<>() {
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

    private void configurarListViews() {
        // Configurar ListView de populares
        listTopPopulares.setCellFactory(param -> new ListCell<Cancion>() {
            @Override
            protected void updateItem(Cancion cancion, boolean empty) {
                super.updateItem(cancion, empty);
                if (empty || cancion == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox container = new HBox(15);
                    container.setAlignment(Pos.CENTER_LEFT);
                    container.setPadding(new Insets(8));

                    Label lblPosicion = new Label("#" + (getIndex() + 1));
                    lblPosicion.setStyle("-fx-font-weight: bold; -fx-font-size: 16; -fx-text-fill: #667eea; -fx-min-width: 30;");

                    VBox info = new VBox(3);
                    Label lblTitulo = new Label(cancion.getTitulo());
                    lblTitulo.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
                    Label lblArtista = new Label(cancion.getArtista() + " • " + cancion.getGenero());
                    lblArtista.setStyle("-fx-text-fill: #666; -fx-font-size: 12;");
                    info.getChildren().addAll(lblTitulo, lblArtista);

                    Button btnPlay = new Button("▶️");
                    btnPlay.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-cursor: hand;");
                    btnPlay.setOnAction(e -> abrirReproductor(cancion));

                    container.getChildren().addAll(lblPosicion, info, btnPlay);
                    setGraphic(container);
                }
            }
        });

        // Configurar ListView de década
        listDecada.setCellFactory(param -> new ListCell<Cancion>() {
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

    private void configurarComboBox() {
        comboPeriodo.setItems(FXCollections.observableArrayList(
                "Último año",
                "Últimos 3 años",
                "Últimos 5 años",
                "Últimos 10 años"
        ));
        comboPeriodo.getSelectionModel().selectFirst();
    }

    private void cargarTopPopulares() {
        // Obtener canciones más favoritas
        Map<String, Integer> conteoFavoritos = new HashMap<>();

        for (Usuario usuario : usuarioService.obtenerTodosLosUsuarios()) {
            for (Cancion cancion : usuario.getListaFavoritos()) {
                conteoFavoritos.put(cancion.getId(),
                        conteoFavoritos.getOrDefault(cancion.getId(), 0) + 1);
            }
        }

        // Ordenar por popularidad
        List<Cancion> todasCanciones = cancionService.obtenerTodasLasCanciones();
        List<Cancion> populares = todasCanciones.stream()
                .sorted((c1, c2) -> {
                    int count1 = conteoFavoritos.getOrDefault(c1.getId(), 0);
                    int count2 = conteoFavoritos.getOrDefault(c2.getId(), 0);
                    return Integer.compare(count2, count1);
                })
                .limit(10)
                .collect(Collectors.toList());

        listTopPopulares.setItems(FXCollections.observableArrayList(populares));
    }

    private void cargarGeneros() {
        flowGeneros.getChildren().clear();

        Set<String> generos = cancionService.obtenerGenerosUnicos();

        // Colores para los géneros
        String[] colores = {
                "#FF6B6B", "#4ECDC4", "#45B7D1", "#96CEB4",
                "#FFEAA7", "#DFE6E9", "#74B9FF", "#A29BFE"
        };

        int i = 0;
        for (String genero : generos) {
            Button btnGenero = new Button(genero);
            String color = colores[i % colores.length];
            btnGenero.setStyle(
                    "-fx-background-color: " + color + ";" +
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 14;" +
                            "-fx-font-weight: bold;" +
                            "-fx-pref-width: 150;" +
                            "-fx-pref-height: 60;" +
                            "-fx-background-radius: 8;" +
                            "-fx-cursor: hand;"
            );

            btnGenero.setOnAction(e -> mostrarCancionesPorGenero(genero));
            flowGeneros.getChildren().add(btnGenero);
            i++;
        }
    }

    private void mostrarCancionesPorGenero(String genero) {
        List<Cancion> canciones = cancionService.buscarPorGenero(genero);

        lblGeneroActual.setText(genero);
        lblCantidadGenero.setText(canciones.size() + " canciones");

        tableGenero.setItems(FXCollections.observableArrayList(canciones));

        panelGeneroSeleccionado.setVisible(true);
        panelGeneroSeleccionado.setManaged(true);
    }

    @FXML
    private void handleDecada() {
        Button btn = (Button) btn1960s.getScene().getFocusOwner();
        if (btn == null) return;

        String texto = btn.getText();
        int decada = 0;

        switch (texto) {
            case "60s": decada = 1960; break;
            case "70s": decada = 1970; break;
            case "80s": decada = 1980; break;
            case "90s": decada = 1990; break;
            case "00s": decada = 2000; break;
            case "10s": decada = 2010; break;
            case "20s": decada = 2020; break;
        }

        if (decada > 0) {
            mostrarCancionesPorDecada(decada);
        }
    }

    private void mostrarCancionesPorDecada(int decadaInicio) {
        int decadaFin = decadaInicio + 9;

        List<Cancion> canciones = cancionService.obtenerTodasLasCanciones().stream()
                .filter(c -> c.getAnio() >= decadaInicio && c.getAnio() <= decadaFin)
                .sorted(Comparator.comparing(Cancion::getAnio))
                .collect(Collectors.toList());

        lblDecadaActual.setText("Década de " + decadaInicio);
        listDecada.setItems(FXCollections.observableArrayList(canciones));

        panelDecada.setVisible(true);
        panelDecada.setManaged(true);
    }

    @FXML
    private void handleSorprendeme() {
        List<Cancion> todasCanciones = cancionService.obtenerTodasLasCanciones();
        if (todasCanciones.isEmpty()) {
            mostrarAdvertencia("No hay canciones en el catálogo");
            return;
        }

        // Seleccionar 5 canciones aleatorias
        List<Cancion> aleatorias = new ArrayList<>(todasCanciones);
        Collections.shuffle(aleatorias);
        List<Cancion> seleccionadas = aleatorias.stream().limit(5).collect(Collectors.toList());

        // Mostrar en panel
        panelAleatorio.getChildren().clear();

        for (Cancion cancion : seleccionadas) {
            HBox card = crearCardCancion(cancion);
            panelAleatorio.getChildren().add(card);
        }
    }

    private HBox crearCardCancion(Cancion cancion) {
        HBox card = new HBox(15);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15; -fx-background-radius: 8;");

        VBox info = new VBox(5);
        Label lblTitulo = new Label(cancion.getTitulo());
        lblTitulo.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        Label lblArtista = new Label(cancion.getArtista() + " • " + cancion.getAnio());
        lblArtista.setStyle("-fx-text-fill: #666;");
        info.getChildren().addAll(lblTitulo, lblArtista);

        Button btnReproducir = new Button("▶️ Reproducir");
        btnReproducir.setStyle("-fx-background-color: #FF0000; -fx-text-fill: white; -fx-cursor: hand;");
        btnReproducir.setOnAction(e -> abrirReproductor(cancion));

        Button btnFavorito = new Button("❤️");
        btnFavorito.setStyle("-fx-background-color: #E91E63; -fx-text-fill: white; -fx-cursor: hand;");
        btnFavorito.setOnAction(e -> agregarAFavoritos(cancion));

        card.getChildren().addAll(info, btnReproducir, btnFavorito);
        return card;
    }

    @FXML
    private void handleCambioPeriodo() {
        // Esta funcionalidad se puede expandir para mostrar lanzamientos recientes
    }

    @FXML
    private void handleVerMasPopulares() {
        mostrarInformacion("Próximamente", "Vista expandida de canciones populares");
    }

    @FXML
    private void handleRefrescar() {
        cargarTopPopulares();
        cargarGeneros();
        mostrarExito("Contenido actualizado");
    }

    @FXML
    private void handleVolver() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/universidad/estructuras/proyecto_estructura/main.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/universidad/estructuras/proyecto_estructura/reproductor-youtube.fxml"));
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
                cargarTopPopulares(); // Actualizar populares
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