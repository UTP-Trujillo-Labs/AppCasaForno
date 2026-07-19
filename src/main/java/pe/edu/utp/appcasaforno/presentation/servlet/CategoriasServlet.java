package pe.edu.utp.appcasaforno.presentation.servlet;

import pe.edu.utp.appcasaforno.application.CategoriaServicio;
import pe.edu.utp.appcasaforno.infraestructure.web.ApiServlet;
import pe.edu.utp.appcasaforno.presentation.handler.categorias.ListarCategoriasHandler;

import java.util.Collections;
import java.util.Map;

public class CategoriasServlet extends ApiServlet {

    public CategoriasServlet(CategoriaServicio categoriaServicio) {
        super(
                Map.of("/", new ListarCategoriasHandler(categoriaServicio)),
                Collections.emptyMap());
    }
}
