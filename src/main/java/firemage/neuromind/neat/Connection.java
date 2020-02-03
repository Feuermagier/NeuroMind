package firemage.neuromind.neat;

public class Connection extends Gene {

    private final Node from, to;

    public Connection(int innovation, Node from, Node to) {
        super(innovation);
        this.from = from;
        this.to = to;
    }

    public Node getFrom() {
        return from;
    }

    public Node getTo() {
        return to;
    }
}
