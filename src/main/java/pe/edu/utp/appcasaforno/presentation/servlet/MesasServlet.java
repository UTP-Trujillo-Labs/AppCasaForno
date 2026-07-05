package pe.edu.utp.appcasaforno.presentation.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pe.edu.utp.appcasaforno.application.MesasServicio;
import pe.edu.utp.appcasaforno.domain.model.ActualizarMesaRequest;
import pe.edu.utp.appcasaforno.infraestructure.util.JsonUtil;

import java.io.IOException;

public class MesasServlet extends HttpServlet {

    private final MesasServicio mesasServicio = new MesasServicio();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JsonUtil.prepareJsonResponse(resp);

        String path = JsonUtil.normalizePath(req.getPathInfo());
        if ("/".equals(path)) {
            JsonUtil.write(resp, mesasServicio.listar());
            return;
        }

        JsonUtil.writeError(resp, HttpServletResponse.SC_NOT_FOUND, "Ruta no encontrada.");
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JsonUtil.prepareJsonResponse(resp);

        try {
            int numero = parseNumeroMesa(JsonUtil.normalizePath(req.getPathInfo()));
            ActualizarMesaRequest request = JsonUtil.read(req, ActualizarMesaRequest.class);
            JsonUtil.write(resp, mesasServicio.actualizarEstado(numero, request.estado()));
        } catch (IllegalArgumentException ex) {
            JsonUtil.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
        }
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        JsonUtil.prepareJsonResponse(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    private int parseNumeroMesa(String path) {
        if (path == null || !path.matches("/\\d+")) {
            throw new IllegalArgumentException("Número de mesa inválido.");
        }
        return Integer.parseInt(path.substring(1));
    }
}
