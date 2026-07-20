package pe.edu.utp.appcasaforno.application;

import pe.edu.utp.appcasaforno.domain.model.Categoria;
import pe.edu.utp.appcasaforno.infraestructure.persistence.CategoriasStore;

import java.util.List;

public class CategoriaServicio {

    private final CategoriasStore categoriasStore;

    public CategoriaServicio() {
        this(new CategoriasStore());
    }

    public CategoriaServicio(CategoriasStore categoriasStore) {
        this.categoriasStore = categoriasStore;
    }

    public List<Categoria> listarCategorias() {
        return categoriasStore.listar();
    }
}
