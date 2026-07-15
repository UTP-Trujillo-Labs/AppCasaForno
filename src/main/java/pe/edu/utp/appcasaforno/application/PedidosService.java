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

            new Producto("b1", "Coca Cola 500 ml", 4.0, "bebidas", "/img/catalog/500ml.webp"),
            new Producto("b2", "Coca Cola 1 L", 4.0, "bebidas", "/img/catalog/1L.webp"),
            new Producto("b3", "Coca Cola 1.5 L", 4.0, "bebidas", "/img/catalog/1.5L.webp"),
            new Producto("b4", "Jugo natural", 6.0, "bebidas", "/img/catalog/pizza.svg"),

            new Producto("pa1", "Spaghetti bolognesa", 22.0, "pastas", "/img/catalog/pizza.svg"),
            new Producto("pa2", "Lasaña", 26.0, "pastas", "/img/catalog/pizza.svg"),

            new Producto("bf1", "Limonada Cherry", 26.0, "bfrias", "/img/catalog/limonadacherry.jpg"),
            new Producto("bf2", "Limonada", 26.0, "bfrias", "/img/catalog/Limonada.webp"),
            new Producto("bf3", "Maracuyá", 26.0, "bfrias", "/img/catalog/maracuya.avif"),

            new Producto("ca1", "Amnesia", 26.0, "cart", "/img/catalog/amnesia.jpg"),
            new Producto("ca2", "Doble Tramposo", 26.0, "cart", "/img/catalog/Dobletramposo.webp"),
            new Producto("ca3", "Magia Negra", 26.0, "cart", "/img/catalog/magianegra.jpg"),
            new Producto("ca4", "Maracumanto", 26.0, "cart", "/img/catalog/maracumanto.jpg"),
            new Producto("ca5", "Osadia", 26.0, "cart", "/img/catalog/osadia.webp"),
            new Producto("ca6", "Poseida", 26.0, "cart", "/img/catalog/Poseida .png"),

            new Producto("ci1", "Corona", 26.0, "cind", "/img/catalog/corona.jpg"),
            new Producto("ci2", "Cusqueña", 26.0, "cind", "/img/catalog/cusqueña.jpg"),
            new Producto("ci3", "Pilsen Callao", 26.0, "cind", "/img/catalog/pilsencallao.jpg"),
            new Producto("ci4", "Stela", 26.0, "cind", "/img/catalog/stela.jpg"),

            new Producto("ccl1", "Algarrobina", 26.0, "cclas", "/img/catalog/algarrobina.jpg"),
            new Producto("ccl2", "Capirinha", 26.0, "cclas", "/img/catalog/caipirinha.webp"),
            new Producto("ccl3", "Chilcano", 26.0, "cclas", "/img/catalog/chilcano.jpg"),
            new Producto("ccl4", "Cuba Libre", 26.0, "cclas", "/img/catalog/cubalibre.jpg"),
            new Producto("ccl5", "Daiquiri", 26.0, "cclas", "/img/catalog/daiquiri.jpg"),
            new Producto("ccl6", "Machu Picchu", 26.0, "cclas", "/img/catalog/machupicchu.webp"),
            new Producto("ccl7", "Manhattan", 26.0, "cclas", "/img/catalog/manhattan.jpg"),
            new Producto("ccl8", "Margarita", 26.0, "cclas", "/img/catalog/margarita.jpg"),
            new Producto("ccl9", "Mojito", 26.0, "cclas", "/img/catalog/mojito.jpg"),
            new Producto("ccl10", "Negroni", 26.0, "cclas", "/img/catalog/negroni.jpg"),
            new Producto("ccl11", "Old Fashioned", 26.0, "cclas", "/img/catalog/oldfashined.jpg"),
            new Producto("ccl12", "Orgasmo", 26.0, "cclas", "/img/catalog/orgasmo.jpg"),
            new Producto("ccl13", "Piña Colada", 26.0, "cclas", "/img/catalog/piñacolada d.webp"),
            new Producto("ccl14", "Pisco Sour", 26.0, "cclas", "/img/catalog/piscosour.jpg"),
            new Producto("ccl15", "Vodka Sunrise", 26.0, "cclas", "/img/catalog/vodkasurnise.jpg"),

            new Producto("cau1", "Atrevida", 26.0, "caut", "/img/catalog/atrevida.jpg"),
            new Producto("cau2", "Caribbean", 26.0, "caut", "/img/catalog/caribbean.jpg"),
            new Producto("cau3", "Forno Punvh", 26.0, "caut", "/img/catalog/Fornopunvh.avif"),
            new Producto("cau4", "Hechizo de Sirena", 26.0, "caut", "/img/catalog/hechizodesirena.png"),
            new Producto("cau5", "Red Sour", 26.0, "caut", "/img/catalog/redsour.jpg"),
            new Producto("cau6", "Tiki Jack", 26.0, "caut", "/img/catalog/tikijack.webp"),

            new Producto("mock1", "Ave Maria", 26.0, "mock", "/img/catalog/avemaria.avif"),
            new Producto("mock2", "Buena Muchacha", 26.0, "mock", "/img/catalog/buenamuchacha.webp"),
            new Producto("mock3", "La Elegida", 26.0, "mock", "/img/catalog/laelegida.png"),

            new Producto("esp1", "Amaretto", 26.0, "especial", "/img/catalog/amaretto.jpg"),
            new Producto("esp2", "Baileys", 26.0, "especial", "/img/catalog/baileys.jpg"),
            new Producto("esp3", "Beee Feater", 26.0, "especial", "/img/catalog/beeefeater.webp"),
            new Producto("esp4", "Sangría Casa Forno", 26.0, "especial", "/img/catalog/sangriacasaformo.jpg"),
            new Producto("esp5", "Casillero Del Diablo", 26.0, "especial", "/img/catalog/casillerodeldiablo.webp"),
            new Producto("esp6", "Double Black Label", 26.0, "especial", "/img/catalog/doubleblacklalbel.webp")
        );

    private final ColaPedidos colaPedidos = new ColaPedidos();
    private final HistoricoPedidos historicoPedidos = new HistoricoPedidos();
    private final MesasServicio mesasServicio;
    private int ticketCounter = 1047;

    public PedidosService(MesasServicio mesasServicio) {
        this.mesasServicio = mesasServicio;
    }

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

        int idMesa = parseIdMesa(request.mesa());

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
                String.valueOf(idMesa),
                nota,
                List.copyOf(lineas),
                EstadoPedido.PENDIENTE);

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
            case COMPLETADO -> historicoPedidos.listar();
        };
    }

    public List<TicketCocina> listarPedidosPorMesa(int idMesa) {
        mesasServicio.obtenerPorId(idMesa);
        List<TicketCocina> pedidos = new ArrayList<>();
        pedidos.addAll(colaPedidos.listarPorMesa(idMesa));
        pedidos.addAll(historicoPedidos.listarPorMesa(idMesa));
        return List.copyOf(pedidos);
    }

    /**
     * Cobra la mesa solo si todos sus pedidos están COMPLETADOS y libera la mesa.
     */
    public Mesa cobrarMesa(int idMesa) {
        mesasServicio.obtenerPorId(idMesa);
        List<TicketCocina> pendientes = colaPedidos.listarPorMesa(idMesa);
        if (!pendientes.isEmpty()) {
            throw new IllegalArgumentException(
                    "La mesa " + idMesa + " aún tiene pedidos pendientes en cocina.");
        }

        List<TicketCocina> completados = historicoPedidos.listarPorMesa(idMesa);
        if (completados.isEmpty()) {
            throw new IllegalArgumentException(
                    "La mesa " + idMesa + " no tiene pedidos completados para cobrar.");
        }

        historicoPedidos.extraerPorMesa(idMesa);
        return mesasServicio.avanzarEstado(idMesa);
    }
}
