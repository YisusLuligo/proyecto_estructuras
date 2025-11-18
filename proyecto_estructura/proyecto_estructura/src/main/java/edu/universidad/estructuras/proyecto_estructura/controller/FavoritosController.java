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
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controlador para la vista mejorada de favoritos
 */
public class FavoritosController {

    @FXML private Button btnVolver;
    @FXML private Label lblTotalFavoritos;
    @FXML private Label lblDuracionTotal;
    @FXML private Label lblGeneroFavorito;

    // Búsqueda y filtros
    @FXML private TextField txtBuscar;
    @FXML private Button btnBuscar;
    @FXML private Button btnLimpiar;
    @FXML private ComboBox<String> comboGenero;
    @FXML private ComboBox<String> comboOrdenar;

    // Tabla
    @FXML private TableView<Cancion> tableFavoritos;
    @FXML private TableColumn<Cancion, String> colTitulo;
    @FXML private TableColumn<Cancion, String> colArtista;
    @FXML private TableColumn<Cancion, String> colGenero;
    @FXML private TableColumn<Cancion, Integer> colAnio;
    @FXML private TableColumn<Cancion, String> colDuracion;
    @FXML private TableColumn<Cancion, Void> colAcciones;

    // Botones de acción
    @FXML private Button btnReproducir;
    @FXML private Button btnEliminar;
    @FXML private Button btnAgregarPlaylist;
    @FXML private Button btnDescargarCSV;
    @FXML private Button btnReproducirTodo;

    private UsuarioService usuarioService;
    private CancionService cancionService;
    private PlaylistService playlistService;
    private Usuario usuarioActual;
    private ObservableList<Cancion> favoritosObservable;

