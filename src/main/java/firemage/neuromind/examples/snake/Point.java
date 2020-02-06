package firemage.neuromind.examples.snake;

import java.util.concurrent.ThreadLocalRandom;

public class Point {
    private final int x, y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Point add(int x, int y) {
        return new Point(this.x + x, this.y + y);
    }

    // Max values are exclusive
    public static Point random(int maxX, int maxY) {
        return random(0, maxX, 0, maxY);
    }

    // Max values are exclusive, min values are inclusive
    public static Point random(int minX, int maxX, int minY, int maxY) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return new Point(random.nextInt(minX, maxX), random.nextInt(minY, maxY));
    }
}
