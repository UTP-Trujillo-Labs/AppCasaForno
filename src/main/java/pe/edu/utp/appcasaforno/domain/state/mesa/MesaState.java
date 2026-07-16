package pe.edu.utp.appcasaforno.domain.state.mesa;

import pe.edu.utp.appcasaforno.domain.model.EstadoMesa;

/**
 * Estado concreto del patrón State para el control de mesas.
 * Solo los estados que permiten una transición deben sobrescribirla;
 * el resto hereda el rechazo por defecto.
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
     * Solo {@link LibreState} lo permite; en el resto se rechaza.
     */
    default void ocupar(MesaContext context) {
        throw new IllegalArgumentException(
                "La mesa " + context.getIdMesa() + " no se puede ocupar porque está "
                        + asEstado().getValue() + ".");
    }
}
