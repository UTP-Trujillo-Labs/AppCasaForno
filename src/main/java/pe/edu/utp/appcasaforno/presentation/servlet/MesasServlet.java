package pe.edu.utp.appcasaforno.presentation.servlet;

import pe.edu.utp.appcasaforno.application.MesasServicio;
import pe.edu.utp.appcasaforno.infraestructure.web.ApiHandler;
import pe.edu.utp.appcasaforno.infraestructure.web.ApiServlet;
import pe.edu.utp.appcasaforno.presentation.handler.mesas.ActualizarMesaHandler;
import pe.edu.utp.appcasaforno.presentation.handler.mesas.ListarMesasHandler;

import java.util.Map;

public class MesasServlet extends ApiServlet {

    public MesasServlet(MesasServicio mesasServicio) {
        Map<String, ApiHandler> getHandlers = Map.of("/", new ListarMesasHandler(mesasServicio));
        Map<String, ApiHandler> postHandlers = Map.of("/cocina", new ActualizarMesaHandler(mesasServicio));

        super(getHandlers, postHandlers);
    }
}
