package pe.edu.utp.appcasaforno.domain.state.mesa;

import pe.edu.utp.appcasaforno.domain.model.EstadoMesa;

/**
 * Mesa ocupada: al avanzar vuelve a libre; no se puede volver a ocupar.
 */
public final class OcupadaState implements MesaState {

    public static final OcupadaState INSTANCE = new OcupadaState();

    private OcupadaState() {
    }

    @Override
    public EstadoMesa asEstado() {
        return EstadoMesa.OCUPADA;
    }

    @Override
    public void avanzar(MesaContext context) {
        context.setState(LibreState.INSTANCE);
    }

    @Override
    public void ocupar(MesaContext context) {
        throw new IllegalArgumentException(
                "La mesa " + context.getIdMesa() + " ya está ocupada.");
    }
}
