package pe.edu.utp.appcasaforno.presentation.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pe.edu.utp.appcasaforno.application.CategoriaServicio;
import pe.edu.utp.appcasaforno.infraestructure.util.JsonUtil;

import java.io.IOException;

public class CategoriasServlet extends HttpServlet {
    private final CategoriaServicio categoriaServicio;

    public CategoriasServlet() {
        this.categoriaServicio = new CategoriaServicio();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonUtil.prepareJsonResponse(resp);

        String path = JsonUtil.normalizePath(req.getPathInfo());
        switch (path) {
            case "/" -> JsonUtil.write(resp, categoriaServicio.listarCategorias());
            default -> JsonUtil.writeError(resp, HttpServletResponse.SC_NOT_FOUND, "Ruta no encontrada.");
        }
    }
}
