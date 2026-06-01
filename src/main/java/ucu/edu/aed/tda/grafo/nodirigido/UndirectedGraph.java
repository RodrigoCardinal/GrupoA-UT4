package ucu.edu.aed.tda.grafo.nodirigido;

import ucu.edu.aed.tda.grafo.model.edge.Edge;
import ucu.edu.aed.tda.grafo.model.edge.UndirectedEdge;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UndirectedGraph<V, D> implements IUndirectedGraph<V, D> {

    protected final Map<V, List<Edge<V, D>>> adj = new LinkedHashMap<>();

    @Override
    public boolean agregarVertice(V vertex) {
        if (vertex == null || adj.containsKey(vertex)) return false;
        adj.put(vertex, new ArrayList<>());
        return true;
    }

    @Override
    public V buscarVertice(Comparable<V> criterio) {
        for (V v : adj.keySet()) {
            if (criterio.compareTo(v) == 0) return v;
        }
        return null;
    }

    @Override
    public boolean agregarArista(V source, V target, D dato) {
        if (!adj.containsKey(source) || !adj.containsKey(target)) return false;
        if (existeArista(construirComparable(source), construirComparable(target))) return false;
        // arista única; pero adyacencia desde ambos lados
        UndirectedEdge<V, D> fwd = new UndirectedEdge<>(source, target, dato);
        UndirectedEdge<V, D> bwd = new UndirectedEdge<>(target, source, dato);
        adj.get(source).add(fwd);
        if (!source.equals(target)) adj.get(target).add(bwd);
        return true;
    }

    @Override
    public boolean eliminarArista(Comparable<V> source, Comparable<V> target) {
        V s = buscarVertice(source);
        V t = buscarVertice(target);
        if (s == null || t == null) return false;
        boolean r1 = adj.get(s).removeIf(e -> target.compareTo(e.target()) == 0);
        boolean r2 = adj.get(t).removeIf(e -> source.compareTo(e.target()) == 0);
        return r1 || r2;
    }

    @Override
    public boolean removerVertice(Comparable<V> criteria) {
        V v = buscarVertice(criteria);
        if (v == null) return false;
        adj.remove(v);
        for (List<Edge<V, D>> es : adj.values()) {
            es.removeIf(e -> e.target().equals(v));
        }
        return true;
    }

    @Override
    public Set<V> vertices() {
        return Collections.unmodifiableSet(adj.keySet());
    }

    @Override
    public Set<Edge<V, D>> aristas() {
        // las aristas no dirigidas son equals simétrico, así que el Set las deduplica
        Set<Edge<V, D>> all = new LinkedHashSet<>();
        for (List<Edge<V, D>> es : adj.values()) all.addAll(es);
        return Collections.unmodifiableSet(all);
    }

    @Override
    public boolean existeArista(Comparable<V> sourceCriteria, Comparable<V> targetCriteria) {
        V s = buscarVertice(sourceCriteria);
        if (s == null) return false;
        for (Edge<V, D> e : adj.get(s)) {
            if (targetCriteria.compareTo(e.target()) == 0) return true;
        }
        return false;
    }

    @Override
    public Edge<V, D> obtenerArista(Comparable<V> sourceCriteria, Comparable<V> targetCriteria) {
        V s = buscarVertice(sourceCriteria);
        if (s == null) return null;
        for (Edge<V, D> e : adj.get(s)) {
            if (targetCriteria.compareTo(e.target()) == 0) return e;
        }
        return null;
    }

    @Override
    public List<Edge<V, D>> adyacencias(Comparable<V> verticeCriteria) {
        V v = buscarVertice(verticeCriteria);
        if (v == null) return Collections.emptyList();
        return Collections.unmodifiableList(adj.get(v));
    }

    @Override
    public boolean esConexo() {
        if (adj.isEmpty()) return true;
        V start = adj.keySet().iterator().next();
        Set<V> visited = new HashSet<>();
        Deque<V> stack = new ArrayDeque<>();
        stack.push(start);
        while (!stack.isEmpty()) {
            V cur = stack.pop();
            if (!visited.add(cur)) continue;
            for (Edge<V, D> e : adj.get(cur)) {
                if (!visited.contains(e.target())) stack.push(e.target());
            }
        }
        return visited.size() == adj.size();
    }

    @Override
    public void vaciar() {
        adj.clear();
    }

    @Override
    public boolean tieneCiclos() {
        Set<V> visited = new HashSet<>();
        Map<V, V> parent = new HashMap<>();
        for (V v : adj.keySet()) {
            if (!visited.contains(v)) {
                if (dfsCycle(v, null, visited, parent)) return true;
            }
        }
        return false;
    }

    private boolean dfsCycle(V v, V parent, Set<V> visited, Map<V, V> parentMap) {
        visited.add(v);
        for (Edge<V, D> e : adj.get(v)) {
            V w = e.target();
            if (!visited.contains(w)) {
                if (dfsCycle(w, v, visited, parentMap)) return true;
            } else if (!w.equals(parent)) {
                return true;
            }
        }
        return false;
    }
}
