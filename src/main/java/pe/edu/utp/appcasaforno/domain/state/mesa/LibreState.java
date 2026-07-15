package pe.edu.utp.appcasaforno.domain.state.mesa;

import pe.edu.utp.appcasaforno.domain.model.EstadoMesa;

/**
 * Mesa libre: al avanzar pasa a reservada; al ocupar pasa a ocupada.
 */
public final class LibreState implements MesaState {

    public static final LibreState INSTANCE = new LibreState();

    private LibreState() {
    }

    @Override
    public EstadoMesa asEstado() {
        return EstadoMesa.LIBRE;
    }

    @Override
    public void avanzar(MesaContext context) {
        context.setState(ReservadaState.INSTANCE);
    }

    @Override
    public void ocupar(MesaContext context) {
        context.setState(OcupadaState.INSTANCE);
    }
}