    @FXML
    public void initialize() {
        usuarioService = UsuarioService.getInstance();
        cancionService = CancionService.getInstance();
        playlistService = PlaylistService.getInstance();
        usuarioActual = usuarioService.getUsuarioActual();

        if (usuarioActual != null) {
            configurarTabla();
            configurarComboBoxes();
            cargarFavoritos();
            actualizarEstadisticas();
        }
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
            private final Button btnQuitar = new Button("💔");

            {
                btnReproducir.setStyle("-fx-background-color: #FF0000; -fx-text-fill: white; -fx-cursor: hand;");
                btnQuitar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;");

                btnReproducir.setOnAction(event -> {
                    Cancion cancion = getTableView().getItems().get(getIndex());
                    abrirReproductor(cancion);
                });

                btnQuitar.setOnAction(event -> {
                    Cancion cancion = getTableView().getItems().get(getIndex());
                    eliminarDeFavoritos(cancion);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(5, btnReproducir, btnQuitar);
                    hbox.setAlignment(Pos.CENTER);
                    setGraphic(hbox);
                }
            }
        });
    }

    private void configurarComboBoxes() {
        // Configurar géneros
        Set<String> generos = usuarioActual.getListaFavoritos().stream()
                .map(Cancion::getGenero)
                .collect(Collectors.toSet());

        List<String> listaGeneros = new ArrayList<>(generos);
        listaGeneros.add(0, "Todos los géneros");
        comboGenero.setItems(FXCollections.observableArrayList(listaGeneros));
        comboGenero.getSelectionModel().selectFirst();

        // Configurar ordenamiento
        comboOrdenar.setItems(FXCollections.observableArrayList(
                "Título (A-Z)",
                "Título (Z-A)",
                "Artista (A-Z)",
                "Artista (Z-A)",
                "Año (Más reciente)",
                "Año (Más antiguo)",
                "Género"
        ));
        comboOrdenar.getSelectionModel().selectFirst();

        comboOrdenar.setOnAction(e -> handleOrdenar());
    }

    private void cargarFavoritos() {
        List<Cancion> favoritos = usuarioActual.getListaFavoritos();
        favoritosObservable = FXCollections.observableArrayList(favoritos);
        tableFavoritos.setItems(favoritosObservable);
    }

    private void actualizarEstadisticas() {
        List<Cancion> favoritos = usuarioActual.getListaFavoritos();

        // Total de favoritos
        lblTotalFavoritos.setText(String.valueOf(favoritos.size()));

        // Duración total
        double duracionTotal = favoritos.stream()
                .mapToDouble(Cancion::getDuracion)
                .sum();
        int horas = (int) (duracionTotal / 60);
        int minutos = (int) (duracionTotal % 60);
        lblDuracionTotal.setText(String.format("%dh %dm", horas, minutos));

        // Género favorito
        Map<String, Long> conteoPorGenero = favoritos.stream()
                .collect(Collectors.groupingBy(Cancion::getGenero, Collectors.counting()));

        String generoFavorito = conteoPorGenero.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");

        lblGeneroFavorito.setText(generoFavorito);
    }

    @FXML
    private void handleBuscar() {
        String busqueda = txtBuscar.getText().trim().toLowerCase();
        String generoSeleccionado = comboGenero.getValue();

        List<Cancion> resultados = usuarioActual.getListaFavoritos();

        // Aplicar filtro de búsqueda
        if (!busqueda.isEmpty()) {
            resultados = resultados.stream()
                    .filter(c -> c.getTitulo().toLowerCase().contains(busqueda) ||
                            c.getArtista().toLowerCase().contains(busqueda))
                    .collect(Collectors.toList());
        }

        // Aplicar filtro de género
        if (generoSeleccionado != null && !generoSeleccionado.equals("Todos los géneros")) {
            resultados = resultados.stream()
                    .filter(c -> c.getGenero().equals(generoSeleccionado))
                    .collect(Collectors.toList());
        }

        favoritosObservable.setAll(resultados);
    }

    @FXML
    private void handleLimpiar() {
        txtBuscar.clear();
        comboGenero.getSelectionModel().selectFirst();
        cargarFavoritos();
    }

    private void handleOrdenar() {
        String ordenSeleccionado = comboOrdenar.getValue();
        if (ordenSeleccionado == null || favoritosObservable.isEmpty()) {
            return;
        }

        List<Cancion> lista = new ArrayList<>(favoritosObservable);

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
            case "Género":
                lista.sort(Comparator.comparing(Cancion::getGenero)
                        .thenComparing(Cancion::getTitulo));
                break;
        }

        favoritosObservable.setAll(lista);
    }

    @FXML
    private void handleReproducir() {
        Cancion seleccionada = tableFavoritos.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAdvertencia("Selecciona una canción para reproducir");
            return;
        }
        abrirReproductor(seleccionada);
    }

    @FXML
    private void handleEliminar() {
        Cancion seleccionada = tableFavoritos.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAdvertencia("Selecciona una canción para eliminar");
            return;
        }
        eliminarDeFavoritos(seleccionada);
    }

    @FXML
    private void handleAgregarPlaylist() {
        Cancion seleccionada = tableFavoritos.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAdvertencia("Selecciona una canción");
            return;
        }

        List<String> playlistsNombres = playlistService.obtenerPlaylistsDeUsuario(usuarioActual.getUsername())
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
            var playlist = playlistService.obtenerPlaylistsDeUsuario(usuarioActual.getUsername())
                    .stream()
                    .filter(p -> p.getNombre().equals(playlistNombre))
                    .findFirst()
                    .orElse(null);

            if (playlist != null) {
                if (playlistService.agregarCancionAPlaylist(
                        usuarioActual.getUsername(),
                        playlist.getId(),
                        seleccionada)) {
                    mostrarExito("Canción agregada a la playlist");
                } else {
                    mostrarAdvertencia("La canción ya está en la playlist");
                }
            }
        });
    }

    @FXML
    private void handleDescargarCSV() {
        if (usuarioActual.getListaFavoritos().isEmpty()) {
            mostrarAdvertencia("No hay favoritos para exportar");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar archivo CSV");
        fileChooser.setInitialFileName("mis_favoritos.csv");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos CSV", "*.csv")
        );

        Stage stage = (Stage) btnDescargarCSV.getScene().getWindow();
        File archivo = fileChooser.showSaveDialog(stage);

        if (archivo != null) {
            exportarACSV(archivo);
        }
    }

    private void exportarACSV(File archivo) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(archivo))) {
            // Encabezados
            writer.println("Título,Artista,Género,Año,Duración");

            // Datos
            for (Cancion cancion : usuarioActual.getListaFavoritos()) {
                writer.println(String.format("%s,%s,%s,%d,%.2f",
                        cancion.getTitulo(),
                        cancion.getArtista(),
                        cancion.getGenero(),
                        cancion.getAnio(),
                        cancion.getDuracion()
                ));
            }

            mostrarExito("Archivo CSV exportado exitosamente");
        } catch (IOException e) {
            mostrarError("Error al exportar archivo: " + e.getMessage());
        }
    }

    @FXML
    private void handleReproducirTodo() {
        if (usuarioActual.getListaFavoritos().isEmpty()) {
            mostrarAdvertencia("No hay canciones en favoritos");
            return;
        }

        mostrarInformacion("Reproducción",
                "Reproduciendo " + usuarioActual.getCantidadFavoritos() +
                        " canciones favoritas");
    }

    private void eliminarDeFavoritos(Cancion cancion) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar");
        confirmacion.setHeaderText("¿Eliminar de favoritos?");
        confirmacion.setContentText(cancion.getTitulo() + " - " + cancion.getArtista());

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            if (usuarioActual.eliminarFavorito(cancion)) {
                favoritosObservable.remove(cancion);
                actualizarEstadisticas();
                mostrarExito("Canción eliminada de favoritos");
            }
        }
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