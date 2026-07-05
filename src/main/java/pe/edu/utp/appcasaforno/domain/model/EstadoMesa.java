package pe.edu.utp.appcasaforno.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EstadoMesa {
    LIBRE("libre"),
    RESERVADO("reservado"),
    OCUPADO("ocupado");

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
        for (EstadoMesa estado : values()) {
            if (estado.value.equalsIgnoreCase(value)) {
                return estado;
            }
        }
        throw new IllegalArgumentException("Estado de mesa inválido: " + value);
    }
}
