package edu.universidad.estructuras.proyecto_estructuras;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class RegistroController {
    @FXML
    private Button backButton;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Button createAccountButton;

    @FXML
    private TextField emailField;

    @FXML
    private TextField fullNameField;

    @FXML
    private Hyperlink loginLink;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button toggleConfirmPasswordButton;

    @FXML
    private Button togglePasswordButton;

    @FXML
    private TextField usernameField;

    @FXML
    void handleBack(ActionEvent event) throws IOException {
        HelloApplication.setRoot("login");
    }

    @FXML
    void handleCreateAccount(ActionEvent event) {

    }

    @FXML
    void handleGoToLogin(ActionEvent event) throws IOException {
        HelloApplication.setRoot("login");
    }

    @FXML
    void handleToggleConfirmPassword(ActionEvent event) {

    }

    @FXML
    void handleTogglePassword(ActionEvent event) {

    }

}
