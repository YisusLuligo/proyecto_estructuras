package edu.universidad.estructuras.proyecto_estructura;

import edu.universidad.estructuras.proyecto_estructura.service.PersistenciaService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Clase principal de la aplicación SyncUp
 *
 * @author SyncUp Team
 * @version 1.0
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║   SyncUp - Sistema de Música          ║");
            System.out.println("╚════════════════════════════════════════╝\n");

            // ✨ CARGAR DATOS AL INICIAR
            PersistenciaService.getInstance().cargarTodo();

            // RUTA CORREGIDA: Usar la ruta absoluta con /
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();

            // Configurar escena
            Scene scene = new Scene(root, 800, 600);

            // Configurar stage
            primaryStage.setTitle("SyncUp - Motor de Recomendaciones Musicales");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);

            // ✨ GUARDAR DATOS AL CERRAR
            primaryStage.setOnCloseRequest(event -> {
                System.out.println("\n=== Cerrando aplicación ===");
                PersistenciaService.getInstance().guardarTodo();
                System.out.println("=== Aplicación cerrada ===\n");
            });

            primaryStage.show();
            System.out.println("\n✓ Aplicación iniciada correctamente\n");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("✗ Error al iniciar la aplicación: " + e.getMessage());
        }
    }

    @Override
    public void stop() throws Exception {
        // ✨ GUARDAR DATOS AL CERRAR LA APLICACIÓN
        System.out.println("\n=== Guardando datos finales ===");
        PersistenciaService.getInstance().guardarTodo();
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}