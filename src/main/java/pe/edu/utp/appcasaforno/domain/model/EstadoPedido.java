package pe.edu.utp.appcasaforno.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EstadoPedido {
    PENDIENTE("pendiente"),
    COMPLETADO("completado"),
    PAGADO("pagado");

    private final String value;

    EstadoPedido(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static EstadoPedido from(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Estado de pedido inválido: " + value);
        }
        for (EstadoPedido estado : values()) {
            if (estado.value.equalsIgnoreCase(value.trim())
                    || estado.name().equalsIgnoreCase(value.trim())) {
                return estado;
            }
        }
        throw new IllegalArgumentException("Estado de pedido inválido: " + value);
    }
}
