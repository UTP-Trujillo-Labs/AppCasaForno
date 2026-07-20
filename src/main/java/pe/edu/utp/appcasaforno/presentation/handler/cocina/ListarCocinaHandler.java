package pe.edu.utp.appcasaforno.presentation.handler.cocina;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pe.edu.utp.appcasaforno.application.CocinaServicio;
import pe.edu.utp.appcasaforno.infraestructure.util.JsonUtil;
import pe.edu.utp.appcasaforno.infraestructure.web.ApiHandler;

import java.io.IOException;

public class ListarCocinaHandler implements ApiHandler {

    private final CocinaServicio cocinaServicio;

    public ListarCocinaHandler(CocinaServicio cocinaServicio) {
        this.cocinaServicio = cocinaServicio;
    }

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JsonUtil.write(resp, cocinaServicio.listarTicketsPendientes());
    }
}
