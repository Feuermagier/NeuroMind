package firemage.neuromind.neat;

public class Node extends Gene implements Comparable<Node> {

    private double x, y;

    public Node(int innovation, double x, double y) {
        super(innovation);

        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public int compareTo(Node o) {
        return Double.compare(o.getX(), x);
    }
}
