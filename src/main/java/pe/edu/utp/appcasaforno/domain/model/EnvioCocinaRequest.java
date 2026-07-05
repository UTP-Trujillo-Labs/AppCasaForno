package pe.edu.utp.appcasaforno.domain.model;

import java.util.List;

// equivalente a un class + getter + setter + contructor todos sus parametros
// + toString + hashCode + equals
public record EnvioCocinaRequest(String cliente, String mesa, List<ItemPedido> items) {
}
