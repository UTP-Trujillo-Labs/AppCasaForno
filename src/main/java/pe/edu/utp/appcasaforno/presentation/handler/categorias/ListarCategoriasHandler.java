package pe.edu.utp.appcasaforno.presentation.handler.categorias;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pe.edu.utp.appcasaforno.application.CategoriaServicio;
import pe.edu.utp.appcasaforno.infraestructure.util.JsonUtil;
import pe.edu.utp.appcasaforno.infraestructure.web.ApiHandler;

import java.io.IOException;

public class ListarCategoriasHandler implements ApiHandler {

    private final CategoriaServicio categoriaServicio;

    public ListarCategoriasHandler(CategoriaServicio categoriaServicio) {
        this.categoriaServicio = categoriaServicio;
    }

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JsonUtil.write(resp, categoriaServicio.listarCategorias());
    }
}
