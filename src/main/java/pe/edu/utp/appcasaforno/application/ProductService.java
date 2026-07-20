package pe.edu.utp.appcasaforno.application;

import pe.edu.utp.appcasaforno.domain.model.Producto;
import pe.edu.utp.appcasaforno.infraestructure.persistence.ProductosStore;

import java.util.List;
import java.util.Locale;

public class ProductService {

    private final ProductosStore productosStore;

    public ProductService() {
        this(new ProductosStore());
    }

    public ProductService(ProductosStore productosStore) {
        this.productosStore = productosStore;
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
}
