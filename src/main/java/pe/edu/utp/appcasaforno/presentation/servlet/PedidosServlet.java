package pe.edu.utp.appcasaforno.presentation.servlet;

import pe.edu.utp.appcasaforno.application.PedidosService;
import pe.edu.utp.appcasaforno.infraestructure.web.ApiHandler;
import pe.edu.utp.appcasaforno.infraestructure.web.ApiServlet;
import pe.edu.utp.appcasaforno.presentation.handler.pedidos.*;

import java.util.Map;

public class PedidosServlet extends ApiServlet {

    public PedidosServlet(PedidosService pedidosService) {
        Map<String, ApiHandler> getHandlers = Map.of(
                "/productos", new ListarProductosHandler(pedidosService),
                "/pendientes", new ListarPedidosPendientesHandler(pedidosService),
                "/completados", new ListarPedidosCompletadosHandler(pedidosService),
                "/cocina", new ListarCocinaHandler(pedidosService),
                "/mesa/{numero}", new ListarPedidosPorMesaHandler(pedidosService));
        Map<String, ApiHandler> postHandlers = Map.of(
                "/cocina", new EnviarCocinaHandler(pedidosService),
                "/mesa/{numero}/pagar", new CobrarMesaHandler(pedidosService));

        super(getHandlers, postHandlers);
    }
}
