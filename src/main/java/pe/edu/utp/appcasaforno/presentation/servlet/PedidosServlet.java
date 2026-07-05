package pe.edu.utp.appcasaforno.presentation.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pe.edu.utp.appcasaforno.application.PedidosService;
import pe.edu.utp.appcasaforno.domain.model.EnvioCocinaRequest;
import pe.edu.utp.appcasaforno.domain.model.EnvioCocinaResponse;
import pe.edu.utp.appcasaforno.domain.model.EstadoPedido;
import pe.edu.utp.appcasaforno.infraestructure.util.JsonUtil;

import java.io.IOException;

public class PedidosServlet extends HttpServlet {

    private final PedidosService pedidosService = new PedidosService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JsonUtil.prepareJsonResponse(resp);

        String path = JsonUtil.normalizePath(req.getPathInfo());
        switch (path) {
            case "/productos" -> JsonUtil.write(resp, pedidosService.listarProductos(
                    req.getParameter("categoria"),
                    req.getParameter("busqueda")));
            case "/pendientes" -> JsonUtil.write(resp,
                    pedidosService.listarPedidosPorEstado(EstadoPedido.PENDIENTE));
            case "/completados" -> JsonUtil.write(resp,
                    pedidosService.listarPedidosPorEstado(EstadoPedido.COMPLETADO));
            case "/cocina" -> JsonUtil.write(resp, pedidosService.listarTicketsPendientes());
            default -> JsonUtil.writeError(resp, HttpServletResponse.SC_NOT_FOUND, "Ruta no encontrada.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JsonUtil.prepareJsonResponse(resp);

        String path = JsonUtil.normalizePath(req.getPathInfo());
        if (!"/cocina".equals(path)) {
            JsonUtil.writeError(resp, HttpServletResponse.SC_NOT_FOUND, "Ruta no encontrada.");
            return;
        }

        try {
            EnvioCocinaRequest request = JsonUtil.read(req, EnvioCocinaRequest.class);
            EnvioCocinaResponse response = EnvioCocinaResponse.from(pedidosService.enviarACocina(request));

            resp.setStatus(HttpServletResponse.SC_CREATED);
            JsonUtil.write(resp, response);
        } catch (IllegalArgumentException ex) {
            JsonUtil.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
        }
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        JsonUtil.prepareJsonResponse(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
