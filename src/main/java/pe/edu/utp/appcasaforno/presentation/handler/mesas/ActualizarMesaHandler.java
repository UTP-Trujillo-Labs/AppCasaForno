package pe.edu.utp.appcasaforno.presentation.handler.mesas;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pe.edu.utp.appcasaforno.application.MesasServicio;
import pe.edu.utp.appcasaforno.infraestructure.util.JsonUtil;
import pe.edu.utp.appcasaforno.infraestructure.web.ApiHandler;

import java.io.IOException;

/**
 * Avanza el estado de la mesa según las reglas del patrón State.
 * No recibe el estado destino: el backend decide la transición.
 */
public class ActualizarMesaHandler implements ApiHandler {

    private final MesasServicio mesasServicio;

    public ActualizarMesaHandler(MesasServicio mesasServicio) {
        this.mesasServicio = mesasServicio;
    }

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int numero = parseNumeroMesa(JsonUtil.normalizePath(req.getPathInfo()));
        JsonUtil.write(resp, mesasServicio.avanzarEstado(numero));
    }

    private int parseNumeroMesa(String path) {
        if (path == null || !path.matches("/\\d+")) {
            throw new IllegalArgumentException("Número de mesa inválido.");
        }
        return Integer.parseInt(path.substring(1));
    }
}
