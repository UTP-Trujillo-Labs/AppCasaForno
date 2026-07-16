package pe.edu.utp.appcasaforno.presentation.servlet;

import pe.edu.utp.appcasaforno.application.PedidosService;
import pe.edu.utp.appcasaforno.infraestructure.web.ApiServlet;
import pe.edu.utp.appcasaforno.presentation.handler.cocina.DespacharPedidoHandler;
import pe.edu.utp.appcasaforno.presentation.handler.cocina.EnviarCocinaHandler;
import pe.edu.utp.appcasaforno.presentation.handler.cocina.ListarCocinaHandler;

import java.util.Map;

public class CocinaServlet extends ApiServlet {

    public CocinaServlet(PedidosService pedidosService) {
        super(
                Map.of("/", new ListarCocinaHandler(pedidosService)),
                Map.of(
                        "/", new EnviarCocinaHandler(pedidosService),
                        "/{ticket}/despachar", new DespacharPedidoHandler(pedidosService)));
    }
}
