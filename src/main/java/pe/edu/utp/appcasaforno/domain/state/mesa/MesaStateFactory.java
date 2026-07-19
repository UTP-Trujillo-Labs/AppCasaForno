package pe.edu.utp.appcasaforno.domain.state.mesa;

import pe.edu.utp.appcasaforno.domain.model.EstadoMesa;

public final class MesaStateFactory {

    private MesaStateFactory() {
    }

    public static MesaState from(EstadoMesa estado) {
        return switch (estado) {
            case LIBRE -> LibreState.INSTANCE;
            case RESERVADA -> ReservadaState.INSTANCE;
            case OCUPADA -> OcupadaState.INSTANCE;
        };
    }
}
