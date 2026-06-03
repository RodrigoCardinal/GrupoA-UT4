package ucu.edu.aed.tda.grafo.implementacion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;

import ucu.edu.aed.tda.grafo.IDirectedGraphAlgorithms;
import ucu.edu.aed.tda.grafo.IDirectedIGraph;
import ucu.edu.aed.tda.grafo.model.IGraph;
import ucu.edu.aed.tda.grafo.model.edge.Edge;
import ucu.edu.aed.tda.grafo.model.edge.WeightedEdge;
import ucu.edu.aed.tda.grafo.model.result.IDijkstraResult;
import ucu.edu.aed.tda.grafo.model.result.IFloydWarshallResult;
import ucu.edu.aed.tda.grafo.model.result.Path;

public class DirectedGraphAlgorithms implements IDirectedGraphAlgorithms
{
        /**
     * ejecuta el algoritmos Dijkstra sobre el grafo pasado y utilizando source como vértice de origen
     */
    @Override
    public <V, D extends WeightedEdge> IDijkstraResult<V> dijkstra(Comparable<V> source, IDirectedIGraph<V, D> grafo) 
    {
        Set<V> vertices = grafo.vertices();
        Map<V, Double> dist = new HashMap<>();
        Map<V, V> prev = new HashMap<>();
        Set<V> visited = new HashSet<>();
        for (V v : vertices) {
            dist.put(v, Double.POSITIVE_INFINITY);
            prev.put(v, null);
        }
        V origin = grafo.buscarVertice(source);
        dist.put(origin, 0.0);
        for (V v : vertices) {
            if (v.equals(origin)) continue;
            Edge<V, D> edge = grafo.obtenerArista(source, grafo.construirComparable(v));
            if (edge != null) {
                dist.put(v, edge.dato().getWeight());
                prev.put(v, origin);
            }
        }
        visited.add(origin);
        while (visited.size() < vertices.size()) {
            V w = null;
            double minDist = Double.POSITIVE_INFINITY;
            for (V v : vertices) {
                if (!visited.contains(v) && dist.get(v) < minDist) {
                    minDist = dist.get(v);
                    w = v;
                }
            }
            if (w == null) {
                break;
            }
            visited.add(w);
            Comparable<V> wComp = grafo.construirComparable(w);
            for (V v : vertices) {
                if (visited.contains(v)) continue;
                Edge<V, D> edge = grafo.obtenerArista(wComp, grafo.construirComparable(v));
                if (edge == null) continue;
                double newDist = dist.get(w) + edge.dato().getWeight();
                if (newDist < dist.get(v)) {
                    dist.put(v, newDist);
                    prev.put(v, w);
                }
            }
        }
        return new DijkstraResult<>(origin, dist, prev);
    }

        /**
     * ejecuta Floyd sobre el grafo pasado, sabiendo que el grafo es weighted
     */
    @Override
    public <V, D extends WeightedEdge> IFloydWarshallResult<V> floyd(IDirectedIGraph<V, D> grafo) {
    List<V> vertices = new ArrayList<>(grafo.vertices());
    Map<V, Map<V, Double>> dist = new HashMap<>();
    Map<V, Map<V, V>> next = new HashMap<>();
    for (V v : vertices) {
        dist.put(v, new HashMap<>());
        next.put(v, new HashMap<>());
        for (V u : vertices) {
            if (v.equals(u)) {
                dist.get(v).put(u, 0.0);
                next.get(v).put(u, v);
            } else {
                Edge<V, D> edge = grafo.obtenerArista(
                        grafo.construirComparable(v),
                        grafo.construirComparable(u));
                if (edge != null) {
                    dist.get(v).put(u, edge.dato().getWeight());
                    next.get(v).put(u, u);
                } else {
                    dist.get(v).put(u, Double.POSITIVE_INFINITY);
                }
            }
        }
    }
    for (V k : vertices) {
        for (V i : vertices) {
            for (V j : vertices) {
                if (!Double.isInfinite(dist.get(i).get(k))
                        && !Double.isInfinite(dist.get(k).get(j))
                        && dist.get(i).get(k) + dist.get(k).get(j) < dist.get(i).get(j)) {
                    dist.get(i).put(j,
                            dist.get(i).get(k) + dist.get(k).get(j));
                    next.get(i).put(j, next.get(i).get(k));
                }
            }
        }
    }
    return new FloydWarshallResult<>(dist, next);
}
    /**
     * ejecuta Warshall sobre el grafo pasado, sabiendo que el grafo es weighted
     */
    @Override
    public <V, D extends WeightedEdge> IFloydWarshallResult<V> warshall(IDirectedIGraph<V, D> grafo) {
        List<V> vertices = new ArrayList<>(grafo.vertices());
        Map<V, Map<V, Double>> dist = new HashMap<>();
        Map<V, Map<V, V>> next = new HashMap<>();
        for (V v : vertices) {
            dist.put(v, new HashMap<>());
            next.put(v, new HashMap<>());
            for (V u : vertices) {
                dist.get(v).put(u, v.equals(u) ? 0.0 : Double.POSITIVE_INFINITY);
            }
        }
        for (Edge<V, D> edge : grafo.aristas()) {
            dist.get(edge.source()).put(edge.target(), 1.0);
            next.get(edge.source()).put(edge.target(), edge.target());
        }
        for (V k : vertices) {
            for (V i : vertices) {
                for (V j : vertices) {
                    if (!Double.isInfinite(dist.get(i).get(k)) && !Double.isInfinite(dist.get(k).get(j))) {
                        dist.get(i).put(j, 1.0);
                        if (next.get(i).get(j) == null) {
                            next.get(i).put(j, next.get(i).get(k));
                        }
                    }
                }
            }
        }
        return new FloydWarshallResult<>(dist, next);
    }

