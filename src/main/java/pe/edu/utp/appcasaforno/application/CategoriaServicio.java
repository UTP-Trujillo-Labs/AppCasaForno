package pe.edu.utp.appcasaforno.application;

import pe.edu.utp.appcasaforno.domain.model.Categoria;

import java.util.List;

public class CategoriaServicio {

    private static final List<Categoria> CATEGORIAS = List.of(
            new Categoria("pizzas", "Pizzas"),
            new Categoria("alitas", "Alitas"),
            new Categoria("salchipapas", "salchipapas"),
            new Categoria("bebidas", "Bebidas"),
            new Categoria("pastas", "Pastas"),
            new Categoria("bfrias", "Bebidas frías"),
            new Categoria("cart", "Cervezas Artesanales"),
            new Categoria("cind", "Cerveza Industriales"),
            new Categoria("cclas", "Cocteles Clásicos"),
            new Categoria("caut", "Cocteles de Autor"),
            new Categoria("mock", "Mocktails"),
            new Categoria("especial", "Sección Especial")
        );

    public List<Categoria> listarCategorias() {
        return CATEGORIAS;
    }
}
