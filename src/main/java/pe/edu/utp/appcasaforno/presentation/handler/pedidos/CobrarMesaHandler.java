package pe.edu.utp.appcasaforno.presentation.handler.pedidos;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pe.edu.utp.appcasaforno.application.PedidosService;
import pe.edu.utp.appcasaforno.infraestructure.util.JsonUtil;
import pe.edu.utp.appcasaforno.infraestructure.web.ApiHandler;

import java.io.IOException;

public class CobrarMesaHandler implements ApiHandler {

    private final PedidosService pedidosService;

    public CobrarMesaHandler(PedidosService pedidosService) {
        this.pedidosService = pedidosService;
    }

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int idMesa = parseIdMesa(JsonUtil.normalizePath(req.getPathInfo()));
        JsonUtil.write(resp, pedidosService.cobrarMesa(idMesa));
    }

    private int parseIdMesa(String path) {
        if (path == null || !path.matches("/mesa/\\d+/pagar")) {
            throw new IllegalArgumentException("Número de mesa inválido.");
        }
        String numero = path.substring("/mesa/".length(), path.length() - "/pagar".length());
        return Integer.parseInt(numero);
    }
}
