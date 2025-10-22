package br.ufrn.imd;

import br.ufrn.imd.Grafo;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;
import guru.nidi.graphviz.parse.Parser;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class LeitorDOT {

    public Grafo lerArquivo(String caminhoArquivo) throws IOException {
        MutableGraph gvGraph = new Parser().read(new File(caminhoArquivo));
        boolean direcionado = gvGraph.isDirected();
        Grafo meuGrafo = new Grafo(direcionado);

        // Adiciona todos os vértices
        for (MutableNode node : gvGraph.nodes()) {
            String rotulo = node.name().value();
            meuGrafo.addVertice(rotulo);
        }

        // Conjunto para rastrear arestas já processadas em grafos não-direcionados
        Set<String> arestasProcessadas = new HashSet<>();

        // Adiciona as arestas
        for (MutableNode node : gvGraph.nodes()) {
            String rotuloOrigem = node.name().value();

            node.links().forEach(link -> {
                String rotuloDestino = link.to().name().value();
                double peso = 1.0;

                Object weightAttr = link.attrs().get("weight");
                if (weightAttr != null) {
                    try {
                        peso = Double.parseDouble(weightAttr.toString());
                    } catch (NumberFormatException e) {
                        System.err.println("Peso inválido para aresta " + rotuloOrigem + " -> " + rotuloDestino
                                + ". Usando 1.0");
                    }
                }

                if (direcionado) {
                    // Grafo direcionado: adiciona apenas uma vez
                    meuGrafo.addAresta(rotuloOrigem, rotuloDestino, peso);
                } else {
                    // Grafo não-direcionado: evita processar a mesma aresta duas vezes
                    // (a biblioteca retorna a -- b tanto em 'a' quanto em 'b')
                    String chaveAresta = criarChaveAresta(rotuloOrigem, rotuloDestino);

                    if (!arestasProcessadas.contains(chaveAresta)) {
                        // addAresta já cuida de criar a aresta bidirecional!
                        meuGrafo.addAresta(rotuloOrigem, rotuloDestino, peso);
                        arestasProcessadas.add(chaveAresta);
                    }
                }
            });
        }

        return meuGrafo;
    }

    /**
     * Cria uma chave única para identificar uma aresta não-direcionada.
     * A ordem dos vértices não importa: (a,b) = (b,a)
     */
    private String criarChaveAresta(String v1, String v2) {
        if (v1.compareTo(v2) < 0) {
            return v1 + "-" + v2;
        } else {
            return v2 + "-" + v1;
        }
    }
}