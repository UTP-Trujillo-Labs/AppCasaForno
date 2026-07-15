package pe.edu.utp.appcasaforno.infraestructure.persistence;

import pe.edu.utp.appcasaforno.domain.model.TicketCocina;

import java.util.ArrayList;
import java.util.List;

public class HistoricoPedidos {

    private final ArrayList<TicketCocina> pedidos = new ArrayList<>();

    public void registrar(TicketCocina pedido) {
        pedidos.add(pedido);
    }

    public List<TicketCocina> listar() {
        return List.copyOf(pedidos);
    }

    public List<TicketCocina> listarPorMesa(int idMesa) {
        String mesa = String.valueOf(idMesa);
        return pedidos.stream()
                .filter(pedido -> mesa.equals(pedido.mesa()))
                .toList();
    }

    public List<TicketCocina> extraerPorMesa(int idMesa) {
        String mesa = String.valueOf(idMesa);
        List<TicketCocina> extraidos = new ArrayList<>();
        pedidos.removeIf(pedido -> {
            if (mesa.equals(pedido.mesa())) {
                extraidos.add(pedido);
                return true;
            }
            return false;
        });
        return List.copyOf(extraidos);
    }

    public int size() {
        return pedidos.size();
    }
}
