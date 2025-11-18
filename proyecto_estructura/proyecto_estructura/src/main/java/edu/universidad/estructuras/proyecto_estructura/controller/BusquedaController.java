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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;


/**
 * Controlador para la búsqueda avanzada de canciones
 */
public class BusquedaController {
    @FXML private ProgressIndicator progressIndicator;
    @FXML private Label lblBuscando;
    @FXML private Button btnVolver;
    @FXML private TextField txtBusqueda;
    @FXML private Button btnBuscar;
    @FXML private Button btnLimpiar;
    @FXML private Label lblResultados;

    // Filtros
    @FXML private VBox panelFiltros;
    @FXML private Button btnMostrarFiltros;
    @FXML private ComboBox<String> comboGenero;
    @FXML private TextField txtAnioMin;
    @FXML private TextField txtAnioMax;
    @FXML private TextField txtArtista;
    @FXML private TextField txtDuracionMin;
    @FXML private TextField txtDuracionMax;
    @FXML private Button btnAplicarFiltros;
    @FXML private Button btnLimpiarFiltros;

    // Tabla
    @FXML private TableView<Cancion> tableResultados;
    @FXML private TableColumn<Cancion, String> colTitulo;
    @FXML private TableColumn<Cancion, String> colArtista;
    @FXML private TableColumn<Cancion, String> colGenero;
    @FXML private TableColumn<Cancion, Integer> colAnio;
    @FXML private TableColumn<Cancion, String> colDuracion;
    @FXML private TableColumn<Cancion, Void> colAcciones;

    @FXML private ComboBox<String> comboOrdenar;
    @FXML private Button btnReproducir;
    @FXML private Button btnAgregarFavoritos;
    @FXML private Button btnAgregarPlaylist;

    private CancionService cancionService;
    private UsuarioService usuarioService;
    private PlaylistService playlistService;
    private ObservableList<Cancion> resultadosObservable;
    private boolean filtrosVisibles = false;

    @FXML
    public void initialize() {
        cancionService = CancionService.getInstance();
        usuarioService = UsuarioService.getInstance();
        playlistService = PlaylistService.getInstance();

        configurarTabla();
        configurarComboBoxes();
        cargarTodasLasCanciones();

        // Ocultar panel de filtros por defecto
        panelFiltros.setVisible(false);
        panelFiltros.setManaged(false);

        // ✨ NUEVO: Configurar autocompletado en tiempo real
        configurarAutocompletado();
    }

