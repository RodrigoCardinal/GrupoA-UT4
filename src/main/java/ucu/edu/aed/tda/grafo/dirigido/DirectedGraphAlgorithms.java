package ucu.edu.aed.tda.grafo.dirigido;

import ucu.edu.aed.tda.grafo.model.IGraph;
import ucu.edu.aed.tda.grafo.model.edge.Edge;
import ucu.edu.aed.tda.grafo.model.edge.WeightedEdge;
import ucu.edu.aed.tda.grafo.model.result.DijkstraResult;
import ucu.edu.aed.tda.grafo.model.result.FloydWarshallResult;
import ucu.edu.aed.tda.grafo.model.result.IDijkstraResult;
import ucu.edu.aed.tda.grafo.model.result.IFloydWarshallResult;
import ucu.edu.aed.tda.grafo.model.result.Path;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;

public class DirectedGraphAlgorithms implements IDirectedGraphAlgorithms {

    @Override
    public <V, D extends WeightedEdge> IDijkstraResult<V> dijkstra(
            Comparable<V> source, IDirectedIGraph<V, D> grafo) {

        V src = grafo.buscarVertice(source);
        Map<V, Double> dist = new LinkedHashMap<>();
        Map<V, V> prev = new HashMap<>();
        Set<V> S = new HashSet<>();

        for (V v : grafo.vertices()) dist.put(v, Double.POSITIVE_INFINITY);
        dist.put(src, 0.0);

        // S inicial sólo contiene origen
        S.add(src);
        // inicializar D con costos directos desde origen
        for (Edge<V, D> e : grafo.adyacencias(grafo.construirComparable(src))) {
            dist.put(e.target(), e.dato().getWeight());
            prev.put(e.target(), src);
        }

        int n = grafo.cantidadDeVertices();
        for (int i = 1; i < n; i++) {
            // elegir w en V-S con dist mínimo
            V w = null;
            double min = Double.POSITIVE_INFINITY;
            for (V v : grafo.vertices()) {
                if (!S.contains(v) && dist.get(v) < min) {
                    min = dist.get(v);
                    w = v;
                }
            }
            if (w == null) break;
            S.add(w);

            // relajar aristas desde w
            for (Edge<V, D> e : grafo.adyacencias(grafo.construirComparable(w))) {
                V t = e.target();
                if (S.contains(t)) continue;
                double nd = dist.get(w) + e.dato().getWeight();
                if (nd < dist.get(t)) {
                    dist.put(t, nd);
                    prev.put(t, w);
                }
            }
        }

        return new DijkstraResult<>(src, dist, prev);
    }

