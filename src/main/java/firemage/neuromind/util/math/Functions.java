package firemage.neuromind.util.math;

public final class Functions {
    private Functions() { }

    public static double sigmoid(double value) {
        return 1d / (1 + Math.exp(-value));
    }
}
