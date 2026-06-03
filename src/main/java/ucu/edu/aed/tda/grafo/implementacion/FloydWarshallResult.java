package ucu.edu.aed.tda.grafo.implementacion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ucu.edu.aed.tda.grafo.model.result.IFloydWarshallResult;

public final class FloydWarshallResult<V> implements IFloydWarshallResult<V> {

    public final Map<V, Map<V, Double>> dist;
    public final Map<V, Map<V, V>> rec; // para reconstruir camino

    public FloydWarshallResult(Map<V, Map<V, Double>> dist, Map<V, Map<V, V>> rec) {
        this.dist = dist;
        this.rec = rec;
    }
    /**
     * Retorna el camino para ir de source a target
     */
    public List<V> getPath(V source, V target) {
        List<V> resultado = new ArrayList<>();
        V o = source;
        while (!o.equals(target)) {
            resultado.add(o);
            o = rec.get(o).get(target);
            if (o == null) {
                return List.of();
            }
        }
        resultado.add(target);
        return resultado;
    }
    /**
     * retorna el costo asociado para ir de source a target
     */
    public double getCost(V source, V target) {
        return dist.get(source).getOrDefault(target, Double.POSITIVE_INFINITY);
    }
    /**
     * retorna true si existe conectividad entre source y target
     */
    public boolean connected(V source, V target) {
        Map<V, Double> fila = dist.get(source);
        if (fila == null) {
            return false;
        }
        Double v = fila.get(target);
        if (v == null) {
            return false;
        }
        return v != 0 && !Double.isInfinite(v);
    }
}
