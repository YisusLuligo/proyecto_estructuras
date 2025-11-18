package edu.universidad.estructuras.proyecto_estructura.controller;

import edu.universidad.estructuras.proyecto_estructura.model.Cancion;
import edu.universidad.estructuras.proyecto_estructura.model.Usuario;
import edu.universidad.estructuras.proyecto_estructura.service.UsuarioService;
import edu.universidad.estructuras.proyecto_estructura.utils.Validaciones;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

/**
 * Controlador para la vista de perfil de usuario
 *
 */
public class PerfilController {

    @FXML private Label lblUsername;
    @FXML private TextField txtNombre;
    @FXML private Label lblTipoUsuario;
    @FXML private Label lblCantidadFavoritos;
    @FXML private PasswordField txtPasswordActual;
    @FXML private PasswordField txtPasswordNueva;
    @FXML private PasswordField txtPasswordConfirmar;
    @FXML private ListView<Cancion> listViewFavoritos;
    @FXML private Button btnVolver;
    @FXML private Button btnCerrarSesion;
    @FXML private Button btnActualizarNombre;
    @FXML private Button btnCambiarPassword;
    @FXML private Button btnAgregarCancionPrueba;
    @FXML private Button btnEliminarFavorito;
    @FXML private Button btnLimpiarFavoritos;

    private UsuarioService usuarioService;
    private Usuario usuarioActual;
    private ObservableList<Cancion> favoritosObservable;

    /**
     * Inicializa el controlador
     */
    @FXML
    public void initialize() {
        usuarioService = UsuarioService.getInstance();
        usuarioActual = usuarioService.getUsuarioActual();

        if (usuarioActual != null) {
            cargarDatosUsuario();
            configurarListaFavoritos();
        }
    }

    /**
     * Carga los datos del usuario en la interfaz
     */
    private void cargarDatosUsuario() {
        lblUsername.setText(usuarioActual.getUsername());
        txtNombre.setText(usuarioActual.getNombre());
        lblTipoUsuario.setText(usuarioActual.getTipoUsuario().toString());
        actualizarContadorFavoritos();
    }

