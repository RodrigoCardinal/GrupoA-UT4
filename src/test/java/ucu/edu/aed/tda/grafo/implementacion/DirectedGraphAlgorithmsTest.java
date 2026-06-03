package ucu.edu.aed.tda.grafo.implementacion;

import org.junit.Before;
import org.junit.Test;
import ucu.edu.aed.tda.grafo.model.edge.WeightedEdge;
import ucu.edu.aed.tda.grafo.model.result.IDijkstraResult;
import ucu.edu.aed.tda.grafo.model.result.IFloydWarshallResult;
import ucu.edu.aed.tda.grafo.model.result.Path;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DirectedGraphAlgorithmsTest {

    private static final double DELTA = 1e-9;

    private DirectedGraphAlgorithms algoritmos;
    private DirectedIGraph<String, WeightedEdge> grafo;

    @Before
    public void setUp() {
        algoritmos = new DirectedGraphAlgorithms();
        grafo = new DirectedIGraph<>();

        // Grafo dirigido base (DAG):

        grafo.agregarVertice("A");
        grafo.agregarVertice("B");
        grafo.agregarVertice("C");
        grafo.agregarVertice("D");
        grafo.agregarVertice("E");

        grafo.agregarArista("A", "B", new WeightedEdge(1));
        grafo.agregarArista("A", "C", new WeightedEdge(4));
        grafo.agregarArista("B", "C", new WeightedEdge(2));
        grafo.agregarArista("B", "D", new WeightedEdge(5));
        grafo.agregarArista("C", "D", new WeightedEdge(1));
        grafo.agregarArista("D", "E", new WeightedEdge(3));
    }

    @Test
    public void dijkstraDevuelveCostosMinimosDesdeOrigen() {
        IDijkstraResult<String> res = algoritmos.dijkstra("A", grafo);

        assertEquals(0.0, res.getCost("A"), DELTA);
        assertEquals(1.0, res.getCost("B"), DELTA);
        assertEquals(3.0, res.getCost("C"), DELTA);   
        assertEquals(4.0, res.getCost("D"), DELTA);   
        assertEquals(7.0, res.getCost("E"), DELTA);   }

    @Test
    public void dijkstraDevuelveCaminoMinimo() {
        IDijkstraResult<String> res = algoritmos.dijkstra("A", grafo);
        assertEquals(Arrays.asList("A", "B", "C", "D", "E"), res.getPath("E"));
    }

    @Test
    public void dijkstraVerticeInalcanzableDaInfinito() {
        grafo.agregarVertice("Z");
        IDijkstraResult<String> res = algoritmos.dijkstra("A", grafo);
        assertTrue(Double.isInfinite(res.getCost("Z")));
        assertTrue(res.getPath("Z").isEmpty());
    }

    //  FLOYD 

    @Test
    public void floydCalculaDistanciasMinimasEntreTodosLosPares() {
        IFloydWarshallResult<String> res = algoritmos.floyd(grafo);

        assertEquals(0.0, res.getCost("A", "A"), DELTA);
        assertEquals(1.0, res.getCost("A", "B"), DELTA);
        assertEquals(3.0, res.getCost("A", "C"), DELTA);
        assertEquals(4.0, res.getCost("A", "D"), DELTA);
        assertEquals(7.0, res.getCost("A", "E"), DELTA);
        assertEquals(3.0, res.getCost("B", "D"), DELTA); 
        assertEquals(1.0, res.getCost("C", "D"), DELTA);
    }

    @Test
    public void floydSinCaminoDevuelveInfinito() {
        // De E no sale ninguna arista, no puede llegar a A
        IFloydWarshallResult<String> res = algoritmos.floyd(grafo);
        assertTrue(Double.isInfinite(res.getCost("E", "A")));
    }

    //  WARSHALL 
    @Test
    public void warshallDetectaConectividad() {
        IFloydWarshallResult<String> res = algoritmos.warshall(grafo);

        assertTrue(res.connected("A", "E"));
        assertTrue(res.connected("B", "D"));
        assertTrue(res.connected("A", "D"));
        assertFalse(res.connected("E", "A"));
        assertFalse(res.connected("D", "A"));
    }

    // EXCENTRICIDAD / CENTRO 

    @Test
    public void excentricidadEsLaMayorDistanciaMinimaDesdeElVertice() {
       
        double exc = algoritmos.obtenerExcentricidad(grafo, "A");
        assertEquals(7.0, exc, DELTA);
    }

    @Test
    public void centroDelGrafoEsElDeMenorExcentricidad() {
        
        String centro = algoritmos.obtenerCentroGrafo(grafo);
        assertEquals("E", centro);
    }

    // DFS / BFS 
    @Test
    public void dfsRecorreTodosLosVerticesAlcanzablesDesdeOrigen() {
        List<String> visitados = new ArrayList<>();
        algoritmos.recorridoEnProfundidad(grafo, "A", visitados::add);

        assertEquals("A", visitados.get(0));
        assertEquals(5, visitados.size());
        assertEquals(new HashSet<>(Arrays.asList("A", "B", "C", "D", "E")),
                new HashSet<>(visitados));
    }

    @Test
    public void bfsComienzaPorElOrigenYAlcanzaTodos() {
        List<String> visitados = new ArrayList<>();
        algoritmos.recorridoEnAmplitud(grafo, "A", visitados::add);

        assertEquals("A", visitados.get(0));
        assertEquals(5, visitados.size());
        assertEquals(new HashSet<>(Arrays.asList("A", "B", "C", "D", "E")),
                new HashSet<>(visitados));
    }

    @Test
    public void dfsConOrigenInexistenteNoInvocaConsumer() {
        List<String> visitados = new ArrayList<>();
        algoritmos.recorridoEnProfundidad(grafo, "Z", visitados::add);
        assertTrue(visitados.isEmpty());
    }

    @Test
    public void bfsConOrigenInexistenteNoInvocaConsumer() {
        List<String> visitados = new ArrayList<>();
        algoritmos.recorridoEnAmplitud(grafo, "Z", visitados::add);
        assertTrue(visitados.isEmpty());
    }

    // TODOS LOS CAMINOS 

    @Test
    public void obtenerTodosLosCaminosEntreDosVertices() {
        List<Path<String>> caminos = algoritmos.obtenerTodosLosCaminos("A", "D", grafo);

        assertEquals(3, caminos.size());

        Set<Double> costos = new HashSet<>();
        for (Path<String> p : caminos) {
            costos.add(p.getCost());
            assertEquals("A", p.getPath().get(0));
            assertEquals("D", p.getPath().get(p.getPath().size() - 1));
        }
        assertEquals(new HashSet<>(Arrays.asList(4.0, 5.0, 6.0)), costos);
    }

    @Test
    public void obtenerTodosLosCaminosConVerticeInexistenteDevuelveVacio() {
        List<Path<String>> caminos = algoritmos.obtenerTodosLosCaminos("A", "Z", grafo);
        assertTrue(caminos.isEmpty());
    }

    //CLASIFICACIÓN TOPOLÓGICA

    @Test
    public void clasificacionTopologicaRespetaPrecedencia() {
        List<String> orden = algoritmos.calcularClasificacionTopologica(grafo);

        assertEquals(5, orden.size());
        assertTrue(orden.indexOf("A") < orden.indexOf("B"));
        assertTrue(orden.indexOf("A") < orden.indexOf("C"));
        assertTrue(orden.indexOf("B") < orden.indexOf("C"));
        assertTrue(orden.indexOf("B") < orden.indexOf("D"));
        assertTrue(orden.indexOf("C") < orden.indexOf("D"));
        assertTrue(orden.indexOf("D") < orden.indexOf("E"));
    }

    @Test(expected = IllegalStateException.class)
    public void clasificacionTopologicaConCicloLanzaExcepcion() {
        DirectedIGraph<String, WeightedEdge> ciclico = new DirectedIGraph<>();
        ciclico.agregarVertice("X");
        ciclico.agregarVertice("Y");
        ciclico.agregarVertice("Z");
        ciclico.agregarArista("X", "Y", new WeightedEdge(1));
        ciclico.agregarArista("Y", "Z", new WeightedEdge(1));
        ciclico.agregarArista("Z", "X", new WeightedEdge(1));

        algoritmos.calcularClasificacionTopologica(ciclico);
    }
}
