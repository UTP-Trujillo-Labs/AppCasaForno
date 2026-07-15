package pe.edu.utp.appcasaforno.presentation.servlet;

import pe.edu.utp.appcasaforno.application.PedidosService;
import pe.edu.utp.appcasaforno.infraestructure.web.ApiHandler;
import pe.edu.utp.appcasaforno.infraestructure.web.ApiServlet;
import pe.edu.utp.appcasaforno.presentation.handler.cocina.EnviarCocinaHandler;
import pe.edu.utp.appcasaforno.presentation.handler.cocina.ListarCocinaHandler;

import java.util.Map;

public class CocinaServlet extends ApiServlet {

    public CocinaServlet(PedidosService pedidosService) {
        Map<String, ApiHandler> getHandlers = Map.of("/", new ListarCocinaHandler(pedidosService));
        Map<String, ApiHandler> postHandlers = Map.of("/", new EnviarCocinaHandler(pedidosService));

        super(getHandlers, postHandlers);
    }
}
