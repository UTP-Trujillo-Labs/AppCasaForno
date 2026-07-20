package pe.edu.utp.appcasaforno.presentation.handler.cocina;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pe.edu.utp.appcasaforno.application.CocinaServicio;
import pe.edu.utp.appcasaforno.domain.model.EnvioCocinaRequest;
import pe.edu.utp.appcasaforno.domain.model.EnvioCocinaResponse;
import pe.edu.utp.appcasaforno.infraestructure.util.JsonUtil;
import pe.edu.utp.appcasaforno.infraestructure.web.ApiHandler;

import java.io.IOException;

public class EnviarCocinaHandler implements ApiHandler {

    private final CocinaServicio cocinaServicio;

    public EnviarCocinaHandler(CocinaServicio cocinaServicio) {
        this.cocinaServicio = cocinaServicio;
    }

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        EnvioCocinaRequest request = JsonUtil.read(req, EnvioCocinaRequest.class);
        EnvioCocinaResponse response = EnvioCocinaResponse.from(cocinaServicio.enviarACocina(request));
        resp.setStatus(HttpServletResponse.SC_CREATED);
        JsonUtil.write(resp, response);
    }
}
