package firemage.neuromind.neat;

import java.util.*;
import java.util.stream.IntStream;

public class GenePool {

    public static final double INPUT_X = 0.1;
    public static final double OUTPUT_X = 0.9;

    private List<Node> nodes = new ArrayList<>();
    private List<Connection> connections = new ArrayList<>();
    private List<Node> inputNodes = new ArrayList<>();
    private List<Node> outputNodes = new ArrayList<>();
    private Map<Connection, Node> replaceMap = new HashMap<>();

    public GenePool(int inputSize, int outputSize) {
        IntStream.range(0, inputSize).forEach(i -> inputNodes.add(createNode(INPUT_X, (i + 1d) / (inputSize + 1), null)));
        IntStream.range(0, outputSize).forEach(i -> outputNodes.add(createNode(OUTPUT_X, (i + 1d) / (outputSize + 1), null)));
    }

    public Node createNode(double x, double y, Connection replacedConnection) {
        if (replacedConnection != null && replaceMap.containsKey(replacedConnection)) {
            return replaceMap.get(replacedConnection);
        }
        Node node = new Node(nodes.size(), x, y);
        nodes.add(node);
        if (replacedConnection != null)
            replaceMap.put(replacedConnection, node);
        return node;
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

    public List<Node> getInputNodes() {
        return inputNodes;
    }

    public List<Node> getOutputNodes() {
        return outputNodes;
    }
}
