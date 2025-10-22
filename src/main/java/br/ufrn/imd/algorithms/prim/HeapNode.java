package br.ufrn.imd.algorithms.prim;

import br.ufrn.imd.Vertice;

class HeapNode {
    Vertice vertice;
    double chave; // A "chave" (peso) do vértice no algoritmo de Prim

    public HeapNode(Vertice vertice, double chave) {
        this.vertice = vertice;
        this.chave = chave;
    }
}