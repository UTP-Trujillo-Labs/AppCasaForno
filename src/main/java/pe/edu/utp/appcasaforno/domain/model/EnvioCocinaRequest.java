package pe.edu.utp.appcasaforno.domain.model;

import java.util.List;

public record EnvioCocinaRequest(String cliente, String mesa, List<ItemPedido> items) {
}
