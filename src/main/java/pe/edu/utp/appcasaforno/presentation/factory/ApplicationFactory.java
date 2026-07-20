package pe.edu.utp.appcasaforno.presentation.factory;

import pe.edu.utp.appcasaforno.application.CategoriaServicio;
import pe.edu.utp.appcasaforno.application.CocinaServicio;
import pe.edu.utp.appcasaforno.application.MesasServicio;
import pe.edu.utp.appcasaforno.application.PedidosService;
import pe.edu.utp.appcasaforno.application.ProductService;
import pe.edu.utp.appcasaforno.domain.api.ServletRegistration;
import pe.edu.utp.appcasaforno.infraestructure.persistence.ColaPedidos;
import pe.edu.utp.appcasaforno.infraestructure.persistence.HistoricoPedidos;
import pe.edu.utp.appcasaforno.infraestructure.persistence.MesasStore;
import pe.edu.utp.appcasaforno.presentation.servlet.CategoriasServlet;
import pe.edu.utp.appcasaforno.presentation.servlet.CocinaServlet;
import pe.edu.utp.appcasaforno.presentation.servlet.MesasServlet;
import pe.edu.utp.appcasaforno.presentation.servlet.PedidosServlet;
import pe.edu.utp.appcasaforno.presentation.servlet.ProductosServlet;

import java.util.List;

/**
 * Composition root: centraliza la creación de servicios y el catálogo de servlets.
 */
public class ApplicationFactory {

    private final ColaPedidos colaPedidos = new ColaPedidos();
    private final HistoricoPedidos historicoPedidos = new HistoricoPedidos();
    private final MesasServicio mesasServicio = new MesasServicio(
            new MesasStore(), colaPedidos, historicoPedidos);
    private final ProductService productService = new ProductService();
    private final CocinaServicio cocinaServicio = new CocinaServicio(
            mesasServicio, productService, colaPedidos, historicoPedidos);
    private final PedidosService pedidosService = new PedidosService(
            mesasServicio, colaPedidos, historicoPedidos);
    private final CategoriaServicio categoriaServicio = new CategoriaServicio();

    public List<ServletRegistration> crearRegistrosServlets() {
        return List.of(
                new ServletRegistration("pedidosServlet", "/api/pedidos/*",
                        new PedidosServlet(pedidosService)),
                new ServletRegistration("cocinaServlet", "/api/cocina/*",
                        new CocinaServlet(cocinaServicio)),
                new ServletRegistration("productosServlet", "/api/productos/*",
                        new ProductosServlet(productService)),
                new ServletRegistration("categoriasServlet", "/api/categorias/*",
                        new CategoriasServlet(categoriaServicio)),
                new ServletRegistration("mesasServlet", "/api/mesas/*",
                        new MesasServlet(mesasServicio)));
    }
}
