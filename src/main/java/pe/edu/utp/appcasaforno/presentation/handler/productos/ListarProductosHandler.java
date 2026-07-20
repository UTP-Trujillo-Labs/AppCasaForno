package pe.edu.utp.appcasaforno.presentation.handler.productos;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pe.edu.utp.appcasaforno.application.ProductService;
import pe.edu.utp.appcasaforno.infraestructure.util.JsonUtil;
import pe.edu.utp.appcasaforno.infraestructure.web.ApiHandler;

import java.io.IOException;

public class ListarProductosHandler implements ApiHandler {

    private final ProductService productService;

    public ListarProductosHandler(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JsonUtil.write(resp, productService.listarProductos(
                req.getParameter("categoria"),
                req.getParameter("busqueda")));
    }
}
