package pe.edu.utp.appcasaforno.domain.model;

import java.util.List;

public record TicketCocina(
        int ticket,
        String cliente,
        String mesa,
        String nota,
        List<String> items,
        EstadoPedido estado,
        double total) {

    public TicketCocina marcarCompletado() {
        return new TicketCocina(ticket, cliente, mesa, nota, items, EstadoPedido.COMPLETADO, total);
    }

    public TicketCocina marcarPagado() {
        return new TicketCocina(ticket, cliente, mesa, nota, items, EstadoPedido.PAGADO, total);
    }
}
