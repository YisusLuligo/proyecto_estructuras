package edu.universidad.estructuras.proyecto_estructuras.viewController;




import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

    public class LoginViewController {

        @FXML
        private ResourceBundle resources;

        @FXML
        private URL location;

        @FXML
        private Button btnIngresar;

        @FXML
        private Label lbErrorIn;

        @FXML
        private PasswordField txtContrasena;

        @FXML
        private TextField txtNombreUsuario;

        @FXML
        void solicitarIngreso(ActionEvent event) {

        }

        @FXML
        void initialize() {
            assert btnIngresar != null : "fx:id=\"btnIngresar\" was not injected: check your FXML file 'login.fxml'.";
            assert lbErrorIn != null : "fx:id=\"lbErrorIn\" was not injected: check your FXML file 'login.fxml'.";
            assert txtContrasena != null : "fx:id=\"txtContrasena\" was not injected: check your FXML file 'login.fxml'.";
            assert txtNombreUsuario != null : "fx:id=\"txtNombreUsuario\" was not injected: check your FXML file 'login.fxml'.";

        }

    }


