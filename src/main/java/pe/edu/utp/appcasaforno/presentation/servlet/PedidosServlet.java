package pe.edu.utp.appcasaforno.presentation.servlet;

import pe.edu.utp.appcasaforno.application.PedidosService;
import pe.edu.utp.appcasaforno.infraestructure.web.ApiServlet;
import pe.edu.utp.appcasaforno.presentation.handler.pedidos.CobrarMesaHandler;
import pe.edu.utp.appcasaforno.presentation.handler.pedidos.ListarPedidosCompletadosHandler;
import pe.edu.utp.appcasaforno.presentation.handler.pedidos.ListarPedidosPorMesaHandler;

import java.util.Map;

public class PedidosServlet extends ApiServlet {

    public PedidosServlet(PedidosService pedidosService) {
        super(
                Map.of(
                        "/completados", new ListarPedidosCompletadosHandler(pedidosService),
                        "/mesa/{numero}", new ListarPedidosPorMesaHandler(pedidosService)),
                Map.of(
                        "/mesa/{numero}/pagar", new CobrarMesaHandler(pedidosService)));
    }
}
