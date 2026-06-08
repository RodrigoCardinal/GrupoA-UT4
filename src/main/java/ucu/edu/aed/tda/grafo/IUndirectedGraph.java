package ucu.edu.aed.tda.grafo;

import ucu.edu.aed.tda.grafo.model.IGraph;
import ucu.edu.aed.tda.grafo.model.edge.Edge;

import java.util.List;
import java.util.Set;

public interface IUndirectedGraph<V, D> extends IGraph<V, D> {

    boolean agregarVertice(V vertex);

    V buscarVertice(Comparable<V> criterio);

    boolean agregarArista(V source, V target, D dato);

    boolean eliminarArista(Comparable<V> source, Comparable<V> target);

    boolean removerVertice(Comparable<V> criteria);

    Set<V> vertices();

    Set<Edge<V, D>> aristas();

    boolean existeArista(Comparable<V> source, Comparable<V> target);

    Edge<V, D> obtenerArista(Comparable<V> source, Comparable<V> target);

    List<Edge<V, D>> adyacencias(Comparable<V> verticeCriteria);

    boolean esConexo();

    void vaciar();

    boolean tieneCiclos();
}