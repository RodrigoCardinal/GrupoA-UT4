package ucu.edu.aed.tda.grafo.implementacion;


import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ucu.edu.aed.tda.grafo.model.result.IDijkstraResult;

public class DijkstraResult<V> implements IDijkstraResult<V> {
    private final V source;
    private final Map<V, Double> distancias;
    private final Map<V, V> predecesor;

    public DijkstraResult(V source, Map<V, Double> distancias, Map<V, V> predecesor) {
        this.source = source;
        this.distancias = distancias;
        this.predecesor = predecesor;
    }
    /**
     * Devuelve el costo para ir a "otherVertex"
     */
    @Override
    public double getCost(V otherVertex) {
        return distancias.getOrDefault(otherVertex, Double.POSITIVE_INFINITY);
    }
    /**
     * Retorna el camino para ir a "otherVertex"
     */
    public List<V> getPath(V destino) {
        LinkedList<V> path = new LinkedList<>();
        if (!distancias.containsKey(destino) || distancias.get(destino).isInfinite()) {
            return path;
        }
        for (V actual = destino; actual != null; actual = predecesor.get(actual)){
            path.addFirst(actual);
        }
        if (!path.isEmpty() && !path.getFirst().equals(source)) {
            return List.of();
        }
        return path;
    }
}
