package pe.edu.utp.appcasaforno.presentation.servlet;

import pe.edu.utp.appcasaforno.application.MesasServicio;
import pe.edu.utp.appcasaforno.infraestructure.web.ApiServlet;
import pe.edu.utp.appcasaforno.presentation.handler.mesas.ActualizarMesaHandler;
import pe.edu.utp.appcasaforno.presentation.handler.mesas.ListarMesasHandler;

import java.util.Map;

public class MesasServlet extends ApiServlet {

    public MesasServlet(MesasServicio mesasServicio) {
        super(
                Map.of("/", new ListarMesasHandler(mesasServicio)),
                Map.of("/{numero}", new ActualizarMesaHandler(mesasServicio)));
    }
}
