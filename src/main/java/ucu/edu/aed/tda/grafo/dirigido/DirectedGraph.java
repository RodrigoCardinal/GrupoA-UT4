package ucu.edu.aed.tda.grafo.dirigido;

import ucu.edu.aed.tda.grafo.model.edge.DirectedEdge;
import ucu.edu.aed.tda.grafo.model.edge.Edge;

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

public class DirectedGraph<V, D> implements IDirectedIGraph<V, D> {

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
        adj.get(source).add(new DirectedEdge<>(source, target, dato));
        return true;
    }

    @Override
    public boolean eliminarArista(Comparable<V> source, Comparable<V> target) {
        V s = buscarVertice(source);
        if (s == null) return false;
        return adj.get(s).removeIf(e -> target.compareTo(e.target()) == 0);
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
    public Set<V> successors(Comparable<V> criteria) {
        V v = buscarVertice(criteria);
        if (v == null) return Collections.emptySet();
        Set<V> s = new LinkedHashSet<>();
        for (Edge<V, D> e : adj.get(v)) s.add(e.target());
        return s;
    }

    @Override
    public Set<V> predecessors(Comparable<V> criteria) {
        V v = buscarVertice(criteria);
        if (v == null) return Collections.emptySet();
        Set<V> s = new LinkedHashSet<>();
        for (Map.Entry<V, List<Edge<V, D>>> ent : adj.entrySet()) {
            for (Edge<V, D> e : ent.getValue()) {
                if (e.target().equals(v)) {
                    s.add(ent.getKey());
                    break;
                }
            }
        }
        return s;
    }

    @Override
    public boolean esConexo() {
        if (adj.isEmpty()) return true;
        // Conectividad "débil": ignorar dirección
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
            // predecesores también
            for (V p : predecessors(construirComparable(cur))) {
                if (!visited.contains(p)) stack.push(p);
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
        Map<V, Integer> color = new HashMap<>(); // 0 blanco, 1 gris, 2 negro
        for (V v : adj.keySet()) color.put(v, 0);
        for (V v : adj.keySet()) {
            if (color.get(v) == 0 && dfsCycle(v, color)) return true;
        }
        return false;
    }

    private boolean dfsCycle(V v, Map<V, Integer> color) {
        color.put(v, 1);
        for (Edge<V, D> e : adj.get(v)) {
            V w = e.target();
            int c = color.get(w);
            if (c == 1) return true;
            if (c == 0 && dfsCycle(w, color)) return true;
        }
        color.put(v, 2);
        return false;
    }
}
