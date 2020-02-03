package firemage.neuromind.neat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GenePool {

    public static final int MAX_NODES = (int) Math.pow(2, 20);

    private List<Node> nodes = new ArrayList<>();
    private List<Connection> connections = new ArrayList<>();

    public void addNode(double x, double y) {
        nodes.add(new Node(nodes.size(), x, y));
    }

    public Node getNode(int innovation) {
        return nodes.get(innovation);
    }

    public synchronized Connection requestConnection(Node from, Node to) {
        Optional<Connection> connection = connections.stream().filter(c -> c.getFrom().equals(from) && c.getTo().equals(to)).findFirst();
        if (connection.isPresent()) {
            return connection.get();
        } else {
            Connection newConnection = new Connection(connections.size(), from, to);
            connections.add(newConnection);
            return newConnection;
        }
    }

    public int nodeCount() {
        return nodes.size();
    }

    public int connectionCount() {
        return connections.size();
    }
}
