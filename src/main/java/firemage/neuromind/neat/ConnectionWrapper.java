package firemage.neuromind.neat;

public class ConnectionWrapper {
    private final Connection connection;

    private double weight;
    private boolean enabled;

    public ConnectionWrapper(Connection connection, double weight) {
        this.connection = connection;
        this.weight = weight;
        this.enabled = true;
    }

    public ConnectionWrapper(ConnectionWrapper wrapper) {
        this(wrapper.getConnection(), wrapper.getWeight());
        this.enabled = wrapper.isEnabled();
    }

    public void disable() {
        enabled = false;
    }

    public double getWeight() {
        return weight;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Connection getConnection() {
        return connection;
    }
}
