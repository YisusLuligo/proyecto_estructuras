package edu.universidad.estructuras.proyecto_estructura.controller;


import edu.universidad.estructuras.proyecto_estructura.model.Usuario;
import edu.universidad.estructuras.proyecto_estructura.service.UsuarioService;
import edu.universidad.estructuras.proyecto_estructura.utils.Validaciones;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controlador para la vista de Login
 *
 * @author SyncUp Team
 * @version 1.0
 */
public class LoginController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblError;
    @FXML private Button btnLogin;
    @FXML private Button btnRegistro;

    private UsuarioService usuarioService;

    /**
     * Inicializa el controlador
     */
    @FXML
    public void initialize() {
        usuarioService = UsuarioService.getInstance();
        lblError.setVisible(false);
    }

    /**
     * Maneja el evento de inicio de sesión
     */
    @FXML
    private void handleLogin() {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        // Validaciones
        if (!Validaciones.esTextoValido(username) || !Validaciones.esTextoValido(password)) {
            mostrarError("Por favor, complete todos los campos");
            return;
        }

        // Intentar iniciar sesión
        Usuario usuario = usuarioService.iniciarSesion(username, password);

        if (usuario != null) {
            abrirVentanaPrincipal();
        } else {
            mostrarError("Usuario o contraseña incorrectos");
        }
    }

    /**
     * Maneja el evento de registro
     */
    @FXML
    private void handleRegistro() {
        // Crear diálogo de registro
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Registrar Usuario");
        dialog.setHeaderText("Crear una nueva cuenta");

        // Configurar botones
        ButtonType btnRegistrar = new ButtonType("Registrar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnRegistrar, ButtonType.CANCEL);

        // Crear formulario
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField txtNewUsername = new TextField();
        txtNewUsername.setPromptText("Username");
        PasswordField txtNewPassword = new PasswordField();
        txtNewPassword.setPromptText("Contraseña");
        TextField txtNewNombre = new TextField();
        txtNewNombre.setPromptText("Nombre completo");

        grid.add(new Label("Usuario:"), 0, 0);
        grid.add(txtNewUsername, 1, 0);
        grid.add(new Label("Contraseña:"), 0, 1);
        grid.add(txtNewPassword, 1, 1);
        grid.add(new Label("Nombre:"), 0, 2);
        grid.add(txtNewNombre, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Procesar resultado
        dialog.showAndWait().ifPresent(response -> {
            if (response == btnRegistrar) {
                String newUsername = txtNewUsername.getText();
                String newPassword = txtNewPassword.getText();
                String newNombre = txtNewNombre.getText();

                // Validaciones
                if (!Validaciones.esUsernameValido(newUsername)) {
                    mostrarError("Username inválido (3-20 caracteres, solo letras, números y _)");
                    return;
                }
                if (!Validaciones.esPasswordValido(newPassword)) {
                    mostrarError("La contraseña debe tener al menos 6 caracteres");
                    return;
                }
                if (!Validaciones.esTextoValido(newNombre)) {
                    mostrarError("Por favor, ingrese un nombre válido");
                    return;
                }

                // Intentar registrar
                if (usuarioService.registrarUsuario(newUsername, newPassword, newNombre)) {
                    mostrarExito("Usuario registrado exitosamente. Ahora puede iniciar sesión.");
                } else {
                    mostrarError("El username ya existe");
                }
            }
        });
    }

    /**
     * Abre la ventana principal
     */
    private void abrirVentanaPrincipal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("edu/universidad/estructuras/proyecto_estructura/main.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnLogin.getScene().getWindow();
            Scene scene = new Scene(root, 900, 600);
            stage.setScene(scene);
            stage.setTitle("SyncUp - Panel Principal");
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("Error al cargar la ventana principal");
        }
    }

    /**
     * Muestra un mensaje de error
     */
    private void mostrarError(String mensaje) {
        lblError.setText(mensaje);
        lblError.setVisible(true);
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
}