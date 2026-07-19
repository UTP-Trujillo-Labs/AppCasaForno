package pe.edu.utp.appcasaforno.domain.model;

import java.util.List;

public record EnvioCocinaResponse(
        int ticket,
        String cliente,
        String mesa,
        List<String> items,
        double total,
        String message) {

    public static EnvioCocinaResponse from(TicketCocina ticket) {
        return new EnvioCocinaResponse(
                ticket.ticket(),
                ticket.cliente(),
                ticket.mesa(),
                ticket.items(),
                ticket.total(),
                "Pedido enviado a cocina.");
    }
}
