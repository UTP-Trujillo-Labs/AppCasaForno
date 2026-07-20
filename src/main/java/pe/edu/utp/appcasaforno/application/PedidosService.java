package pe.edu.utp.appcasaforno.application;

import pe.edu.utp.appcasaforno.domain.model.EstadoPedido;
import pe.edu.utp.appcasaforno.domain.model.TicketCocina;
import pe.edu.utp.appcasaforno.infraestructure.persistence.ColaPedidos;
import pe.edu.utp.appcasaforno.infraestructure.persistence.HistoricoPedidos;

import java.util.ArrayList;
import java.util.List;

public class PedidosService {

    private final ColaPedidos colaPedidos;
    private final HistoricoPedidos historicoPedidos;
    private final MesasServicio mesasServicio;

    public PedidosService(MesasServicio mesasServicio) {
        this(mesasServicio, new ColaPedidos(), new HistoricoPedidos());
    }

    public PedidosService(MesasServicio mesasServicio,
                          ColaPedidos colaPedidos,
                          HistoricoPedidos historicoPedidos) {
        this.mesasServicio = mesasServicio;
        this.colaPedidos = colaPedidos;
        this.historicoPedidos = historicoPedidos;
    }

    public List<TicketCocina> listarPedidosPorEstado(EstadoPedido estado) {
        return switch (estado) {
            case PENDIENTE -> colaPedidos.listar();
            case COMPLETADO, PAGADO -> historicoPedidos.listar();
        };
    }

    public List<TicketCocina> listarPedidosPorMesa(int idMesa) {
        mesasServicio.obtenerPorId(idMesa);
        List<TicketCocina> pedidos = new ArrayList<>();
        pedidos.addAll(colaPedidos.listarPorMesa(idMesa));
        pedidos.addAll(historicoPedidos.listarCompletadosPorMesa(idMesa));
        return List.copyOf(pedidos);
    }
}
