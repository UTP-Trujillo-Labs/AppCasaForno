package pe.edu.utp.appcasaforno.presentation.servlet;

import pe.edu.utp.appcasaforno.application.CategoriaServicio;
import pe.edu.utp.appcasaforno.infraestructure.web.ApiHandler;
import pe.edu.utp.appcasaforno.infraestructure.web.ApiServlet;
import pe.edu.utp.appcasaforno.presentation.handler.categorias.ListarCategoriasHandler;

import java.util.HashMap;
import java.util.Map;

public class CategoriasServlet extends ApiServlet {

    public CategoriasServlet(CategoriaServicio categoriaServicio) {
        Map<String, ApiHandler> getHandlers = Map.of("/", new ListarCategoriasHandler(categoriaServicio));
        Map<String, ApiHandler> postHandlers = new HashMap<>();
        super(getHandlers, postHandlers);
    }
}
