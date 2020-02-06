package firemage.neuromind.neat;

import firemage.neuromind.util.structures.RandomSet;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Species {

    public static final double CLIENT_DISTANCE_THRESHOLD = 3;

    private RandomSet<Client> clients = new RandomSet<>();
    // The representative is in the most cases not part of the population of this species (he's part of the previous generation)
    private Client representative;
    private double score;

    public Species(Client representative) {
        this.representative = representative;
    }

    public void add(Client client) {
        clients.add(client);
    }

    public boolean matches(Client client) {
        return client.distance(representative) < CLIENT_DISTANCE_THRESHOLD;
    }

    public void evaluateScore() {
        double sum = 0;
        for (Client client : clients.asSet()) {
            sum += client.getScore();
        }
        score = sum / clients.size();
    }

    public void kill(double percentage) {
        List<Client> clientsToRemove = new ArrayList<>();
        clients.stream().sorted(Comparator.comparingDouble(Client::getScore)).limit((int) Math.floor(percentage * clients.size())).forEach(clientsToRemove::add);
        clients.removeAll(clientsToRemove);
    }

    public int size() {
        return clients.size();
    }

    public RandomSet<Client> getClients() {
        return clients;
    }

    public Client getRepresentative() {
        return representative;
    }

    public double getScore() {
        return score;
    }

    public Client getBestClient() {
        Client tmp = clients.get(0);
        for (Client client : clients.asSet()) {
            if (client.getScore() > tmp.getScore())
                tmp = client;
        }
        return tmp;
    }

    @Override
    public String toString() {
        return "Score: " + score + "  Size: " + clients.size();
    }
}
