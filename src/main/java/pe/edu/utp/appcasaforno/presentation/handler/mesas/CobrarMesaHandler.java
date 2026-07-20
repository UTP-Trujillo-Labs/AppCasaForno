package pe.edu.utp.appcasaforno.presentation.handler.mesas;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pe.edu.utp.appcasaforno.application.MesasServicio;
import pe.edu.utp.appcasaforno.infraestructure.util.JsonUtil;
import pe.edu.utp.appcasaforno.infraestructure.web.ApiHandler;

import java.io.IOException;

public class CobrarMesaHandler implements ApiHandler {

    private final MesasServicio mesasServicio;

    public CobrarMesaHandler(MesasServicio mesasServicio) {
        this.mesasServicio = mesasServicio;
    }

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int idMesa = parseIdMesa(JsonUtil.normalizePath(req.getPathInfo()));
        JsonUtil.write(resp, mesasServicio.cobrarMesa(idMesa));
    }

    private int parseIdMesa(String path) {
        if (path == null || !path.matches("/\\d+/pagar")) {
            throw new IllegalArgumentException("Número de mesa inválido.");
        }
        String numero = path.substring(1, path.indexOf('/', 1));
        return Integer.parseInt(numero);
    }
}
