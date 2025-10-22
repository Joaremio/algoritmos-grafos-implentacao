package br.ufrn.imd.algorithms.prim;

import br.ufrn.imd.Vertice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HeapBinarioMin {

    // O array (base 0) que armazena os nós do heap
    private final List<HeapNode> heap;

    // Mapeia um Vértice para sua posição (índice) no array 'heap'
    // Isso é essencial para a operação decreaseKey em O(log N).
    private final Map<Vertice, Integer> posicoes;

    public HeapBinarioMin() {
        this.heap = new ArrayList<>();
        this.posicoes = new HashMap<>();
    }

    /**
     * Insere um novo vértice no heap com uma chave (peso).
     */
    public void inserir(Vertice vertice, double chave) {
        HeapNode node = new HeapNode(vertice, chave);
        heap.add(node); // Adiciona ao final
        int indiceAtual = heap.size() - 1;
        posicoes.put(vertice, indiceAtual);

        // Sobe no heap (heapify-up)
        subir(indiceAtual);
    }

    /**
     * Remove e retorna o nó com a menor chave (raiz do heap).
     */
    public Vertice extrairMin() {
        if (isEmpty()) {
            throw new IllegalStateException("Heap está vazio");
        }

        HeapNode minNode = heap.get(0);
        HeapNode ultimoNode = heap.remove(heap.size() - 1);

        if (!isEmpty()) {
            // Move o último nó para a raiz
            heap.set(0, ultimoNode);
            posicoes.put(ultimoNode.vertice, 0);

            // Desce no heap (heapify-down)
            descer(0);
        }

        posicoes.remove(minNode.vertice);
        return minNode.vertice;
    }

    /**
     * Diminui a chave (peso) de um vértice que já está no heap.
     */
    public void decreaseKey(Vertice vertice, double novaChave) {
        Integer indice = posicoes.get(vertice);
        if (indice == null) {
            return; // Vértice não está no heap
        }

        HeapNode node = heap.get(indice);
        if (novaChave >= node.chave) {
            return; // Nova chave não é menor
        }

        node.chave = novaChave;
        subir(indice); // Sobe com a nova chave menor
    }

    /**
     * Verifica se o heap contém um determinado vértice.
     */
    public boolean contem(Vertice v) {
        return posicoes.containsKey(v);
    }

    public boolean isEmpty() {
        return heap.isEmpty();
    }

    // --- Métodos Auxiliares do Heap ---

    private void subir(int indice) {
        int pai = (indice - 1) / 2;
        while (indice > 0 && heap.get(indice).chave < heap.get(pai).chave) {
            trocar(indice, pai);
            indice = pai;
            pai = (indice - 1) / 2;
        }
    }

    private void descer(int indice) {
        int menor = indice;
        int esquerda = 2 * indice + 1;
        int direita = 2 * indice + 2;

        if (esquerda < heap.size() && heap.get(esquerda).chave < heap.get(menor).chave) {
            menor = esquerda;
        }
        if (direita < heap.size() && heap.get(direita).chave < heap.get(menor).chave) {
            menor = direita;
        }

        if (menor != indice) {
            trocar(indice, menor);
            descer(menor); // Continua descendo recursivamente
        }
    }

    private void trocar(int i, int j) {
        HeapNode nodeI = heap.get(i);
        HeapNode nodeJ = heap.get(j);

        // Troca no array
        heap.set(i, nodeJ);
        heap.set(j, nodeI);

        // Atualiza as posições no mapa
        posicoes.put(nodeI.vertice, j);
        posicoes.put(nodeJ.vertice, i);
    }
}