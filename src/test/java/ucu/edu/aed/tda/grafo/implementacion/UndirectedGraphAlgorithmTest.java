package ucu.edu.aed.tda.grafo.implementacion;

import org.junit.Before;
import org.junit.Test;
import ucu.edu.aed.tda.grafo.IUndirectedGraph;
import ucu.edu.aed.tda.grafo.model.edge.WeightedEdge;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

public class UndirectedGraphAlgorithmTest {

    private UndirectedGraphAlgorithm algo;
    private IUndirectedGraph<String, WeightedEdge> graph;

    @Before
    public void setUp() {
        algo = new UndirectedGraphAlgorithm();
        graph = new UndirectedGraph<>();
    }

    // PRIM

    @Test
    public void prim_grafoConexo_retornaMST() {
        graph.agregarArista("A", "B", new WeightedEdge(1.0));
        graph.agregarArista("B", "C", new WeightedEdge(2.0));
        graph.agregarArista("A", "C", new WeightedEdge(5.0));

        IUndirectedGraph<String, WeightedEdge> mst = algo.prim(graph, x -> x.equals("A") ? 0 : 1);

        assertEquals(3, mst.cantidadDeVertices());
        assertEquals(2, mst.cantidadDeAristas());
    }

    @Test
    public void prim_verticeOrigenNoExiste_retornaNull() {
        graph.agregarArista("A", "B", new WeightedEdge(1.0));

        IUndirectedGraph<String, WeightedEdge> mst = algo.prim(graph, x -> x.equals("Z") ? 0 : 1);

        assertNull(mst);
    }

    // KRUSKAL

    @Test
    public void kruskal_grafoConexo_retornaMST() {
        graph.agregarArista("A", "B", new WeightedEdge(1.0));
        graph.agregarArista("B", "C", new WeightedEdge(2.0));
        graph.agregarArista("A", "C", new WeightedEdge(5.0));

        IUndirectedGraph<String, WeightedEdge> mst = algo.kruskal(graph);

        assertEquals(3, mst.cantidadDeVertices());
        assertEquals(2, mst.cantidadDeAristas());
    }

    @Test
    public void kruskal_grafoVacio_retornaGrafoVacio() {
        IUndirectedGraph<String, WeightedEdge> mst = algo.kruskal(graph);

        assertEquals(0, mst.cantidadDeVertices());
        assertEquals(0, mst.cantidadDeAristas());
    }

    // BEA

    @Test
    public void bea_grafoConexo_visitaTodosLosVertices() {
        graph.agregarArista("A", "B", new WeightedEdge(1.0));
        graph.agregarArista("B", "C", new WeightedEdge(1.0));

        List<String> visitados = new ArrayList<>();
        algo.bea(graph, visitados::add);

        assertEquals(3, visitados.size());
        assertTrue(visitados.contains("A"));
        assertTrue(visitados.contains("B"));
        assertTrue(visitados.contains("C"));
    }

    @Test
    public void bea_grafoVacio_noLlamaAlConsumer() {
        List<String> visitados = new ArrayList<>();
        algo.bea(graph, visitados::add);

        assertTrue(visitados.isEmpty());
    }

    // PUNTOS DE ARTICULACION

    @Test
    // A-B-C (B es punto de articulación)
    public void puntosDeArticulacion_conPuntos_retornaElCorrecto() {
        graph.agregarArista("A", "B", new WeightedEdge(1.0));
        graph.agregarArista("B", "C", new WeightedEdge(1.0));

        Collection<String> puntos = algo.puntosDeArticulacion(graph);

        assertTrue(puntos.contains("B"));
    }

    @Test
    public void puntosDeArticulacion_sinPuntos_retornaVacio() {
        // Triangulo: ningun vertice es punto de articulación
        graph.agregarArista("A", "B", new WeightedEdge(1.0));
        graph.agregarArista("B", "C", new WeightedEdge(1.0));
        graph.agregarArista("A", "C", new WeightedEdge(1.0));

        Collection<String> puntos = algo.puntosDeArticulacion(graph);

        assertTrue(puntos.isEmpty());
    }
}