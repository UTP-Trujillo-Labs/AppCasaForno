package pe.edu.utp.appcasaforno.infraestructure.persistence;

import pe.edu.utp.appcasaforno.domain.model.EstadoPedido;
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

    public List<TicketCocina> listarCompletadosPorMesa(int idMesa) {
        String mesa = String.valueOf(idMesa);
        return pedidos.stream()
                .filter(pedido -> mesa.equals(pedido.mesa()))
                .filter(pedido -> pedido.estado() == EstadoPedido.COMPLETADO)
                .toList();
    }

    public void marcarPagadosPorMesa(int idMesa) {
        String mesa = String.valueOf(idMesa);
        for (int i = 0; i < pedidos.size(); i++) {
            TicketCocina pedido = pedidos.get(i);
            if (mesa.equals(pedido.mesa()) && pedido.estado() == EstadoPedido.COMPLETADO) {
                pedidos.set(i, pedido.marcarPagado());
            }
        }
    }

    public int size() {
        return pedidos.size();
    }
}
