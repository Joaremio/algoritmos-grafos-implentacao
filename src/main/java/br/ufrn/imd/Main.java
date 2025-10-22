package br.ufrn.imd;

import br.ufrn.imd.algorithms.bellman_ford.BellmanFord;
import br.ufrn.imd.algorithms.prim.Prim;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        // 1. VERIFICAR O ARGUMENTO DE LINHA DE COMANDO
        if (args.length == 0) {
            System.err.println("Erro: Nenhum arquivo .dot de entrada foi fornecido.");
            System.err.println("Uso: java -jar seu-programa.jar <caminho_do_arquivo.dot>");
            return; // Encerra o programa
        }

        String caminhoArquivo = args[0];
        System.out.println("Lendo o arquivo: " + caminhoArquivo);

        try {
            // CRIAR O LEITOR E CARREGAR O GRAFO
            LeitorDOT leitor = new LeitorDOT();
            Grafo grafo = leitor.lerArquivo(caminhoArquivo);

            // IMPRIMIR O GRAFO
            System.out.println("Grafo carregado com sucesso:");
            System.out.println(grafo);

            // 4. CHAMAR OS ALGORITMOS
            System.out.println("\n--- Executando Algoritmo de Prim ---");

            Prim prim = new Prim();
            Vertice inicioPrim = grafo.getVertice("a");

            if (inicioPrim != null) {
                List<Aresta> mst = prim.executar(grafo, inicioPrim);
                System.out.println("Arestas da MST:");
                for (Aresta a : mst) {
                    System.out.println(a.getOrigem() + " --(" + a.getPeso() + ")--> " + a.getDestino());
                }
            } else {
                System.out.println("Vértice inicial 'a' não encontrado para Prim.");
            }

            System.out.println("\n--- Executando Algoritmo de Bellman-Ford ---");

            Vertice origemBellmanFord = grafo.getVertice("a");
            if (origemBellmanFord != null) {
                try {
                    Map<Vertice, Double> distancias = BellmanFord.calcular(grafo, origemBellmanFord);

                    System.out.println("\nDistâncias mínimas a partir de " + origemBellmanFord + ":");
                    for (Vertice v : distancias.keySet()) {
                        double d = distancias.get(v);
                        System.out.printf(" - %s: %.2f\n", v.getRotulo(), d);
                    }
                } catch (IllegalArgumentException e) {
                    System.err.println("Erro: " + e.getMessage());
                }
            } else {
                System.out.println("Vértice inicial 'a' não encontrado para Bellman-Ford.");
            }


            System.out.println("\n--- Executando Algoritmo de Floyd-Warshall ---");


        } catch (IOException e) {
            System.err.println("Erro ao ler ou processar o arquivo DOT: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Ocorreu um erro inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
