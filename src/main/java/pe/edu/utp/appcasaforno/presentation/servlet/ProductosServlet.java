package pe.edu.utp.appcasaforno.presentation.servlet;

import pe.edu.utp.appcasaforno.application.ProductService;
import pe.edu.utp.appcasaforno.infraestructure.web.ApiServlet;
import pe.edu.utp.appcasaforno.presentation.handler.productos.ListarProductosHandler;

import java.util.Collections;
import java.util.Map;

public class ProductosServlet extends ApiServlet {

    public ProductosServlet(ProductService productService) {
        super(
                Map.of("/", new ListarProductosHandler(productService)),
                Collections.emptyMap());
    }
}
