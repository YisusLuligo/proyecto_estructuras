module edu.universidad.estructuras.proyecto_estructuras {
    // Módulos de JavaFX requeridos
    requires javafx.controls;
    requires javafx.fxml;

    // Módulo de Java para seguridad (hashing de contraseñas)
    requires java.desktop;

    // Abrir paquetes para JavaFX
    opens edu.universidad.estructuras.proyecto_estructuras to javafx.fxml;
    exports edu.universidad.estructuras.proyecto_estructuras;

    // Modelo
    opens edu.universidad.estructuras.proyecto_estructuras.model to javafx.fxml;
    exports edu.universidad.estructuras.proyecto_estructuras.model;

    // Controladores
    opens edu.universidad.estructuras.proyecto_estructuras.controller to javafx.fxml;
    exports edu.universidad.estructuras.proyecto_estructuras.controller;

    // Repository
    opens edu.universidad.estructuras.proyecto_estructuras.repository to javafx.fxml;
    exports edu.universidad.estructuras.proyecto_estructuras.repository;

    // Service
    opens edu.universidad.estructuras.proyecto_estructuras.service to javafx.fxml;
    exports edu.universidad.estructuras.proyecto_estructuras.service;

    // Algoritmos
    opens edu.universidad.estructuras.proyecto_estructuras.algoritmos to javafx.fxml;
    exports edu.universidad.estructuras.proyecto_estructuras.algoritmos;

    // Utilidades
    opens edu.universidad.estructuras.proyecto_estructuras.util to javafx.fxml;
    exports edu.universidad.estructuras.proyecto_estructuras.util;

    // Excepciones
    exports edu.universidad.estructuras.proyecto_estructuras.exception;
}