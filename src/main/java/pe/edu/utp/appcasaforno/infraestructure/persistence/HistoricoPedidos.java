package pe.edu.utp.appcasaforno.infraestructure.persistence;

import pe.edu.utp.appcasaforno.domain.model.TicketCocina;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class HistoricoPedidos {

    private final ArrayList<TicketCocina> pedidos = new ArrayList<>();

    public void registrar(TicketCocina pedido) {
        pedidos.add(pedido);
    }

    public List<TicketCocina> listar() {
        return List.copyOf(pedidos);
    }

    public int size() {
        return pedidos.size();
    }
}
