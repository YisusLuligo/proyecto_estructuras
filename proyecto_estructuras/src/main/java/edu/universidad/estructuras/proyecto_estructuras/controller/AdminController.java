package edu.universidad.estructuras.proyecto_estructuras.controller;

import edu.universidad.estructuras.proyecto_estructuras.Main;
import edu.universidad.estructuras.proyecto_estructuras.model.Cancion;
import edu.universidad.estructuras.proyecto_estructuras.model.Usuario;
import edu.universidad.estructuras.proyecto_estructuras.service.AdminService;
import edu.universidad.estructuras.proyecto_estructuras.service.AutenticacionService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.List;

/**
 * Controlador para el panel de administración.
 * RF-010, RF-011, RF-012
 */
public class AdminController {

    @FXML private TableView<Cancion> cancionesTable;
    @FXML private TableView<Usuario> usuariosTable;
    @FXML private Label totalCancionesLabel;
    @FXML private Label totalUsuariosLabel;
    @FXML private Button volverInicioButton;
    @FXML private Button agregarCancionButton;
    @FXML private Button eliminarCancionButton;
    @FXML private Button cargaMasivaButton;
    @FXML private Button eliminarUsuarioButton;

    private AdminService adminService;

    @FXML
    public void initialize() {
        adminService = AdminService.obtenerInstancia();

        cargarDatos();
    }

    private void cargarDatos() {
        // Cargar canciones
        List<Cancion> canciones = adminService.listarCanciones();
        ObservableList<Cancion> cancionesObs = FXCollections.observableArrayList(canciones);
        cancionesTable.setItems(cancionesObs);

        // Cargar usuarios
        List<Usuario> usuarios = adminService.listarUsuarios();
        ObservableList<Usuario> usuariosObs = FXCollections.observableArrayList(usuarios);
        usuariosTable.setItems(usuariosObs);

        // Actualizar contadores
        totalCancionesLabel.setText(String.valueOf(adminService.contarCanciones()));
        totalUsuariosLabel.setText(String.valueOf(adminService.contarUsuarios()));
    }

    @FXML
    void handleVolverInicio(ActionEvent event) {
        try {
            Main.cambiarVista("principal");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleAgregarCancion(ActionEvent event) {
        // Dialog para agregar canción
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Agregar Canción");
        dialog.setHeaderText("Ingresa los datos de la canción");
        dialog.setContentText("Título:");

        dialog.showAndWait().ifPresent(titulo -> {
            // Aquí implementarías un dialog más completo
            mostrarAlerta(Alert.AlertType.INFORMATION, "Info",
                    "Función completa de agregar - Por implementar dialog completo");
        });
    }

    @FXML
    void handleEliminarCancion(ActionEvent event) {
        Cancion seleccionada = cancionesTable.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia",
                    "Selecciona una canción primero");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setContentText("¿Seguro que querés eliminar esta canción?");

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                adminService.eliminarCancion(seleccionada.getId());
                cargarDatos();
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                        "Canción eliminada");
            }
        });
    }

    @FXML
    void handleCargaMasiva(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo de canciones");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos de texto", "*.txt", "*.csv")
        );

        File archivo = fileChooser.showOpenDialog(null);

        if (archivo != null) {
            try {
                int cargadas = adminService.cargarCancionesMasivamente(archivo.getAbsolutePath());
                cargarDatos();
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                        "Se cargaron " + cargadas + " canciones");
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error",
                        "Error al cargar: " + e.getMessage());
            }
        }
    }

    @FXML
    void handleEliminarUsuario(ActionEvent event) {
        Usuario seleccionado = usuariosTable.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia",
                    "Selecciona un usuario primero");
            return;
        }

        if (seleccionado.esAdministrador()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se puede eliminar un administrador");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setContentText("¿Seguro que querés eliminar este usuario?");

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                adminService.eliminarUsuario(seleccionado.getUsername());
                cargarDatos();
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                        "Usuario eliminado");
            }
        });
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}