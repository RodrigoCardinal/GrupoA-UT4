package ucu.edu.aed.tda.grafo.implementacion;
import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import ucu.edu.aed.tda.grafo.model.edge.UndirectedEdge;

public class UndirectedGraphTests {
    UndirectedGraph<String, Integer> prueba;

    @Before
    public void SetUp() {
        prueba=new UndirectedGraph();
    }
    @Test
    public void constructorTest() {
        UndirectedGraph<String, Integer> grafo1=new UndirectedGraph<>(List.of("A", "B", "C"), List.of(new UndirectedEdge<String,Integer>("A", "B", 5), new UndirectedEdge<String,Integer>("A", "C", 10)));
        assertNotNull(grafo1);
    }
    @Test
    public void agregarVerticeTest() {
        boolean result= prueba.agregarVertice("A");
        assertTrue(result); //agrego un vértice con entrada válida
        result=prueba.agregarVertice(null); //agrego un vértice con entrada inválida
        assertFalse(result);
    }

    @Test
    public void buscarVertice() {
        prueba.agregarVertice("A");
        assertEquals("A", prueba.buscarVertice("A")); //busco vértice que existe
        assertEquals(null, prueba.buscarVertice("B")); //busco vertice que no existe
    }


    @Test
    public void agregarAristaTest() {
        prueba.agregarVertice("A");
        prueba.agregarVertice("B");
        boolean result=prueba.agregarArista("A", "B", 5);
        assertTrue(result);
        result=prueba.agregarArista("A", "C", 4); //si se pasan vértices que no existen aún, se agrega 
        assertTrue(result);
    }

    @Test
    public void eliminarAristaTest() {
        prueba.agregarVertice("A");
        prueba.agregarVertice("B");
        prueba.agregarArista("A", "B", 5);
        boolean result=prueba.eliminarArista("A", "B");
        assertTrue(result);
        prueba.agregarArista("A", "B", 5);
        result=prueba.eliminarArista("B", "A");
        assertTrue(result);
        result=prueba.eliminarArista("A", "C"); //arista no existente
        assertFalse(result);
        result=prueba.eliminarArista("C", "A"); //arista no existente
        assertFalse(result);
        result=prueba.eliminarArista("D", "F"); //arista no existente
        assertFalse(result);

    }

    @Test
    public void removerVerticeTest() {
        prueba.agregarVertice("A");
        prueba.agregarVertice("B");
        prueba.agregarVertice("C");
        prueba.agregarArista("A", "C", 4);
        prueba.agregarArista("B", "A", 4);
        boolean result =prueba.removerVertice("A");
        assertTrue(result);
        assertNull(prueba.buscarVertice("A"));
        result=prueba.removerVertice("Z"); //trato de remover vertice inexistente
        assertFalse(result);
    }

    @Test
    public void verticesTest() {
        prueba.agregarVertice("A");
        prueba.agregarVertice("B");
        prueba.agregarVertice("C");
        Set<String> result=new HashSet<>();
        result.addAll(List.of("A", "B", "C"));
        assertEquals(result, prueba.vertices());
    }

}
