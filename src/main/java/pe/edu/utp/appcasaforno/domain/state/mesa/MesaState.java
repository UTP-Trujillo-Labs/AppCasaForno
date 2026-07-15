package pe.edu.utp.appcasaforno.domain.state.mesa;

import pe.edu.utp.appcasaforno.domain.model.EstadoMesa;

/**
 * Estado concreto del patrón State para el control de mesas.
 */
public interface MesaState {

    EstadoMesa asEstado();

    /**
     * Avanza al siguiente estado según las reglas de control:
     * libre → reservada, reservada → libre, ocupada → libre.
     */
    void avanzar(MesaContext context);

    /**
     * Marca la mesa en uso al generar un pedido.
     */
    void ocupar(MesaContext context);
}
