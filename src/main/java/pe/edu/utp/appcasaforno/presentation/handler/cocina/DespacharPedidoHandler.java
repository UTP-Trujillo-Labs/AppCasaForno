package pe.edu.utp.appcasaforno.presentation.handler.cocina;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pe.edu.utp.appcasaforno.application.PedidosService;
import pe.edu.utp.appcasaforno.infraestructure.util.JsonUtil;
import pe.edu.utp.appcasaforno.infraestructure.web.ApiHandler;

import java.io.IOException;

public class DespacharPedidoHandler implements ApiHandler {

    private final PedidosService pedidosService;

    public DespacharPedidoHandler(PedidosService pedidosService) {
        this.pedidosService = pedidosService;
    }

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int numeroTicket = parseTicket(JsonUtil.normalizePath(req.getPathInfo()));
        JsonUtil.write(resp, pedidosService.despacharPedido(numeroTicket));
    }

    private int parseTicket(String path) {
        if (path == null || !path.matches("/\\d+/despachar")) {
            throw new IllegalArgumentException("Número de ticket inválido.");
        }
        String numero = path.substring(1, path.indexOf('/', 1));
        return Integer.parseInt(numero);
    }
}
