package br.ufrn.imd;

import br.ufrn.imd.algorithms.bellman_ford.BellmanFord;
import br.ufrn.imd.algorithms.floyd_warshall.FloydWarshall;
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

            // Verifica se o grafo é direcionado
            if (grafo.isDirecionado()) {
                System.out.println("O algoritmo de Prim não é aplicável a grafos direcionados.");
            } else {
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

            try {
                // Passa o caminho do arquivo DOT para detectar arestas com peso explícito
                FloydWarshall.Resultado resultadoFloyd = FloydWarshall.calcular(grafo, caminhoArquivo);

                // Imprime a matriz de distâncias
                FloydWarshall.imprimirMatrizDistancias(resultadoFloyd);

                // Imprime a matriz de predecessores
                FloydWarshall.imprimirMatrizPredecessores(resultadoFloyd);

                // Exemplo: imprime alguns caminhos mínimos
                System.out.println("Exemplos de caminhos mínimos:");
                List<Vertice> todosVertices = new java.util.ArrayList<>(grafo.getVertices());
                if (todosVertices.size() >= 2) {
                    // Mostra caminhos do primeiro vértice para os demais
                    Vertice origem = todosVertices.get(0);
                    for (int i = 1; i < Math.min(4, todosVertices.size()); i++) {
                        FloydWarshall.imprimirCaminho(resultadoFloyd, origem, todosVertices.get(i));
                    }
                }
            } catch (IllegalArgumentException e) {
                System.err.println("Erro no Floyd-Warshall: " + e.getMessage());
            }


        } catch (IOException e) {
            System.err.println("Erro ao ler ou processar o arquivo DOT: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Ocorreu um erro inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
