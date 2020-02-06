package firemage.neuromind.neat;

import firemage.neuromind.util.structures.RandomSet;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class Genome {

    public static final double C1 = 1.0;    // = C2 in the original paper
    public static final double C2 = 0.4;    // = C3 in the original paper
    public static final double WEIGHT_SHIFT_STRENGTH = 0.8;
    public static final double WEIGHT_RANDOM_STRENGTH = 2.0;

    public static final double CROSSOVER_LINK_DISABLE_PROBABILITY = 0.75;   // If the link is disabled at at least one parent

    public static double MUTATE_ADD_LINK_PROBABILITY_SMALL = 0.05;
    public static double MUTATE_ADD_LINK_PROBABILITY_LARGE = 0.3;
    public static double MUTATE_ADD_NODE_PROBABILITY_SMALL = 0.003;
    public static double MUTATE_ADD_NODE_PROBABILITY_LARGE = 0.003;
    public static double MUTATE_WEIGHT_PROBABILITY = 0.8;
    public static double MUTATE_WEIGHT_SHIFT_PROBABILITY = 0.9;

    public static final int MAX_MUTATE_TRIES = 100;

    private final GenePool genePool;

    private RandomSet<ConnectionWrapper> connections = new RandomSet<>();
    private RandomSet<Node> nodes = new RandomSet<>();

    public Genome(GenePool genePool) {
        this.genePool = genePool;
    }

    // Doesn't distinguish between excess and disjoint genes
    public double distance(Genome g2) {

        int disjoint = 0;
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
                    disjoint++;
                }
            } else if(con2.isPresent()) {
                disjoint++;
            }
        }

        if (similar != 0)
            weightDiff /= similar;
        double N = Math.max(this.getConnections().size(), g2.getConnections().size());
        if (N < 20) N = 1;

        return (C1 * disjoint) / N + C2 * weightDiff;
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
                    ConnectionWrapper connection;
                    if (ThreadLocalRandom.current().nextBoolean()) {
                        connection = new ConnectionWrapper(con1.get());
                    } else {
                        connection = new ConnectionWrapper(con2.get());
                    }
                    if (!con1.get().isEnabled() || !con2.get().isEnabled()) {
                        connection.setEnabled(ThreadLocalRandom.current().nextDouble() >= CROSSOVER_LINK_DISABLE_PROBABILITY);
                    }
                    offspring.addConnection(connection);
                } else {
                    offspring.addConnection(new ConnectionWrapper(con1.get()));
                }
            }
            // Ignore disjoint / excess genes of g2
        }

        offspring.getNodes().addAll(this.getNodes().asList());

        return offspring;
    }

    public void mutateLargeIntensity() {
        final ThreadLocalRandom random = ThreadLocalRandom.current();
        mutateLink(random);
        if (random.nextDouble() < MUTATE_ADD_NODE_PROBABILITY_LARGE) {
            mutateAddNode();
        }
        if (random.nextDouble() < MUTATE_ADD_LINK_PROBABILITY_LARGE) {
            mutateAddLink();
        }
    }

    public void mutateSmallIntensity() {
        final ThreadLocalRandom random = ThreadLocalRandom.current();
        mutateLink(random);
        if (random.nextDouble() < MUTATE_ADD_NODE_PROBABILITY_SMALL) {
            mutateAddNode();
        }
        if (random.nextDouble() < MUTATE_ADD_LINK_PROBABILITY_SMALL) {
            mutateAddLink();
        }
    }

    private void mutateLink(ThreadLocalRandom random) {
        if (random.nextDouble() < MUTATE_WEIGHT_PROBABILITY) {
            connections.forEach(connection -> {
                if(random.nextDouble() < MUTATE_WEIGHT_SHIFT_PROBABILITY) {
                    connection.setWeight(connection.getWeight() + random.nextGaussian() * WEIGHT_SHIFT_STRENGTH);
                } else {
                    connection.setWeight(getRandomWeight());
                }
            });
        }
    }

    private void mutateAddLink() {
        for (int i = 0; i < MAX_MUTATE_TRIES; i++) {
            Node start = nodes.getRandomElement();
            Node end = nodes.getRandomElement();

            if (start.getX() == end.getX()) {
                continue;
            }

            if (start.getX() > end.getX()) {
                Node tmp = end;
                end = start;
                start = tmp;
            }

            Optional<ConnectionWrapper> connection = findConnection(start, end);
            if (connection.isPresent()) {
                if (!connection.get().isEnabled())
                    connection.get().setEnabled(true);
                else
                    continue;
            } else {
                ConnectionWrapper connectionWrapper = new ConnectionWrapper(genePool.requestConnection(start, end), getRandomWeight());
                connections.add(connectionWrapper);
            }
            return;
        }
    }

    private double getRandomWeight() {
        return ThreadLocalRandom.current().nextDouble(-WEIGHT_RANDOM_STRENGTH, WEIGHT_RANDOM_STRENGTH);
    }

    private void mutateAddNode() {
        if (connections.size() == 0) return;
        ConnectionWrapper con = connections.getRandomElement();

        Node middle = genePool.createNode((con.getConnection().getFrom().getX() + con.getConnection().getTo().getX()) / 2,
                (con.getConnection().getFrom().getY() + con.getConnection().getTo().getY()) / 2 + ThreadLocalRandom.current().nextDouble(-0.05, 0.05), con.getConnection());
        nodes.add(middle);
        connections.add(new ConnectionWrapper(genePool.requestConnection(con.getConnection().getFrom(), middle), 1.0));
        ConnectionWrapper end = new ConnectionWrapper(genePool.requestConnection(middle, con.getConnection().getTo()), con.getWeight());
        end.setEnabled(con.isEnabled());
        connections.add(end);

        con.setEnabled(false);
    }

    private Optional<ConnectionWrapper> findConnection(int innovation) {
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

    public List<Node> getInputNodes() {
        return genePool.getInputNodes();
    }


    public List<Node> getOutputNodes() {
        return genePool.getOutputNodes();
    }
}
