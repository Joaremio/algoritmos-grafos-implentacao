package br.ufrn.imd.algorithms.prim;

import br.ufrn.imd.Aresta;
import br.ufrn.imd.Grafo;
import br.ufrn.imd.Vertice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementação do Algoritmo de Prim para encontrar a Árvore Geradora Mínima (MST).
 *
 */
public class Prim {

    // Mapas para armazenar os pais (arestas) e as chaves (pesos)
    private Map<Vertice, Aresta> arestaParaPai;
    private Map<Vertice, Double> chaves;
    private HeapBinarioMin filaPrioridade;

    /**
     * Executa o algoritmo de Prim em um grafo.
     *
     * @param grafo O grafo (deve ser não-direcionado e ponderado).
     * @param inicio O vértice inicial.
     * @return Uma lista de arestas que compõem a MST.
     */
    public List<Aresta> executar(Grafo grafo, Vertice inicio) {
        if (grafo.isDirecionado()) {
            System.err.println("Aviso: O Algoritmo de Prim é para grafos não-direcionados.");
        }

        // Inicialização
        arestaParaPai = new HashMap<>();
        chaves = new HashMap<>();
        filaPrioridade = new HeapBinarioMin(); //  Usa nosso heap

        for (Vertice v : grafo.getVertices()) {
            chaves.put(v, Double.POSITIVE_INFINITY);
            arestaParaPai.put(v, null);
        }

        // Define a chave do vértice inicial como 0
        chaves.put(inicio, 0.0);

        // Adiciona todos os vértices ao heap
        for (Vertice v : grafo.getVertices()) {
            filaPrioridade.inserir(v, chaves.get(v));
        }

        // Loop principal do algoritmo (referência: Cormen, 21.2)
        while (!filaPrioridade.isEmpty()) {
            Vertice u = filaPrioridade.extrairMin();

            // Para cada vizinho 'v' do vértice 'u'
            for (Aresta aresta : grafo.getArestasVizinhas(u)) {
                Vertice v = aresta.getDestino();
                double peso = aresta.getPeso();

                // Se 'v' está na fila E o peso da aresta (u,v) é menor que a chave de 'v'
                if (filaPrioridade.contem(v) && peso < chaves.get(v)) {
                    // Atualiza a chave de 'v'
                    chaves.put(v, peso);
                    // Define 'u' (via aresta) como o pai de 'v'
                    arestaParaPai.put(v, aresta);
                    // Atualiza a posição de 'v' no heap
                    filaPrioridade.decreaseKey(v, peso);
                }
            }
        }

        // Coleta os resultados (as arestas da MST)
        List<Aresta> mst = new ArrayList<>();
        double custoTotal = 0;
        for (Vertice v : arestaParaPai.keySet()) {
            Aresta aresta = arestaParaPai.get(v);
            if (aresta != null) {
                mst.add(aresta);
                custoTotal += aresta.getPeso();
            }
        }

        System.out.println("Custo total da MST (Prim): " + custoTotal);
        return mst;
    }
}