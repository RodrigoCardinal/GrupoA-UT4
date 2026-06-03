package ucu.edu.aed.tda.grafo.implementacion;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class DijkstraResultTest {

    // Nodos del grafo de prueba: A -> B -> C -> D
    private DijkstraResult<String> result;

    @Before
    public void setUp() {
        // Fuente: "A"
        // Distancias: A=0, B=1, C=3, D=6
        // Predecesores: B<-A, C<-B, D<-C
        Map<String, Double> distancias = new HashMap<>();
        distancias.put("A", 0.0);
        distancias.put("B", 1.0);
        distancias.put("C", 3.0);
        distancias.put("D", 6.0);

        Map<String, String> predecesor = new HashMap<>();
        predecesor.put("B", "A");
        predecesor.put("C", "B");
        predecesor.put("D", "C");

        result = new DijkstraResult<>("A", distancias, predecesor);
    }

    /*
     Verifica que devuelve la distancia correcta para un nodo que sí existe.
     */
    @Test
    public void testGetCost_nodoAlcanzable() {
        assertEquals(1.0, result.getCost("B"), 0.0001);
        assertEquals(3.0, result.getCost("C"), 0.0001);
        assertEquals(6.0, result.getCost("D"), 0.0001);
    }

    @Test
    public void testGetCost_nodoFuente() {
        assertEquals(0.0, result.getCost("A"), 0.0001);
    }

    /*
     nodo inexistente
     Si el nodo no está en el mapa, debe devolver infinito (POSITIVE_INFINITY).
     */
    @Test
    public void testGetCost_nodoInexistente() {
        assertEquals(Double.POSITIVE_INFINITY, result.getCost("Z"), 0.0001);
    }


    /*
     camino completo desde la fuente hasta un nodo lejano
     Debe reconstruir correctamente la secuencia [A, B, C, D].
     */
    @Test
    public void testGetPath_caminoCompleto() {
        List<String> path = result.getPath("D");
        assertEquals(List.of("A", "B", "C", "D"), path);
    }

    /**
     getPath() - destino es un vecino directo de la fuente
     El camino debe ser [A, B].
     */
    @Test
    public void testGetPath_vecinoDirecto() {
        List<String> path = result.getPath("B");
        assertEquals(List.of("A", "B"), path);
    }

    /**
     destino es la propia fuente
     El camino a la fuente debe contener solo [A]
     */
    @Test
    public void testGetPath_destinoEsFuente() {
        List<String> path = result.getPath("A");
        assertEquals(List.of("A"), path);
    }

    /**
     nodo inalcanzable (no está en el mapa de distancias)
    Debe devolver una lista vacía
     */
    @Test
    public void testGetPath_nodoInalcanzable() {
        List<String> path = result.getPath("Z");
        assertTrue(path.isEmpty());
    }
}