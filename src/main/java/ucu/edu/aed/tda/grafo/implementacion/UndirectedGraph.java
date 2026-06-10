package ucu.edu.aed.tda.grafo.implementacion;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import ucu.edu.aed.tda.grafo.IUndirectedGraph;
import ucu.edu.aed.tda.grafo.model.edge.Edge;
import ucu.edu.aed.tda.grafo.model.edge.UndirectedEdge;

public class UndirectedGraph<V, D> implements IUndirectedGraph<V, D> {

    private final Set<V> vertices;
    private final Set<Edge<V, D>> aristas;

    public UndirectedGraph() {
        vertices = new HashSet<>();
        aristas = new HashSet<>();
    }

    public UndirectedGraph(Collection<V> vertices, Collection<Edge<V, D>> aristas) {
        this();
        this.vertices.addAll(vertices);
        this.aristas.addAll(aristas);
    }

    /**
     * Agrega un vértice, y retorna true si efectivamente lo agrega
     */
    @Override
    public boolean agregarVertice(V vertex) {
        if (vertex!=null){
            return vertices.add(vertex);
        }
        return false;
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
        boolean added = aristas.add(new UndirectedEdge<>(source, target, dato));
        if (added) {
            vertices.add(source);
            vertices.add(target);
        }
        return added;
    }

    /**
     * Elimina una arista del grafo
     */
    @Override
    public boolean eliminarArista(Comparable<V> source, Comparable<V> target) {
        for (Edge<V, D> arista : aristas) {
            V src = arista.source();
            V trg = arista.target();
            boolean cond = (source.compareTo(src) == 0 && target.compareTo(trg) == 0)
                        || (source.compareTo(trg) == 0 && target.compareTo(src) == 0);
            if (cond) {
                return aristas.remove(arista);
            }
        }
        return false;
    }

    /**
     * remueve un vértice del grafo, retorna true si el vértice fue efectivamente removido
     */
    @Override
    public boolean removerVertice(Comparable<V> criteria) {
        Set<V> verticesToRemove = new HashSet<>();
        Set<Edge<V, D>> aristaToRemove = new HashSet<>();
        for (V vertex : vertices) {
            if (criteria.compareTo(vertex) == 0) {
                verticesToRemove.add(vertex);
            }
        }
        for (Edge<V, D> arista : aristas) {
            V src = arista.source();
            V trg = arista.target();
            if (verticesToRemove.contains(src) || verticesToRemove.contains(trg)) {
                aristaToRemove.add(arista);
            }
        }
        boolean b1 = vertices.removeAll(verticesToRemove);
        boolean b2 = aristas.removeAll(aristaToRemove);
        return b1 || b2;
    }

    /**
     * Conjunto de vértices (preferible devolver vista inmodificable).
     */
    @Override
    public Set<V> vertices() {
        return new HashSet<>(vertices);
    }

    /**
     * Conjunto de aristas (preferible vista inmodificable).
     */
    @Override
    public Set<Edge<V, D>> aristas() {
        return new HashSet<>(aristas);
    }

    /**
     * ¿Existe la arista (u -> v) en un grafo dirigido o (u,v) en uno no dirigido?
     */
    @Override
    public boolean existeArista(Comparable<V> sourceCriteria, Comparable<V> targetCriteria) {
        for (Edge<V, D> arista : aristas) {
            V src = arista.source();
            V trg = arista.target();
            boolean cond = (sourceCriteria.compareTo(src) == 0 && targetCriteria.compareTo(trg) == 0)
                        || (sourceCriteria.compareTo(trg) == 0 && targetCriteria.compareTo(src) == 0);
            if (cond) return true;
        }
        return false;
    }

    /**
     * Retorna una arista que tiene un origen y destino source y target respectivamente
     */
    @Override
    public Edge<V, D> obtenerArista(Comparable<V> source, Comparable<V> target) {
        for (Edge<V, D> arista : aristas) {
            V src = arista.source();
            V trg = arista.target();
            boolean cond = (source.compareTo(src) == 0 && target.compareTo(trg) == 0)
                        || (source.compareTo(trg) == 0 && target.compareTo(src) == 0);
            if (cond) return arista;
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
        List<Edge<V, D>> adyacencias = new LinkedList<>();
        for (Edge<V, D> arista : aristas) {
            V src = arista.source();
            V trg = arista.target();
            if (verticeCriteria.compareTo(src) == 0 || verticeCriteria.compareTo(trg) == 0) {
                adyacencias.add(arista);
            }
        }
        return adyacencias;
    }

    /**
     * Retorna true si el grafo es conexo
     */
    @Override
    public boolean esConexo() {
        int size = vertices.size();
        if (size == 0) return false;

        Set<V> visitados = new HashSet<>();
        esConexoAux(vertices.iterator().next(), visitados);
        return visitados.size() == size;
    }

    private void esConexoAux(V vertice, Set<V> visitados) {
        if (visitados.contains(vertice)) return;
        visitados.add(vertice);
        for (Edge<V, D> arista : adyacencias(construirComparable(vertice))) {
            V otro = arista.source().equals(vertice) ? arista.target() : arista.source();
            esConexoAux(otro, visitados);
        }
    }

    /**
     * vacía el grafo
     */
    @Override
    public void vaciar() {
        vertices.clear();
        aristas.clear();
    }

    /**
     * retorna true si el grafo tiene ciclos
     */
    @Override
    public boolean tieneCiclos() {
        Set<V> visitados = new HashSet<>();
        for (V vertex : vertices) {
            if (!visitados.contains(vertex)) {
                if (tieneCiclosAux(vertex, visitados, null)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean tieneCiclosAux(V node, Set<V> visitados, V padre) {
        visitados.add(node);
        for (Edge<V, D> edge : adyacencias(construirComparable(node))) {
            V vecino = edge.source().equals(node) ? edge.target() : edge.source();
            if (!visitados.contains(vecino)) {
                if (tieneCiclosAux(vecino, visitados, node)) return true;
            } else if (!vecino.equals(padre)) {
                return true;
            }
        }
        return false;
    }
}

