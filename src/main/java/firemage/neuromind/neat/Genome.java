package firemage.neuromind.neat;

import firemage.neuromind.util.structures.RandomSet;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class Genome {

    public static final double C1 = 1.0;
    public static final double C2 = 1.0;
    public static final double C3 = 0.4;

    private final GenePool genePool;

    private RandomSet<ConnectionWrapper> connections = new RandomSet<>();
    private RandomSet<Node> nodes = new RandomSet<>();

    public Genome(GenePool genePool) {
        this.genePool = genePool;
    }

    public double distance(Genome g2) {

        int disjoint = 0;
        int excess = 0;
        int similar = 0;
        double weightDiff = 0;

        for (int i = 0; i < genePool.connectionCount(); i++) {
            Optional<ConnectionWrapper> con1 = this.findConnection(i);
            Optional<ConnectionWrapper> con2 = g2.findConnection(i);
            if (con1.isPresent()) {
                if (con2.isPresent()) {
                    // Similar gene
                    similar++;
                    weightDiff += Math.abs(con1.get().getWeight() - con2.get().getWeight());
                } else {
                    if (i >= g2.getConnections().size()) {
                        // Excess gene
                        excess++;
                    } else {
                        // Disjoint gene
                        disjoint++;
                    }
                }
            } else {
                if (i >= this.getConnections().size()) {
                    // Excess gene
                    excess++;
                } else {
                    // Disjoint gene
                    disjoint++;
                }
            }
        }

        weightDiff /= similar;
        double N = Math.max(this.getConnections().size(), g2.getConnections().size());
        if (N < 20) N = 1;

        return (C1 * disjoint + C2 * excess) / N + C3 * weightDiff;
    }

    //TODO
    public void mutate() {

    }

    // Assumes g2 is less fit than this genome
    public Genome crossover(Genome g2) {

        Genome offspring = new Genome(genePool);

        for (int i = 0; i < genePool.connectionCount(); i++) {
            Optional<ConnectionWrapper> con1 = this.findConnection(i);
            Optional<ConnectionWrapper> con2 = g2.findConnection(i);
            if (con1.isPresent()) {
                if (con2.isPresent()) {
                    // Similar gene -> choose random connection
                    if (ThreadLocalRandom.current().nextDouble() >= 0.5) {
                        offspring.addConnection(new ConnectionWrapper(con1.get()));
                    } else {
                        offspring.addConnection(new ConnectionWrapper(con2.get()));
                    }
                } else {
                    offspring.addConnection(new ConnectionWrapper(con1.get()));
                }
            }
            // Ignore disjoint / excess genes of g2
        }

        return offspring;
    }

    public Optional<ConnectionWrapper> findConnection(int innovation) {
        return connections.stream().filter(c -> c.getConnection().getInnovation() == innovation).findAny();
    }

    public RandomSet<ConnectionWrapper> getConnections() {
        return connections;
    }

    public void addConnection(ConnectionWrapper connectionWrapper) {
        // Copy nodes
        addNode(connectionWrapper.getConnection().getFrom());
        addNode(connectionWrapper.getConnection().getTo());

        connections.add(connectionWrapper);
    }

    public void addNode(Node node) {
        nodes.add(node);
    }

    public RandomSet<Node> getNodes() {
        return nodes;
    }
}
