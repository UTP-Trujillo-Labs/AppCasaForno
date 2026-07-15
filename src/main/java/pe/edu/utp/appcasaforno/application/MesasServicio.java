package pe.edu.utp.appcasaforno.application;

import pe.edu.utp.appcasaforno.domain.model.EstadoMesa;
import pe.edu.utp.appcasaforno.domain.model.Mesa;
import pe.edu.utp.appcasaforno.domain.model.MesasResumen;
import pe.edu.utp.appcasaforno.infraestructure.persistence.MesasStore;

/**
 * Servicio de acceso y control de mesas.
 * Delega la persistencia a {@link MesasStore}.
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

    public Mesa actualizarEstado(int idMesa, EstadoMesa estado) {
        obtenerPorId(idMesa);
        return mesasStore.actualizarEstado(idMesa, estado);
    }

    /**
     * Marca la mesa como ocupada al generar un pedido.
     * Solo permite ocupar mesas en estado libre.
     */
    public Mesa marcarEnUso(int idMesa) {
        Mesa mesa = obtenerPorId(idMesa);
        if (mesa.estado() == EstadoMesa.OCUPADA) {
            throw new IllegalArgumentException("La mesa " + idMesa + " ya está ocupada.");
        }
        if (mesa.estado() == EstadoMesa.RESERVADA) {
            throw new IllegalArgumentException("La mesa " + idMesa + " está reservada.");
        }
        return mesasStore.actualizarEstado(idMesa, EstadoMesa.OCUPADA);
    }

    public Mesa liberar(int idMesa) {
        obtenerPorId(idMesa);
        return mesasStore.actualizarEstado(idMesa, EstadoMesa.LIBRE);
    }

    public Mesa reservar(int idMesa) {
        Mesa mesa = obtenerPorId(idMesa);
        if (mesa.estado() == EstadoMesa.OCUPADA) {
            throw new IllegalArgumentException("No se puede reservar la mesa " + idMesa + ": está ocupada.");
        }
        return mesasStore.actualizarEstado(idMesa, EstadoMesa.RESERVADA);
    }
}
