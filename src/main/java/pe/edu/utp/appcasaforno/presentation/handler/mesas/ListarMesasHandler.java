package pe.edu.utp.appcasaforno.presentation.handler.mesas;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pe.edu.utp.appcasaforno.application.MesasServicio;
import pe.edu.utp.appcasaforno.infraestructure.util.JsonUtil;
import pe.edu.utp.appcasaforno.infraestructure.web.ApiHandler;

import java.io.IOException;

public class ListarMesasHandler implements ApiHandler {

    private final MesasServicio mesasServicio;

    public ListarMesasHandler(MesasServicio mesasServicio) {
        this.mesasServicio = mesasServicio;
    }

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JsonUtil.write(resp, mesasServicio.listar());
    }
}
