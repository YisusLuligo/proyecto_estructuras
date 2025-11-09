module edu.universidad.estructuras.proyecto_estructuras {
    requires javafx.controls;
    requires javafx.fxml;


    opens edu.universidad.estructuras.proyecto_estructuras to javafx.fxml;
    exports edu.universidad.estructuras.proyecto_estructuras;
    opens edu.universidad.estructuras.proyecto_estructuras.model;
    exports edu.universidad.estructuras.proyecto_estructuras.model;
    opens edu.universidad.estructuras.proyecto_estructuras.viewController;
    exports edu.universidad.estructuras.proyecto_estructuras.viewController;
}