package firemage.neuromind.neat;

public class Gene {
    private final int innovation;

    public Gene(int innovation) {
        this.innovation = innovation;
    }

    public int getInnovation() {
        return innovation;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Gene gene = (Gene) o;
        return innovation == gene.innovation;
    }

    @Override
    public int hashCode() {
        return innovation;
    }
}
