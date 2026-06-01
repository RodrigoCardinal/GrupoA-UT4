package ucu.edu.aed.tda.grafo.implementacion;

import ucu.edu.aed.tda.grafo.IDirectedIGraph;
import ucu.edu.aed.tda.grafo.IGraphAlgorithms;
import ucu.edu.aed.tda.grafo.model.IGraph;
import ucu.edu.aed.tda.grafo.model.edge.Edge;
import ucu.edu.aed.tda.grafo.model.edge.WeightedEdge;
import ucu.edu.aed.tda.grafo.model.result.IDijkstraResult;
import ucu.edu.aed.tda.grafo.model.result.IFloydWarshallResult;
import ucu.edu.aed.tda.grafo.model.result.Path;

import java.util.*;
import java.util.function.Consumer;

public class GraphAlgorithms implements IGraphAlgorithms {

    // ─────────────────────────────────────────────
    // DIJKSTRA
    // ─────────────────────────────────────────────

    /*
     * Algoritmo de Dijkstra: camino más corto desde un origen a todos los demás vértices.
     *
     * Idea:
     *  - Arrancamos con costo 0 en el origen e infinito en todos los demás.
     *  - En cada paso, tomamos el vértice no visitado con menor costo acumulado.
     *  - Para cada vecino, si encontramos un camino más barato, actualizamos.
     *
     * Estructuras:
     *  - distancias: costo mínimo conocido desde source a cada vértice
     *  - predecesor: vértice anterior en el camino más corto (para reconstruir)
     *  - visitados:  vértices ya "cerrados" (su distancia es definitiva)
     */
    
    @Override
    public <V, D extends WeightedEdge> IDijkstraResult<V> dijkstra(Comparable<V> source, IDirectedIGraph<V, D> grafo) {
        Map<V, Double> distancias = new HashMap<>();
        Map<V, V> predecesor = new HashMap<>();
        Set<V> visitados = new HashSet<>();

        // Inicializar todas las distancias en infinito
        for (V v : grafo.vertices()) {
            distancias.put(v, Double.POSITIVE_INFINITY);
        }

        // El origen tiene costo 0
        V sourceVertex = grafo.buscarVertice(source);
        distancias.put(sourceVertex, 0.0);

        // PriorityQueue: primero el vértice con menor distancia acumulada
        PriorityQueue<V> pq = new PriorityQueue<>(Comparator.comparingDouble(distancias::get));
        pq.add(sourceVertex);

        while (!pq.isEmpty()) {
            V actual = pq.poll();
            if (visitados.contains(actual)) continue;
            visitados.add(actual);

            // Revisar cada arista saliente del vértice actual
            for (Edge<V, D> edge : grafo.adyacencias(grafo.construirComparable(actual))) {
                V vecino = edge.target();
                double nuevoCosto = distancias.get(actual) + edge.dato().getWeight();

                // Si encontramos un camino más corto, actualizamos
                if (nuevoCosto < distancias.getOrDefault(vecino, Double.POSITIVE_INFINITY)) {
                    distancias.put(vecino, nuevoCosto);
                    predecesor.put(vecino, actual);
                    pq.add(vecino);
                }
            }
        }

        return new DijkstraResult<>(sourceVertex, distancias, predecesor);
    }

    // ─────────────────────────────────────────────
    // FLOYD
    // ─────────────────────────────────────────────

    /*
     * Floyd-Warshall: calcula el camino más corto entre TODOS los pares de vértices.
     *
     * Idea:
     *  - Construimos una tabla dist[i][j] = costo mínimo de i a j.
     *  - Para cada vértice intermedio k, preguntamos:
     *    "¿Es más barato ir de i a j pasando por k?"
     *    Si dist[i][k] + dist[k][j] < dist[i][j] → actualizamos.
     *
     * next[i][j] guarda el siguiente vértice en el camino de i a j (para reconstruir).
     */
    @Override
    public <V, D extends WeightedEdge> IFloydWarshallResult<V> floyd(IDirectedIGraph<V, D> grafo) {
        List<V> vertices = new ArrayList<>(grafo.vertices());
        Map<V, Map<V, Double>> dist = new HashMap<>();
        Map<V, Map<V, V>> next = new HashMap<>();

        // Inicializar: infinito por defecto, 0 en diagonal
        for (V v : vertices) {
            dist.put(v, new HashMap<>());
            next.put(v, new HashMap<>());
            for (V u : vertices) {
                dist.get(v).put(u, v.equals(u) ? 0.0 : Double.POSITIVE_INFINITY);
            }
        }

        // Cargar pesos reales de las aristas
        for (Edge<V, D> edge : grafo.aristas()) {
            dist.get(edge.source()).put(edge.target(), edge.dato().getWeight());
            next.get(edge.source()).put(edge.target(), edge.target());
        }

        // Triple loop: para cada vértice intermedio k
        for (V k : vertices) {
            for (V i : vertices) {
                for (V j : vertices) {
                    double porK = dist.get(i).get(k) + dist.get(k).get(j);
                    if (porK < dist.get(i).get(j)) {
                        dist.get(i).put(j, porK);
                        next.get(i).put(j, next.get(i).get(k)); // el siguiente paso de i->j es el mismo que i->k
                    }
                }
            }
        }

        return new FloydWarshallResult<>(dist, next);
    }

