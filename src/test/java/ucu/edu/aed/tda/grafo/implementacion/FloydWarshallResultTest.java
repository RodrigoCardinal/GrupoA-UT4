package ucu.edu.aed.tda.grafo.implementacion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;

;

public class FloydWarshallResultTest {

    FloydWarshallResult<String> prueba;
    Map<String, Map<String, Double>> dist;
    Map<String, Map<String, String>> next;

    @Before
    public void setUp() {
        dist = new HashMap();
        next = new HashMap();
    }

    @Test
    public void getCostoTest() {
        dist.put("A", new HashMap<>()); //creo  a->b (con un peso de 5)
        dist.get("A").put("B", 5.0);
        dist.put("B", new HashMap<>()); 
        dist.get("B").put("A", 10.0);//conecto b->a (con un peso de 10)
        prueba = new FloydWarshallResult<>(dist, next);
        Assert.assertEquals(5.0, prueba.getCost("A", "B")); //a->b con un peso de 5
        Assert.assertEquals(10.0, prueba.getCost("B", "A")); //b->a con un peso de 10
    }

    @Test
    public void getPathTest() {
        next.put("A", new HashMap<>());
        next.put("B", new HashMap<>());
        next.put("C", new HashMap<>()); //creo los nodos
        next.get("A").put("C", "B"); //a->c (por b)
        next.get("B").put("C", "C"); 
        prueba = new FloydWarshallResult<>(dist, next);
        List result = List.of("A", "B", "C");
        Assert.assertEquals(result, prueba.getPath("A", "C"));
        result = List.of(); 
        Assert.assertEquals(result, prueba.getPath("C", "A")); //no hay ninguna conexión de c->a
    }

    @Test
    public void connectedTrueTest() {
        dist.put("A", new HashMap<>());
        dist.put("B", new HashMap<>());
        dist.get("A").put("B", 2.0); //conecto a->b
        dist.get("B").put("C", 3.0); //conecto b->c
        prueba = new FloydWarshallResult<>(dist, next);
        Assert.assertTrue(prueba.connected("A", "B"));
        Assert.assertTrue(prueba.connected("B", "C"));
    }
    

    @Test
    public void connectedFalsoTest() {
        dist.put("A", new HashMap<>());
        dist.put("B", new HashMap<>());
        dist.put("C", new HashMap<>());
        dist.get("A").put("B", 2.0);//conecto a->b
        dist.get("B").put("C", 3.0);//conecto b->c
        prueba = new FloydWarshallResult<>(dist, next);
        Assert.assertFalse(prueba.connected("C", "B")); //no existe c->b
        Assert.assertFalse(prueba.connected("B", "A")); //no existe b->a
        Assert.assertFalse(prueba.connected(null, "A")); //null no debería estar conectado a ningún nodo
        Assert.assertFalse(prueba.connected("B", null));
    }
}
