package br.ufrn.imd;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Representa um grafo (direcionado ou não) usando uma lista de adjacência.
 * * A estrutura principal é um Mapa onde a chave é um Vértice
 * e o valor é uma Lista de Arestas que saem daquele vértice.
 */
public class Grafo {

    private final boolean direcionado;

    /**
     * Mapeia um Vértice para sua lista de arestas de saída (vizinhas).
     * Ex: A -> [ (A->B, 10), (A->C, 5) ]
     * B -> [ (B->A, 2) ]
     */
    private final Map<Vertice, List<Aresta>> listaAdjacencia;

    /**
     * Mapeia o rótulo (String) para o objeto Vertice.
     * Isso nos permite encontrar rapidamente um vértice pelo seu nome.
     */
    private final Map<String, Vertice> vertices;

    /**
     * Uma lista simples de todas as arestas.
     * Isso é muito útil para o algoritmo de Bellman-Ford.
     */
    private final List<Aresta> todasArestas;

    /**
     * Construtor do Grafo.
     * @param direcionado true se o grafo for direcionado (digraph),
     * false se não for (graph).
     */
    public Grafo(boolean direcionado) {
        this.direcionado = direcionado;
        this.listaAdjacencia = new HashMap<>();
        this.vertices = new HashMap<>();
        this.todasArestas = new ArrayList<>();
    }

    /**
     * Adiciona um vértice ao grafo, se ele ainda não existir.
     * * @param rotulo O rótulo do vértice (ex: "a", "b").
     * @return O objeto Vertice (novo ou o já existente).
     */
    public Vertice addVertice(String rotulo) {
        // Verifica se o vértice já existe. Se não, cria e armazena.
        Vertice v = vertices.computeIfAbsent(rotulo, Vertice::new);

        // Garante que o vértice tenha uma entrada na lista de adjacência.
        listaAdjacencia.putIfAbsent(v, new ArrayList<>());

        return v;
    }

    /**
     * Adiciona uma aresta ao grafo.
     * Os vértices de origem e destino serão criados se não existirem.
     * * @param rotuloOrigem Rótulo do vértice de origem.
     * @param rotuloDestino Rótulo do vértice de destino.
     * @param peso O peso da aresta.
     */
    public void addAresta(String rotuloOrigem, String rotuloDestino, double peso) {
        // Obtém ou cria os vértices
        Vertice origem = addVertice(rotuloOrigem);
        Vertice destino = addVertice(rotuloDestino);

        // Cria a aresta principal (origem -> destino)
        Aresta arestaOrigemDestino = new Aresta(origem, destino, peso);

        // Adiciona à lista de adjacência da origem
        listaAdjacencia.get(origem).add(arestaOrigemDestino);

        // Adiciona à lista geral de arestas
        todasArestas.add(arestaOrigemDestino);

        // Se o grafo NÃO for direcionado, temos que adicionar
        // a aresta no sentido oposto também.
        if (!this.direcionado) {
            Aresta arestaDestinoOrigem = new Aresta(destino, origem, peso);
            listaAdjacencia.get(destino).add(arestaDestinoOrigem);
            // Adiciona a aresta reversa também. Os algoritmos sabem lidar com isso.
            todasArestas.add(arestaDestinoOrigem);
        }
    }

    // --- MÉTODOS DE ACESSO (Getters) ---
    // Estes métodos serão usados pelos seus algoritmos.

    /**
     * Retorna um Vértice específico pelo seu rótulo.
     * @return O Vertice ou null se não for encontrado.
     */
    public Vertice getVertice(String rotulo) {
        return vertices.get(rotulo);
    }

    /**
     * Retorna uma coleção com todos os vértices do grafo.
     */
    public Collection<Vertice> getVertices() {
        return vertices.values();
    }

    /**
     * Retorna a lista de todas as arestas do grafo.
     * (Essencial para Bellman-Ford)
     */
    public List<Aresta> getTodasArestas() {
        return todasArestas;
    }

    /**
     * Retorna as arestas de saída (vizinhas) de um determinado vértice.
     * (Essencial para Prim)
     */
    public List<Aresta> getArestasVizinhas(Vertice v) {
        // Retorna a lista de vizinhos, ou uma lista vazia se o vértice
        // não tiver vizinhos (ou não existir, embora não deva acontecer).
        return listaAdjacencia.getOrDefault(v, new ArrayList<>());
    }

    /**
     * Retorna o número total de vértices.
     */
    public int getNumVertices() {
        return vertices.size();
    }

    /**
     * Verifica se o grafo é direcionado.
     */
    public boolean isDirecionado() {
        return direcionado;
    }

    @Override
    public String toString() {
        // Um toString() simples para ajudar no debugging
        StringBuilder sb = new StringBuilder();
        sb.append("Grafo ").append(direcionado ? "(Direcionado)" : "(Não-Direcionado)").append("\n");
        for (Vertice v : listaAdjacencia.keySet()) {
            sb.append(v).append(" -> ");
            sb.append(listaAdjacencia.get(v));
            sb.append("\n");
        }
        return sb.toString();
    }
}