package pe.edu.utp.appcasaforno.infraestructure.util;

import java.util.List;

public final class HelperUtil {
    private HelperUtil() {
    }
    public static String toJsonArray(List<String> items) {
        var sb = new StringBuilder("[");
        for (int i = 0; i < items.size(); i++) {
            sb.append("\"").append(escapeJson(items.get(i))).append("\"");
            if (i < items.size() - 1)
                sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    public static String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
