package pe.edu.utp.appcasaforno.infraestructure.web;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pe.edu.utp.appcasaforno.infraestructure.util.JsonUtil;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

public class ApiServlet extends HttpServlet {

    private final Map<String, ApiHandler> getHandlers;
    private final Map<String, ApiHandler> postHandlers;

    protected ApiServlet(Map<String, ApiHandler> getHandlers,
                         Map<String, ApiHandler> postHandlers) {
        this.getHandlers = getHandlers != null ? getHandlers : Collections.emptyMap();
        this.postHandlers = postHandlers != null ? postHandlers : Collections.emptyMap();
    }

    @Override
    protected final void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        dispatch(req, resp, getHandlers);
    }

    @Override
    protected final void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        dispatch(req, resp, postHandlers);
    }

    @Override
    protected final void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        JsonUtil.prepareJsonResponse(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    private void dispatch(HttpServletRequest req, HttpServletResponse resp,
                        Map<String, ApiHandler> handlers) throws IOException {
        JsonUtil.prepareJsonResponse(resp);
        try {
            String path = JsonUtil.normalizePath(req.getPathInfo());
            ApiHandler handler = resolveHandler(handlers, path);
            if (handler == null) {
                JsonUtil.writeError(resp, HttpServletResponse.SC_NOT_FOUND, "Ruta no encontrada.");
                return;
            }
            handler.handle(req, resp);
        } catch (IllegalArgumentException ex) {
            JsonUtil.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
        }
    }

    private ApiHandler resolveHandler(Map<String, ApiHandler> handlers, String path) {
        ApiHandler handler = handlers.get(path);
        if (handler != null) {
            return handler;
        }
        if (path == null) {
            return null;
        }
        if (path.matches("/\\d+/despachar")) {
            return handlers.get("/{ticket}/despachar");
        }
        if (path.matches("/\\d+/pagar")) {
            return handlers.get("/{numero}/pagar");
        }
        if (path.matches("/\\d+")) {
            return handlers.get("/{numero}");
        }
        if (path.matches("/mesa/\\d+")) {
            return handlers.get("/mesa/{numero}");
        }
        return null;
    }
}
