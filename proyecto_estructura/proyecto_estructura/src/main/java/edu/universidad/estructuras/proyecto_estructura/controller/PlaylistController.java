package edu.universidad.estructuras.proyecto_estructura.controller;

import edu.universidad.estructuras.proyecto_estructura.model.Cancion;
import edu.universidad.estructuras.proyecto_estructura.model.Playlist;
import edu.universidad.estructuras.proyecto_estructura.model.Usuario;
import edu.universidad.estructuras.proyecto_estructura.service.CancionService;
import edu.universidad.estructuras.proyecto_estructura.service.PersistenciaService;
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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controlador para la gestión de playlists del usuario
 */
public class PlaylistController {

    @FXML private Button btnVolver;
    @FXML private Button btnNuevaPlaylist;
    @FXML private ListView<Playlist> listPlaylists;
    @FXML private Button btnEditarPlaylist;
    @FXML private Button btnEliminarPlaylist;
    @FXML private Button btnDuplicarPlaylist;
    @FXML private Label lblTotalPlaylists;

    // Panel de detalles
    @FXML private Label lblNombrePlaylist;
    @FXML private Label lblDescripcionPlaylist;
    @FXML private Button btnReproducirTodo;
    @FXML private Label lblCantidadCanciones;
    @FXML private Label lblDuracionTotal;
    @FXML private Label lblFechaCreacion;

    // Tabla de canciones
    @FXML private TableView<Cancion> tableCanciones;
    @FXML private TableColumn<Cancion, Integer> colNumero;
    @FXML private TableColumn<Cancion, String> colTitulo;
    @FXML private TableColumn<Cancion, String> colArtista;
    @FXML private TableColumn<Cancion, String> colDuracion;
    @FXML private TableColumn<Cancion, Void> colAcciones;

    @FXML private Button btnAgregarCancion;
    @FXML private Button btnOrdenarCanciones;
    @FXML private Button btnReproducirSeleccion;
    @FXML private Button btnSubir;
    @FXML private Button btnBajar;
    @FXML private Button btnEliminarCancion;

    private PlaylistService playlistService;
    private CancionService cancionService;
    private UsuarioService usuarioService;
    private Usuario usuarioActual;
    private Playlist playlistActual;
    private ObservableList<Playlist> playlistsObservable;
    private ObservableList<Cancion> cancionesObservable;

    @FXML
    public void initialize() {
        playlistService = PlaylistService.getInstance();
        cancionService = CancionService.getInstance();
        usuarioService = UsuarioService.getInstance();
        usuarioActual = usuarioService.getUsuarioActual();

        configurarListaPlaylists();
        configurarTabla();
        cargarPlaylists();
    }

