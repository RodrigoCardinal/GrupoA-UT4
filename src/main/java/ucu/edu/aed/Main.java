package ucu.edu.aed;

import ucu.edu.aed.tda.grafo.dirigido.DirectedGraph;
import ucu.edu.aed.tda.grafo.dirigido.DirectedGraphAlgorithms;
import ucu.edu.aed.tda.grafo.dirigido.IDirectedIGraph;
import ucu.edu.aed.tda.grafo.nodirigido.IUndirectedGraph;
import ucu.edu.aed.tda.grafo.nodirigido.UndirectedGraph;
import ucu.edu.aed.tda.grafo.nodirigido.UndirectedGraphAlgorithm;
import ucu.edu.aed.tda.grafo.model.edge.WeightedEdge;
import ucu.edu.aed.tda.grafo.model.result.IDijkstraResult;
import ucu.edu.aed.tda.grafo.model.result.IFloydWarshallResult;
import ucu.edu.aed.tda.grafo.model.result.Path;
import ucu.edu.aed.utils.PrettyGrid;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        demoFloyd();
        System.out.println();
        demoDijkstra();
        System.out.println();
        demoMST();
        System.out.println();
        demoRecorridosYTopologico();
    }

    private static void demoFloyd() {
        System.out.println("=== Floyd-Warshall (ejemplo del PDF) ===");
        IDirectedIGraph<Integer, WeightedEdge> g = new DirectedGraph<>();
        g.agregarVertices(List.of(1, 2, 3));
        g.agregarArista(1, 2, new WeightedEdge(8));
        g.agregarArista(2, 1, new WeightedEdge(3));
        g.agregarArista(3, 2, new WeightedEdge(2));
        g.agregarArista(1, 3, new WeightedEdge(5));

        DirectedGraphAlgorithms algos = new DirectedGraphAlgorithms();
        IFloydWarshallResult<Integer> r = algos.floyd(g);

        System.out.println("Matriz de distancias mínimas:");
        for (Integer i : List.of(1, 2, 3)) {
            StringBuilder sb = new StringBuilder("  " + i + ": ");
            for (Integer j : List.of(1, 2, 3)) {
                double d = r.getCost(i, j);
                sb.append(Double.isInfinite(d) ? " ∞" : String.format(" %.0f", d));
            }
            System.out.println(sb);
        }

        Integer centro = algos.obtenerCentroGrafo(g);
        System.out.println("Centro del grafo: " + centro);
        System.out.println("Excentricidad(2) = " + algos.obtenerExcentricidad(g, g.construirComparable(2)));
        System.out.println("Camino 1 -> 2: " + r.getPath(1, 2));
    }

    private static void demoDijkstra() {
        System.out.println("=== Dijkstra ===");
        IDirectedIGraph<String, WeightedEdge> g = new DirectedGraph<>();
        g.agregarVertices(List.of("A", "B", "C", "D", "E"));
        g.agregarArista("A", "B", new WeightedEdge(10));
        g.agregarArista("A", "D", new WeightedEdge(30));
        g.agregarArista("A", "E", new WeightedEdge(100));
        g.agregarArista("B", "C", new WeightedEdge(50));
        g.agregarArista("C", "E", new WeightedEdge(10));
        g.agregarArista("D", "C", new WeightedEdge(20));
        g.agregarArista("D", "E", new WeightedEdge(60));

        DirectedGraphAlgorithms algos = new DirectedGraphAlgorithms();
        IDijkstraResult<String> r = algos.dijkstra(g.construirComparable("A"), g);
        for (String v : List.of("A", "B", "C", "D", "E")) {
            System.out.printf("  A -> %s : costo=%.0f  camino=%s%n",
                    v, r.getCost(v), r.getPath(v));
        }
    }

    private static void demoMST() {
        System.out.println("=== Kruskal y Prim (no dirigido) ===");
        IUndirectedGraph<String, WeightedEdge> g = new UndirectedGraph<>();
        g.agregarVertices(List.of("a", "b", "c", "d", "e", "f"));
        g.agregarArista("a", "b", new WeightedEdge(6));
        g.agregarArista("a", "c", new WeightedEdge(1));
        g.agregarArista("a", "d", new WeightedEdge(5));
        g.agregarArista("b", "c", new WeightedEdge(5));
        g.agregarArista("b", "e", new WeightedEdge(3));
        g.agregarArista("c", "d", new WeightedEdge(5));
        g.agregarArista("c", "e", new WeightedEdge(6));
        g.agregarArista("c", "f", new WeightedEdge(4));
        g.agregarArista("d", "f", new WeightedEdge(2));
        g.agregarArista("e", "f", new WeightedEdge(6));

        UndirectedGraphAlgorithm algos = new UndirectedGraphAlgorithm();
        IUndirectedGraph<String, WeightedEdge> mstK = algos.kruskal(g);
        IUndirectedGraph<String, WeightedEdge> mstP = algos.prim(g, g.construirComparable("a"));

        System.out.println("Kruskal: " + mstK.aristas());
        System.out.println("Prim   : " + mstP.aristas());
    }

    private static void demoRecorridosYTopologico() {
        System.out.println("=== BPF, BEA y clasificación topológica ===");
        IDirectedIGraph<String, WeightedEdge> g = new DirectedGraph<>();
        g.agregarVertices(List.of("C1", "C2", "C3", "C4", "C5"));
        g.agregarArista("C1", "C3", new WeightedEdge(1));
        g.agregarArista("C2", "C3", new WeightedEdge(1));
        g.agregarArista("C2", "C4", new WeightedEdge(1));
        g.agregarArista("C3", "C5", new WeightedEdge(1));
        g.agregarArista("C4", "C5", new WeightedEdge(1));

        DirectedGraphAlgorithms algos = new DirectedGraphAlgorithms();

        System.out.print("BPF desde C1: ");
        algos.recorridoEnProfundidad(g, g.construirComparable("C1"), v -> System.out.print(v + " "));
        System.out.println();

        System.out.print("BEA desde C2: ");
        algos.recorridoEnAmplitud(g, g.construirComparable("C2"), v -> System.out.print(v + " "));
        System.out.println();

        System.out.println("Orden topológico: " + algos.calcularClasificacionTopologica(g));
        System.out.println("¿Tiene ciclos?   " + g.tieneCiclos());

        System.out.println("Todos los caminos C1 -> C5:");
        List<Path<String>> paths = algos.obtenerTodosLosCaminos(
                g.construirComparable("C1"), g.construirComparable("C5"), g);
        for (Path<String> p : paths) {
            System.out.println("  " + p.getPath() + " costo=" + p.getCost());
        }

        System.out.println("\nMatriz del grafo:");
        PrettyGrid.printGraphMatrix(g, v -> v);
    }
}