    // ─────────────────────────────────────────────
    // WARSHALL
    // ─────────────────────────────────────────────

    /*
     * Warshall: versión simplificada que solo calcula CONECTIVIDAD (no costos).
     * dist[i][j] = 0.0 si hay camino, infinito si no hay.
     *
     * Misma estructura que Floyd pero sin considerar pesos —
     * solo importa si existe algún camino entre cada par.
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

        // Marcar aristas existentes como conectadas (costo 1.0 simbólico)
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

    // ─────────────────────────────────────────────
    // CENTRO DEL GRAFO Y EXCENTRICIDAD
    // ─────────────────────────────────────────────

    /*
     * Excentricidad de un vértice: la mayor distancia mínima hacia cualquier otro vértice.
     * Representa "lo lejos que está el vértice más lejano".
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

    /*
     * Centro del grafo: el vértice con menor excentricidad.
     * Es decir, el que está "más cerca" de todos los demás en el peor caso.
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

    // ─────────────────────────────────────────────
    // DFS - Recorrido en Profundidad
    // ─────────────────────────────────────────────

    /*
     * DFS: explora tan lejos como sea posible antes de retroceder.
     * Usa recursión — cada vez que visita un vértice, llama al consumer
     * y luego visita sus vecinos no visitados.
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

    // ─────────────────────────────────────────────
    // BFS - Recorrido en Amplitud
    // ─────────────────────────────────────────────

    /*
     * BFS: explora nivel por nivel — primero todos los vecinos directos,
     * luego los vecinos de los vecinos, etc.
     * Usa una cola (Queue) en lugar de recursión.
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

    // ─────────────────────────────────────────────
    // TODOS LOS CAMINOS
    // ─────────────────────────────────────────────

    /*
     * Encuentra todos los caminos posibles entre source y target usando backtracking.
     *
     * Idea: DFS explorando todos los caminos, sin marcar nodos como
     * permanentemente visitados — solo los marcamos durante la exploración
     * actual y los desmarcamos al retroceder (backtrack).
     */
    @Override
    public <V, D extends WeightedEdge> List<Path<V>> obtenerTodosLosCaminos(Comparable<V> source, Comparable<V> target, IGraph<V, D> grafo) {
        List<Path<V>> resultado = new ArrayList<>();
        V sourceVertex = grafo.buscarVertice(source);
        V targetVertex = grafo.buscarVertice(target);
        if (sourceVertex == null || targetVertex == null) return resultado;

        LinkedList<V> caminoActual = new LinkedList<>();
        caminoActual.add(sourceVertex);

        dfsAllPaths(grafo, sourceVertex, targetVertex, new HashSet<>(Set.of(sourceVertex)), caminoActual, 0.0, resultado);
        return resultado;
    }

    private <V, D extends WeightedEdge> void dfsAllPaths(IGraph<V, D> grafo, V actual, V target,
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

                // Backtrack
                caminoActual.removeLast();
                visitados.remove(vecino);
            }
        }
    }

    // ─────────────────────────────────────────────
    // CLASIFICACIÓN TOPOLÓGICA
    // ─────────────────────────────────────────────

    /*
     * Ordenamiento topológico: ordena los vértices de un DAG (grafo dirigido sin ciclos)
     * de forma que para toda arista A → B, A aparece antes que B.
     *
     * Usa DFS con un stack — cuando un vértice termina de explorar todos sus vecinos,
     * se apila. Al final, el stack se desapila para obtener el orden.
     */
    @Override
    public <V, D> List<V> calcularClasificacionTopologica(IDirectedIGraph<V, D> grafo) {
        Set<V> visitados = new HashSet<>();
        Deque<V> stack = new ArrayDeque<>();

        for (V v : grafo.vertices()) {
            if (!visitados.contains(v)) {
                topoSort(grafo, v, visitados, stack);
            }
        }

        List<V> orden = new ArrayList<>();
        while (!stack.isEmpty()) orden.add(stack.pop());
        return orden;
    }

    private <V, D> void topoSort(IDirectedIGraph<V, D> grafo, V actual, Set<V> visitados, Deque<V> stack) {
        visitados.add(actual);
        for (Edge<V, D> edge : grafo.adyacencias(grafo.construirComparable(actual))) {
            if (!visitados.contains(edge.target())) {
                topoSort(grafo, edge.target(), visitados, stack);
            }
        }
        stack.push(actual); // se apila cuando ya exploró todos sus vecinos
    }
}