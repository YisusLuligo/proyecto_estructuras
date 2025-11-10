package edu.universidad.estructuras.proyecto_estructuras.controller;

import edu.universidad.estructuras.proyecto_estructuras.Main;
import edu.universidad.estructuras.proyecto_estructuras.exception.AutenticacionException;
import edu.universidad.estructuras.proyecto_estructuras.model.Usuario;
import edu.universidad.estructuras.proyecto_estructuras.service.AutenticacionService;
import edu.universidad.estructuras.proyecto_estructuras.util.Constants;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * Controlador para la vista de login.
 * RF-001: Iniciar sesión
 */
public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Hyperlink forgotPasswordLink;
    @FXML private Hyperlink registerLink;
    @FXML private Button togglePasswordButton;

    private AutenticacionService autenticacionService;

    @FXML
    public void initialize() {
        autenticacionService = AutenticacionService.obtenerInstancia();

        // Enter para login
        passwordField.setOnAction(event -> handleLogin(null));
    }

    @FXML
    void handleLogin(ActionEvent event) {
        String username = emailField.getText().trim();
        String password = passwordField.getText();

        try {
            Usuario usuario = autenticacionService.iniciarSesion(username, password);

            mostrarAlerta(Alert.AlertType.INFORMATION,
                    "¡Bienvenido!",
                    Constants.MSG_LOGIN_EXITOSO + " " + usuario.getNombre());

            // Redirigir según tipo de usuario
            if (usuario.esAdministrador()) {
                Main.cambiarVista("admin");
            } else {
                Main.cambiarVista("principal");
            }

        } catch (AutenticacionException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", e.getMessage());
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handleForgot(ActionEvent event) {
        try {
            Main.cambiarVista("recuperacion");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleRegister(ActionEvent event) {
        try {
            Main.cambiarVista("registro");
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