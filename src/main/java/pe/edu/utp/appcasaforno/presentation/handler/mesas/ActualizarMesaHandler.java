package pe.edu.utp.appcasaforno.presentation.handler.mesas;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pe.edu.utp.appcasaforno.application.MesasServicio;
import pe.edu.utp.appcasaforno.domain.model.ActualizarMesaRequest;
import pe.edu.utp.appcasaforno.infraestructure.util.JsonUtil;
import pe.edu.utp.appcasaforno.infraestructure.web.ApiHandler;

import java.io.IOException;

public class ActualizarMesaHandler implements ApiHandler {

    private final MesasServicio mesasServicio;

    public ActualizarMesaHandler(MesasServicio mesasServicio) {
        this.mesasServicio = mesasServicio;
    }

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int numero = parseNumeroMesa(JsonUtil.normalizePath(req.getPathInfo()));
        ActualizarMesaRequest request = JsonUtil.read(req, ActualizarMesaRequest.class);
        JsonUtil.write(resp, mesasServicio.actualizarEstado(numero, request.estado()));
    }

    private int parseNumeroMesa(String path) {
        if (path == null || !path.matches("/\\d+")) {
            throw new IllegalArgumentException("Número de mesa inválido.");
        }
        return Integer.parseInt(path.substring(1));
    }
}