    /**
     * Configura la lista de favoritos
     */
    private void configurarListaFavoritos() {
        favoritosObservable = FXCollections.observableArrayList(usuarioActual.getListaFavoritos());
        listViewFavoritos.setItems(favoritosObservable);

        // Personalizar cómo se muestra cada canción en la lista
        listViewFavoritos.setCellFactory(param -> new ListCell<Cancion>() {
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

    /**
     * Actualiza el contador de favoritos
     */
    private void actualizarContadorFavoritos() {
        lblCantidadFavoritos.setText(String.valueOf(usuarioActual.getCantidadFavoritos()));
    }

    /**
     * Maneja la actualización del nombre
     */
    @FXML
    private void handleActualizarNombre() {
        String nuevoNombre = txtNombre.getText();

        if (!Validaciones.esTextoValido(nuevoNombre)) {
            mostrarError("El nombre no puede estar vacío");
            return;
        }

        if (usuarioService.actualizarNombre(usuarioActual.getUsername(), nuevoNombre)) {
            mostrarExito("Nombre actualizado exitosamente");
            usuarioActual.setNombre(nuevoNombre);
        } else {
            mostrarError("Error al actualizar el nombre");
        }
    }

    /**
     * Maneja el cambio de contraseña
     */
    @FXML
    private void handleCambiarPassword() {
        String passwordActual = txtPasswordActual.getText();
        String passwordNueva = txtPasswordNueva.getText();
        String passwordConfirmar = txtPasswordConfirmar.getText();

        // Validaciones
        if (!Validaciones.esTextoValido(passwordActual) ||
                !Validaciones.esTextoValido(passwordNueva) ||
                !Validaciones.esTextoValido(passwordConfirmar)) {
            mostrarError("Por favor, complete todos los campos");
            return;
        }

        // Verificar contraseña actual
        if (!usuarioActual.getPassword().equals(passwordActual)) {
            mostrarError("La contraseña actual es incorrecta");
            return;
        }

        // Verificar que las nuevas contraseñas coincidan
        if (!passwordNueva.equals(passwordConfirmar)) {
            mostrarError("Las nuevas contraseñas no coinciden");
            return;
        }

        // Validar formato de la nueva contraseña
        if (!Validaciones.esPasswordValido(passwordNueva)) {
            mostrarError("La nueva contraseña debe tener al menos 6 caracteres");
            return;
        }

        // Actualizar contraseña
        if (usuarioService.actualizarPassword(usuarioActual.getUsername(), passwordNueva)) {
            mostrarExito("Contraseña actualizada exitosamente");
            // Limpiar campos
            txtPasswordActual.clear();
            txtPasswordNueva.clear();
            txtPasswordConfirmar.clear();
        } else {
            mostrarError("Error al actualizar la contraseña");
        }
    }

    /**
     * Agrega una canción de prueba a favoritos
     */
    @FXML
    private void handleAgregarCancionPrueba() {
        // Generar ID único
        String id = "TEST" + String.format("%03d", usuarioActual.getCantidadFavoritos() + 1);

        String[] generos = {"Pop", "Rock", "Jazz", "Electrónica", "Reggaeton", "Salsa"};
        String[] artistas = {"Artista Demo", "The Beatles", "Queen", "Pink Floyd", "Led Zeppelin"};
        String genero = generos[(int) (Math.random() * generos.length)];
        String artista = artistas[(int) (Math.random() * artistas.length)];

        Cancion cancionPrueba = new Cancion(
                id,
                "Canción de Prueba " + (usuarioActual.getCantidadFavoritos() + 1),
                artista,
                genero,
                2020 + (int) (Math.random() * 5),
                2.5 + (Math.random() * 3)
        );

        if (usuarioActual.agregarFavorito(cancionPrueba)) {
            favoritosObservable.add(cancionPrueba);
            actualizarContadorFavoritos();
            mostrarExito("Canción agregada a favoritos");
        } else {
            mostrarError("La canción ya está en favoritos");
        }
    }

    /**
     * Elimina la canción seleccionada de favoritos
     */
    @FXML
    private void handleEliminarFavorito() {
        Cancion cancionSeleccionada = listViewFavoritos.getSelectionModel().getSelectedItem();

        if (cancionSeleccionada == null) {
            mostrarAdvertencia("Por favor, seleccione una canción");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Eliminar canción de favoritos?");
        confirmacion.setContentText(cancionSeleccionada.getTitulo() + " - " + cancionSeleccionada.getArtista());

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            if (usuarioActual.eliminarFavorito(cancionSeleccionada)) {
                favoritosObservable.remove(cancionSeleccionada);
                actualizarContadorFavoritos();
                mostrarExito("Canción eliminada de favoritos");
            }
        }
    }

    /**
     * Limpia todos los favoritos
     */
    @FXML
    private void handleLimpiarFavoritos() {
        if (usuarioActual.getCantidadFavoritos() == 0) {
            mostrarAdvertencia("No hay canciones en favoritos");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar limpieza");
        confirmacion.setHeaderText("¿Eliminar todas las canciones favoritas?");
        confirmacion.setContentText("Esta acción no se puede deshacer. Se eliminarán " +
                usuarioActual.getCantidadFavoritos() + " canciones.");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            usuarioActual.getListaFavoritos().clear();
            favoritosObservable.clear();
            actualizarContadorFavoritos();
            mostrarExito("Todos los favoritos han sido eliminados");
        }
    }

    /**
     * Vuelve a la pantalla principal
     */
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

    /**
     * Maneja el cierre de sesión
     */
    @FXML
    private void handleCerrarSesion() {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Cerrar Sesión");
        confirmacion.setHeaderText("¿Está seguro que desea cerrar sesión?");
        confirmacion.setContentText("Será redirigido a la pantalla de inicio de sesión.");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            usuarioService.cerrarSesion();
            volverALogin();
        }
    }

    /**
     * Vuelve a la pantalla de login
     */
    private void volverALogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/universidad/estructuras/proyecto_estructura/login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnCerrarSesion.getScene().getWindow();
            Scene scene = new Scene(root, 800, 600);
            stage.setScene(scene);
            stage.setTitle("MusicApp - Inicio de Sesión");
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("Error al cargar la ventana de login: " + e.getMessage());
        }
    }

    /**
     * Muestra un mensaje de error
     */
    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Muestra un mensaje de éxito
     */
    private void mostrarExito(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Muestra un mensaje de advertencia
     */
    private void mostrarAdvertencia(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advertencia");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}