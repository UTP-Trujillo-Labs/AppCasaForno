package pe.edu.utp.appcasaforno.presentation;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pe.edu.utp.appcasaforno.infraestructure.util.HelperUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class DemoSerlvet extends HttpServlet {
    private static final List<String> TAGS = List.of(
            "☕ Java 25",
            "🐱 Tomcat 10.1",
            "📦 Maven");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        // Permitir CORS en desarrollo
        resp.setHeader("Access-Control-Allow-Origin", "*");

        PrintWriter out = resp.getWriter();
        out.print(HelperUtil.toJsonArray(TAGS));
        out.flush();
    }
}
