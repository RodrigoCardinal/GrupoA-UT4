package ucu.edu.aed.tda.grafo.implementacion;


import ucu.edu.aed.tda.grafo.model.result.IDijkstraResult;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DijkstraResult<V> implements IDijkstraResult<V> {
    private final V source;
    private final Map<V, Double> distancias;
    private final Map<V, V> predecesor;

    public DijkstraResult(V source, Map<V, Double> distancias, Map<V, V> predecesor) {
        this.source = source;
        this.distancias = distancias;
        this.predecesor = predecesor;
    }

    @Override
    public double getCost(V otherVertex) {
        return distancias.getOrDefault(otherVertex, Double.POSITIVE_INFINITY);
    }

    public List<V> getPath(V destino) {
        LinkedList<V> path = new LinkedList<>();
        if (!distancias.containsKey(destino) || distancias.get(destino).isInfinite()) return path;
        for (V actual = destino; actual != null; actual = predecesor.get(actual)){
            path.addFirst(actual);
        }
        if (!path.isEmpty() && !path.getFirst().equals(source)) return List.of();
        return path;
    }
}
