package pe.edu.utp.appcasaforno.presentation;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import pe.edu.utp.appcasaforno.presentation.servlet.CategoriasServlet;
import pe.edu.utp.appcasaforno.presentation.servlet.PedidosServlet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Controlador del servidor Tomcat embebido.
 * Sirve archivos estáticos desde resources/public en el puerto 8080.
 */
public class ServerController {

    private static final int PORT = 8080;
    private final Tomcat tomcat = new Tomcat();

    /**
     * Inicia el servidor Tomcat en el puerto 8080.
     */
    public void start() throws Exception {
        // 1. Preparar docBase con los archivos estáticos de /public
        File baseDir = new File(System.getProperty("java.io.tmpdir"), "tomcat-demo-base");
        File docBase = prepareDocBase();

        tomcat.setBaseDir(baseDir.getAbsolutePath());

        // 2. Configurar conector HTTP
        Connector connector = new Connector();
        connector.setPort(PORT);
        tomcat.setConnector(connector);

        // 3. Crear contexto apuntando al docBase extraído
        Context ctx = tomcat.addWebapp("", docBase.getAbsolutePath());

        // 4. Registrar servlets API
        Tomcat.addServlet(ctx, "pedidosServlet", new PedidosServlet());
        ctx.addServletMappingDecoded("/api/pedidos/*", "pedidosServlet");

        Tomcat.addServlet(ctx, "categoriaServet", new CategoriasServlet());
        ctx.addServletMappingDecoded("/api/categorias/*", "categoriaServet");

        // 5. Iniciar y bloquear el hilo principal
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

    /**
     * Extrae el contenido de /public del classpath a un directorio temporal.
     * Compatible con ejecución desde IDE (filesystem) y desde fat JAR.
     */
    private File prepareDocBase() throws Exception {
        File docBase = new File(System.getProperty("java.io.tmpdir"), "tomcat-demo-docbase");
        if (docBase.exists())
            deleteDir(docBase);
        docBase.mkdirs();

        URL publicUrl = ServerController.class.getResource("/public");
        if (publicUrl == null) {
            throw new IllegalStateException("No se encontró /public en el classpath.");
        }

        String protocol = publicUrl.getProtocol();

        if ("file".equals(protocol)) {
            // Desde IDE: copiar directorio directamente
            File srcDir = new File(publicUrl.toURI());
            copyDir(srcDir.toPath(), docBase.toPath());

        } else if ("jar".equals(protocol)) {
            // Desde fat JAR: extraer entradas cuyo nombre comience con "public/"
            String jarPath = publicUrl.getPath();
            // formato: file:/path/to/app.jar!/public
            String jarFilePath = jarPath.substring("file:".length(), jarPath.indexOf("!"));
            try (JarFile jar = new JarFile(new File(new java.net.URI("file:" + jarFilePath)))) {
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String name = entry.getName();
                    if (!name.startsWith("public/") || name.equals("public/"))
                        continue;

                    String relative = name.substring("public/".length());
                    File outFile = new File(docBase, relative);

                    if (entry.isDirectory()) {
                        outFile.mkdirs();
                    } else {
                        outFile.getParentFile().mkdirs();
                        try (InputStream in = jar.getInputStream(entry);
                             OutputStream out = Files.newOutputStream(outFile.toPath())) {
                            in.transferTo(out);
                        }
                    }
                }
            }
        } else {
            throw new IllegalStateException("Protocolo no soportado: " + protocol);
        }

        System.out.println("  DocBase preparado en: " + docBase.getAbsolutePath());
        return docBase;
    }

    private void copyDir(Path src, Path dest) throws IOException {
        Files.walkFileTree(src, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                dest.resolve(src.relativize(dir)).toFile().mkdirs();
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.copy(file, dest.resolve(src.relativize(file)), StandardCopyOption.REPLACE_EXISTING);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private void deleteDir(File dir) throws IOException {
        Files.walkFileTree(dir.toPath(), new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path f, BasicFileAttributes a) throws IOException {
                Files.delete(f);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path d, IOException e) throws IOException {
                Files.delete(d);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
