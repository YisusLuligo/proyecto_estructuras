package edu.universidad.estructuras.proyecto_estructuras.controller;

import edu.universidad.estructuras.proyecto_estructuras.Main;
import edu.universidad.estructuras.proyecto_estructuras.model.Cancion;
import edu.universidad.estructuras.proyecto_estructuras.model.Usuario;
import edu.universidad.estructuras.proyecto_estructuras.service.*;
import edu.universidad.estructuras.proyecto_estructuras.util.Constants;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * Controlador para la vista principal del usuario.
 * Muestra el reproductor y recomendaciones.
 */
public class PrincipalController {

    @FXML private Label usuarioNombreLabel;
    @FXML private ListView<Cancion> recomendacionesListView;
    @FXML private ListView<Cancion> favoritosListView;
    @FXML private Button buscarButton;
    @FXML private Button perfilButton;
    @FXML private Button parcerosButton;
    @FXML private Button cerrarSesionButton;
    @FXML private Button descubrimientoButton;

    private SessionManager sessionManager;
    private RecomendacionService recomendacionService;

    @FXML
    public void initialize() {
        sessionManager = SessionManager.obtenerInstancia();
        recomendacionService = RecomendacionService.obtenerInstancia();

        cargarDatosUsuario();
    }

    private void cargarDatosUsuario() {
        Usuario usuario = sessionManager.obtenerUsuarioActual();

        if (usuario != null) {
            usuarioNombreLabel.setText("¡Qué hubo, " + usuario.getNombre() + "!");

            // Cargar favoritos
            ObservableList<Cancion> favoritos =
                    FXCollections.observableArrayList(usuario.getListaFavoritos());
            favoritosListView.setItems(favoritos);

            // Configurar cell factory para mostrar las canciones
            favoritosListView.setCellFactory(lv -> new ListCell<Cancion>() {
                @Override
                protected void updateItem(Cancion cancion, boolean empty) {
                    super.updateItem(cancion, empty);
                    setText(empty || cancion == null ? null : cancion.toString());
                }
            });
        }
    }

    @FXML
    void handleBuscar(ActionEvent event) {
        // Implementar navegación a búsqueda
        mostrarAlerta(Alert.AlertType.INFORMATION, "Info",
                "Función de búsqueda - Por implementar vista");
    }

    @FXML
    void handlePerfil(ActionEvent event) {
        // Implementar navegación a perfil
        mostrarAlerta(Alert.AlertType.INFORMATION, "Info",
                "Función de perfil - Por implementar vista");
    }

    @FXML
    void handleParceros(ActionEvent event) {
        // Implementar navegación a parceros
        mostrarAlerta(Alert.AlertType.INFORMATION, "Info",
                "Función de parceros - Por implementar vista");
    }

    @FXML
    void handleDescubrimiento(ActionEvent event) {
        Usuario usuario = sessionManager.obtenerUsuarioActual();

        if (usuario.getListaFavoritos().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING,
                    "Sin favoritos",
                    "Primero agrega canciones a favoritos, parce!");
            return;
        }

        List<Cancion> descubrimiento =
                recomendacionService.generarDescubrimientoSemanal(usuario);

        ObservableList<Cancion> recomendaciones =
                FXCollections.observableArrayList(descubrimiento);
        recomendacionesListView.setItems(recomendaciones);

        recomendacionesListView.setCellFactory(lv -> new ListCell<Cancion>() {
            @Override
            protected void updateItem(Cancion cancion, boolean empty) {
                super.updateItem(cancion, empty);
                setText(empty || cancion == null ? null : cancion.toString());
            }
        });

        mostrarAlerta(Alert.AlertType.INFORMATION,
                "¡Listo!",
                "Se generaron " + descubrimiento.size() + " recomendaciones para vos");
    }

    @FXML
    void handleCerrarSesion(ActionEvent event) {
        AutenticacionService autenticacionService =
                AutenticacionService.obtenerInstancia();
        autenticacionService.cerrarSesion();

        try {
            Main.cambiarVista("login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}