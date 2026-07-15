package pe.edu.utp.appcasaforno.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EstadoMesa {
    LIBRE("libre"),
    OCUPADA("ocupada"),
    RESERVADA("reservada");

    private final String value;

    EstadoMesa(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static EstadoMesa from(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Estado de mesa inválido: " + value);
        }

        String normalizado = value.trim().toLowerCase();
        // Compatibilidad con formas masculinas usadas antes
        if ("ocupado".equals(normalizado)) {
            return OCUPADA;
        }
        if ("reservado".equals(normalizado)) {
            return RESERVADA;
        }

        for (EstadoMesa estado : values()) {
            if (estado.value.equals(normalizado)) {
                return estado;
            }
        }
        throw new IllegalArgumentException("Estado de mesa inválido: " + value);
    }
}
