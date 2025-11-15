module edu.universidad.estructuras.proyecto_estructura {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;  // ⭐ AGREGAR ESTA LÍNEA
    requires java.desktop;
    opens edu.universidad.estructuras.proyecto_estructura to javafx.fxml;
    exports edu.universidad.estructuras.proyecto_estructura;

    opens edu.universidad.estructuras.proyecto_estructura.model;
    exports edu.universidad.estructuras.proyecto_estructura.model;

    opens edu.universidad.estructuras.proyecto_estructura.service;
    exports edu.universidad.estructuras.proyecto_estructura.service;

    opens edu.universidad.estructuras.proyecto_estructura.controller to javafx.fxml;
    exports edu.universidad.estructuras.proyecto_estructura.controller;

    opens edu.universidad.estructuras.proyecto_estructura.utils;
    exports edu.universidad.estructuras.proyecto_estructura.utils;
}