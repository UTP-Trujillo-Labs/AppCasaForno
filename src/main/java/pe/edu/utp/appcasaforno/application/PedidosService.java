package pe.edu.utp.appcasaforno.application;

import pe.edu.utp.appcasaforno.domain.model.*;
import pe.edu.utp.appcasaforno.infraestructure.persistence.ColaPedidos;
import pe.edu.utp.appcasaforno.infraestructure.persistence.HistoricoPedidos;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PedidosService {

    private static final List<Producto> PRODUCTOS = List.of(
            new Producto("p1", "Pepperoni y carne", 28.0, "pizzas", "/img/catalog/pizza.svg"),
            new Producto("p2", "vegetariana", 25.0, "pizzas", "/img/catalog/pizza.svg"),
            new Producto("p3", "Americana", 24.0, "pizzas", "/img/catalog/pizza.svg"),
            new Producto("p4", "Margarita", 24.0, "pizzas", "/img/catalog/pizza.svg"),
            new Producto("p5", "Pepperoni", 25.0, "pizzas", "/img/catalog/pizza.svg"),
            new Producto("p6", "hawaina", 28.0, "pizzas", "/img/catalog/pizza.svg"),
            new Producto("p7", "pizza cuadrada", 28.0, "pizzas", "/img/catalog/pizzac.jpg"),
            new Producto("a1", "Alitas BBQ ×6", 18.0, "alitas", "/img/catalog/pizza.svg"),
            new Producto("a2", "Alitas picantes ×6", 18.0, "alitas", "/img/catalog/pizza.svg"),
            new Producto("s1", "Salchipapa clásica", 12.0, "salchipapas", "/img/catalog/pizza.svg"),
            new Producto("s2", "Salchipapa especial", 15.0, "salchipapas", "/img/catalog/pizza.svg"),
            new Producto("b1", "Gaseosa 500 ml", 4.0, "bebidas", "/img/catalog/pizza.svg"),
            new Producto("b2", "Jugo natural", 6.0, "bebidas", "/img/catalog/pizza.svg"),
            new Producto("pa1", "Spaghetti bolognesa", 22.0, "pastas", "/img/catalog/pizza.svg"),
            new Producto("pa2", "Lasaña", 26.0, "pastas", "/img/catalog/pizza.svg"));

    private final ColaPedidos colaPedidos = new ColaPedidos();
    private final HistoricoPedidos historicoPedidos = new HistoricoPedidos();
    private int ticketCounter = 1047;

    public List<Producto> listarProductos(String categoria, String busqueda) {
        String termino = busqueda == null ? "" : busqueda.trim().toLowerCase(Locale.ROOT);

        return PRODUCTOS.stream()
                .filter(p -> categoria == null || categoria.isBlank() || p.categoria().equals(categoria))
                .filter(p -> termino.isEmpty() || p.nombre().toLowerCase(Locale.ROOT).contains(termino))
                .toList();
    }

    public Producto buscarProducto(String id) {
        for (Producto p : PRODUCTOS) {
            if (p.id().equals(id)) {
                return p;
            }
        }
        return null;
    }

    public TicketCocina enviarACocina(EnvioCocinaRequest request) {
        if (request.items() == null || request.items().isEmpty()) {
            throw new IllegalArgumentException("El pedido no tiene productos.");
        }

        List<String> lineas = new ArrayList<>();
        for (ItemPedido item : request.items()) {
            Producto producto = buscarProducto(item.productoId());
            if (producto == null) {
                throw new IllegalArgumentException("Producto no encontrado: " + item.productoId());
            }
            if (item.cantidad() <= 0) {
                throw new IllegalArgumentException("Cantidad inválida para: " + item.productoId());
            }
            lineas.add(item.cantidad() + "x " + producto.nombre());
        }

        String nota = request.nota() == null ? "" : request.nota().trim();

        TicketCocina ticket = new TicketCocina(
                ticketCounter++,
                request.cliente(),
                request.mesa(),
                nota,
                List.copyOf(lineas));

        colaPedidos.encolar(ticket);
        return ticket;
    }

    public List<TicketCocina> listarTicketsPendientes() {
        return listarPedidosPorEstado(EstadoPedido.PENDIENTE);
    }

    public List<TicketCocina> listarPedidosPorEstado(EstadoPedido estado) {
        return switch (estado) {
            case PENDIENTE -> colaPedidos.listar();
            case COMPLETADO -> historicoPedidos.listar();
        };
    }
}
