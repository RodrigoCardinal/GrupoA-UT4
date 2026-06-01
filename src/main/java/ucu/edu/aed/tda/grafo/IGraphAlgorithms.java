package ucu.edu.aed.tda.grafo;

import java.util.List;
import java.util.function.Consumer;

import ucu.edu.aed.tda.grafo.implementacion.*;
import ucu.edu.aed.tda.grafo.implementacion.FloydWarshallResult;
import ucu.edu.aed.tda.grafo.IDirectedGraphAlgorithms;
import ucu.edu.aed.tda.grafo.model.IGraph;
import ucu.edu.aed.tda.grafo.model.edge.WeightedEdge;
import ucu.edu.aed.tda.grafo.model.result.Path;

/**
 * Libreria de algoritmos vistos en el curso
 */
public interface IGraphAlgorithms extends IDirectedGraphAlgorithms {

    <V, D extends WeightedEdge> DijkstraResult<V> dijkstra(Comparable<V> source, IDirectedIGraph<V, D> grafo);

    <V, D extends WeightedEdge> FloydWarshallResult<V> floyd(IDirectedIGraph<V, D> grafo);

    <V, D extends WeightedEdge> FloydWarshallResult<V> warshall(IDirectedIGraph<V, D> grafo);

    <V, D extends WeightedEdge> V obtenerCentroGrafo(IDirectedIGraph<V, D> grafo);

    <V, D extends WeightedEdge> double obtenerExcentricidad(IDirectedIGraph<V, D> grafo, Comparable<V> vertexCriteria);

    <V, D> void recorridoEnProfundidad(IGraph<V, D> grafo, Comparable<V> sourceCriteria, Consumer<V> consumer);

    <V, D> void recorridoEnAmplitud(IGraph<V, D> grafo, Comparable<V> sourceCriteria, Consumer<V> consumer);

    <V, D extends WeightedEdge> List<Path<V>> obtenerTodosLosCaminos(Comparable<V> source, Comparable<V> target, IGraph<V, D> grafo);

    <V, D> List<V> calcularClasificacionTopologica(IDirectedIGraph<V, D> grafo);
}


