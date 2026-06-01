package ucu.edu.aed.tda.grafo.model.result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DijkstraResult<V> implements IDijkstraResult<V> {
    private final V source;
    private final Map<V, Double> dist;
    private final Map<V, V> prev;

    public DijkstraResult(V source, Map<V, Double> dist, Map<V, V> prev) {
        this.source = source;
        this.dist = dist;
        this.prev = prev;
    }

    public V getSource() {
        return source;
    }

    public Map<V, Double> getDistances() {
        return dist;
    }

    public Map<V, V> getPredecessors() {
        return prev;
    }

    @Override
    public double getCost(V otherVertex) {
        return dist.getOrDefault(otherVertex, Double.POSITIVE_INFINITY);
    }

    @Override
    public List<V> getPath(V otherVertex) {
        if (!dist.containsKey(otherVertex) || Double.isInfinite(getCost(otherVertex))) {
            return Collections.emptyList();
        }
        List<V> path = new ArrayList<>();
        V cur = otherVertex;
        while (cur != null) {
            path.add(cur);
            if (cur.equals(source)) break;
            cur = prev.get(cur);
        }
        Collections.reverse(path);
        return path;
    }
}
