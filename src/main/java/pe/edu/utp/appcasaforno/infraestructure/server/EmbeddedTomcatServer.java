package pe.edu.utp.appcasaforno.infraestructure.server;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;

import java.io.File;

/**
 * Configura y gestiona el ciclo de vida del servidor Tomcat embebido.
 */
public class EmbeddedTomcatServer {

    private static final int PORT = 8080;

    private final Tomcat tomcat = new Tomcat();

    public void configure(File baseDir, File docBase, ServletRegistry registry) {
        tomcat.setBaseDir(baseDir.getAbsolutePath());

        Connector connector = new Connector();
        connector.setPort(PORT);
        tomcat.setConnector(connector);

        Context ctx = tomcat.addWebapp("", docBase.getAbsolutePath());
        registry.registrar(ctx);
    }

    public void start() throws Exception {
        tomcat.start();
        System.out.println("==============================================");
        System.out.println("  Servidor iniciado en http://localhost:" + PORT);
        System.out.println("==============================================");
        tomcat.getServer().await();
    }

    public void stop() throws Exception {
        tomcat.stop();
        tomcat.destroy();
    }
}
