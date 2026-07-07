package pe.edu.utp.appcasaforno.infraestructure.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Extrae el contenido de /public del classpath a un directorio temporal.
 * Compatible con ejecución desde IDE (filesystem) y desde fat JAR.
 */
public class StaticResourceExtractor {

    public File extract() throws Exception {
        File docBase = new File(System.getProperty("java.io.tmpdir"), "tomcat-demo-docbase");
        if (docBase.exists()) {
            deleteDir(docBase);
        }
        docBase.mkdirs();

        URL publicUrl = StaticResourceExtractor.class.getResource("/public");
        if (publicUrl == null) {
            throw new IllegalStateException("No se encontró /public en el classpath.");
        }

        String protocol = publicUrl.getProtocol();

        if ("file".equals(protocol)) {
            File srcDir = new File(publicUrl.toURI());
            copyDir(srcDir.toPath(), docBase.toPath());
        } else if ("jar".equals(protocol)) {
            extractFromJar(publicUrl, docBase);
        } else {
            throw new IllegalStateException("Protocolo no soportado: " + protocol);
        }

        System.out.println("  DocBase preparado en: " + docBase.getAbsolutePath());
        return docBase;
    }

    private void extractFromJar(URL publicUrl, File docBase) throws Exception {
        String jarPath = publicUrl.getPath();
        String jarFilePath = jarPath.substring("file:".length(), jarPath.indexOf("!"));
        try (JarFile jar = new JarFile(new File(new java.net.URI("file:" + jarFilePath)))) {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                if (!name.startsWith("public/") || name.equals("public/")) {
                    continue;
                }

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
