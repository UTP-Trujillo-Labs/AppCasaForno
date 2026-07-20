package pe.edu.utp.appcasaforno.infraestructure.persistence;

import pe.edu.utp.appcasaforno.domain.model.Producto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Persistencia en memoria del catálogo de productos.
 */
public class ProductosStore {

    private final List<Producto> productos = new ArrayList<>(List.of(
            new Producto("p1", "Americana", 22.0, "pizzas", "/img/catalog/Americana.jpg"),
            new Producto("p2", "Bosque encantado", 30.0, "pizzas", "/img/catalog/Bosque encantado.jpg"),
            new Producto("p3", "Bosque tropical", 31.0, "pizzas", "/img/catalog/Bosque tropical.webp"),
            new Producto("p4", "Champiñones", 26.0, "pizzas", "/img/catalog/Champiñones.jpg"),
            new Producto("p5", "Chorizo", 24.0, "pizzas", "/img/catalog/Chorizo.webp"),
            new Producto("p6", "Continental", 27.0, "pizzas", "/img/catalog/Continental.webp"),
            new Producto("p7", "Full carne BBQ", 34.0, "pizzas", "/img/catalog/Full carne BBQ.jpg"),
            new Producto("p8", "Full carne picante", 35.0, "pizzas", "/img/catalog/Full carne picante.jpg"),
            new Producto("p9", "Full carne", 33.0, "pizzas", "/img/catalog/Full carne.avif"),
            new Producto("p10", "Hawaiana", 25.0, "pizzas", "/img/catalog/Hawaiana.jpg"),
            new Producto("p11", "Margarita", 21.0, "pizzas", "/img/catalog/Margarita.webp"),
            new Producto("p12", "Meat Lover", 36.0, "pizzas", "/img/catalog/Meat lover.jpeg"),
            new Producto("p13", "Mediterránea", 29.0, "pizzas", "/img/catalog/Mediterranea.jpg"),
            new Producto("p14", "Pepperoni y Carne", 30.0, "pizzas", "/img/catalog/Pepperoni y Carne.webp"),
            new Producto("p15", "Pepperoni", 24.0, "pizzas", "/img/catalog/Pepperoni.webp"),
            new Producto("p16", "Romántica", 28.0, "pizzas", "/img/catalog/Romantica.jpg"),
            new Producto("p17", "Tocino y maíz", 26.0, "pizzas", "/img/catalog/Tocino y maiz.png"),
            new Producto("p18", "Tocino", 25.0, "pizzas", "/img/catalog/Tocino.webp"),
            new Producto("p19", "Vegetariana", 23.0, "pizzas", "/img/catalog/Vegetariana.webp"),

            new Producto("a1", "Alitas Acevichadas ×6", 22.0, "alitas", "/img/catalog/AlitasAcevichadas.jpg"),
            new Producto("a2", "Alitas Ajo Parmesano ×6", 23.0, "alitas", "/img/catalog/alitasAjoParmesano.jpg"),
            new Producto("a3", "Alitas BBQ ×6", 18.0, "alitas", "/img/catalog/alitasBBQ.jpg"),
            new Producto("a4", "Alitas Broaster ×6", 20.0, "alitas", "/img/catalog/alitasBroaster.jpg"),
            new Producto("a5", "Alitas Búfalo ×6", 19.0, "alitas", "/img/catalog/AlitasBufalo.webp"),
            new Producto("a6", "Alitas Dragón ×6", 22.0, "alitas", "/img/catalog/alitasDragon.jpg"),
            new Producto("a7", "Alitas Maracuyá ×6", 22.0, "alitas", "/img/catalog/alitasMaracuya.webp"),
            new Producto("a8", "Alitas Oriental ×6", 21.0, "alitas", "/img/catalog/alitasOriental.jpg"),
            new Producto("a9", "Alitas Ostión ×6", 23.0, "alitas", "/img/catalog/alitasOstion.jpg"),
            new Producto("a10", "Alitas Parrilleras ×6", 21.0, "alitas", "/img/catalog/alitasParrillera.avif"),
            new Producto("a11", "Alitas Picantes ×6", 18.0, "alitas", "/img/catalog/alitasPicantes.jpg"),
            new Producto("a12", "Alitas Tocino ×6", 24.0, "alitas", "/img/catalog/alitasTocino.jpg"),

            new Producto("s1", "Salchi Lomo", 18.0, "salchipapas", "/img/catalog/salchiLomo.jpg"),
            new Producto("s2", "Salchi Nuggets", 16.0, "salchipapas", "/img/catalog/salchiNuggets.jpg"),
            new Producto("s3", "Salchipapa Clásica", 12.0, "salchipapas", "/img/catalog/salchipapaClasica.jpg"),
            new Producto("s4", "Salchi Pollo", 17.0, "salchipapas", "/img/catalog/salchiPollo.webp"),
            new Producto("s5", "Salchi Remix", 20.0, "salchipapas", "/img/catalog/salchiRemix.jpg"),

            new Producto("b1", "Coca Cola 500 ml", 4.0, "bebidas", "/img/catalog/500ml.webp", 8, "unid"),
            new Producto("b2", "Coca Cola 1 L", 4.0, "bebidas", "/img/catalog/1L.webp", 11, "unid"),
            new Producto("b3", "Coca Cola 1.5 L", 4.0, "bebidas", "/img/catalog/1.5L.webp", 6, "unid"),
            new Producto("b4", "Sprite 500 ml", 4.0, "bebidas", "/img/catalog/sprite500ml.webp", 9, "unid"),
            new Producto("b5", "Sprite 1 L", 7.0, "bebidas", "/img/catalog/sprite1lt.jpg", 5, "unid"),
            new Producto("b6", "Sprite 1.5 L", 9.0, "bebidas", "/img/catalog/sprite1_5lt.png", 12, "unid"),
            new Producto("b7", "Inca Kola 500 ml", 4.0, "bebidas", "/img/catalog/incakola500ml.webp", 7, "unid"),
            new Producto("b8", "Inca Kola 1 L", 7.0, "bebidas", "/img/catalog/incakola1lt.webp", 10, "unid"),
            new Producto("b9", "Inca Kola 1.5 L", 9.0, "bebidas", "/img/catalog/incakola1_5lt.webp", 8, "unid"),

            new Producto("bf1", "Limonada Cherry", 26.0, "bfrias", "/img/catalog/limonadacherry.jpg", 18, "L"),
            new Producto("bf2", "Limonada", 26.0, "bfrias", "/img/catalog/Limonada.webp", 22, "L"),
            new Producto("bf3", "Maracuyá", 26.0, "bfrias", "/img/catalog/maracuya.avif", 14, "L"),

            new Producto("ca1", "Amnesia", 26.0, "cart", "/img/catalog/amnesia.jpg", 6, "unid"),
            new Producto("ca2", "Doble Tramposo", 26.0, "cart", "/img/catalog/Dobletramposo.webp", 9, "unid"),
            new Producto("ca3", "Magia Negra", 26.0, "cart", "/img/catalog/magianegra.jpg", 11, "unid"),
            new Producto("ca4", "Maracumanto", 26.0, "cart", "/img/catalog/maracumanto.jpg", 5, "unid"),
            new Producto("ca5", "Osadia", 26.0, "cart", "/img/catalog/osadia.webp", 8, "unid"),
            new Producto("ca6", "Poseida", 26.0, "cart", "/img/catalog/Poseida .png", 12, "unid"),

            new Producto("ci1", "Corona", 26.0, "cind", "/img/catalog/corona.jpg", 10, "unid"),
            new Producto("ci2", "Cusqueña", 26.0, "cind", "/img/catalog/cusqueña.jpg", 7, "unid"),
            new Producto("ci3", "Pilsen Callao", 26.0, "cind", "/img/catalog/pilsencallao.jpg", 9, "unid"),
            new Producto("ci4", "Stela", 26.0, "cind", "/img/catalog/stela.jpg", 6, "unid"),

            new Producto("ccl1", "Algarrobina", 26.0, "cclas", "/img/catalog/algarrobina.jpg", 15, "L"),
            new Producto("ccl2", "Capirinha", 26.0, "cclas", "/img/catalog/caipirinha.webp", 20, "L"),
            new Producto("ccl3", "Chilcano", 26.0, "cclas", "/img/catalog/chilcano.jpg", 12, "L"),
            new Producto("ccl4", "Cuba Libre", 26.0, "cclas", "/img/catalog/cubalibre.jpg", 25, "L"),
            new Producto("ccl5", "Daiquiri", 26.0, "cclas", "/img/catalog/daiquiri.jpg", 17, "L"),
            new Producto("ccl6", "Machu Picchu", 26.0, "cclas", "/img/catalog/machupicchu.webp", 11, "L"),
            new Producto("ccl7", "Manhattan", 26.0, "cclas", "/img/catalog/manhattan.jpg", 19, "L"),
            new Producto("ccl8", "Margarita", 26.0, "cclas", "/img/catalog/margarita.jpg", 23, "L"),
            new Producto("ccl9", "Mojito", 26.0, "cclas", "/img/catalog/mojito.jpg", 16, "L"),
            new Producto("ccl10", "Negroni", 26.0, "cclas", "/img/catalog/negroni.jpg", 13, "L"),
            new Producto("ccl11", "Old Fashioned", 26.0, "cclas", "/img/catalog/oldfashined.jpg", 21, "L"),
            new Producto("ccl12", "Orgasmo", 26.0, "cclas", "/img/catalog/orgasmo.jpg", 10, "L"),
            new Producto("ccl13", "Piña Colada", 26.0, "cclas", "/img/catalog/piñacolada d.webp", 24, "L"),
            new Producto("ccl14", "Pisco Sour", 26.0, "cclas", "/img/catalog/piscosour.jpg", 18, "L"),
            new Producto("ccl15", "Vodka Sunrise", 26.0, "cclas", "/img/catalog/vodkasurnise.jpg", 14, "L"),

            new Producto("cau1", "Atrevida", 26.0, "caut", "/img/catalog/atrevida.jpg", 16, "L"),
            new Producto("cau2", "Caribbean", 26.0, "caut", "/img/catalog/caribbean.jpg", 22, "L"),
            new Producto("cau3", "Forno Punvh", 26.0, "caut", "/img/catalog/Fornopunvh.avif", 12, "L"),
            new Producto("cau4", "Hechizo de Sirena", 26.0, "caut", "/img/catalog/hechizodesirena.png", 19, "L"),
            new Producto("cau5", "Red Sour", 26.0, "caut", "/img/catalog/redsour.jpg", 25, "L"),
            new Producto("cau6", "Tiki Jack", 26.0, "caut", "/img/catalog/tikijack.webp", 11, "L"),

            new Producto("mock1", "Ave Maria", 26.0, "mock", "/img/catalog/avemaria.avif", 15, "L"),
            new Producto("mock2", "Buena Muchacha", 26.0, "mock", "/img/catalog/buenamuchacha.webp", 20, "L"),
            new Producto("mock3", "La Elegida", 26.0, "mock", "/img/catalog/laelegida.png", 13, "L"),

            new Producto("esp1", "Amaretto", 26.0, "especial", "/img/catalog/amaretto.jpg", 7, "unid"),
            new Producto("esp2", "Baileys", 26.0, "especial", "/img/catalog/baileys.jpg", 5, "unid"),
            new Producto("esp3", "Beee Feater", 26.0, "especial", "/img/catalog/beeefeater.webp", 11, "unid"),
            new Producto("esp4", "Sangría Casa Forno", 26.0, "especial", "/img/catalog/sangriacasaformo.jpg", 9, "unid"),
            new Producto("esp5", "Casillero Del Diablo", 26.0, "especial", "/img/catalog/casillerodeldiablo.webp", 8, "unid"),
            new Producto("esp6", "Double Black Label", 26.0, "especial", "/img/catalog/doubleblacklalbel.webp", 12, "unid")));

    public List<Producto> listar() {
        return List.copyOf(productos);
    }

    public Optional<Producto> buscarPorId(String id) {
        return productos.stream()
                .filter(p -> p.id().equals(id))
                .findFirst();
    }

    /**
     * Descuenta stock del producto. Si no controla stock ({@code null}), no hace nada.
     */
    public Producto descontarStock(String id, int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("Cantidad inválida para descontar stock: " + cantidad);
        }

        for (int i = 0; i < productos.size(); i++) {
            Producto producto = productos.get(i);
            if (!producto.id().equals(id)) {
                continue;
            }
            if (producto.stock() == null) {
                return producto;
            }
            if (cantidad > producto.stock()) {
                String unidad = producto.unidad() == null ? "" : " " + producto.unidad();
                throw new IllegalArgumentException(
                        "Stock insuficiente para " + producto.nombre()
                                + ". Disponible: " + producto.stock() + unidad
                                + ", solicitado: " + cantidad + unidad);
            }
            Producto actualizado = producto.conStock(producto.stock() - cantidad);
            productos.set(i, actualizado);
            return actualizado;
        }

        throw new IllegalArgumentException("Producto no encontrado: " + id);
    }
}
