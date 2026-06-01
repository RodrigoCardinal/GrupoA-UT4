package ucu.edu.aed.tda.grafo.nodirigido;

import ucu.edu.aed.tda.grafo.model.edge.Edge;
import ucu.edu.aed.tda.grafo.model.edge.WeightedEdge;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;

public class UndirectedGraphAlgorithm implements IUndirectedGraphAlgorithm {

    @Override
    public <V, D extends WeightedEdge> IUndirectedGraph<V, D> kruskal(IUndirectedGraph<V, D> graph) {
        UndirectedGraph<V, D> T = new UndirectedGraph<>();
        for (V v : graph.vertices()) T.agregarVertice(v);

        // Aristas ordenadas por peso
        List<Edge<V, D>> edges = new ArrayList<>(graph.aristas());
        edges.sort((a, b) -> Double.compare(a.dato().getWeight(), b.dato().getWeight()));

        // Union-Find
        Map<V, V> parent = new HashMap<>();
        for (V v : graph.vertices()) parent.put(v, v);

        int needed = graph.cantidadDeVertices() - 1;
        int added = 0;
        for (Edge<V, D> e : edges) {
            if (added == needed) break;
            V u = e.source(), v = e.target();
            V ru = find(parent, u), rv = find(parent, v);
            if (!ru.equals(rv)) {
                T.agregarArista(u, v, e.dato());
                parent.put(ru, rv);
                added++;
            }
        }
        return T;
    }

    private <V> V find(Map<V, V> parent, V v) {
        while (!parent.get(v).equals(v)) {
            parent.put(v, parent.get(parent.get(v)));
            v = parent.get(v);
        }
        return v;
    }

    @Override
    public <V, D extends WeightedEdge> IUndirectedGraph<V, D> prim(
            IUndirectedGraph<V, D> graph, Comparable<V> source) {
        UndirectedGraph<V, D> T = new UndirectedGraph<>();
        for (V v : graph.vertices()) T.agregarVertice(v);

        V src = graph.buscarVertice(source);
        if (src == null) return T;

        Set<V> U = new HashSet<>();
        U.add(src);
        Set<V> rest = new HashSet<>(graph.vertices());
        rest.remove(src);

        while (!rest.isEmpty()) {
            Edge<V, D> min = searchMinEdge(graph, U, rest);
            if (min == null) break; // no conexo
            // determinar cuál extremo está en V-U
            V toAdd = U.contains(min.source()) ? min.target() : min.source();
            T.agregarArista(min.source(), min.target(), min.dato());
            U.add(toAdd);
            rest.remove(toAdd);
        }
        return T;
    }

    @Override
    public <V, D extends WeightedEdge> Edge<V, D> searchMinEdge(
            IUndirectedGraph<V, D> graph, Collection<V> U, Collection<V> V) {
        Edge<V, D> min = null;
        double minW = Double.POSITIVE_INFINITY;
        for (V u : U) {
            for (Edge<V, D> e : graph.adyacencias(graph.construirComparable(u))) {
                if (V.contains(e.target())) {
                    if (e.dato().getWeight() < minW) {
                        minW = e.dato().getWeight();
                        min = e;
                    }
                }
            }
        }
        return min;
    }

    @Override
    public <V, D> void bea(IUndirectedGraph<V, D> graph, Consumer<V> consumer) {
        Set<V> visited = new HashSet<>();
        for (V start : graph.vertices()) {
            if (visited.contains(start)) continue;
            Queue<V> q = new ArrayDeque<>();
            q.add(start);
            visited.add(start);
            while (!q.isEmpty()) {
                V cur = q.poll();
                consumer.accept(cur);
                for (Edge<V, D> e : graph.adyacencias(graph.construirComparable(cur))) {
                    if (visited.add(e.target())) q.add(e.target());
                }
            }
        }
    }
}
