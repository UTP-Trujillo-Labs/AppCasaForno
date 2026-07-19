package pe.edu.utp.appcasaforno.domain.state.mesa;

import pe.edu.utp.appcasaforno.domain.model.EstadoMesa;
import pe.edu.utp.appcasaforno.domain.model.Mesa;

/**
 * Contexto del patrón State: mantiene el estado actual de una mesa
 * y delega las transiciones a la implementación concreta.
 */
public class MesaContext {

    private final int idMesa;
    private MesaState state;

    public MesaContext(Mesa mesa) {
        this.idMesa = mesa.numero();
        this.state = MesaStateFactory.from(mesa.estado());
    }

    public int getIdMesa() {
        return idMesa;
    }

    public MesaState getState() {
        return state;
    }

    public void setState(MesaState state) {
        this.state = state;
    }

    public EstadoMesa getEstado() {
        return state.asEstado();
    }

    public void avanzar() {
        state.avanzar(this);
    }

    public void ocupar() {
        state.ocupar(this);
    }
}
