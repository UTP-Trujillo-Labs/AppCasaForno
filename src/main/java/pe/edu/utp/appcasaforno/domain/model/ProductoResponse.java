package pe.edu.utp.appcasaforno.domain.model;

/**
 * Respuesta de producto con semáforo de stock ({@code estado}).
 * Extiende la información de {@link Producto} para el listado público.
 */
public record ProductoResponse(
        String id,
        String nombre,
        double precio,
        String categoria,
        String imagen,
        Integer stock,
        String unidad,
        Integer stockInicial,
        String estado) {

    public static ProductoResponse from(Producto producto) {
        return new ProductoResponse(
                producto.id(),
                producto.nombre(),
                producto.precio(),
                producto.categoria(),
                producto.imagen(),
                producto.stock(),
                producto.unidad(),
                producto.stockInicial(),
                calcularEstado(producto));
    }

    private static String calcularEstado(Producto producto) {
        if (producto.stock() == null || producto.stockInicial() == null) {
            return null;
        }
        if (producto.stock() == 0) {
            return "red";
        }

        double porcentaje = (producto.stock() * 100.0) / producto.stockInicial();
        if (porcentaje <= 50) {
            return "yellow";
        }
        return "green";
    }
}
