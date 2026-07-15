package pe.edu.utp.appcasaforno.application;

import pe.edu.utp.appcasaforno.domain.model.Mesa;
import pe.edu.utp.appcasaforno.domain.model.MesasResumen;
import pe.edu.utp.appcasaforno.domain.state.mesa.MesaContext;
import pe.edu.utp.appcasaforno.infraestructure.persistence.MesasStore;

/**
 * Servicio de acceso y control de mesas.
 * Las transiciones de estado se delegan al patrón State ({@link MesaContext}).
 */
public class MesasServicio {

    private final MesasStore mesasStore;

    public MesasServicio() {
        this(new MesasStore());
    }

    public MesasServicio(MesasStore mesasStore) {
        this.mesasStore = mesasStore;
    }

    public MesasResumen listar() {
        return new MesasResumen(mesasStore.cantidad(), mesasStore.listar());
    }

    public Mesa obtenerPorId(int idMesa) {
        return mesasStore.buscarPorId(idMesa)
                .orElseThrow(() -> new IllegalArgumentException("Mesa no encontrada: " + idMesa));
    }

    /**
     * Avanza el estado según la secuencia de control:
     * libre → reservada → libre; ocupada → libre.
     */
    public Mesa avanzarEstado(int idMesa) {
        Mesa mesa = obtenerPorId(idMesa);
        MesaContext context = new MesaContext(mesa);
        context.avanzar();
        return mesasStore.actualizarEstado(idMesa, context.getEstado());
    }

    /**
     * Marca la mesa como ocupada al generar un pedido (solo desde libre).
     */
    public Mesa marcarEnUso(int idMesa) {
        Mesa mesa = obtenerPorId(idMesa);
        MesaContext context = new MesaContext(mesa);
        context.ocupar();
        return mesasStore.actualizarEstado(idMesa, context.getEstado());
    }
}
