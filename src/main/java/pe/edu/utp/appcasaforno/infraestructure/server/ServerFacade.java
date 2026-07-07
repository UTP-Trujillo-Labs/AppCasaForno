package pe.edu.utp.appcasaforno.infraestructure.server;

import pe.edu.utp.appcasaforno.presentation.factory.ApplicationFactory;

import java.io.File;

/**
 * Fachada que orquesta el arranque del servidor: recursos estáticos,
 * registro de servlets y Tomcat embebido.
 */
public class ServerFacade {

    private final StaticResourceExtractor resourceExtractor = new StaticResourceExtractor();
    private final EmbeddedTomcatServer tomcatServer = new EmbeddedTomcatServer();
    private final ServletRegistry servletRegistry =
            new ServletRegistry(new ApplicationFactory().crearRegistrosServlets());

    public void start() throws Exception {
        File baseDir = new File(System.getProperty("java.io.tmpdir"), "tomcat-demo-base");
        File docBase = resourceExtractor.extract();
        tomcatServer.configure(baseDir, docBase, servletRegistry);
        tomcatServer.start();
    }

    public void stop() throws Exception {
        tomcatServer.stop();
    }
}
