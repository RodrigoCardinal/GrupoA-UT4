package ucu.edu.aed.tda.grafo.model.result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class FloydWarshallResult<V> implements IFloydWarshallResult<V> {
    private final List<V> vertices;
    private final double[][] dist;
    private final int[][] next;

    public FloydWarshallResult(List<V> vertices, double[][] dist, int[][] next) {
        this.vertices = vertices;
        this.dist = dist;
        this.next = next;
    }

    public List<V> getVertices() {
        return vertices;
    }

    public double[][] getDistances() {
        return dist;
    }

    public int[][] getNextMatrix() {
        return next;
    }

    private int indexOf(V v) {
        for (int i = 0; i < vertices.size(); i++) {
            if (vertices.get(i).equals(v)) return i;
        }
        return -1;
    }

    @Override
    public double getCost(V source, V target) {
        int i = indexOf(source), j = indexOf(target);
        if (i < 0 || j < 0) return Double.POSITIVE_INFINITY;
        return dist[i][j];
    }

    @Override
    public boolean connected(V source, V target) {
        return Double.isFinite(getCost(source, target));
    }

    @Override
    public List<V> getPath(V source, V target) {
        int i = indexOf(source), j = indexOf(target);
        if (i < 0 || j < 0) return Collections.emptyList();
        if (Double.isInfinite(dist[i][j])) return Collections.emptyList();
        LinkedList<V> path = new LinkedList<>();
        buildPath(i, j, path);
        path.addFirst(vertices.get(i));
        return new ArrayList<>(path);
    }

    private void buildPath(int i, int j, LinkedList<V> acc) {
        int k = next[i][j];
        if (k == -1) {
            if (i != j) acc.add(vertices.get(j));
            return;
        }
        buildPath(i, k, acc);
        buildPath(k, j, acc);
    }
}
