package edu.universidad.estructuras.proyecto_estructuras.controller;

import edu.universidad.estructuras.proyecto_estructuras.Main;
import edu.universidad.estructuras.proyecto_estructuras.exception.AutenticacionException;
import edu.universidad.estructuras.proyecto_estructuras.service.AutenticacionService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

/**
 * Controlador para recuperación de contraseña.
 */
public class RecuperarPasswordController {

    @FXML private Button backButton;
    @FXML private Button backToLoginButton;
    @FXML private Label confirmationMessage1;
    @FXML private VBox confirmationView;
    @FXML private Hyperlink loginLink;
    @FXML private VBox requestPasswordView;
    @FXML private Button sendInstructionsButton;
    @FXML private TextField usernameField;
    @FXML private Label usernameLabel;

    private AutenticacionService autenticacionService;

    @FXML
    public void initialize() {
        autenticacionService = AutenticacionService.obtenerInstancia();
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
    void handleBackToLogin(ActionEvent event) {
        try {
            Main.cambiarVista("login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleSendInstructions(ActionEvent event) {
        String username = usernameField.getText().trim();

        if (username.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING,
                    "Campo vacío",
                    "Ingresa tu nombre de usuario, parce");
            return;
        }

        try {
            String passwordTemporal = autenticacionService.recuperarPassword(username);

            // Mostrar vista de confirmación
            requestPasswordView.setVisible(false);
            confirmationView.setVisible(true);
            usernameLabel.setText(username);

            // En un sistema real, esto se enviaría por email
            System.out.println("Password temporal para " + username + ": " + passwordTemporal);

            mostrarAlerta(Alert.AlertType.INFORMATION,
                    "Contraseña temporal",
                    "Tu contraseña temporal es: " + passwordTemporal +
                            "\n\n(En un sistema real, esto se enviaría por correo)");

        } catch (AutenticacionException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", e.getMessage());
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