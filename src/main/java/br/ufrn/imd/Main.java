package br.ufrn.imd;

import br.ufrn.imd.algorithms.prim.Prim;

import java.io.IOException;
import java.util.List;

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

            // Pega um vértice inicial (ex: "a")
            Vertice inicioPrim = grafo.getVertice("a");

            if (inicioPrim != null) {
                List<Aresta> mst = prim.executar(grafo, inicioPrim);
                System.out.println("Arestas da MST:");
                for (Aresta a : mst) {
                    // Imprime a aresta no formato que ela foi encontrada
                    System.out.println(a.getOrigem() + " --(" + a.getPeso() + ")--> " + a.getDestino());
                }
            } else {
                System.out.println("Vértice inicial 'a' não encontrado para Prim.");
            }

            System.out.println("\n--- Executando Algoritmo de Bellman-Ford ---");


            System.out.println("\n--- Executando Algoritmo de Floyd-Warshall ---");


        } catch (IOException e) {
            System.err.println("Erro ao ler ou processar o arquivo DOT: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Ocorreu um erro inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }
}