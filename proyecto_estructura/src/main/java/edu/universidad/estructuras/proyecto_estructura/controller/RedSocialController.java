package edu.universidad.estructuras.proyecto_estructura.controller;


import edu.universidad.estructuras.proyecto_estructura.model.Usuario;
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
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

/**
 * Controlador para la red social - conexiones entre usuarios
 * Utiliza Grafo Social con BFS para sugerencias
 *
 * @author SyncUp Team
 * @version 1.0
 */
public class RedSocialController {

    @FXML private Button btnVolver;
    @FXML private Label lblMisSeguidos;
    @FXML private Label lblMisSeguidores;
    @FXML private TextField txtBuscarUsuario;
    @FXML private Button btnBuscar;


    // Tabla de seguidos
    @FXML private TableView<Usuario> tableSeguidos;
    @FXML private TableColumn<Usuario, String> colSeguidosUsername;
    @FXML private TableColumn<Usuario, String> colSeguidosNombre;
    @FXML private TableColumn<Usuario, Integer> colSeguidosFavoritos;
    @FXML private TableColumn<Usuario, Void> colSeguidosAcciones;

    // Tabla de sugerencias
    @FXML private TableView<Usuario> tableSugerencias;
    @FXML private TableColumn<Usuario, String> colSugerenciasUsername;
    @FXML private TableColumn<Usuario, String> colSugerenciasNombre;
    @FXML private TableColumn<Usuario, Integer> colSugerenciasFavoritos;
    @FXML private TableColumn<Usuario, Void> colSugerenciasAcciones;

    // Tabla de búsqueda
    @FXML private TableView<Usuario> tableBusqueda;
    @FXML private TableColumn<Usuario, String> colBusquedaUsername;
    @FXML private TableColumn<Usuario, String> colBusquedaNombre;
    @FXML private TableColumn<Usuario, String> colBusquedaTipo;
    @FXML private TableColumn<Usuario, Void> colBusquedaAcciones;

    @FXML private VBox panelBusqueda;

    private UsuarioService usuarioService;
    private Usuario usuarioActual;
    private ObservableList<Usuario> seguidosObservable;
    private ObservableList<Usuario> sugerenciasObservable;
    private ObservableList<Usuario> busquedaObservable;
    private final Button btnVerPerfil = new Button("👁️ Ver Perfil");

    @FXML
    public void initialize() {
        usuarioService = UsuarioService.getInstance();
        usuarioActual = usuarioService.getUsuarioActual();

        if (usuarioActual != null) {
            configurarTablas();
            cargarDatos();
            panelBusqueda.setVisible(false);
            panelBusqueda.setManaged(false);
        }
    }

    private void configurarTablas() {
        // ========== TABLA DE SEGUIDOS ==========
        colSeguidosUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colSeguidosNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colSeguidosFavoritos.setCellValueFactory(new PropertyValueFactory<>("cantidadFavoritos"));

        colSeguidosAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnDejarDeSeguir = new Button("👋 Dejar de Seguir");
            private final Button btnVerPerfil = new Button("👁️ Ver Perfil");

            {
                btnDejarDeSeguir.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;");
                btnVerPerfil.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-cursor: hand;");

                btnDejarDeSeguir.setOnAction(event -> {
                    Usuario usuario = getTableView().getItems().get(getIndex());
                    dejarDeSeguir(usuario);
                });

                btnVerPerfil.setOnAction(event -> {
                    Usuario usuario = getTableView().getItems().get(getIndex());
                    abrirPerfilPublico(usuario);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(5, btnDejarDeSeguir, btnVerPerfil);
                    hbox.setAlignment(Pos.CENTER);
                    setGraphic(hbox);
                }
            }
        });

        // ========== TABLA DE SUGERENCIAS ==========
        colSugerenciasUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colSugerenciasNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colSugerenciasFavoritos.setCellValueFactory(new PropertyValueFactory<>("cantidadFavoritos"));

        colSugerenciasAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnSeguir = new Button("➕ Seguir");
            private final Button btnVerPerfil = new Button("👁️ Ver Perfil");

