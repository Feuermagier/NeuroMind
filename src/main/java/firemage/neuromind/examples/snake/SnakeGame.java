package firemage.neuromind.examples.snake;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SnakeGame extends Application {

    private static final int DELAY_MS = 100;
    private static final int GAME_OVER_FRAME_COUNT = 30;


    private boolean gameStarted = false;
    private int gameOverFrames = 0;

    @Override
    public void start(Stage primaryStage) throws Exception {

        Board board = new Board(30,5);

        Pane parent = new Pane();
        BoardDrawer boardDrawer = new BoardDrawer(board);
        parent.getChildren().add(boardDrawer);
        Timeline movement = new Timeline(new KeyFrame(Duration.millis(DELAY_MS), actionEvent -> {
            if (gameOverFrames > 0) {
                gameOverFrames--;
                boardDrawer.drawGameOverScreen();
                if (gameOverFrames == 0)
                    board.reset();
            } else {
                if (gameStarted) {
                    if(board.move() == Result.DIED) {
                        gameStarted = false;
                        gameOverFrames = GAME_OVER_FRAME_COUNT;
                    }
                }
                boardDrawer.draw();
            }
        }));
        movement.setCycleCount(Timeline.INDEFINITE);
        movement.play();

        Scene scene = new Scene(parent);

        scene.setOnKeyPressed(event -> {
            if (gameOverFrames > 0)
                return;

            if (!gameStarted ) {
                gameStarted = true;
            }

            switch(event.getCode()) {
                case UP:
                    board.setHeadDirection(Direction.NORTH);
                    break;
                case RIGHT:
                    board.setHeadDirection(Direction.EAST);
                    break;
                case DOWN:
                    board.setHeadDirection(Direction.SOUTH);
                    break;
                case LEFT:
                    board.setHeadDirection(Direction.WEST);
                    break;
            }
        });

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
