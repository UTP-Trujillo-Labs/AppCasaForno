package pe.edu.utp.appcasaforno.presentation.handler.pedidos;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pe.edu.utp.appcasaforno.application.PedidosService;
import pe.edu.utp.appcasaforno.infraestructure.util.JsonUtil;
import pe.edu.utp.appcasaforno.infraestructure.web.ApiHandler;

import java.io.IOException;

public class ListarProductosHandler implements ApiHandler {

    private final PedidosService pedidosService;

    public ListarProductosHandler(PedidosService pedidosService) {
        this.pedidosService = pedidosService;
    }

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JsonUtil.write(resp, pedidosService.listarProductos(
                req.getParameter("categoria"),
                req.getParameter("busqueda")));
    }
}
