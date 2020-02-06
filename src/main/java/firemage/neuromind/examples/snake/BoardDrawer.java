package firemage.neuromind.examples.snake;

import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class BoardDrawer extends Canvas {

    private static final Color EMPTY_COLOR = Color.SANDYBROWN;
    private static final Color BODY_COLOR = Color.LIGHTGREEN;
    private static final Color HEAD_COLOR = Color.DARKGREEN;
    private static final Color FOOD_COLOR = Color.RED;
    private static final Color TEXT_COLOR = Color.DARKGREEN;

    private static final int SQUARE_SIZE = 60;

    private final Board board;

    public BoardDrawer(Board board) {
        super(board.getSize() * SQUARE_SIZE, board.getSize() * SQUARE_SIZE);

        this.board = board;
        draw();
    }

    public void draw() {

        GraphicsContext gc = getGraphicsContext2D();

        gc.setFill(EMPTY_COLOR);
        gc.fillRect(0, 0, getWidth(), getHeight());

        for (int x = 0; x < board.getSize(); x++) {
            for (int y = 0; y < board.getSize(); y++) {
                switch(board.getStateAt(new Point(x, y))) {
                    case HEAD:
                        gc.setFill(HEAD_COLOR);
                        gc.fillOval(SQUARE_SIZE * x, SQUARE_SIZE * y, SQUARE_SIZE, SQUARE_SIZE);
                        break;
                    case BODY:
                        gc.setFill(BODY_COLOR);
                        gc.fillOval(SQUARE_SIZE * x, SQUARE_SIZE * y, SQUARE_SIZE, SQUARE_SIZE);
                        break;
                    case FOOD:
                        gc.setFill(FOOD_COLOR);
                        gc.fillOval(SQUARE_SIZE * x, SQUARE_SIZE * y, SQUARE_SIZE, SQUARE_SIZE);
                        break;
                }
            }
        }
    }

    public void drawGameOverScreen() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.setFont(new Font(20));
        gc.setFill(TEXT_COLOR);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText("GAME OVER", getWidth() / 2, getHeight() / 2);
    }
}
