package pe.edu.utp.appcasaforno.infraestructure.persistence;

import pe.edu.utp.appcasaforno.domain.model.TicketCocina;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

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

    public int size() {
        return cola.size();
    }

    public boolean estaVacia() {
        return cola.isEmpty();
    }
}