    private void configurarListaPlaylists() {
        listPlaylists.setCellFactory(param -> new ListCell<Playlist>() {
            @Override
            protected void updateItem(Playlist playlist, boolean empty) {
                super.updateItem(playlist, empty);
                if (empty || playlist == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(playlist.toString());
                    setStyle("-fx-padding: 10; -fx-font-size: 13;");
                }
            }
        });

        listPlaylists.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        mostrarDetallesPlaylist(newValue);
                    }
                }
        );
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
            private final Button btnQuitar = new Button("✖");

            {
                btnReproducir.setStyle("-fx-background-color: #FF0000; -fx-text-fill: white; -fx-cursor: hand;");
                btnQuitar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;");

                btnReproducir.setOnAction(event -> {
                    Cancion cancion = getTableView().getItems().get(getIndex());
                    abrirReproductor(cancion);
                });

                btnQuitar.setOnAction(event -> {
                    Cancion cancion = getTableView().getItems().get(getIndex());
                    eliminarCancionDePlaylist(cancion);
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

    private void cargarPlaylists() {
        if (usuarioActual == null) return;

        List<Playlist> playlists = playlistService.obtenerPlaylistsDeUsuario(usuarioActual.getUsername());
        playlistsObservable = FXCollections.observableArrayList(playlists);
        listPlaylists.setItems(playlistsObservable);

        lblTotalPlaylists.setText("Total: " + playlists.size());

        if (!playlists.isEmpty()) {
            listPlaylists.getSelectionModel().selectFirst();
        }
    }

    private void mostrarDetallesPlaylist(Playlist playlist) {
        playlistActual = playlist;

        lblNombrePlaylist.setText(playlist.getNombre());
        lblDescripcionPlaylist.setText(playlist.getDescripcion());
        lblCantidadCanciones.setText(String.valueOf(playlist.getCantidadCanciones()));
        lblDuracionTotal.setText(playlist.getDuracionTotalFormateada());
        lblFechaCreacion.setText(playlist.getFechaCreacionFormateada());

        cancionesObservable = FXCollections.observableArrayList(playlist.getCanciones());
        tableCanciones.setItems(cancionesObservable);
    }

    private void guardarPlaylists() {
        PersistenciaService.getInstance().guardarPlaylists();
    }

    @FXML
    private void handleNuevaPlaylist() {
        Dialog<Playlist> dialog = new Dialog<>();
        dialog.setTitle("Nueva Playlist");
        dialog.setHeaderText("Crear una nueva playlist");

        ButtonType btnCrear = new ButtonType("Crear", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnCrear, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Nombre de la playlist");
        TextArea txtDescripcion = new TextArea();
        txtDescripcion.setPromptText("Descripción (opcional)");
        txtDescripcion.setPrefRowCount(3);

        grid.add(new Label("Nombre:"), 0, 0);
        grid.add(txtNombre, 1, 0);
        grid.add(new Label("Descripción:"), 0, 1);
        grid.add(txtDescripcion, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnCrear) {
                String nombre = txtNombre.getText().trim();
                String descripcion = txtDescripcion.getText().trim();

                if (nombre.isEmpty()) {
                    mostrarError("El nombre de la playlist es obligatorio");
                    return null;
                }

                return playlistService.crearPlaylist(nombre, descripcion, usuarioActual.getUsername());
            }
            return null;
        });

        Optional<Playlist> resultado = dialog.showAndWait();
        if (resultado.isPresent()) {
            guardarPlaylists();
            mostrarExito("Playlist creada exitosamente");
            cargarPlaylists();
        }
    }

    @FXML
    private void handleEditarPlaylist() {
        Playlist seleccionada = listPlaylists.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAdvertencia("Selecciona una playlist para editar");
            return;
        }

        Dialog<Playlist> dialog = new Dialog<>();
        dialog.setTitle("Editar Playlist");
        dialog.setHeaderText("Modificar playlist");

        ButtonType btnGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField txtNombre = new TextField(seleccionada.getNombre());
        TextArea txtDescripcion = new TextArea(seleccionada.getDescripcion());
        txtDescripcion.setPrefRowCount(3);

        grid.add(new Label("Nombre:"), 0, 0);
        grid.add(txtNombre, 1, 0);
        grid.add(new Label("Descripción:"), 0, 1);
        grid.add(txtDescripcion, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnGuardar) {
                String nuevoNombre = txtNombre.getText().trim();
                String nuevaDescripcion = txtDescripcion.getText().trim();

                if (playlistService.actualizarPlaylist(
                        usuarioActual.getUsername(),
                        seleccionada.getId(),
                        nuevoNombre,
                        nuevaDescripcion)) {
                    return seleccionada;
                }
            }
            return null;
        });

        Optional<Playlist> resultado = dialog.showAndWait();
        if (resultado.isPresent()) {
            guardarPlaylists();
            mostrarExito("Playlist actualizada");
            cargarPlaylists();
            mostrarDetallesPlaylist(seleccionada);
        }
    }

    @FXML
    private void handleEliminarPlaylist() {
        Playlist seleccionada = listPlaylists.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAdvertencia("Selecciona una playlist para eliminar");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Eliminar playlist?");
        confirmacion.setContentText(seleccionada.getNombre() + " - " +
                seleccionada.getCantidadCanciones() + " canciones");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            if (playlistService.eliminarPlaylist(usuarioActual.getUsername(), seleccionada.getId())) {
                guardarPlaylists();
                mostrarExito("Playlist eliminada");
                cargarPlaylists();
            }
        }
    }

    @FXML
    private void handleDuplicarPlaylist() {
        Playlist seleccionada = listPlaylists.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAdvertencia("Selecciona una playlist para duplicar");
            return;
        }

        Playlist duplicada = playlistService.duplicarPlaylist(
                usuarioActual.getUsername(),
                seleccionada.getId()
        );

        if (duplicada != null) {
            guardarPlaylists();
            mostrarExito("Playlist duplicada exitosamente");
            cargarPlaylists();
        }
    }

    @FXML
    private void handleAgregarCancion() {
        if (playlistActual == null) {
            mostrarAdvertencia("Selecciona una playlist primero");
            return;
        }

        List<Cancion> todasCanciones = cancionService.obtenerTodasLasCanciones();
        List<Cancion> disponibles = todasCanciones.stream()
                .filter(c -> !playlistActual.contieneCancion(c))
                .collect(Collectors.toList());

        if (disponibles.isEmpty()) {
            mostrarAdvertencia("Todas las canciones ya están en la playlist");
            return;
        }

        ChoiceDialog<Cancion> dialog = new ChoiceDialog<>(disponibles.get(0), disponibles);
        dialog.setTitle("Agregar Canción");
        dialog.setHeaderText("Selecciona una canción para agregar");
        dialog.setContentText("Canción:");

        Optional<Cancion> resultado = dialog.showAndWait();
        resultado.ifPresent(cancion -> {
            if (playlistService.agregarCancionAPlaylist(
                    usuarioActual.getUsername(),
                    playlistActual.getId(),
                    cancion)) {
                guardarPlaylists();
                mostrarExito("Canción agregada");
                mostrarDetallesPlaylist(playlistActual);
            }
        });
    }

    @FXML
    private void handleOrdenarCanciones() {
        if (playlistActual == null || playlistActual.getCantidadCanciones() == 0) {
            return;
        }

        String[] opciones = {
                "Título (A-Z)",
                "Título (Z-A)",
                "Artista (A-Z)",
                "Artista (Z-A)",
                "Año (Reciente primero)",
                "Año (Antiguo primero)"
        };

        ChoiceDialog<String> dialog = new ChoiceDialog<>(opciones[0], opciones);
        dialog.setTitle("Ordenar Canciones");
        dialog.setHeaderText("Selecciona el criterio de ordenamiento");

        Optional<String> resultado = dialog.showAndWait();
        resultado.ifPresent(criterio -> {
            List<Cancion> canciones = playlistActual.getCanciones();

            switch (criterio) {
                case "Título (A-Z)":
                    canciones.sort(Comparator.comparing(Cancion::getTitulo));
                    break;
                case "Título (Z-A)":
                    canciones.sort(Comparator.comparing(Cancion::getTitulo).reversed());
                    break;
                case "Artista (A-Z)":
                    canciones.sort(Comparator.comparing(Cancion::getArtista));
                    break;
                case "Artista (Z-A)":
                    canciones.sort(Comparator.comparing(Cancion::getArtista).reversed());
                    break;
                case "Año (Reciente primero)":
                    canciones.sort(Comparator.comparing(Cancion::getAnio).reversed());
                    break;
                case "Año (Antiguo primero)":
                    canciones.sort(Comparator.comparing(Cancion::getAnio));
                    break;
            }

            cancionesObservable.setAll(canciones);
            guardarPlaylists();
            mostrarExito("Playlist ordenada");
        });
    }

    @FXML
    private void handleReproducirTodo() {
        if (playlistActual == null || playlistActual.getCantidadCanciones() == 0) {
            mostrarAdvertencia("La playlist está vacía");
            return;
        }

        mostrarInformacion("Reproducción",
                "Reproduciendo playlist: " + playlistActual.getNombre() +
                        "\nCanciones: " + playlistActual.getCantidadCanciones());
    }

    @FXML
    private void handleReproducirSeleccion() {
        Cancion seleccionada = tableCanciones.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAdvertencia("Selecciona una canción");
            return;
        }
        abrirReproductor(seleccionada);
    }

    @FXML
    private void handleSubirCancion() {
        int indiceActual = tableCanciones.getSelectionModel().getSelectedIndex();
        if (indiceActual <= 0) return;

        if (playlistService.moverCancion(
                usuarioActual.getUsername(),
                playlistActual.getId(),
                indiceActual,
                indiceActual - 1)) {
            guardarPlaylists();
            mostrarDetallesPlaylist(playlistActual);
            tableCanciones.getSelectionModel().select(indiceActual - 1);
        }
    }

    @FXML
    private void handleBajarCancion() {
        int indiceActual = tableCanciones.getSelectionModel().getSelectedIndex();
        if (indiceActual < 0 || indiceActual >= playlistActual.getCantidadCanciones() - 1) {
            return;
        }

        if (playlistService.moverCancion(
                usuarioActual.getUsername(),
                playlistActual.getId(),
                indiceActual,
                indiceActual + 1)) {
            guardarPlaylists();
            mostrarDetallesPlaylist(playlistActual);
            tableCanciones.getSelectionModel().select(indiceActual + 1);
        }
    }

    @FXML
    private void handleEliminarCancion() {
        Cancion seleccionada = tableCanciones.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAdvertencia("Selecciona una canción");
            return;
        }
        eliminarCancionDePlaylist(seleccionada);
    }

    private void eliminarCancionDePlaylist(Cancion cancion) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar");
        confirmacion.setHeaderText("¿Quitar canción de la playlist?");
        confirmacion.setContentText(cancion.getTitulo());

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            if (playlistService.eliminarCancionDePlaylist(
                    usuarioActual.getUsername(),
                    playlistActual.getId(),
                    cancion)) {
                guardarPlaylists();
                mostrarExito("Canción quitada de la playlist");
                mostrarDetallesPlaylist(playlistActual);
            }
        }
    }

    @FXML
    private void handleVolver() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/universidad/estructuras/proyecto_estructura/main.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnVolver.getScene().getWindow();

            // Resetear tamaño
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