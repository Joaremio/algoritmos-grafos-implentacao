package br.ufrn.imd.algorithms.bellman_ford;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufrn.imd.Aresta;
import br.ufrn.imd.Grafo;
import br.ufrn.imd.Vertice;

public class BellmanFord {

    /**
     * Executa o algoritmo de Bellman-Ford.
     * 
     * @param grafo   Grafo (pode ser direcionado ou não)
     * @param origem  Vértice de origem
     * @return Um mapa com as menores distâncias do vértice de origem a cada vértice
     * @throws IllegalArgumentException se o grafo contiver ciclo de peso negativo
     */
    public static Map<Vertice, Double> calcular(Grafo grafo, Vertice origem) {
    
        Map<Vertice, Double> distancia = new HashMap<>();
        Map<Vertice, Vertice> predecessor = new HashMap<>();

        for (Vertice v : grafo.getVertices()) {
            distancia.put(v, Double.POSITIVE_INFINITY);
            predecessor.put(v, null);
        }
        distancia.put(origem, 0.0);

        List<Aresta> arestas = grafo.getTodasArestas();
        int numVertices = grafo.getNumVertices();

        for (int i = 1; i <= numVertices - 1; i++) {
            boolean houveAtualizacao = false;
            for (Aresta a : arestas) {
                Vertice u = a.getOrigem();
                Vertice v = a.getDestino();
                double peso = a.getPeso();

                if (distancia.get(u) + peso < distancia.get(v)) {
                    distancia.put(v, distancia.get(u) + peso);
                    predecessor.put(v, u);
                    houveAtualizacao = true;
                }
            }
            if (!houveAtualizacao) break;
        }

        for (Aresta a : arestas) {
            Vertice u = a.getOrigem();
            Vertice v = a.getDestino();
            double peso = a.getPeso();

            if (distancia.get(u) + peso < distancia.get(v)) {
                throw new IllegalArgumentException("Grafo contém ciclo de peso negativo!");
            }
        }

        System.out.println("Resultados do Bellman-Ford a partir de " + origem + ":");
        for (Vertice v : grafo.getVertices()) {
            System.out.printf(" - %s: distância = %.2f\n", v.getRotulo(), distancia.get(v));
        }

        return distancia;
    }
}
