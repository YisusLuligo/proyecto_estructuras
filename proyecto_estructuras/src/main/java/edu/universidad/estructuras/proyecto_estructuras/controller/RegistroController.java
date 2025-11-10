package edu.universidad.estructuras.proyecto_estructuras.controller;

import edu.universidad.estructuras.proyecto_estructuras.Main;
import edu.universidad.estructuras.proyecto_estructuras.exception.AutenticacionException;
import edu.universidad.estructuras.proyecto_estructuras.service.AutenticacionService;
import edu.universidad.estructuras.proyecto_estructuras.service.SocialService;
import edu.universidad.estructuras.proyecto_estructuras.util.Constants;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * Controlador para registro de usuarios.
 * RF-001: Registrarse
 */
public class RegistroController {

    @FXML private Button backButton;
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button createAccountButton;
    @FXML private Hyperlink loginLink;
    @FXML private Button togglePasswordButton;
    @FXML private Button toggleConfirmPasswordButton;

    private AutenticacionService autenticacionService;
    private SocialService socialService;

    @FXML
    public void initialize() {
        autenticacionService = AutenticacionService.obtenerInstancia();
        socialService = SocialService.obtenerInstancia();
    }

    @FXML
    void handleBack(ActionEvent event) {
        try {
            Main.cambiarVista("login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleCreateAccount(ActionEvent event) {
        String nombre = fullNameField.getText().trim();
        String correo = emailField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Validar que las contraseñas coincidan
        if (!password.equals(confirmPassword)) {
            mostrarAlerta(Alert.AlertType.ERROR,
                    "Error",
                    Constants.MSG_PASSWORDS_NO_COINCIDEN);
            return;
        }

        try {
            var usuario = autenticacionService.registrar(username, password, nombre, correo);

            // Agregar al grafo social
            socialService.agregarUsuario(usuario);

            mostrarAlerta(Alert.AlertType.INFORMATION,
                    "¡Éxito!",
                    Constants.MSG_REGISTRO_EXITOSO);

            // Volver al login
            Main.cambiarVista("login");

        } catch (AutenticacionException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", e.getMessage());
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handleGoToLogin(ActionEvent event) {
        try {
            Main.cambiarVista("login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleTogglePassword(ActionEvent event) {
        // Implementar mostrar/ocultar contraseña si deseas
    }

    @FXML
    void handleToggleConfirmPassword(ActionEvent event) {
        // Implementar mostrar/ocultar contraseña si deseas
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}