    @Override
    public <V, D extends WeightedEdge> IFloydWarshallResult<V> floyd(IDirectedIGraph<V, D> grafo) {
        List<V> vs = new ArrayList<>(grafo.vertices());
        int n = vs.size();
        double[][] A = new double[n][n];
        int[][] P = new int[n][n];

        // Inicialización: A[i,j] = C[i,j], 0 en diagonal, ∞ si no hay arco. P[i,j] = -1
        for (int i = 0; i < n; i++) {
            Arrays.fill(A[i], Double.POSITIVE_INFINITY);
            Arrays.fill(P[i], -1);
            A[i][i] = 0.0;
        }
        for (int i = 0; i < n; i++) {
            for (Edge<V, D> e : grafo.adyacencias(grafo.construirComparable(vs.get(i)))) {
                int j = vs.indexOf(e.target());
                if (j >= 0) A[i][j] = e.dato().getWeight();
            }
        }

        // Iteración Floyd
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (A[i][k] + A[k][j] < A[i][j]) {
                        A[i][j] = A[i][k] + A[k][j];
                        P[i][j] = k;
                    }
                }
            }
        }

        return new FloydWarshallResult<>(vs, A, P);
    }

    @Override
    public <V, D extends WeightedEdge> IFloydWarshallResult<V> warshall(IDirectedIGraph<V, D> grafo) {
        List<V> vs = new ArrayList<>(grafo.vertices());
        int n = vs.size();
        boolean[][] A = new boolean[n][n];

        for (int i = 0; i < n; i++) {
            for (Edge<V, D> e : grafo.adyacencias(grafo.construirComparable(vs.get(i)))) {
                int j = vs.indexOf(e.target());
                if (j >= 0) A[i][j] = true;
            }
        }
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (!A[i][j]) A[i][j] = A[i][k] && A[k][j];
                }
            }
        }

        // Empaquetar como FloydWarshallResult (1.0 = conectado, ∞ no)
        double[][] dist = new double[n][n];
        int[][] next = new int[n][n];
        for (int i = 0; i < n; i++) {
            Arrays.fill(next[i], -1);
            for (int j = 0; j < n; j++) {
                dist[i][j] = A[i][j] ? 1.0 : Double.POSITIVE_INFINITY;
                if (i == j) dist[i][j] = 0.0;
            }
        }
        return new FloydWarshallResult<>(vs, dist, next);
    }

    @Override
    public <V, D extends WeightedEdge> V obtenerCentroGrafo(IDirectedIGraph<V, D> grafo) {
        IFloydWarshallResult<V> r = floyd(grafo);
        List<V> vs = ((FloydWarshallResult<V>) r).getVertices();
        double[][] A = ((FloydWarshallResult<V>) r).getDistances();
        int n = vs.size();

        V centro = null;
        double mejorExc = Double.POSITIVE_INFINITY;
        for (int j = 0; j < n; j++) {
            double exc = 0.0;
            for (int i = 0; i < n; i++) {
                if (i == j) continue;
                if (A[i][j] > exc) exc = A[i][j];
            }
            if (exc < mejorExc) {
                mejorExc = exc;
                centro = vs.get(j);
            }
        }
        return centro;
    }

    @Override
    public <V, D extends WeightedEdge> double obtenerExcentricidad(
            IDirectedIGraph<V, D> grafo, Comparable<V> vertexCriteria) {
        IFloydWarshallResult<V> r = floyd(grafo);
        List<V> vs = ((FloydWarshallResult<V>) r).getVertices();
        double[][] A = ((FloydWarshallResult<V>) r).getDistances();
        V target = grafo.buscarVertice(vertexCriteria);
        int j = vs.indexOf(target);
        if (j < 0) return Double.POSITIVE_INFINITY;
        double exc = 0.0;
        for (int i = 0; i < vs.size(); i++) {
            if (i == j) continue;
            if (A[i][j] > exc) exc = A[i][j];
        }
        return exc;
    }

    @Override
    public <V, D extends WeightedEdge> List<Path<V>> obtenerTodosLosCaminos(
            Comparable<V> source, Comparable<V> target, IGraph<V, D> grafo) {
        V src = grafo.buscarVertice(source);
        V dst = grafo.buscarVertice(target);
        List<Path<V>> result = new ArrayList<>();
        if (src == null || dst == null) return result;

        LinkedList<V> path = new LinkedList<>();
        Set<V> visited = new HashSet<>();
        path.add(src);
        visited.add(src);
        dfsPaths(src, dst, grafo, path, visited, 0.0, result);
        return result;
    }

    private <V, D extends WeightedEdge> void dfsPaths(
            V cur, V dst, IGraph<V, D> grafo,
            LinkedList<V> path, Set<V> visited, double cost, List<Path<V>> out) {
        if (cur.equals(dst)) {
            out.add(new Path<>(new ArrayList<>(path), cost));
            return;
        }
        for (Edge<V, D> e : grafo.adyacencias(grafo.construirComparable(cur))) {
            V w = e.target();
            if (visited.contains(w)) continue;
            visited.add(w);
            path.add(w);
            dfsPaths(w, dst, grafo, path, visited, cost + e.dato().getWeight(), out);
            path.removeLast();
            visited.remove(w);
        }
    }

    @Override
    public <V, D> void recorridoEnProfundidad(
            IGraph<V, D> grafo, Comparable<V> sourceCriteria, Consumer<V> consumer) {
        V src = grafo.buscarVertice(sourceCriteria);
        if (src == null) return;
        Set<V> visited = new HashSet<>();
        dfs(src, grafo, visited, consumer);
    }

    private <V, D> void dfs(V v, IGraph<V, D> grafo, Set<V> visited, Consumer<V> consumer) {
        if (!visited.add(v)) return;
        consumer.accept(v);
        for (Edge<V, D> e : grafo.adyacencias(grafo.construirComparable(v))) {
            if (!visited.contains(e.target())) dfs(e.target(), grafo, visited, consumer);
        }
    }

    @Override
    public <V, D> void recorridoEnAmplitud(
            IGraph<V, D> grafo, Comparable<V> sourceCriteria, Consumer<V> consumer) {
        V src = grafo.buscarVertice(sourceCriteria);
        if (src == null) return;
        Set<V> visited = new HashSet<>();
        Queue<V> q = new ArrayDeque<>();
        q.add(src);
        visited.add(src);
        while (!q.isEmpty()) {
            V cur = q.poll();
            consumer.accept(cur);
            for (Edge<V, D> e : grafo.adyacencias(grafo.construirComparable(cur))) {
                if (visited.add(e.target())) q.add(e.target());
            }
        }
    }

    @Override
    public <V, D> List<V> calcularClasificacionTopologica(IDirectedIGraph<V, D> grafo) {
        // BPF con post-orden, después invertir
        Set<V> visited = new HashSet<>();
        Deque<V> stack = new ArrayDeque<>();
        for (V v : grafo.vertices()) {
            if (!visited.contains(v)) topoDfs(v, grafo, visited, stack);
        }
        return new ArrayList<>(stack);
    }

    private <V, D> void topoDfs(V v, IDirectedIGraph<V, D> grafo, Set<V> visited, Deque<V> stack) {
        visited.add(v);
        for (Edge<V, D> e : grafo.adyacencias(grafo.construirComparable(v))) {
            if (!visited.contains(e.target())) topoDfs(e.target(), grafo, visited, stack);
        }
        stack.push(v);
    }
}
