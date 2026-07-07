package pe.edu.utp.appcasaforno.infraestructure.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Estrategia de manejo para una ruta de la API REST.
 */
@FunctionalInterface
public interface ApiHandler {

    void handle(HttpServletRequest req, HttpServletResponse resp) throws IOException;
}
