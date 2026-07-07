package pe.edu.utp.appcasaforno.infraestructure.server;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import pe.edu.utp.appcasaforno.domain.api.ServletRegistration;

import java.util.List;

/**
 * Registra en Tomcat los servlets descritos por la factory.
 * No conoce tipos concretos: solo itera nombre, path e instancia.
 */
public class ServletRegistry {

    private final List<ServletRegistration> registros;

    public ServletRegistry(List<ServletRegistration> registros) {
        this.registros = List.copyOf(registros);
    }

    public void registrar(Context ctx) {
        for (ServletRegistration registro : registros) {
            Tomcat.addServlet(ctx, registro.nombre(), registro.servlet());
            ctx.addServletMappingDecoded(registro.path(), registro.nombre());
        }
    }
}
