package ucu.edu.aed.tda.grafo.implementacion;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import ucu.edu.aed.tda.grafo.IDirectedIGraph;
import ucu.edu.aed.tda.grafo.model.edge.DirectedEdge;
import ucu.edu.aed.tda.grafo.model.edge.Edge;

public class DirectedIGraph<V, D> implements IDirectedIGraph<V, D> {

    private final Set<V> vertices;
    private final Set<Edge<V, D>> edges;

    public DirectedIGraph() {
        vertices = new HashSet<>();
        edges = new HashSet<>();
    }
    //métodos de IdirectedIgraph
    /**
     * Sucesores (vecinos alcanzables por aristas salientes) de v.
     */
    @Override
    public Set<V> successors(Comparable<V> criteria) {
        Set<V> resultado = new HashSet<>();
        for (Edge<V, D> edge : edges) {
            if (criteria.compareTo(edge.source()) == 0) {
                resultado.add(edge.target());
            }
        }
        return resultado;
    }
    /**
     * Predecesores (vecinos con aristas entrantes) de v.
     */
    @Override
    public Set<V> predecessors(Comparable<V> criteria) {
        Set<V> resultado = new HashSet<>();
        for (Edge<V, D> edge : edges) {
            if (criteria.compareTo(edge.target()) == 0) {
                resultado.add(edge.source());
            }
        }
        return resultado;
    }

 //métodos de IGraph
    /**
     * Agrega un vértice, y retorna true si efectivamente lo agrega
     */
    @Override
    public boolean agregarVertice(V vertex) {
        return vertices.add(vertex);
    }

    @Override
    public V buscarVertice(Comparable<V> criterio) {
        for (V vertex : vertices) {
            if (criterio.compareTo(vertex) == 0) {
                return vertex;
            }
        }
        return null;
    }
    /**
     * Agrega una arista al grafo, indicando con un booleano si la agregó
     */
    @Override
    public boolean agregarArista(V source, V target, D dato) {
        return edges.add(new DirectedEdge<>(source, target, dato));
    }
    /**
     * Elimina una arista del grafo
     */
    @Override
    public boolean eliminarArista(Comparable<V> source, Comparable<V> target) {
        List<Edge<V, D>> eliminar = new LinkedList<>();
        for (Edge<V, D> edge : edges) {
            if (source.compareTo(edge.source()) == 0
                    && target.compareTo(edge.target()) == 0) {
                eliminar.add(edge);
            }
        }
        for (Edge<V, D> edge : eliminar) {
            edges.remove(edge);
        }
        return !eliminar.isEmpty();
    }
    /**
     * remueve un vértice del grafo, retorna true si el vértice fue efectivamente removido
     */
    @Override
    public boolean removerVertice(Comparable<V> criteria) {
        List<V> verticesEliminar = new LinkedList<>();
        List<Edge<V, D>> aristasEliminar = new LinkedList<>();
        for (V vertex : vertices) {
            if (criteria.compareTo(vertex) == 0) {
                verticesEliminar.add(vertex);
            }
        }
        for (Edge<V, D> edge : edges) {
            if (criteria.compareTo(edge.source()) == 0
                    || criteria.compareTo(edge.target()) == 0) {
                aristasEliminar.add(edge);
            }
        }
        for (V vertex : verticesEliminar) {
            vertices.remove(vertex);
        }
        for (Edge<V, D> edge : aristasEliminar) {
            edges.remove(edge);
        }
        return !verticesEliminar.isEmpty();
    }

    /**
     * Conjunto de vértices (preferible devolver vista inmodificable).
     */
    @Override
    public Set<V> vertices() {
        return Set.copyOf(vertices);
    }
    /**
     * Conjunto de aristas (preferible vista inmodificable).
     */
    @Override
    public Set<Edge<V, D>> aristas() {
        return Set.copyOf(edges);
    }
    /**
     * ¿Existe la arista (u -> v) en un grafo dirigido o (u,v) en uno no dirigido?
     */
    @Override
    public boolean existeArista(Comparable<V> sourceCriteria, Comparable<V> targetCriteria) {
        for (Edge<V, D> edge : edges) {
            if (sourceCriteria.compareTo(edge.source()) == 0
                    && targetCriteria.compareTo(edge.target()) == 0) {
                return true;
            }
        }
        return false;
        }

    /**
     * Retorna una arista que tiene un origen y destino source y target respectivamente
     */
    @Override
    public Edge<V, D> obtenerArista(Comparable<V> sourceCriteria, Comparable<V> targetCriteria) {
        for (Edge<V, D> edge : edges) {
            if (sourceCriteria.compareTo(edge.source()) == 0
                    && targetCriteria.compareTo(edge.target()) == 0) {
                return edge;
            }
        }
        return null;
        }
    /**
     * Retorna todas las aristas que el vertex tiene como adyacentes.
     * En caso de que sea un grafo no dirigido, el método "source()"
     * referencia al vértice "verticeCriteria"
     */
    @Override
    public List<Edge<V, D>> adyacencias(Comparable<V> verticeCriteria) {
        List<Edge<V, D>> ady = new LinkedList<>();
        for (Edge<V, D> edge : edges) {
            if (verticeCriteria.compareTo(edge.source()) == 0) {
                ady.add(edge);
            }
        }
        return ady;
    }
    /**
     * Retorna true si el grafo es conexo
     */
    @Override
    public boolean esConexo() {
        int size = vertices.size();
        if (size == 0) {
            return false;
        }
        DirectedGraphAlgorithms algorithms = new DirectedGraphAlgorithms();
        for (V vertex : vertices) {
            List<V> nodes = new LinkedList<>();
            algorithms.recorridoEnProfundidad(this, this.construirComparable(vertex), nodes::add);
            if (size != nodes.size()) {
                return false;
            }
        }
        return true;
    }
    /**
     * vacía el grafo
     */
    @Override
    public void vaciar() {
        edges.clear();
        vertices.clear();
    }
    /**
     * retorna true si el grafo tiene ciclos
     */
    @Override
    public boolean tieneCiclos() {
        for (V vertex : vertices) {
            if (tieneCiclosAux(vertex, new HashSet<>())) {
                return true;
            }
        }
        return false;
    }

    private boolean tieneCiclosAux(V node, Set<V> visitados) {
        if (visitados.contains(node)) {
            return true;
        }
        visitados.add(node);
        List<Edge<V, D>> edgeList = adyacencias(construirComparable(node));
        for (Edge<V, D> edge : edgeList) {
            boolean b = tieneCiclosAux(edge.target(), visitados);
            if (b) {
                return true;
            }
        }
        return false;
    }

}
