package pe.edu.utp.appcasaforno.domain.state.mesa;

import pe.edu.utp.appcasaforno.domain.model.EstadoMesa;

/**
 * Mesa reservada: al avanzar vuelve a libre; no se puede ocupar con un pedido.
 */
public final class ReservadaState implements MesaState {

    public static final ReservadaState INSTANCE = new ReservadaState();

    private ReservadaState() {
    }

    @Override
    public EstadoMesa asEstado() {
        return EstadoMesa.RESERVADA;
    }

    @Override
    public void avanzar(MesaContext context) {
        context.setState(LibreState.INSTANCE);
    }

    @Override
    public void ocupar(MesaContext context) {
        throw new IllegalArgumentException(
                "La mesa " + context.getIdMesa() + " está reservada.");
    }
}
