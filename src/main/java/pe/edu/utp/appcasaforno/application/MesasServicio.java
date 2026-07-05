package pe.edu.utp.appcasaforno.application;

import pe.edu.utp.appcasaforno.domain.model.EstadoMesa;
import pe.edu.utp.appcasaforno.domain.model.Mesa;
import pe.edu.utp.appcasaforno.domain.model.MesasResumen;

import java.util.ArrayList;
import java.util.List;

public class MesasServicio {

    private final ArrayList<Mesa> mesas = new ArrayList<>(List.of(
            new Mesa(1, EstadoMesa.LIBRE),
            new Mesa(2, EstadoMesa.OCUPADO),
            new Mesa(3, EstadoMesa.RESERVADO),
            new Mesa(4, EstadoMesa.LIBRE),
            new Mesa(5, EstadoMesa.OCUPADO),
            new Mesa(6, EstadoMesa.LIBRE)));

    public MesasResumen listar() {
        return new MesasResumen(mesas.size(), List.copyOf(mesas));
    }

    public Mesa actualizarEstado(int numero, EstadoMesa estado) {
        for (int i = 0; i < mesas.size(); i++) {
            Mesa mesa = mesas.get(i);
            if (mesa.numero() == numero) {
                Mesa actualizada = new Mesa(numero, estado);
                mesas.set(i, actualizada);
                return actualizada;
            }
        }
        throw new IllegalArgumentException("Mesa no encontrada: " + numero);
    }
}
