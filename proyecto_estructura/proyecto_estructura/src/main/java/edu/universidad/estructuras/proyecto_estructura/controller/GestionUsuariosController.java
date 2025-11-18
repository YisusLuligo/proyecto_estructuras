package edu.universidad.estructuras.proyecto_estructura.controller;

import edu.universidad.estructuras.proyecto_estructura.model.Usuario;
import edu.universidad.estructuras.proyecto_estructura.service.UsuarioService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Controlador para la gestión de usuarios (Admin)
 */
public class GestionUsuariosController {

    @FXML private Button btnVolver;
    @FXML private Button btnRefrescar;
    @FXML private Button btnEliminar;
    @FXML private TextField txtBuscar;
    @FXML private Label lblTotalUsuarios;
    @FXML private TableView<Usuario> tableUsuarios;
    @FXML private TableColumn<Usuario, String> colUsername;
    @FXML private TableColumn<Usuario, String> colNombre;
    @FXML private TableColumn<Usuario, String> colTipo;
    @FXML private TableColumn<Usuario, Integer> colFavoritos;

    private UsuarioService usuarioService;
    private ObservableList<Usuario> usuariosObservable;

    @FXML
    public void initialize() {
        usuarioService = UsuarioService.getInstance();
        configurarTabla();
        cargarUsuarios();
        actualizarContador();
    }

    private void configurarTabla() {
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colTipo.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getTipoUsuario().toString()
                )
        );
        colFavoritos.setCellValueFactory(new PropertyValueFactory<>("cantidadFavoritos"));
    }

    private void cargarUsuarios() {
        List<Usuario> usuarios = usuarioService.obtenerTodosLosUsuarios();
        usuariosObservable = FXCollections.observableArrayList(usuarios);
        tableUsuarios.setItems(usuariosObservable);
    }

    private void actualizarContador() {
        lblTotalUsuarios.setText("Total: " + usuarioService.getCantidadUsuarios());
    }

    @FXML
    private void handleEliminar() {
        Usuario seleccionado = tableUsuarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAdvertencia("Por favor, seleccione un usuario para eliminar");
            return;
        }

        if (seleccionado.esAdministrador()) {
            mostrarError("No se puede eliminar un administrador");
            return;
        }

        if (seleccionado.getUsername().equals(usuarioService.getUsuarioActual().getUsername())) {
            mostrarError("No puedes eliminarte a ti mismo");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Eliminar usuario?");
        confirmacion.setContentText(seleccionado.getUsername() + " - " + seleccionado.getNombre());

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            if (usuarioService.eliminarUsuario(seleccionado.getUsername())) {
                mostrarExito("Usuario eliminado exitosamente");
                cargarUsuarios();
                actualizarContador();
            } else {
                mostrarError("Error al eliminar el usuario");
            }
        }
    }

    @FXML
    private void handleBuscar() {
        String busqueda = txtBuscar.getText().trim().toLowerCase();
        if (busqueda.isEmpty()) {
            cargarUsuarios();
            return;
        }

        List<Usuario> todos = usuarioService.obtenerTodosLosUsuarios();
        List<Usuario> filtrados = todos.stream()
                .filter(u -> u.getUsername().toLowerCase().contains(busqueda) ||
                        u.getNombre().toLowerCase().contains(busqueda))
                .toList();

        usuariosObservable = FXCollections.observableArrayList(filtrados);
        tableUsuarios.setItems(usuariosObservable);
    }

    @FXML
    private void handleRefrescar() {
        txtBuscar.clear();
        cargarUsuarios();
        actualizarContador();
        mostrarExito("Lista actualizada");
    }

    @FXML
    private void handleVolver() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/universidad/estructuras/proyecto_estructura/main.fxml"));
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