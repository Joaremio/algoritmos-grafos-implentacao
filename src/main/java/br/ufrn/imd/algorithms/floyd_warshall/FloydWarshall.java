package br.ufrn.imd.algorithms.floyd_warshall;

import br.ufrn.imd.Aresta;
import br.ufrn.imd.Grafo;
import br.ufrn.imd.Vertice;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;
import guru.nidi.graphviz.parse.Parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
  - Implementação do Algoritmo de Floyd-Warshall para calcular os caminhos mínimos 
    de todos os vértices para todos os vértices de um grafo.
  - Complexidade: O(V³) onde V é o número de vértices.
 */
public class FloydWarshall {

    /**
     Resultado do algoritmo de Floyd-Warshall contendo:
        - Matriz de distâncias mínimas entre todos os pares de vértices
        - Matriz de predecessores para reconstruir os caminhos
        - Mapeamento de índices para vértices
     */
    public static class Resultado {
        private final double[][] distancias;
        private final Vertice[][] predecessores;
        private final List<Vertice> verticesPorIndice;
        private final Map<Vertice, Integer> indicePorVertice;

        public Resultado(double[][] distancias, Vertice[][] predecessores,
                        List<Vertice> verticesPorIndice, Map<Vertice, Integer> indicePorVertice) {
            this.distancias = distancias;
            this.predecessores = predecessores;
            this.verticesPorIndice = verticesPorIndice;
            this.indicePorVertice = indicePorVertice;
        }

        public double[][] getDistancias() {
            return distancias;
        }

        public Vertice[][] getPredecessores() {
            return predecessores;
        }

        public List<Vertice> getVerticesPorIndice() {
            return verticesPorIndice;
        }

        public Map<Vertice, Integer> getIndicePorVertice() {
            return indicePorVertice;
        }

        //Retorna a distância mínima do vértice origem ao vértice destino.
        public double getDistancia(Vertice origem, Vertice destino) {
            Integer i = indicePorVertice.get(origem);
            Integer j = indicePorVertice.get(destino);
            if (i == null || j == null) {
                return Double.POSITIVE_INFINITY;
            }
            return distancias[i][j];
        }
    }

    /**
     * Executa o algoritmo de Floyd-Warshall.
     * 
     * @param grafo O grafo (pode ser direcionado ou não)
     * @return Resultado contendo a matriz de distâncias e predecessores
     */
    public static Resultado calcular(Grafo grafo) {
        return calcular(grafo, null);
    }

