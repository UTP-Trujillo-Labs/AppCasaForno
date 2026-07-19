package pe.edu.utp.appcasaforno.infraestructure.persistence;

import pe.edu.utp.appcasaforno.domain.model.TicketCocina;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;

public class ColaPedidos {

    private final Queue<TicketCocina> cola = new LinkedList<>();

    public void encolar(TicketCocina pedido) {
        cola.add(pedido);
    }

    public Optional<TicketCocina> desencolar() {
        return Optional.ofNullable(cola.poll());
    }

    public Optional<TicketCocina> verSiguiente() {
        return Optional.ofNullable(cola.peek());
    }

    public List<TicketCocina> listar() {
        return List.copyOf(cola);
    }

    public List<TicketCocina> listarPorMesa(int idMesa) {
        String mesa = String.valueOf(idMesa);
        return cola.stream()
                .filter(pedido -> mesa.equals(pedido.mesa()))
                .toList();
    }

    public List<TicketCocina> extraerPorMesa(int idMesa) {
        String mesa = String.valueOf(idMesa);
        List<TicketCocina> extraidos = new ArrayList<>();
        cola.removeIf(pedido -> {
            if (mesa.equals(pedido.mesa())) {
                extraidos.add(pedido);
                return true;
            }
            return false;
        });
        return List.copyOf(extraidos);
    }

    public Optional<TicketCocina> extraerPorTicket(int numeroTicket) {
        Optional<TicketCocina> encontrado = cola.stream()
                .filter(pedido -> pedido.ticket() == numeroTicket)
                .findFirst();
        encontrado.ifPresent(cola::remove);
        return encontrado;
    }

    public int size() {
        return cola.size();
    }

    public boolean estaVacia() {
        return cola.isEmpty();
    }
}
