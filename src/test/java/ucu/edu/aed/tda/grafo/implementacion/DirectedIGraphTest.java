package ucu.edu.aed.tda.grafo.implementacion;

import org.junit.Before;
import org.junit.Test;
import ucu.edu.aed.tda.grafo.model.edge.Edge;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class DirectedIGraphTest {

    private DirectedIGraph<String, Integer> grafo;

    // Se ejecuta antes de cada test, arma un grafo 
    @Before
    public void setUp() {
        grafo = new DirectedIGraph<>();
        grafo.agregarVertice("A");
        grafo.agregarVertice("B");
        grafo.agregarVertice("C");
        grafo.agregarArista("A", "B", 1);
        grafo.agregarArista("B", "C", 2);
        grafo.agregarArista("A", "C", 3);
    }


    // agregarVertice()
    // Verifica que agregar un vertice nuevo devuelve true
    @Test
    public void testAgregarVertice_nuevo() {
        assertTrue(grafo.agregarVertice("D"));
    }

    // Verifica que agregar un vertice que ya existe devuelve false
    @Test
    public void testAgregarVertice_duplicado() {
        assertFalse(grafo.agregarVertice("A"));
    }

    // buscarVertice()

    // Verifica que encuentra un vertice que sí existe en el grafo
    @Test
    public void testBuscarVertice_existe() {
        String encontrado = grafo.buscarVertice("B"::compareTo);
        assertEquals("B", encontrado);
    }

    // Verifica que devuelve null si el vertice no existe
    @Test
    public void testBuscarVertice_noExiste() {
        String encontrado = grafo.buscarVertice("Z"::compareTo);
        assertNull(encontrado);
    }

    // agregarArista()

    // Verifica que agregar una arista nueva devuelve true
    @Test
    public void testAgregarArista_nueva() {
        grafo.agregarVertice("D");
        assertTrue(grafo.agregarArista("C", "D", 5));
    }

    // Verifica que agregar una arista duplicada devuelve false
    @Test
    public void testAgregarArista_duplicada() {
        assertFalse(grafo.agregarArista("A", "B", 1));
    }

    // existeArista()

    // Verifica que detecta correctamente una arista que si fue agregada
    @Test
    public void testExisteArista_existe() {
        assertTrue(grafo.existeArista("A"::compareTo, "B"::compareTo));
    }

    // Verifica que devuelve false para una arista que no fue agregada
    @Test
    public void testExisteArista_noExiste() {
        assertFalse(grafo.existeArista("C"::compareTo, "A"::compareTo));
    }

    // eliminarArista()

    // Verifica que eliminar una arista existente devuelve true y ya no existe
    @Test
    public void testEliminarArista_existente() {
        boolean resultado = grafo.eliminarArista("A"::compareTo, "B"::compareTo);
        assertTrue(resultado);
        assertFalse(grafo.existeArista("A"::compareTo, "B"::compareTo));
    }

    // Verifica que eliminar una arista que no existe devuelve false
    @Test
    public void testEliminarArista_noExiste() {
        assertFalse(grafo.eliminarArista("C"::compareTo, "A"::compareTo));
    }

    // obtenerArista()


    // Verifica que devuelve null si la arista no existe
    @Test
    public void testObtenerArista_noExiste() {
        Edge<String, Integer> arista = grafo.obtenerArista("C"::compareTo, "A"::compareTo);
        assertNull(arista);
    }

    // Verifica que devuelve los nodos que apuntan hacia un vertice
    @Test
    public void testPredecessors() {
        Set<String> predecesores = grafo.predecessors("C"::compareTo);
        assertTrue(predecesores.contains("A"));
        assertTrue(predecesores.contains("B"));
        assertEquals(2, predecesores.size());
    }

    
    // adyacencias()

    // Verifica que devuelve todas las aristas que salen de un vertice
    @Test
    public void testAdyacencias() {
        List<Edge<String, Integer>> ady = grafo.adyacencias("A"::compareTo);
        assertEquals(2, ady.size());
    }

    // Método: adyacencias
    // Verifica que un nodo sin aristas salientes devuelve lista vacía
    @Test
    public void testAdyacencias_sinSalidas() {
        List<Edge<String, Integer>> ady = grafo.adyacencias("C"::compareTo);
        assertTrue(ady.isEmpty());
    }

    // removerVertice()

    // Verifica que al remover un vertice, también se eliminan sus aristas
    @Test
    public void testRemoverVertice_eliminaAristasRelacionadas() {
        grafo.removerVertice("B"::compareTo);
        assertFalse(grafo.existeArista("A"::compareTo, "B"::compareTo));
        assertFalse(grafo.existeArista("B"::compareTo, "C"::compareTo));
    }

    // vaciar()

    // Verifica que tras vaciar el grafo no quedan vertices ni aristas
    @Test
    public void testVaciar() {
        grafo.vaciar();
        assertTrue(grafo.vertices().isEmpty());
        assertTrue(grafo.aristas().isEmpty());
    }



    // Verifica que devuelve exactamente las aristas agregadas
    @Test
    public void testAristas() {
        assertEquals(3, grafo.aristas().size());
    }
}