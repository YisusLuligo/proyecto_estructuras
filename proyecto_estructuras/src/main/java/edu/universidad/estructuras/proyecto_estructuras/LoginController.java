package edu.universidad.estructuras.proyecto_estructuras;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private Hyperlink forgotPasswordLink;

    @FXML
    private Button loginButton;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Hyperlink registerLink;

    @FXML
    private Button togglePasswordButton;

    @FXML
    void handleForgot(ActionEvent event) throws IOException {
        HelloApplication.setRoot("recuperacion");

    }

    @FXML
    void handleLogin(ActionEvent event) {
    }

    @FXML
    void handleRegister(ActionEvent event) throws IOException {
        HelloApplication.setRoot("registro");
    }

}
