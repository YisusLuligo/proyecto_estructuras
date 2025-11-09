package edu.universidad.estructuras.proyecto_estructuras;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class RecuperarPasswordController {

    @FXML
    private Button backButton;

    @FXML
    private Button backToLoginButton;

    @FXML
    private Label confirmationMessage1;

    @FXML
    private VBox confirmationView;

    @FXML
    private Hyperlink loginLink;

    @FXML
    private VBox requestPasswordView;

    @FXML
    private Button sendInstructionsButton;

    @FXML
    private TextField usernameField;

    @FXML
    private Label usernameLabel;

    @FXML
    void handleBack(ActionEvent event) throws IOException {
        HelloApplication.setRoot("login");
    }

    @FXML
    void handleBackToLogin(ActionEvent event) throws IOException {
        HelloApplication.setRoot("login");
    }

    @FXML
    void handleSendInstructions(ActionEvent event) {

    }

}
