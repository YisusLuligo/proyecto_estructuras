package edu.universidad.estructuras.proyecto_estructura.controller;

import edu.universidad.estructuras.proyecto_estructura.model.Cancion;
import edu.universidad.estructuras.proyecto_estructura.model.Usuario;
import edu.universidad.estructuras.proyecto_estructura.service.CancionService;
import edu.universidad.estructuras.proyecto_estructura.service.UsuarioService;
import edu.universidad.estructuras.proyecto_estructura.utils.Validaciones;
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

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Controlador para la gestión de canciones (Admin)
 */
public class GestionCancionesController {

    @FXML private Button btnVolver;
    @FXML private Button btnAgregar;
    @FXML private Button btnCargarMasivo;
    @FXML private Button btnRefrescar;
    @FXML private Button btnBuscar;
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;
    @FXML private Button btnYoutube;
    @FXML private TextField txtBuscar;
    @FXML private Label lblTotalCanciones;
    @FXML private TableView<Cancion> tableCanciones;
    @FXML private TableColumn<Cancion, String> colId;
    @FXML private TableColumn<Cancion, String> colTitulo;
    @FXML private TableColumn<Cancion, String> colArtista;
    @FXML private TableColumn<Cancion, String> colGenero;
    @FXML private TableColumn<Cancion, Integer> colAnio;
    @FXML private TableColumn<Cancion, String> colDuracion;
    @FXML private TableColumn<Cancion, Void> colAcciones;

    private CancionService cancionService;
    private UsuarioService usuarioService;
    private ObservableList<Cancion> cancionesObservable;

    @FXML
    public void initialize() {
        cancionService = CancionService.getInstance();
        usuarioService = UsuarioService.getInstance();
        configurarTabla();
        cargarCanciones();
        actualizarContador();
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
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
                    abrirReproductorYoutube(cancion);
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

    private void cargarCanciones() {
        List<Cancion> canciones = cancionService.obtenerTodasLasCanciones();
        cancionesObservable = FXCollections.observableArrayList(canciones);
        tableCanciones.setItems(cancionesObservable);
    }

    private void actualizarContador() {
        lblTotalCanciones.setText("Total: " + cancionService.getCantidadCanciones());
    }

    @FXML
    private void handleAgregar() {
        mostrarDialogoCancion(null);
    }

    @FXML
    private void handleEditar() {
        Cancion seleccionada = tableCanciones.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAdvertencia("Por favor, seleccione una canción para editar");
            return;
        }
        mostrarDialogoCancion(seleccionada);
    }

