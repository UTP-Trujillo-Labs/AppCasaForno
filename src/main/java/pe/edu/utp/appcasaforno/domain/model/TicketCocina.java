package pe.edu.utp.appcasaforno.domain.model;

import java.util.List;

public record TicketCocina(int ticket, String cliente, String mesa, List<String> items) {
}
