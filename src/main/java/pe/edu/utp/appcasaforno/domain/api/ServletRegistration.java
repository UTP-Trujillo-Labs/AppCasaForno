package pe.edu.utp.appcasaforno.domain.api;

import jakarta.servlet.http.HttpServlet;

/**
 * Descriptor de un servlet: nombre en el contexto Tomcat, patrón de URL e instancia.
 */
public record ServletRegistration(String nombre, String path, HttpServlet servlet) {
}
