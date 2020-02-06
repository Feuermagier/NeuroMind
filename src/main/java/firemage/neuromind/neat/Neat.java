package firemage.neuromind.neat;

import firemage.neuromind.util.RandomSelector;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Neat {

    public static final double SURVIVORS = 0.3;
    // If a species contains more than CLIENT_COPY_THRESHOLD clients, the best one will be copied to the next generation
    public static int CLIENT_COPY_THRESHOLD = 5;

    public static final int LARGE_SPECIES_MIN_SIZE = 50;

    private ScoreFunction scoreFunction;
    private List<Client> clients = new ArrayList<>();
    private List<Species> species = new ArrayList<>();

    private final GenePool genePool;
    private final int clientCount;

    public Neat(int inputSize, int outputSize, ScoreFunction scoreFunction, int clientCount, boolean fullyConnected) {
        if (clientCount <= 0)
            throw new IllegalArgumentException("maxClients must be >= 1");

        this.scoreFunction = scoreFunction;
        this.clientCount = clientCount;
        genePool = new GenePool(inputSize, outputSize);
        IntStream.range(0, clientCount).forEach(i -> clients.add(new Client(createEmptyGenome(fullyConnected), scoreFunction)));
    }

    public Genome createEmptyGenome(boolean fullyConnected) {
        Genome genome = new Genome(genePool);
        genome.getNodes().addAll(genePool.getInputNodes());
        genome.getNodes().addAll(genePool.getOutputNodes());

        if (fullyConnected) {
            genePool.getInputNodes().forEach(input -> {
                genePool.getOutputNodes().forEach(output -> {
                    genome.addConnection(new ConnectionWrapper(genePool.requestConnection(input, output), 0));
                });
            });
        }
        return genome;
    }

    public GenePool getGenePool() {
        return genePool;
    }

    public List<Client> getClients() {
        return clients;
    }

    public List<Species> getSpecies() {
        return species;
    }

    public List<Client> getBestClients() {
        return species.stream().map(s -> s.getClients().get(0)).collect(Collectors.toList());
    }

    public Client getBestClient() {
        Client tmp = clients.get(0);
        for (Client client : clients) {
            if (client.getScore() > tmp.getScore())
                tmp = client;
        }
        return tmp;
    }

    public void evolve() {
        generateSpecies();
        removeExtinctSpecies();
        kill();
        reproduce();
    }

    public void generateSpecies() {
        List<Species> newSpecies = new ArrayList<>();

        species.forEach(s -> newSpecies.add(new Species(s.getClients().getRandomElement())));

        clients.forEach(c -> {
            boolean speciesFound = false;
            for (Species s : newSpecies) {
                if (s.matches(c)) {
                    s.add(c);
                    speciesFound = true;
                    break;
                }
            }
            if (!speciesFound) {
                Species created = new Species(c);
                created.add(c);
                newSpecies.add(created);
            }
        });
        // Evaluate average score
        newSpecies.forEach(Species::evaluateScore);


        species = newSpecies;
    }

    public void removeExtinctSpecies() {
        List<Species> emptySpecies = new ArrayList<>();
        species.forEach(s -> {
            if (s.size() == 0) {
                emptySpecies.add(s);
            }
        });
        species.removeAll(emptySpecies);
    }

    public void kill() {
        species.forEach(s -> s.kill(1 - SURVIVORS));
    }

    public void reproduce() {

        // Copy best genome per species
        List<Client> newClients = new ArrayList<>();
        species.forEach(species -> {
            if (species.size() >= CLIENT_COPY_THRESHOLD) {
                newClients.add(new Client(species.getBestClient().getGenome(), scoreFunction));
            }
        });
        //newClients.add(new Client(getBestClient().getGenome(), scoreFunction));

        RandomSelector<Species> speciesSelector = new RandomSelector<>();
        species.forEach(species -> speciesSelector.add(species, species.getScore()));


        while (newClients.size() < clientCount) {
            Species species = speciesSelector.randomElement();
            RandomSelector<Client> clientSelector = new RandomSelector<>();
            species.getClients().forEach(client -> clientSelector.add(client, client.getScore()));

            Client first = clientSelector.randomElement();
            Client second = clientSelector.randomElement();

            Client newClient;
            if (first.getScore() >= second.getScore())
                newClient = new Client(first.getGenome().crossover(second.getGenome()), scoreFunction);
            else
                newClient = new Client(second.getGenome().crossover(first.getGenome()), scoreFunction);

            if (species.size() >= LARGE_SPECIES_MIN_SIZE)
                newClient.getGenome().mutateLargeIntensity();
            else
                newClient.getGenome().mutateSmallIntensity();

            newClients.add(newClient);
        }

        clients.clear();
        clients.addAll(newClients);

        if (clients.size() != clientCount)
            throw new IllegalStateException("Client count doesn't match");
    }

    public void dumpClients() {
        clients.forEach(client -> {
            System.out.println("Client (Score " + client.getScore() + "):");
            System.out.println("Nodes:");
            client.getGenome().getNodes().stream().mapToInt(Node::getInnovation).sorted().forEach(n -> System.out.print(n + ", "));
            System.out.println("\nConnections:");
            client.getGenome().getConnections().stream().map(ConnectionWrapper::getConnection).forEach(c -> {
                System.out.print(c.getFrom().getInnovation() + " -> " + c.getTo().getInnovation() + " (" + c.getInnovation() + "), ");
            });
            System.out.println("\n");
        });
    }
}