    /**
     * Calcula el centro del grafo
     */
    @Override
    public <V, D extends WeightedEdge> V obtenerCentroGrafo(IDirectedIGraph<V, D> grafo) {
        V centro = null;
        double minExcentricidad = Double.POSITIVE_INFINITY;
        for (V v : grafo.vertices()) {
            double exc = obtenerExcentricidad(grafo, grafo.construirComparable(v));
            if (exc < minExcentricidad) {
                minExcentricidad = exc;
                centro = v;
            }
        }
        return centro;
    }
    /**
     * Calcula la excentrecidad de un vértice
     */
    @Override
    public <V, D extends WeightedEdge> double obtenerExcentricidad(IDirectedIGraph<V, D> grafo, Comparable<V> vertexCriteria) {
        IDijkstraResult<V> result = dijkstra(vertexCriteria, grafo);
        double maxDist = 0.0;
        for (V v : grafo.vertices()) {
            double costo = result.getCost(v);
            if (!Double.isInfinite(costo) && costo > maxDist) {
                maxDist = costo;
            }
        }
        return maxDist;
    }
        /**
     * Retorna todos los caminos posibles para ir de "source" a "target"
     */
    @Override
    public <V, D extends WeightedEdge> List<Path<V>> obtenerTodosLosCaminos(
            Comparable<V> source, Comparable<V> target, IGraph<V, D> grafo) {
        List<Path<V>> resultado = new ArrayList<>();
        V sourceVertex = grafo.buscarVertice(source);
        V targetVertex = grafo.buscarVertice(target);
        if (sourceVertex == null || targetVertex == null) return resultado;
        Set<V> visitados = new HashSet<>();
        visitados.add(sourceVertex);
        LinkedList<V> caminoActual = new LinkedList<>();
        caminoActual.add(sourceVertex);
        dfsAllPaths(grafo, sourceVertex, targetVertex, visitados, caminoActual, 0.0, resultado);
        return resultado;
    }

    private <V, D extends WeightedEdge> void dfsAllPaths(
            IGraph<V, D> grafo, V actual, V target,
            Set<V> visitados, LinkedList<V> caminoActual,
            double costoActual, List<Path<V>> resultado) {
        if (actual.equals(target)) {
            resultado.add(new Path<>(new ArrayList<>(caminoActual), costoActual));
            return;
        }
        for (Edge<V, D> edge : grafo.adyacencias(grafo.construirComparable(actual))) {
            V vecino = edge.target();
            if (!visitados.contains(vecino)) {
                visitados.add(vecino);
                caminoActual.add(vecino);
                dfsAllPaths(grafo, vecino, target, visitados, caminoActual,
                        costoActual + edge.dato().getWeight(), resultado);
                caminoActual.removeLast();
                visitados.remove(vecino);
            }
        }
    }    
    /**
     * Aplica un recorrido en profundidad del grafo y pasa los datos al consumer
     */
    @Override
    public <V, D> void recorridoEnProfundidad(IGraph<V, D> grafo, Comparable<V> sourceCriteria, Consumer<V> consumer) {
        V source = grafo.buscarVertice(sourceCriteria);
        if (source == null) return;
        dfs(grafo, source, new HashSet<>(), consumer);
    }

    private <V, D> void dfs(IGraph<V, D> grafo, V actual, Set<V> visitados, Consumer<V> consumer) {
        visitados.add(actual);
        consumer.accept(actual);
        for (Edge<V, D> edge : grafo.adyacencias(grafo.construirComparable(actual))) {
            if (!visitados.contains(edge.target())) {
                dfs(grafo, edge.target(), visitados, consumer);
            }
        }
    }
    /**
     * Aplica un recorrido en amplitud del grafo y pasa los datos al consumer
     */
    @Override
    public <V, D> void recorridoEnAmplitud(IGraph<V, D> grafo, Comparable<V> sourceCriteria, Consumer<V> consumer) {
        V source = grafo.buscarVertice(sourceCriteria);
        if (source == null) return;
        Set<V> visitados = new HashSet<>();
        Queue<V> cola = new LinkedList<>();
        cola.add(source);
        visitados.add(source);
        while (!cola.isEmpty()) {
            V actual = cola.poll();
            consumer.accept(actual);
            for (Edge<V, D> edge : grafo.adyacencias(grafo.construirComparable(actual))) {
                if (!visitados.contains(edge.target())) {
                    visitados.add(edge.target());
                    cola.add(edge.target());
                }
            }
        }
    }

    /**
     * Calcula la clasificación topológica del grafo actual
     */
    @Override
    public <V, D> List<V> calcularClasificacionTopologica(IDirectedIGraph<V, D> grafo) {
        Set<V> vertices = grafo.vertices();
        Map<V, Integer> inDegree = new HashMap<>();
        for (V v : vertices) inDegree.put(v, 0);
        for (V v : vertices) {
            for (V s : grafo.successors(grafo.construirComparable(v))) {
                inDegree.merge(s, 1, Integer::sum);
            }
        }
        Queue<V> queue = new LinkedList<>();
        for (V v : vertices) {
            if (inDegree.get(v) == 0) queue.add(v);
        }
        List<V> order = new ArrayList<>();
        while (!queue.isEmpty()) {
            V current = queue.poll();
            order.add(current);
            for (V s : grafo.successors(grafo.construirComparable(current))) {
                if (inDegree.merge(s, -1, Integer::sum) == 0) queue.add(s);
            }
        }
        if (order.size() != vertices.size()) {
            throw new IllegalStateException("El grafo contiene un ciclo; no es posible el orden topológico.");
        }
        return order;
    }

}