package firemage.neuromind.examples.snake;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class Board {
    private final State[][] positions;
    private final int snakePlacementOffset;

    private Queue<Point> snake = new LinkedList<>();
    private Point head;
    private Direction headDirection;
    private Direction lastHeadDirection;
    private Point food;

    public Board(int size, int snakePlacementOffset) {
        positions = new State[size][size];
        this.snakePlacementOffset = snakePlacementOffset;

        reset();
    }

    public void reset() {

        //head = Point.random(snakePlacementOffset, getSize() - snakePlacementOffset, snakePlacementOffset, getSize() - snakePlacementOffset);
        //headDirection = Direction.random();
        head = new Point(Math.floorDiv(getSize(), 2), Math.floorDiv(getSize(), 2));
        headDirection = Direction.NORTH;
        lastHeadDirection = headDirection;

        for(State[] position : positions) {
            Arrays.fill(position, State.EMPTY);
        }
        snake.clear();
        snake.add(head);
        setPosition(head, State.HEAD);

        placeFood();
    }


    public State getStateAt(Point point) {
        if (point.getX() < 0 || point.getX() >= positions.length || point.getY() < 0 || point.getY() >= positions.length)
            return State.BLOCKED;
        return positions[point.getX()][point.getY()];
    }


    public Result move() {
        Point newHead = null;
        switch(headDirection) {
            case NORTH:
                newHead = head.add(0, -1);
                break;
            case SOUTH:
                newHead = head.add(0, 1);
                break;
            case WEST:
                newHead = head.add(-1, 0);
                break;
            case EAST:
                newHead = head.add(1, 0);
                break;
        }

        if (getStateAt(newHead) == State.EMPTY) {
            setPosition(head, State.BODY);
            setPosition(newHead, State.HEAD);
            snake.add(newHead);
            setPosition(snake.remove(), State.EMPTY);
            head = newHead;
            return Result.OK;
        } else if(getStateAt(newHead) == State.FOOD) {
            setPosition(head, State.BODY);
            setPosition(newHead, State.HEAD);
            snake.add(newHead);
            placeFood();
            head = newHead;
            return Result.EATEN;
        } else {
            return Result.DIED;
        }
    }

    public void setHeadDirection(Direction direction) {
        lastHeadDirection = headDirection;
        this.headDirection = direction;
    }

    public Direction getLastHeadDirection() {
        return lastHeadDirection;
    }

    public int getSize() {
        return positions.length;
    }

    public int getSnakeLength() {
        return snake.size();
    }

    public Point getHead() {
        return head;
    }

    public Point getFood() {
        return food;
    }

    private void placeFood() {
        while (true) {
            food = Point.random(positions.length - 1, positions.length - 1);
            if (positions[food.getX()][food.getY()] == State.EMPTY) {
                setPosition(food, State.FOOD);
                break;
            }
        }
    }

    private void setPosition(Point point, State state) {
        positions[point.getX()][point.getY()] = state;
    }
}
