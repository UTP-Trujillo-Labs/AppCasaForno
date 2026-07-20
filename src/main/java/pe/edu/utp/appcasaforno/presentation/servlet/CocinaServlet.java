package pe.edu.utp.appcasaforno.presentation.servlet;

import pe.edu.utp.appcasaforno.application.CocinaServicio;
import pe.edu.utp.appcasaforno.infraestructure.web.ApiServlet;
import pe.edu.utp.appcasaforno.presentation.handler.cocina.DespacharPedidoHandler;
import pe.edu.utp.appcasaforno.presentation.handler.cocina.EnviarCocinaHandler;
import pe.edu.utp.appcasaforno.presentation.handler.cocina.ListarCocinaHandler;

import java.util.Map;

public class CocinaServlet extends ApiServlet {

    public CocinaServlet(CocinaServicio cocinaServicio) {
        super(
                Map.of("/", new ListarCocinaHandler(cocinaServicio)),
                Map.of(
                        "/", new EnviarCocinaHandler(cocinaServicio),
                        "/{ticket}/despachar", new DespacharPedidoHandler(cocinaServicio)));
    }
}
