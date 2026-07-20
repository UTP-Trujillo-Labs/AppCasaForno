package pe.edu.utp.appcasaforno.application;

import pe.edu.utp.appcasaforno.domain.model.*;
import pe.edu.utp.appcasaforno.infraestructure.persistence.ColaPedidos;
import pe.edu.utp.appcasaforno.infraestructure.persistence.HistoricoPedidos;
import pe.edu.utp.appcasaforno.infraestructure.persistence.ProductosStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PedidosService {

    private final ProductosStore productosStore;
    private final ColaPedidos colaPedidos;
    private final HistoricoPedidos historicoPedidos;
    private final MesasServicio mesasServicio;
    private int ticketCounter = 1047;

    public PedidosService(MesasServicio mesasServicio) {
        this(mesasServicio, new ProductosStore(), new ColaPedidos(), new HistoricoPedidos());
    }

    public PedidosService(MesasServicio mesasServicio,
                          ProductosStore productosStore,
                          ColaPedidos colaPedidos,
                          HistoricoPedidos historicoPedidos) {
        this.mesasServicio = mesasServicio;
        this.productosStore = productosStore;
        this.colaPedidos = colaPedidos;
        this.historicoPedidos = historicoPedidos;
    }

    public List<Producto> listarProductos(String categoria, String busqueda) {
        String termino = busqueda == null ? "" : busqueda.trim().toLowerCase(Locale.ROOT);

        return productosStore.listar().stream()
                .filter(p -> categoria == null || categoria.isBlank() || p.categoria().equals(categoria))
                .filter(p -> termino.isEmpty() || p.nombre().toLowerCase(Locale.ROOT).contains(termino))
                .toList();
    }

    public Producto buscarProducto(String id) {
        return productosStore.buscarPorId(id).orElse(null);
    }

    public TicketCocina enviarACocina(EnvioCocinaRequest request) {
        if (request.items() == null || request.items().isEmpty()) {
            throw new IllegalArgumentException("El pedido no tiene productos.");
        }

        int idMesa = parseIdMesa(request.mesa());

        List<String> lineas = new ArrayList<>();
        double total = 0;
        for (ItemPedido item : request.items()) {
            Producto producto = buscarProducto(item.productoId());
            if (producto == null) {
                throw new IllegalArgumentException("Producto no encontrado: " + item.productoId());
            }
            if (item.cantidad() <= 0) {
                throw new IllegalArgumentException("Cantidad inválida para: " + item.productoId());
            }
            lineas.add(item.cantidad() + "x " + producto.nombre());
            total += producto.precio() * item.cantidad();
        }

        String nota = request.nota() == null ? "" : request.nota().trim();

        TicketCocina ticket = new TicketCocina(
                ticketCounter++,
                request.cliente(),
                String.valueOf(idMesa),
                nota,
                List.copyOf(lineas),
                EstadoPedido.PENDIENTE,
                total);

        colaPedidos.encolar(ticket);
        mesasServicio.marcarEnUso(idMesa);
        return ticket;
    }

    /**
     * Despacha un ticket de cocina: PENDIENTE → COMPLETADO.
     */
    public TicketCocina despacharPedido(int numeroTicket) {
        TicketCocina pendiente = colaPedidos.extraerPorTicket(numeroTicket)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Pedido pendiente no encontrado: " + numeroTicket));

        TicketCocina completado = pendiente.marcarCompletado();
        historicoPedidos.registrar(completado);
        return completado;
    }

    private int parseIdMesa(String mesa) {
        if (mesa == null || mesa.isBlank()) {
            throw new IllegalArgumentException("Debe indicar el ID numérico de la mesa.");
        }
        try {
            int idMesa = Integer.parseInt(mesa.trim());
            if (idMesa <= 0) {
                throw new IllegalArgumentException("ID de mesa inválido: " + mesa);
            }
            return idMesa;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("ID de mesa inválido: " + mesa);
        }
    }

    public List<TicketCocina> listarTicketsPendientes() {
        return listarPedidosPorEstado(EstadoPedido.PENDIENTE);
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

    /**
     * Cobra la mesa solo si todos sus pedidos están COMPLETADOS,
     * los marca como PAGADO (quedan en histórico) y libera la mesa.
     */
    public Mesa cobrarMesa(int idMesa) {
        mesasServicio.obtenerPorId(idMesa);
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
        return mesasServicio.avanzarEstado(idMesa);
    }
}
