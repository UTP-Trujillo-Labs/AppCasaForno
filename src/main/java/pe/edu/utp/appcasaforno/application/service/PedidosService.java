package pe.edu.utp.appcasaforno.application.service;

import pe.edu.utp.appcasaforno.domain.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class PedidosService {

    private static final List<Categoria> CATEGORIAS = List.of(
            new Categoria("pizzas", "Pizzas"),
            new Categoria("alitas", "Alitas"),
            new Categoria("salchipapas", "salchipapas"),
            new Categoria("bebidas", "Bebidas"),
            new Categoria("pastas", "Pastas"));

    private static final List<Producto> PRODUCTOS = List.of(
            new Producto("p1", "Pepperoni y carne", 28.0, "pizzas", "/img/catalog/pizza.svg"),
            new Producto("p2", "vegetariana", 25.0, "pizzas", "/img/catalog/pizza.svg"),
            new Producto("p3", "Americana", 24.0, "pizzas", "/img/catalog/pizza.svg"),
            new Producto("p4", "Margarita", 24.0, "pizzas", "/img/catalog/pizza.svg"),
            new Producto("p5", "Pepperoni", 25.0, "pizzas", "/img/catalog/pizza.svg"),
            new Producto("p6", "hawaina", 28.0, "pizzas", "/img/catalog/pizza.svg"),
            new Producto("a1", "Alitas BBQ ×6", 18.0, "alitas", "/img/catalog/pizza.svg"),
            new Producto("a2", "Alitas picantes ×6", 18.0, "alitas", "/img/catalog/pizza.svg"),
            new Producto("s1", "Salchipapa clásica", 12.0, "salchipapas", "/img/catalog/pizza.svg"),
            new Producto("s2", "Salchipapa especial", 15.0, "salchipapas", "/img/catalog/pizza.svg"),
            new Producto("b1", "Gaseosa 500 ml", 4.0, "bebidas", "/img/catalog/pizza.svg"),
            new Producto("b2", "Jugo natural", 6.0, "bebidas", "/img/catalog/pizza.svg"),
            new Producto("pa1", "Spaghetti bolognesa", 22.0, "pastas", "/img/catalog/pizza.svg"),
            new Producto("pa2", "Lasaña", 26.0, "pastas", "/img/catalog/pizza.svg"));

    private final AtomicInteger ticketCounter = new AtomicInteger(1047);
    private final List<TicketCocina> ticketsPendientes = new CopyOnWriteArrayList<>();

    public List<Categoria> listarCategorias() {
        return CATEGORIAS;
    }

    public List<Producto> listarProductos(String categoria, String busqueda) {
        String termino = busqueda == null ? "" : busqueda.trim().toLowerCase(Locale.ROOT);

        return PRODUCTOS.stream()
                .filter(p -> categoria == null || categoria.isBlank() || p.categoria().equals(categoria))
                .filter(p -> termino.isEmpty() || p.nombre().toLowerCase(Locale.ROOT).contains(termino))
                .toList();
    }

    public Producto buscarProducto(String id) {
        return PRODUCTOS.stream()
                .filter(p -> p.id().equals(id))
                .findFirst()
                .orElse(null);
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

        TicketCocina ticket = new TicketCocina(
                ticketCounter.getAndIncrement(),
                request.cliente(),
                request.mesa(),
                List.copyOf(lineas));

        ticketsPendientes.add(ticket);
        return ticket;
    }

    public List<TicketCocina> listarTicketsPendientes() {
        return List.copyOf(ticketsPendientes);
    }
}
