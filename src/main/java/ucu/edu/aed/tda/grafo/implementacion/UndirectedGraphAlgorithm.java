package ucu.edu.aed.tda.grafo.implementacion;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import ucu.edu.aed.tda.grafo.IUndirectedGraph;
import ucu.edu.aed.tda.grafo.IUndirectedGraphAlgorithm;
import ucu.edu.aed.tda.grafo.model.edge.Edge;
import ucu.edu.aed.tda.grafo.model.edge.WeightedEdge;

public class UndirectedGraphAlgorithm implements IUndirectedGraphAlgorithm {
        /**
     * Implementa el algoritmo Kruskal
     */
    @Override
    public <V, D extends WeightedEdge> IUndirectedGraph<V, D> kruskal(IUndirectedGraph<V, D> graph) {

        Map<V, List<V>> colecciones = new HashMap<>();
        for (V v : graph.vertices()) {
            List<V> col = new LinkedList<>();
            col.add(v);
            colecciones.put(v, col);
        }

        List<Edge<V, D>> ordenadas = new LinkedList<>(graph.aristas());
        ordenadas.sort(Comparator.comparingDouble(e -> e.dato().getWeight()));

        List<Edge<V, D>> aristasKruskal = new LinkedList<>();

        for (Edge<V, D> e : ordenadas) {
            V origen = e.source();
            V destino = e.target();

            List<V> colO = colecciones.get(origen);
            List<V> colD = colecciones.get(destino);

            if (colO != colD) {
                colO.addAll(colD);
                for (V x : colD) colecciones.put(x, colO);
                aristasKruskal.add(e);
            }
        }

        IUndirectedGraph<V, D> mst = new UndirectedGraph<>();
        for (V v : graph.vertices()) mst.agregarVertice(v);
        for (Edge<V, D> e : aristasKruskal) mst.agregarArista(e.source(), e.target(), e.dato());

        return mst;
    }
    /**
     * ejecuta el algoritmo Prim sobre el grafo
     */
    @Override
    public <V, D extends WeightedEdge> IUndirectedGraph<V, D> prim(IUndirectedGraph<V, D> graph, Comparable<V> sourceCriteria) {

        V source = graph.buscarVertice(sourceCriteria);
        if (source == null) return null;

        IUndirectedGraph<V, D> mst = new UndirectedGraph<>();

        for (V v : graph.vertices()) mst.agregarVertice(v);

        Set<V> U = new HashSet<>();
        U.add(source);

        List<V> restantes = new LinkedList<>(graph.vertices());
        restantes.remove(source);

        while (!restantes.isEmpty()) {

            Edge<V, D> mejor = null;
            double mejorPeso = Double.POSITIVE_INFINITY;

            for (V u : U) {
                for (Edge<V, D> e : graph.adyacencias(graph.construirComparable(u))) {

                    V a = e.source();
                    V b = e.target();
                    V otro = a.equals(u) ? b : a;

                    if (restantes.contains(otro)) {
                        double peso = e.dato().getWeight();
                        if (peso < mejorPeso) {
                            mejorPeso = peso;
                            mejor = e;
                        }
                    }
                }
            }

            if (mejor == null) break;

            mst.agregarArista(mejor.source(), mejor.target(), mejor.dato());

            V nuevo = U.contains(mejor.source()) ? mejor.target() : mejor.source();
            U.add(nuevo);
            restantes.remove(nuevo);
        }

        return mst;
    }

    /**
     * Retorna la mínima arista (u,v) del grafo "graph", tal que u está en U, y v está en V.
     * Este método es útil para implementar "Prim"
     */
    @Override
    public <V, D extends WeightedEdge> Edge<V, D> searchMinEdge(IUndirectedGraph<V, D> graph, Collection<V> U, Collection<V> R) {

        Edge<V, D> mejor = null;
        double menor = Double.POSITIVE_INFINITY;

        for (V u : U) {
            for (Edge<V, D> e : graph.adyacencias(graph.construirComparable(u))) {

                V a = e.source();
                V b = e.target();
                V otro = a.equals(u) ? b : a;

                if (R.contains(otro)) {
                    double peso = e.dato().getWeight();
                    if (peso < menor) {
                        menor = peso;
                        mejor = e;
                    }
                }
            }
        }
        return mejor;
    }
    /**
     * Implementa el algoritmo de búsqueda en amplitud
     */
    @Override
    public <V, D> void bea(IUndirectedGraph<V, D> graph, Consumer<V> consumer) {

        if (graph.vertices().isEmpty()) return;

        Set<V> visitados = new HashSet<>();
        LinkedList<V> cola = new LinkedList<>();

        V origen = graph.vertices().iterator().next();
        cola.add(origen);
        visitados.add(origen);

        while (!cola.isEmpty()) {
            V actual = cola.poll();
            consumer.accept(actual);

            for (Edge<V, D> e : graph.adyacencias(graph.construirComparable(actual))) {
                V u = e.source().equals(actual) ? e.target() : e.source();
                if (!visitados.contains(u)) {
                    visitados.add(u);
                    cola.add(u);
                }
            }
        }
    }
    /**
     * Retorna los puntos de articulación del grafo
     */
    @Override
    public <V, D> Collection<V> puntosDeArticulacion(IUndirectedGraph<V, D> graph) {

        Set<V> articulaciones = new HashSet<>();
        Map<V, Integer> disc = new HashMap<>();
        Map<V, Integer> low = new HashMap<>();
        Map<V, V> parent = new HashMap<>();
        int[] tiempo = {0};

        for (V v : graph.vertices()) {
            disc.put(v, -1);
            low.put(v, -1);
            parent.put(v, null);
        }

        for (V v : graph.vertices()) {
            if (disc.get(v) == -1) {
                dfsPuntosArticulacion(graph, v, tiempo, disc, low, parent, articulaciones);
            }
        }

        return articulaciones;
    }

    private <V, D> void dfsPuntosArticulacion(IUndirectedGraph<V, D> graph, V v, int[] tiempo,
            Map<V, Integer> disc, Map<V, Integer> low, Map<V, V> parent, Set<V> articulaciones) {

        disc.put(v, tiempo[0]);
        low.put(v, tiempo[0]);
        tiempo[0]++;

        int hijos = 0;

        for (Edge<V, D> e : graph.adyacencias(graph.construirComparable(v))) {

            V u = e.source().equals(v) ? e.target() : e.source();

            if (disc.get(u) == -1) {
                parent.put(u, v);
                hijos++;

                dfsPuntosArticulacion(graph, u, tiempo, disc, low, parent, articulaciones);

                low.put(v, Math.min(low.get(v), low.get(u)));

                if (parent.get(v) != null && low.get(u) >= disc.get(v)) {
                    articulaciones.add(v);
                }

            } else if (!u.equals(parent.get(v))) {
                low.put(v, Math.min(low.get(v), disc.get(u)));
            }
        }

        if (parent.get(v) == null && hijos >= 2) {
            articulaciones.add(v);
        }
    }
}