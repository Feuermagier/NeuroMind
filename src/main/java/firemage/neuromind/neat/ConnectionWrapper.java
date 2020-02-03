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

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setWeight(double weight) {
        this.weight = weight;
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