    @FXML
    private void handleEliminar() {
        Cancion seleccionada = tableCanciones.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAdvertencia("Por favor, seleccione una canción para eliminar");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Eliminar canción?");
        confirmacion.setContentText(seleccionada.getTitulo() + " - " + seleccionada.getArtista());

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            if (cancionService.eliminarCancion(seleccionada.getId())) {
                mostrarExito("Canción eliminada exitosamente");
                cargarCanciones();
                actualizarContador();
            } else {
                mostrarError("Error al eliminar la canción");
            }
        }
    }

    @FXML
    private void handleVerYoutube() {
        Cancion seleccionada = tableCanciones.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAdvertencia("Por favor, seleccione una canción");
            return;
        }
        abrirReproductorYoutube(seleccionada);
    }

    @FXML
    private void handleBuscar() {
        String busqueda = txtBuscar.getText().trim();
        if (busqueda.isEmpty()) {
            cargarCanciones();
            return;
        }

        List<Cancion> resultados = cancionService.buscarPorTitulo(busqueda);
        List<Cancion> porArtista = cancionService.buscarPorArtista(busqueda);

        for (Cancion c : porArtista) {
            if (!resultados.contains(c)) {
                resultados.add(c);
            }
        }

        cancionesObservable = FXCollections.observableArrayList(resultados);
        tableCanciones.setItems(cancionesObservable);
    }

    @FXML
    private void handleCargarMasivo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo de canciones");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos de texto", "*.txt")
        );

        Stage stage = (Stage) btnCargarMasivo.getScene().getWindow();
        File archivo = fileChooser.showOpenDialog(stage);

        if (archivo != null) {
            int cargadas = cancionService.cargarCancionesMasivamente(archivo.getAbsolutePath());
            mostrarExito("Se cargaron " + cargadas + " canciones exitosamente");
            cargarCanciones();
            actualizarContador();
        }
    }

    @FXML
    private void handleRefrescar() {
        txtBuscar.clear();
        cargarCanciones();
        actualizarContador();
        mostrarExito("Lista actualizada");
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

    private void mostrarDialogoCancion(Cancion cancion) {
        Dialog<Cancion> dialog = new Dialog<>();
        dialog.setTitle(cancion == null ? "Nueva Canción" : "Editar Canción");
        dialog.setHeaderText(cancion == null ? "Ingrese los datos de la canción" : "Modifique los datos");

        ButtonType btnGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, ButtonType.CANCEL);

        // Crear campos del formulario
        TextField txtTitulo = new TextField(cancion != null ? cancion.getTitulo() : "");
        txtTitulo.setPromptText("Título");

        TextField txtArtista = new TextField(cancion != null ? cancion.getArtista() : "");
        txtArtista.setPromptText("Artista");

        TextField txtGenero = new TextField(cancion != null ? cancion.getGenero() : "");
        txtGenero.setPromptText("Género");

        TextField txtAnio = new TextField(cancion != null ? String.valueOf(cancion.getAnio()) : "");
        txtAnio.setPromptText("Año");

        TextField txtDuracion = new TextField(cancion != null ? String.valueOf(cancion.getDuracion()) : "");
        txtDuracion.setPromptText("Duración (ej: 3.45)");

        // ✨ NUEVO: Campo para URL de YouTube
        TextField txtUrl = new TextField(cancion != null ? cancion.getUrlYoutube() : "");
        txtUrl.setPromptText("URL de YouTube (opcional)");
        txtUrl.setPrefColumnCount(30);

        // ✨ Label con instrucciones
        Label lblUrlInfo = new Label("💡 Copia la URL completa del video de YouTube");
        lblUrlInfo.setStyle("-fx-text-fill: #666; -fx-font-size: 11;");

        // Crear grid con los campos
        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20));

        grid.add(new Label("Título:"), 0, 0);
        grid.add(txtTitulo, 1, 0);

        grid.add(new Label("Artista:"), 0, 1);
        grid.add(txtArtista, 1, 1);

        grid.add(new Label("Género:"), 0, 2);
        grid.add(txtGenero, 1, 2);

        grid.add(new Label("Año:"), 0, 3);
        grid.add(txtAnio, 1, 3);

        grid.add(new Label("Duración:"), 0, 4);
        grid.add(txtDuracion, 1, 4);

        grid.add(new Label("URL YouTube:"), 0, 5);
        grid.add(txtUrl, 1, 5);

        grid.add(lblUrlInfo, 1, 6);

        dialog.getDialogPane().setContent(grid);

        // ✨ Validación y guardado
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnGuardar) {
                try {
                    String titulo = txtTitulo.getText().trim();
                    String artista = txtArtista.getText().trim();
                    String genero = txtGenero.getText().trim();
                    String urlYoutube = txtUrl.getText().trim();

                    int anio = Integer.parseInt(txtAnio.getText().trim());
                    double duracion = Double.parseDouble(txtDuracion.getText().trim().replace(",", "."));

                    // Validar campos obligatorios
                    if (!Validaciones.esTextoValido(titulo) || !Validaciones.esTextoValido(artista)) {
                        mostrarError("Título y Artista son obligatorios");
                        return null;
                    }

                    // Validar URL si fue proporcionada
                    if (!urlYoutube.isEmpty() && !validarUrlYoutube(urlYoutube)) {
                        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
                        confirmacion.setTitle("URL no válida");
                        confirmacion.setHeaderText("La URL no parece ser de YouTube");
                        confirmacion.setContentText("¿Desea guardarla de todos modos?");

                        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
                            return null;
                        }
                    }

                    // Guardar o actualizar
                    if (cancion == null) {
                        // Nueva canción
                        Cancion nueva = cancionService.agregarCancion(titulo, artista, genero, anio, duracion, urlYoutube);
                        if (nueva != null) {
                            mostrarExito("Canción agregada exitosamente");
                            return nueva;
                        } else {
                            mostrarError("Ya existe una canción con ese título");
                            return null;
                        }
                    } else {
                        // Actualizar canción existente
                        cancion.setTitulo(titulo);
                        cancion.setArtista(artista);
                        cancion.setGenero(genero);
                        cancion.setAnio(anio);
                        cancion.setDuracion(duracion);
                        cancion.setUrlYoutube(urlYoutube);

                        if (cancionService.actualizarCancion(cancion.getId(), titulo, artista, genero, anio, duracion)) {
                            mostrarExito("Canción actualizada exitosamente");
                            return cancion;
                        }
                    }
                } catch (NumberFormatException e) {
                    mostrarError("Formato de número inválido en Año o Duración");
                }
            }
            return null;
        });

        Optional<Cancion> resultado = dialog.showAndWait();
        if (resultado.isPresent()) {
            cargarCanciones();
            actualizarContador();
        }
    }

    private void abrirReproductorYoutube(Cancion cancion) {
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
    private boolean validarUrlYoutube(String url) {
        return url.contains("youtube.com") || url.contains("youtu.be");
    }
}