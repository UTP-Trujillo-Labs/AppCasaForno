package pe.edu.utp.appcasaforno.infraestructure.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pe.edu.utp.appcasaforno.domain.model.ErrorResponse;

import java.io.IOException;

public final class JsonUtil {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonUtil() {
    }

    public static ObjectMapper mapper() {
        return MAPPER;
    }

    public static void write(HttpServletResponse resp, Object body) throws IOException {
        MAPPER.writeValue(resp.getWriter(), body);
    }

    public static void writeError(HttpServletResponse resp, int status, String message) throws IOException {
        resp.setStatus(status);
        write(resp, new ErrorResponse(message));
    }

    public static <T> T read(HttpServletRequest req, Class<T> type) throws IOException {
        return MAPPER.readValue(req.getInputStream(), type);
    }

    public static String normalizePath(String pathInfo) {
        if (pathInfo == null || pathInfo.isBlank()) {
            return "/";
        }
        return pathInfo.endsWith("/") && pathInfo.length() > 1
                ? pathInfo.substring(0, pathInfo.length() - 1)
                : pathInfo;
    }

    public static void prepareJsonResponse(HttpServletResponse resp) {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }
}
