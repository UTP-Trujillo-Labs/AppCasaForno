package pe.edu.utp.appcasaforno.application;

import pe.edu.utp.appcasaforno.domain.model.Mesa;
import pe.edu.utp.appcasaforno.domain.model.MesasResumen;
import pe.edu.utp.appcasaforno.domain.model.TicketCocina;
import pe.edu.utp.appcasaforno.domain.state.mesa.MesaContext;
import pe.edu.utp.appcasaforno.infraestructure.persistence.ColaPedidos;
import pe.edu.utp.appcasaforno.infraestructure.persistence.HistoricoPedidos;
import pe.edu.utp.appcasaforno.infraestructure.persistence.MesasStore;

import java.util.List;

/**
 * Servicio de acceso y control de mesas.
 * Las transiciones de estado se delegan al patrón State ({@link MesaContext}).
 */
public class MesasServicio {

    private final MesasStore mesasStore;
    private final ColaPedidos colaPedidos;
    private final HistoricoPedidos historicoPedidos;

    public MesasServicio() {
        this(new MesasStore(), new ColaPedidos(), new HistoricoPedidos());
    }

    public MesasServicio(MesasStore mesasStore,
                         ColaPedidos colaPedidos,
                         HistoricoPedidos historicoPedidos) {
        this.mesasStore = mesasStore;
        this.colaPedidos = colaPedidos;
        this.historicoPedidos = historicoPedidos;
    }

    public MesasResumen listar() {
        return new MesasResumen(mesasStore.cantidad(), mesasStore.listar());
    }

    public Mesa obtenerPorId(int idMesa) {
        return mesasStore.buscarPorId(idMesa)
                .orElseThrow(() -> new IllegalArgumentException("Mesa no encontrada: " + idMesa));
    }

    /**
     * Avanza el estado según la secuencia de control:
     * libre → reservada → libre; ocupada → libre.
     */
    public Mesa avanzarEstado(int idMesa) {
        Mesa mesa = obtenerPorId(idMesa);
        MesaContext context = new MesaContext(mesa);
        context.avanzar();
        return mesasStore.actualizarEstado(idMesa, context.getEstado());
    }

    /**
     * Marca la mesa como ocupada al generar un pedido (solo desde libre).
     */
    public Mesa marcarEnUso(int idMesa) {
        Mesa mesa = obtenerPorId(idMesa);
        MesaContext context = new MesaContext(mesa);
        context.ocupar();
        return mesasStore.actualizarEstado(idMesa, context.getEstado());
    }

    /**
     * Cobra la mesa solo si todos sus pedidos están COMPLETADOS,
     * los marca como PAGADO (quedan en histórico) y libera la mesa.
     */
    public Mesa cobrarMesa(int idMesa) {
        obtenerPorId(idMesa);
        List<TicketCocina> pendientes = colaPedidos.listarPorMesa(idMesa);
        if (!pendientes.isEmpty()) {
            throw new IllegalArgumentException(
                    "La mesa " + idMesa + " aún tiene pedidos pendientes en cocina.");
        }

        List<TicketCocina> completados = historicoPedidos.listarCompletadosPorMesa(idMesa);
        if (completados.isEmpty()) {
            throw new IllegalArgumentException(
                    "La mesa " + idMesa + " no tiene pedidos completados para cobrar.");
        }

        historicoPedidos.marcarPagadosPorMesa(idMesa);
        return avanzarEstado(idMesa);
    }
}
