package br.ufrn.imd;

public class Vertice {
    private final String rotulo;

    public Vertice(String rotulo){
        this.rotulo = rotulo;
    }

    public String getRotulo(){
        return rotulo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vertice vertice = (Vertice) o;
        return rotulo != null ? rotulo.equals(vertice.rotulo) : vertice.rotulo == null;
    }

    @Override
    public int hashCode() {
        return rotulo != null ? rotulo.hashCode() : 0;
    }

    @Override
    public String toString() {
        return rotulo;
    }
}
