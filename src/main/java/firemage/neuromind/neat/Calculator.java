package firemage.neuromind.neat;

import firemage.neuromind.util.math.Functions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Calculator {

    // Innovation number maps to value
    private Map<Integer, Double> nodeValues = new HashMap<>();

    public List<Double> calculate(Genome genome, List<Double> input) {
        nodeValues.clear();
        if (input.size() != genome.getInputNodes().size())
            throw new IllegalArgumentException("Provided data doesn't match");

        for (Node node : genome.getInputNodes()) {
            nodeValues.put(node.getInnovation(),input.get(node.getInnovation()));
        }

        List<Double> output = new ArrayList<>(genome.getOutputNodes().size());
        for (Node node : genome.getOutputNodes()) {
            output.add(evaluateNode(node, genome));
        }
        return output;
    }

    private double evaluateNode(Node node, Genome genome) {
        Double value = nodeValues.get(node.getInnovation());
        if (value != null) return value;

        double sum = 0d;
        for (ConnectionWrapper c : genome.getConnections().asSet()) {
            if (!c.isEnabled()) continue;
            if (c.getConnection().getTo().equals(node)) {
                sum += c.getWeight() * evaluateNode(c.getConnection().getFrom(), genome);
            }
        }
        value = Functions.sigmoid(sum);
        nodeValues.put(node.getInnovation(), value);
        return value;
    }
}
