package pe.edu.utp.appcasaforno.infraestructure.persistence;

import pe.edu.utp.appcasaforno.domain.model.Categoria;

import java.util.List;
import java.util.Optional;

/**
 * Persistencia en memoria del catálogo de categorías.
 */
public class CategoriasStore {

    private final List<Categoria> categorias = List.of(
            new Categoria("pizzas", "Pizzas"),
            new Categoria("alitas", "Alitas"),
            new Categoria("salchipapas", "salchipapas"),
            new Categoria("bebidas", "Bebidas"),
            new Categoria("bfrias", "Bebidas frías"),
            new Categoria("cart", "Cervezas Artesanales"),
            new Categoria("cind", "Cerveza Industriales"),
            new Categoria("cclas", "Cocteles Clásicos"),
            new Categoria("caut", "Cocteles de Autor"),
            new Categoria("mock", "Mocktails"),
            new Categoria("especial", "Sección Especial")
    );

    public List<Categoria> listar() {
        return categorias;
    }

    public Optional<Categoria> buscarPorId(String id) {
        return categorias.stream()
                .filter(c -> c.id().equals(id))
                .findFirst();
    }
}
