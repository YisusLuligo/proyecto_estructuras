package edu.universidad.estructuras.proyecto_estructuras;

import edu.universidad.estructuras.proyecto_estructuras.model.Cancion;
import edu.universidad.estructuras.proyecto_estructuras.repository.CancionRepository;
import edu.universidad.estructuras.proyecto_estructuras.service.RecomendacionService;
import edu.universidad.estructuras.proyecto_estructuras.util.CSVImporter;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.List;

/**
 * Clase principal de SyncUp - Motor de Recomendaciones Musicales.
 * Inicializa el sistema y carga la interfaz gráfica.
 *
 * @author Tu Nombre
 * @version 1.0
 */
public class Main extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws Exception {
        // Cargar datos iniciales
        System.out.println("═══════════════════════════════════════");
        System.out.println("    🎵 SYNCUP - INICIANDO SISTEMA 🎵    ");
        System.out.println("═══════════════════════════════════════");

        inicializarDatos();

        // Cargar vista de login
        scene = new Scene(cargarFXML("login"), 641, 1007);

        stage.setTitle("SyncUp - Motor de Recomendaciones Musicales");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        System.out.println("✓ Sistema iniciado correctamente");
        System.out.println("═══════════════════════════════════════\n");
    }

    /**
     * Cambia la vista actual por otra.
     *
     * @param fxml Nombre del archivo FXML (sin extensión)
     * @throws Exception Si no se puede cargar el archivo
     */
    public static void cambiarVista(String fxml) throws Exception {
        scene.setRoot(cargarFXML(fxml));
    }

    /**
     * Carga un archivo FXML.
     *
     * @param fxml Nombre del archivo (sin extensión)
     * @return Parent con la vista cargada
     * @throws Exception Si no se puede cargar
     */
    private static Parent cargarFXML(String fxml) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                Main.class.getResource(fxml + ".fxml")
        );
        return loader.load();
    }

    /**
     * Inicializa los datos del sistema:
     * - Carga catálogo de canciones
     * - Construye grafo de similitud
     * - Crea usuarios de prueba
     */
    private void inicializarDatos() {
        try {
            System.out.println("📂 Cargando catálogo de canciones...");

            // Cargar canciones desde archivo
            String rutaCatalogo = getClass()
                    .getResource("data/canciones.txt")
                    .getPath();

            List<Cancion> canciones = CSVImporter.importarCanciones(rutaCatalogo);

            // Guardar en repositorio
            CancionRepository cancionRepo = CancionRepository.obtenerInstancia();
            for (Cancion cancion : canciones) {
                cancionRepo.guardar(cancion);
            }

            System.out.println("✓ Catálogo cargado: " + canciones.size() + " canciones");

            // Construir grafo de similitud
            System.out.println("🔗 Construyendo grafo de similitud...");
            RecomendacionService recomendacionService =
                    RecomendacionService.obtenerInstancia();
            recomendacionService.construirGrafoDeSimilitud(canciones);
            System.out.println("✓ Grafo de similitud construido");

        } catch (Exception e) {
            System.err.println("❌ Error cargando datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}