    /**
     * Executa o algoritmo de Floyd-Warshall com tratamento especial para arestas sem peso.
     * 
     * @param grafo O grafo (pode ser direcionado ou não)
     * @param caminhoArquivoDOT Caminho para o arquivo DOT original (opcional).
     *                         Se fornecido, verifica quais arestas têm peso explícito.
     *                         Se null, usa a lógica padrão (todas as arestas com peso 1.0
     *                         são tratadas como sem peso explícito).
     * @return Resultado contendo a matriz de distâncias e predecessores
     */
    public static Resultado calcular(Grafo grafo, String caminhoArquivoDOT) {
        List<Vertice> vertices = new ArrayList<>(grafo.getVertices());
        int n = vertices.size();

        Map<Vertice, Integer> indicePorVertice = new HashMap<>();
        for (int i = 0; i < n; i++) {
            indicePorVertice.put(vertices.get(i), i);
        }

        // DETECTAR ARESTAS COM PESO EXPLÍCITO
        // Lê o arquivo DOT original para identificar quais arestas têm o atributo "weight"
        // Isso permite distinguir entre arestas com weight=1 (explícito) e arestas sem weight (padrão 1.0)
        Set<String> arestasComPesoExplicito = new HashSet<>();
        
        if (caminhoArquivoDOT != null) {
            try {
                arestasComPesoExplicito = detectarArestasComPesoExplicito(caminhoArquivoDOT);
            } catch (IOException e) {
                // Se não conseguir ler o arquivo, usa a lógica de fallback
                System.err.println("Aviso: Não foi possível ler o arquivo DOT para detectar pesos explícitos: " + e.getMessage());
            }
        }
        
        // Para arestas sem peso explícito, usamos infinito
        // Isso significa que essas arestas não devem ser consideradas no cálculo
        // de caminhos mínimos, a menos que sejam a única opção (mas isso não acontecerá
        // porque o algoritmo já trata infinito corretamente nas somas)
        double pesoParaArestasSemPeso = Double.POSITIVE_INFINITY;

        // PASSO 1: INICIALIZAÇÃO DA MATRIZ DE DISTÂNCIAS
        // dist[i][j] representa a distância mínima do vértice i ao vértice j
        double[][] dist = new double[n][n];
        Vertice[][] pred = new Vertice[n][n];

        // Inicializar todas as distâncias como infinito
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                dist[i][j] = Double.POSITIVE_INFINITY;
                pred[i][j] = null;
            }
        }

        // D[i][i] = 0 (distância de um vértice para si mesmo é zero)
        // pred[i][i] = i (predecessor de i em relação a si mesmo é i)
        for (int i = 0; i < n; i++) {
            dist[i][i] = 0.0;
            pred[i][i] = vertices.get(i);
        }

        // D[i][j] = w(i,j) se existe aresta (i,j)
        // Se a aresta tem peso 1.0 E não há pesos explícitos no grafo,
        // usa um valor muito grande para que só seja usada se não houver alternativa
        // pred[i][j] = i (predecessor de j em relação a i é i se há aresta direta)
        for (Aresta aresta : grafo.getTodasArestas()) {
            Vertice origem = aresta.getOrigem();
            Vertice destino = aresta.getDestino();
            double peso = aresta.getPeso();

            Integer i = indicePorVertice.get(origem);
            Integer j = indicePorVertice.get(destino);

            if (i != null && j != null) {
                // Verifica se esta aresta tem peso explícito no arquivo DOT
                String chaveAresta = criarChaveAresta(origem.getRotulo(), destino.getRotulo());
                
                // Se a aresta não tem peso explícito (não está no conjunto detectado),
                // trata com valor muito grande para evitar que seja considerada "boa"
                if (!arestasComPesoExplicito.contains(chaveAresta)) {
                    peso = pesoParaArestasSemPeso;
                }
                
                // Se não há caminho direto ou encontramos um caminho mais curto
                if (dist[i][j] == Double.POSITIVE_INFINITY || peso < dist[i][j]) {
                    dist[i][j] = peso;
                    pred[i][j] = origem; // O predecessor de j vindo de i é i
                }
            }
        }

        // PASSO 2: RELAXAMENTO DAS ARESTAS
        // Para cada vértice k (intermediário), verifica todos os pares (i,j)
        // D[i][j] = min(D[i][j], D[i][k] + D[k][j])
        // 
        // Este é o coração do algoritmo: programação dinâmica
        // Consideramos cada vértice k como um possível vértice intermediário
        // e atualizamos as distâncias se encontrarmos um caminho mais curto
        // passando por k.
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    // Evitar overflow ao somar infinitos
                    double distanciaViaK = dist[i][k] + dist[k][j];
                    
                    // Se encontramos um caminho mais curto passando por k
                    if (dist[i][k] != Double.POSITIVE_INFINITY &&
                        dist[k][j] != Double.POSITIVE_INFINITY &&
                        distanciaViaK < dist[i][j]) {
                        
                        dist[i][j] = distanciaViaK;
                        // O predecessor de j vindo de i é o mesmo predecessor de j vindo de k
                        pred[i][j] = pred[k][j];
                    }
                }
            }
        }

        // PASSO 3: VERIFICAÇÃO DE CICLOS DE PESO NEGATIVO
        // Se após todos os relaxamentos ainda houver uma distância que pode ser melhorada,
        // significa que há um ciclo de peso negativo
        for (int i = 0; i < n; i++) {
            if (dist[i][i] < 0) {
                throw new IllegalArgumentException(
                    "Grafo contém ciclo de peso negativo passando pelo vértice " + 
                    vertices.get(i).getRotulo());
            }
        }

        return new Resultado(dist, pred, vertices, indicePorVertice);
    }

    /**
     * Imprime a matriz de distâncias de forma legível.
     */
    public static void imprimirMatrizDistancias(Resultado resultado) {
        double[][] dist = resultado.getDistancias();
        List<Vertice> vertices = resultado.getVerticesPorIndice();
        int n = vertices.size();

        System.out.println("\nMatriz de Distâncias Mínimas (Floyd-Warshall):");
        System.out.println("=".repeat(60));

        // Cabeçalho com rótulos dos vértices
        System.out.print("     ");
        for (Vertice v : vertices) {
            System.out.printf("%8s", v.getRotulo());
        }
        System.out.println();

        // Linhas da matriz
        for (int i = 0; i < n; i++) {
            System.out.printf("%4s ", vertices.get(i).getRotulo());
            for (int j = 0; j < n; j++) {
                if (dist[i][j] == Double.POSITIVE_INFINITY) {
                    System.out.printf("%8s", "∞");
                } else {
                    System.out.printf("%8.2f", dist[i][j]);
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    /**
     * Imprime a matriz de predecessores de forma legível.
     */
    public static void imprimirMatrizPredecessores(Resultado resultado) {
        Vertice[][] pred = resultado.getPredecessores();
        List<Vertice> vertices = resultado.getVerticesPorIndice();
        int n = vertices.size();

        System.out.println("\nMatriz de Predecessores (Floyd-Warshall):");
        System.out.println("=".repeat(60));

        // Cabeçalho com rótulos dos vértices
        System.out.print("     ");
        for (Vertice v : vertices) {
            System.out.printf("%8s", v.getRotulo());
        }
        System.out.println();

        // Linhas da matriz
        for (int i = 0; i < n; i++) {
            System.out.printf("%4s ", vertices.get(i).getRotulo());
            for (int j = 0; j < n; j++) {
                if (pred[i][j] == null) {
                    System.out.printf("%8s", "-");
                } else {
                    System.out.printf("%8s", pred[i][j].getRotulo());
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    /**
     * Reconstrói e imprime o caminho mínimo entre dois vértices.
     */
    public static void imprimirCaminho(Resultado resultado, Vertice origem, Vertice destino) {
        Map<Vertice, Integer> indicePorVertice = resultado.getIndicePorVertice();
        Vertice[][] pred = resultado.getPredecessores();

        Integer i = indicePorVertice.get(origem);
        Integer j = indicePorVertice.get(destino);

        if (i == null || j == null) {
            System.out.println("Vértice não encontrado no grafo.");
            return;
        }

        double distancia = resultado.getDistancia(origem, destino);
        
        if (distancia == Double.POSITIVE_INFINITY) {
            System.out.printf("Não há caminho de %s para %s.\n", origem, destino);
            return;
        }

        // Reconstruir o caminho usando a matriz de predecessores
        List<Vertice> caminho = new ArrayList<>();
        Vertice atual = destino;

        // Caminhamos de trás para frente usando os predecessores
        while (atual != null && !atual.equals(origem)) {
            caminho.add(0, atual); // Adiciona no início
            atual = pred[i][indicePorVertice.get(atual)];
        }

        if (atual != null && atual.equals(origem)) {
            caminho.add(0, origem);
        }

        // Imprimir o caminho
        System.out.printf("Caminho mínimo de %s para %s (distância: %.2f): ", 
                         origem, destino, distancia);
        for (int idx = 0; idx < caminho.size(); idx++) {
            System.out.print(caminho.get(idx));
            if (idx < caminho.size() - 1) {
                System.out.print(" -> ");
            }
        }
        System.out.println();
    }

    /**
     * Detecta quais arestas têm peso explícito (atributo weight) no arquivo DOT.
     * 
     * @param caminhoArquivoDOT Caminho para o arquivo DOT
     * @return Conjunto de chaves de arestas que têm peso explícito
     * @throws IOException Se houver erro ao ler o arquivo
     */
    private static Set<String> detectarArestasComPesoExplicito(String caminhoArquivoDOT) throws IOException {
        Set<String> arestasComPeso = new HashSet<>();
        Set<String> arestasProcessadas = new HashSet<>(); // Para evitar duplicatas em grafos não-direcionados
        
        MutableGraph gvGraph = new Parser().read(new File(caminhoArquivoDOT));
        boolean direcionado = gvGraph.isDirected();
        
        for (MutableNode node : gvGraph.nodes()) {
            String rotuloOrigem = node.name().value();
            
            node.links().forEach(link -> {
                String rotuloDestino = link.to().name().value();
                
                // Para grafos não-direcionados, cria uma chave normalizada para evitar processar duas vezes
                String chaveArestaNormalizada;
                if (direcionado) {
                    chaveArestaNormalizada = rotuloOrigem + "->" + rotuloDestino;
                } else {
                    // Para não-direcionados, normaliza a ordem (menor primeiro)
                    if (rotuloOrigem.compareTo(rotuloDestino) < 0) {
                        chaveArestaNormalizada = rotuloOrigem + "--" + rotuloDestino;
                    } else {
                        chaveArestaNormalizada = rotuloDestino + "--" + rotuloOrigem;
                    }
                }
                
                // Processa cada aresta apenas uma vez
                if (!arestasProcessadas.contains(chaveArestaNormalizada)) {
                    arestasProcessadas.add(chaveArestaNormalizada);
                    
                    // Verifica se a aresta tem o atributo weight
                    Object weightAttr = link.attrs().get("weight");
                    if (weightAttr != null) {
                        // Aresta tem peso explícito - adiciona ambas as direções se for não-direcionado
                        String chave1 = rotuloOrigem + "->" + rotuloDestino;
                        String chave2 = rotuloDestino + "->" + rotuloOrigem;
                        
                        arestasComPeso.add(chave1);
                        if (!direcionado) {
                            // Para não-direcionados, adiciona também a direção reversa
                            arestasComPeso.add(chave2);
                        }
                    }
                }
            });
        }
        
        return arestasComPeso;
    }

    /**
     * Cria uma chave única para identificar uma aresta.
     * Mantém a ordem origem->destino para garantir que arestas direcionadas
     * sejam identificadas corretamente.
     */
    private static String criarChaveAresta(String origem, String destino) {
        // Sempre mantém a ordem origem -> destino
        // Isso funciona tanto para grafos direcionados quanto não-direcionados
        return origem + "->" + destino;
    }
}