    private void configurarTabla() {
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

    private void configurarComboBoxes() {
        // Configurar géneros
        List<String> generos = new ArrayList<>(cancionService.obtenerGenerosUnicos());
        generos.add(0, "Todos los géneros");
        comboGenero.setItems(FXCollections.observableArrayList(generos));
        comboGenero.getSelectionModel().selectFirst();

        // Configurar ordenamiento
        comboOrdenar.setItems(FXCollections.observableArrayList(
                "Título (A-Z)",
                "Título (Z-A)",
                "Artista (A-Z)",
                "Artista (Z-A)",
                "Año (Más reciente)",
                "Año (Más antiguo)",
                "Duración (Más larga)",
                "Duración (Más corta)"
        ));
        comboOrdenar.getSelectionModel().selectFirst();
    }

    private void cargarTodasLasCanciones() {
        List<Cancion> canciones = cancionService.obtenerTodasLasCanciones();
        resultadosObservable = FXCollections.observableArrayList(canciones);
        tableResultados.setItems(resultadosObservable);
        actualizarContador();
    }

    @FXML
    private void handleBuscar() {
        String busqueda = txtBusqueda.getText().trim();
        if (busqueda.isEmpty()) {
            cargarTodasLasCanciones();
            return;
        }

        // Búsqueda simple por título y artista
        List<Cancion> resultados = new ArrayList<>();
        resultados.addAll(cancionService.buscarPorTitulo(busqueda));

        List<Cancion> porArtista = cancionService.buscarPorArtista(busqueda);
        for (Cancion c : porArtista) {
            if (!resultados.contains(c)) {
                resultados.add(c);
            }
        }

        resultadosObservable = FXCollections.observableArrayList(resultados);
        tableResultados.setItems(resultadosObservable);
        actualizarContador();
    }

    @FXML
    private void handleLimpiar() {
        txtBusqueda.clear();
        cargarTodasLasCanciones();
    }

    @FXML
    private void handleToggleFiltros() {
        filtrosVisibles = !filtrosVisibles;
        panelFiltros.setVisible(filtrosVisibles);
        panelFiltros.setManaged(filtrosVisibles);
        btnMostrarFiltros.setText(filtrosVisibles ? "▲ Ocultar" : "▼ Mostrar");
    }

    // En BusquedaController.java

    @FXML
    private void handleAplicarFiltros() {
        // Mostrar indicador de carga
        progressIndicator.setVisible(true);
        btnAplicarFiltros.setDisable(true);

        // Obtener parámetros de filtros
        String generoSeleccionado = comboGenero.getValue();
        String anioMinStr = txtAnioMin.getText().trim();
        String anioMaxStr = txtAnioMax.getText().trim();
        String artistaBusqueda = txtArtista.getText().trim();

        // 🔥 USAR THREADS - ExecutorService
        ExecutorService executor = Executors.newFixedThreadPool(4);
        List<Future<List<Cancion>>> futures = new ArrayList<>();

        // Tarea 1: Filtrar por género
        futures.add(executor.submit(() -> {
            if (generoSeleccionado != null && !generoSeleccionado.equals("Todos los géneros")) {
                return cancionService.buscarPorGenero(generoSeleccionado);
            }
            return cancionService.obtenerTodasLasCanciones();
        }));

        // Tarea 2: Filtrar por artista
        futures.add(executor.submit(() -> {
            if (!artistaBusqueda.isEmpty()) {
                return cancionService.buscarPorArtista(artistaBusqueda);
            }
            return new ArrayList<>();
        }));

        // Tarea 3: Filtrar por año
        futures.add(executor.submit(() -> {
            List<Cancion> resultado = new ArrayList<>();
            if (!anioMinStr.isEmpty() || !anioMaxStr.isEmpty()) {
                try {
                    int min = anioMinStr.isEmpty() ? 0 : Integer.parseInt(anioMinStr);
                    int max = anioMaxStr.isEmpty() ? 3000 : Integer.parseInt(anioMaxStr);

                    for (Cancion c : cancionService.obtenerTodasLasCanciones()) {
                        if (c.getAnio() >= min && c.getAnio() <= max) {
                            resultado.add(c);
                        }
                    }
                } catch (NumberFormatException e) {
                    // Manejar error
                }
            }
            return resultado;
        }));

        // Combinar resultados en hilo separado
        new Thread(() -> {
            try {
                // Esperar resultados de todos los hilos
                List<Cancion> porGenero = futures.get(0).get();
                List<Cancion> porArtista = futures.get(1).get();
                List<Cancion> porAnio = futures.get(2).get();

                // Aplicar lógica AND/OR
                Set<Cancion> resultadoFinal = new HashSet<>(porGenero);

                if (!porArtista.isEmpty()) {
                    resultadoFinal.retainAll(porArtista); // AND
                }

                if (!porAnio.isEmpty()) {
                    resultadoFinal.retainAll(porAnio); // AND
                }

                // Actualizar UI en hilo de JavaFX
                javafx.application.Platform.runLater(() -> {
                    resultadosObservable = FXCollections.observableArrayList(resultadoFinal);
                    tableResultados.setItems(resultadosObservable);
                    actualizarContador();

                    progressIndicator.setVisible(false);
                    btnAplicarFiltros.setDisable(false);
                    mostrarExito("Filtros aplicados: " + resultadoFinal.size() + " resultados");
                });

            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    progressIndicator.setVisible(false);
                    btnAplicarFiltros.setDisable(false);
                    mostrarError("Error en búsqueda: " + e.getMessage());
                });
            } finally {
                executor.shutdown();
            }
        }).start();
    }

    @FXML
    private void handleLimpiarFiltros() {
        comboGenero.getSelectionModel().selectFirst();
        txtAnioMin.clear();
        txtAnioMax.clear();
        txtArtista.clear();
        txtDuracionMin.clear();
        txtDuracionMax.clear();
        cargarTodasLasCanciones();
        mostrarExito("Filtros limpiados");
    }

    @FXML
    private void handleOrdenar() {
        String ordenSeleccionado = comboOrdenar.getValue();
        if (ordenSeleccionado == null || resultadosObservable.isEmpty()) {
            return;
        }

        List<Cancion> lista = new ArrayList<>(resultadosObservable);

        switch (ordenSeleccionado) {
            case "Título (A-Z)":
                lista.sort(Comparator.comparing(Cancion::getTitulo));
                break;
            case "Título (Z-A)":
                lista.sort(Comparator.comparing(Cancion::getTitulo).reversed());
                break;
            case "Artista (A-Z)":
                lista.sort(Comparator.comparing(Cancion::getArtista));
                break;
            case "Artista (Z-A)":
                lista.sort(Comparator.comparing(Cancion::getArtista).reversed());
                break;
            case "Año (Más reciente)":
                lista.sort(Comparator.comparing(Cancion::getAnio).reversed());
                break;
            case "Año (Más antiguo)":
                lista.sort(Comparator.comparing(Cancion::getAnio));
                break;
            case "Duración (Más larga)":
                lista.sort(Comparator.comparing(Cancion::getDuracion).reversed());
                break;
            case "Duración (Más corta)":
                lista.sort(Comparator.comparing(Cancion::getDuracion));
                break;
        }

        resultadosObservable.setAll(lista);
    }

    @FXML
    private void handleReproducir() {
        Cancion seleccionada = tableResultados.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAdvertencia("Por favor, seleccione una canción");
            return;
        }
        abrirReproductor(seleccionada);
    }

    @FXML
    private void handleAgregarFavoritos() {
        Cancion seleccionada = tableResultados.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAdvertencia("Por favor, seleccione una canción");
            return;
        }
        agregarAFavoritos(seleccionada);
    }

    @FXML
    private void handleAgregarPlaylist() {
        Cancion seleccionada = tableResultados.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAdvertencia("Por favor, seleccione una canción");
            return;
        }

        Usuario usuario = usuarioService.getUsuarioActual();
        if (usuario == null) {
            return;
        }

        List<String> playlistsNombres = playlistService.obtenerPlaylistsDeUsuario(usuario.getUsername())
                .stream()
                .map(p -> p.getNombre())
                .collect(Collectors.toList());

        if (playlistsNombres.isEmpty()) {
            mostrarAdvertencia("No tienes playlists. Crea una primero.");
            return;
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(playlistsNombres.get(0), playlistsNombres);
        dialog.setTitle("Agregar a Playlist");
        dialog.setHeaderText("Selecciona una playlist");
        dialog.setContentText("Playlist:");

        dialog.showAndWait().ifPresent(playlistNombre -> {
            var playlist = playlistService.obtenerPlaylistsDeUsuario(usuario.getUsername())
                    .stream()
                    .filter(p -> p.getNombre().equals(playlistNombre))
                    .findFirst()
                    .orElse(null);

            if (playlist != null) {
                if (playlistService.agregarCancionAPlaylist(usuario.getUsername(), playlist.getId(), seleccionada)) {
                    mostrarExito("Canción agregada a la playlist");
                } else {
                    mostrarAdvertencia("La canción ya está en la playlist");
                }
            }
        });
    }

    @FXML
    private void handleVolver() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/universidad/estructuras/proyecto_estructura/main.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnVolver.getScene().getWindow();
            Scene scene = new Scene(root, 900, 800);
            stage.setScene(scene);
            stage.setTitle("MusicApp - Panel Principal");
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
            } else {
                mostrarAdvertencia("La canción ya está en favoritos");
            }
        }
    }

    private void actualizarContador() {
        lblResultados.setText("Resultados: " + resultadosObservable.size());
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

    /**
     * Configura el autocompletado en tiempo real para el campo de búsqueda
     */
    private void configurarAutocompletado() {
        // Listener que se activa cada vez que el usuario escribe
        txtBusqueda.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.length() >= 2) {
                // Obtener sugerencias del Trie
                List<String> sugerenciasTitulos = cancionService.autocompletarTitulos(newValue);
                List<String> sugerenciasArtistas = cancionService.autocompletarArtistas(newValue);

                // Combinar resultados
                List<Cancion> resultados = new ArrayList<>();

                // Buscar por títulos sugeridos
                for (String titulo : sugerenciasTitulos) {
                    List<Cancion> cancionesPorTitulo = cancionService.buscarPorTitulo(titulo);
                    for (Cancion c : cancionesPorTitulo) {
                        if (!resultados.contains(c)) {
                            resultados.add(c);
                        }
                    }
                }

                // Buscar por artistas sugeridos
                for (String artista : sugerenciasArtistas) {
                    List<Cancion> cancionesPorArtista = cancionService.buscarPorArtista(artista);
                    for (Cancion c : cancionesPorArtista) {
                        if (!resultados.contains(c)) {
                            resultados.add(c);
                        }
                    }
                }

                // Actualizar tabla con resultados
                if (!resultados.isEmpty()) {
                    resultadosObservable = FXCollections.observableArrayList(resultados);
                    tableResultados.setItems(resultadosObservable);
                    actualizarContador();
                }
            } else if (newValue == null || newValue.isEmpty()) {
                // Si el campo está vacío, mostrar todas las canciones
                cargarTodasLasCanciones();
            }
        });

    }
    @FXML
    private void handleIniciarRadio() {
        Cancion seleccionada = tableResultados.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAdvertencia("Por favor, selecciona una canción");
            return;
        }
        abrirRadio(seleccionada);
    }

    private void abrirRadio(Cancion cancion) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/edu/universidad/estructuras/proyecto_estructura/radio.fxml"));
            Parent root = loader.load();

            RadioController controller = loader.getController();
            controller.setCancionSemilla(cancion);

            Stage stage = (Stage) btnBuscar.getScene().getWindow();
            Scene scene = new Scene(root, 1200, 800);
            stage.setScene(scene);
            stage.setTitle("MusicApp - Radio: " + cancion.getTitulo());
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("Error al abrir Radio: " + e.getMessage());
        }
    }
}