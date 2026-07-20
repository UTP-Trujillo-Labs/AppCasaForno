package pe.edu.utp.appcasaforno.infraestructure.persistence;

import pe.edu.utp.appcasaforno.domain.model.EstadoMesa;
import pe.edu.utp.appcasaforno.domain.model.Mesa;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class MesasStore {

    private static final int CANTIDAD_MESAS = 6;

    private final Map<Integer, Mesa> mesas = new ConcurrentHashMap<>();

    public MesasStore() {
        inicializar();
    }

    private void inicializar() {
        for (int id = 1; id <= CANTIDAD_MESAS; id++) {
            mesas.put(id, new Mesa(id, EstadoMesa.LIBRE));
        }
    }

    public List<Mesa> listar() {
        List<Mesa> copia = new ArrayList<>(mesas.values());
        copia.sort(Comparator.comparingInt(Mesa::numero));
        return List.copyOf(copia);
    }

    public int cantidad() {
        return mesas.size();
    }

    public Optional<Mesa> buscarPorId(int id) {
        return Optional.ofNullable(mesas.get(id));
    }

    public Mesa actualizarEstado(int id, EstadoMesa estado) {
        if (!mesas.containsKey(id)) {
            throw new IllegalArgumentException("Mesa no encontrada: " + id);
        }
        if (estado == null) {
            throw new IllegalArgumentException("El estado de la mesa es obligatorio.");
        }
        Mesa actualizada = new Mesa(id, estado);
        mesas.put(id, actualizada);
        return actualizada;
    }
}
