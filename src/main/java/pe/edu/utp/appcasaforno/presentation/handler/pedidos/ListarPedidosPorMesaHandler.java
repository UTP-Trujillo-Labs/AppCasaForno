package pe.edu.utp.appcasaforno.presentation.handler.pedidos;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pe.edu.utp.appcasaforno.application.PedidosService;
import pe.edu.utp.appcasaforno.infraestructure.util.JsonUtil;
import pe.edu.utp.appcasaforno.infraestructure.web.ApiHandler;

import java.io.IOException;

public class ListarPedidosPorMesaHandler implements ApiHandler {

    private final PedidosService pedidosService;

    public ListarPedidosPorMesaHandler(PedidosService pedidosService) {
        this.pedidosService = pedidosService;
    }

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int idMesa = parseIdMesa(JsonUtil.normalizePath(req.getPathInfo()));
        JsonUtil.write(resp, pedidosService.listarPedidosPorMesa(idMesa));
    }

    private int parseIdMesa(String path) {
        if (path == null || !path.matches("/mesa/\\d+")) {
            throw new IllegalArgumentException("Número de mesa inválido.");
        }
        return Integer.parseInt(path.substring("/mesa/".length()));
    }
}
