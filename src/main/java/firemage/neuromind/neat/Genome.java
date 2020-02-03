package firemage.neuromind.neat;

import firemage.neuromind.util.structures.RandomSet;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class Genome {

    public static final double C1 = 1.0;
    public static final double C2 = 1.0;
    public static final double C3 = 0.4;
    public static final double WEIGHT_SHIFT_STRENGTH = 0.3;
    public static final double WEIGHT_RANDOM_STRENGTH = 1.0;

    public static final int MAX_MUTATE_TRIES = 100;

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

    public void mutate() {

    }

    public void mutateLink() {
        for (int i = 0; i < MAX_MUTATE_TRIES; i++) {
            Node start = nodes.getRandomElement();
            Node end = nodes.getRandomElement();

            if (start.getX() == end.getX()) {
                continue;
            } else if (start.getX() < end.getX()) {
                Optional<ConnectionWrapper> connection = findConnection(start, end);
                if (connection.isPresent() && !connection.get().isEnabled()) {
                    connection.get().setEnabled(true);
                }
                ConnectionWrapper connectionWrapper = new ConnectionWrapper(genePool.requestConnection(start, end), getRandomWeight());
            }
        }
    }

    public void mutateNode() {
        ConnectionWrapper connection = connections.getRandomElement();

    }

    public void mutateWeightShift() {
        if (connections.size() > 0) {
            ConnectionWrapper connection = connections.getRandomElement();
            connection.setWeight(ThreadLocalRandom.current().nextDouble(-1, 1) * WEIGHT_SHIFT_STRENGTH + connection.getWeight());
        }
    }

    public void mutateWeightRandom() {
        if (connections.size() > 0) {
            ConnectionWrapper connection = connections.getRandomElement();
            connection.setWeight(getRandomWeight());
        }
    }

    private double getRandomWeight() {
        return ThreadLocalRandom.current().nextDouble(-1, 1) * WEIGHT_RANDOM_STRENGTH;
    }

    public void mutateLinkToggle() {
        if (connections.size() > 0) {
            ConnectionWrapper connection = connections.getRandomElement();
            connection.setEnabled(!connection.isEnabled());
        }
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

    public Optional<ConnectionWrapper> findConnection(Node from, Node to) {
        return connections.stream().filter(c -> c.getConnection().getFrom().equals(from) && c.getConnection().getTo().equals(to)).findAny();
    }
}
