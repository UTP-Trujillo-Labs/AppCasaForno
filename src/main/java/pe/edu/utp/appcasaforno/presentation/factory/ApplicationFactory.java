package pe.edu.utp.appcasaforno.presentation.factory;

import pe.edu.utp.appcasaforno.application.CategoriaServicio;
import pe.edu.utp.appcasaforno.application.MesasServicio;
import pe.edu.utp.appcasaforno.application.PedidosService;
import pe.edu.utp.appcasaforno.domain.api.ServletRegistration;
import pe.edu.utp.appcasaforno.presentation.servlet.CategoriasServlet;
import pe.edu.utp.appcasaforno.presentation.servlet.CocinaServlet;
import pe.edu.utp.appcasaforno.presentation.servlet.MesasServlet;
import pe.edu.utp.appcasaforno.presentation.servlet.PedidosServlet;

import java.util.List;

/**
 * Composition root: centraliza la creación de servicios y el catálogo de servlets.
 */
public class ApplicationFactory {

    private final MesasServicio mesasServicio = new MesasServicio();
    private final PedidosService pedidosService = new PedidosService(mesasServicio);
    private final CategoriaServicio categoriaServicio = new CategoriaServicio();

    public List<ServletRegistration> crearRegistrosServlets() {
        return List.of(
                new ServletRegistration("pedidosServlet", "/api/pedidos/*",
                        new PedidosServlet(pedidosService)),
                new ServletRegistration("cocinaServlet", "/api/cocina/*",
                        new CocinaServlet(pedidosService)),
                new ServletRegistration("categoriasServlet", "/api/categorias/*",
                        new CategoriasServlet(categoriaServicio)),
                new ServletRegistration("mesasServlet", "/api/mesas/*",
                        new MesasServlet(mesasServicio)));
    }
}