            {
                btnSeguir.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-cursor: hand;");
                btnVerPerfil.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-cursor: hand;");

                btnSeguir.setOnAction(event -> {
                    Usuario usuario = getTableView().getItems().get(getIndex());
                    seguir(usuario);
                });

                btnVerPerfil.setOnAction(event -> {
                    Usuario usuario = getTableView().getItems().get(getIndex());
                    abrirPerfilPublico(usuario);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(5, btnSeguir, btnVerPerfil);
                    hbox.setAlignment(Pos.CENTER);
                    setGraphic(hbox);
                }
            }
        });

        // ========== TABLA DE BÚSQUEDA ==========
        colBusquedaUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colBusquedaNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colBusquedaTipo.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getTipoUsuario().toString()
                )
        );

        colBusquedaAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnSeguir = new Button("➕ Seguir");
            private final Button btnDejarDeSeguir = new Button("👋 Dejar de Seguir");
            private final Button btnVerPerfil = new Button("👁️ Ver Perfil");

            {
                btnSeguir.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-cursor: hand;");
                btnDejarDeSeguir.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;");
                btnVerPerfil.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-cursor: hand;");

                btnSeguir.setOnAction(event -> {
                    Usuario usuario = getTableView().getItems().get(getIndex());
                    seguir(usuario);
                });

                btnDejarDeSeguir.setOnAction(event -> {
                    Usuario usuario = getTableView().getItems().get(getIndex());
                    dejarDeSeguir(usuario);
                });

                btnVerPerfil.setOnAction(event -> {
                    Usuario usuario = getTableView().getItems().get(getIndex());
                    abrirPerfilPublico(usuario);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Usuario usuario = getTableView().getItems().get(getIndex());
                    Button botonSeguimiento = usuarioService.estaSiguiendo(
                            usuarioActual.getUsername(),
                            usuario.getUsername()
                    ) ? btnDejarDeSeguir : btnSeguir;

                    HBox hbox = new HBox(5, botonSeguimiento, btnVerPerfil);
                    hbox.setAlignment(Pos.CENTER);
                    setGraphic(hbox);
                }
            }
        });
    }

    private void cargarDatos() {
        cargarSeguidos();
        cargarSugerencias();
        actualizarEstadisticas();
    }

    private void cargarSeguidos() {
        List<Usuario> seguidos = usuarioService.obtenerSeguidos(usuarioActual.getUsername());
        seguidosObservable = FXCollections.observableArrayList(seguidos);
        tableSeguidos.setItems(seguidosObservable);
    }

    private void cargarSugerencias() {
        // Usar BFS para obtener sugerencias de "amigos de amigos"
        List<Usuario> sugerencias = usuarioService.obtenerSugerenciasUsuarios(
                usuarioActual.getUsername(), 10);
        sugerenciasObservable = FXCollections.observableArrayList(sugerencias);
        tableSugerencias.setItems(sugerenciasObservable);
    }

    private void actualizarEstadisticas() {
        int seguidos = usuarioService.getCantidadSeguidos(usuarioActual.getUsername());
        int seguidores = usuarioService.getCantidadSeguidores(usuarioActual.getUsername());

        lblMisSeguidos.setText(String.valueOf(seguidos));
        lblMisSeguidores.setText(String.valueOf(seguidores));
    }

    @FXML
    private void handleBuscar() {
        String busqueda = txtBuscarUsuario.getText().trim();
        if (busqueda.isEmpty()) {
            panelBusqueda.setVisible(false);
            panelBusqueda.setManaged(false);
            return;
        }

        List<Usuario> resultados = usuarioService.buscarUsuarios(busqueda);

        // Excluir al usuario actual
        resultados.removeIf(u -> u.getUsername().equals(usuarioActual.getUsername()));

        busquedaObservable = FXCollections.observableArrayList(resultados);
        tableBusqueda.setItems(busquedaObservable);

        panelBusqueda.setVisible(true);
        panelBusqueda.setManaged(true);
    }

    private void seguir(Usuario usuario) {
        if (usuarioService.seguirUsuario(usuarioActual.getUsername(), usuario.getUsername())) {
            mostrarExito("Ahora sigues a " + usuario.getUsername());
            cargarDatos();
            handleBuscar(); // Actualizar tabla de búsqueda si está visible
        } else {
            mostrarError("Error al seguir al usuario");
        }
    }

    private void dejarDeSeguir(Usuario usuario) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar");
        confirmacion.setHeaderText("¿Dejar de seguir a " + usuario.getUsername() + "?");

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (usuarioService.dejarDeSeguirUsuario(usuarioActual.getUsername(), usuario.getUsername())) {
                    mostrarExito("Dejaste de seguir a " + usuario.getUsername());
                    cargarDatos();
                    handleBuscar(); // Actualizar tabla de búsqueda si está visible
                }
            }
        });
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

    private void abrirPerfilPublico(Usuario usuario) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/edu/universidad/estructuras/proyecto_estructura/perfil-publico.fxml"));
            Parent root = loader.load();

            PerfilPublicoController controller = loader.getController();
            controller.setUsuarioPublico(usuario);

            Stage stage = (Stage) btnVolver.getScene().getWindow();
            Scene scene = new Scene(root, 1200, 900);
            stage.setScene(scene);
            stage.setTitle("SyncUp - Perfil de " + usuario.getUsername());
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("Error al abrir perfil: " + e.getMessage());
        }
    }




}
