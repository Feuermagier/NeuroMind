package firemage.neuromind.examples.snake;

import java.util.concurrent.ThreadLocalRandom;

public enum Direction {
    NORTH(-1, 0),
    EAST(0, 1),
    SOUTH(1, 0),
    WEST(0, -1);

    private int x;
    private int y;

    Direction(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public static Direction random() {
        return values()[ThreadLocalRandom.current().nextInt(values().length)];
    }
}
