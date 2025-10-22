package br.ufrn.imd;

public class Aresta {
    private final Vertice origem;
    private final Vertice destino;
    private final double peso; // pode ser 1.0 se não for ponderado

    public Aresta(Vertice origem, Vertice destino, double peso) {
        this.origem = origem;
        this.destino = destino;
        this.peso = peso;
    }

    public Vertice getOrigem() {
        return origem;
    }

    public Vertice getDestino() {
        return destino;
    }

    public double getPeso() {
        return peso;
    }

    @Override
    public String toString() {
        return origem + " -> " + destino + " [peso=" + peso + "]";
    }
}
