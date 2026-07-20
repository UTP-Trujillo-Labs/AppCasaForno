package pe.edu.utp.appcasaforno.application;

import pe.edu.utp.appcasaforno.domain.model.*;
import pe.edu.utp.appcasaforno.infraestructure.persistence.ColaPedidos;
import pe.edu.utp.appcasaforno.infraestructure.persistence.HistoricoPedidos;

import java.util.ArrayList;
import java.util.List;

public class CocinaServicio {

    private final ProductService productService;
    private final MesasServicio mesasServicio;
    private final ColaPedidos colaPedidos;
    private final HistoricoPedidos historicoPedidos;
    private int ticketCounter = 1047;

    public CocinaServicio(MesasServicio mesasServicio, ProductService productService) {
        this(mesasServicio, productService, new ColaPedidos(), new HistoricoPedidos());
    }

    public CocinaServicio(MesasServicio mesasServicio,
                          ProductService productService,
                          ColaPedidos colaPedidos,
                          HistoricoPedidos historicoPedidos) {
        this.mesasServicio = mesasServicio;
        this.productService = productService;
        this.colaPedidos = colaPedidos;
        this.historicoPedidos = historicoPedidos;
    }

    public TicketCocina enviarACocina(EnvioCocinaRequest request) {
        if (request.items() == null || request.items().isEmpty()) {
            throw new IllegalArgumentException("El pedido no tiene productos.");
        }

        int idMesa = parseIdMesa(request.mesa());

        List<String> lineas = new ArrayList<>();
        double total = 0;
        for (ItemPedido item : request.items()) {
            Producto producto = productService.buscarProducto(item.productoId());
            if (producto == null) {
                throw new IllegalArgumentException("Producto no encontrado: " + item.productoId());
            }
            if (item.cantidad() <= 0) {
                throw new IllegalArgumentException("Cantidad inválida para: " + item.productoId());
            }
            if (producto.stock() != null && item.cantidad() > producto.stock()) {
                String unidad = producto.unidad() == null ? "" : " " + producto.unidad();
                throw new IllegalArgumentException(
                        "Stock insuficiente para " + producto.nombre()
                                + ". Disponible: " + producto.stock() + unidad
                                + ", solicitado: " + item.cantidad() + unidad);
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

    public List<TicketCocina> listarTicketsPendientes() {
        return colaPedidos.listar();
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
}
