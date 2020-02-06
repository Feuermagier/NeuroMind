package firemage.neuromind.examples.snake;

import firemage.neuromind.neat.Client;
import firemage.neuromind.neat.Neat;
import firemage.neuromind.neat.ScoreFunction;
import firemage.neuromind.neat.ui.GenomeDrawer;
import firemage.neuromind.neat.ui.Organism;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class NeatSnake extends Application {

    private static final int BOARD_SIZE = 10;
    private static final int SNAKE_PLACEMENT_OFFSET = 1;
    private static final int MAX_MOVES_WITHOUT_FOOD = 20;
    private static final int SNAKE_MOVE_DELAY_MS = 100;
    private static final int TRIES_FOR_SCORE = 50;

    private boolean shouldEvolve = false;
    private int generation = 0;

    @Override
    public void start(Stage primaryStage) throws Exception {
        ObservableList<Client> topClients = trainNeat(1500);

        VBox mainBox = new VBox();
        Button stepButton = new Button("Loading...");
        stepButton.setOnAction(actionEvent -> {
            Client client = topClients.get(topClients.size() - 1);
            GenomeDrawer genomeDrawer = new GenomeDrawer();
            genomeDrawer.setGenome(client.getGenome(), client.getScore());

            final Board board = new Board(BOARD_SIZE, SNAKE_PLACEMENT_OFFSET);
            BoardDrawer boardDrawer = new BoardDrawer(board);
            genomeDrawer.setWidth(boardDrawer.getWidth());
            genomeDrawer.setHeight(boardDrawer.getHeight());

            Timeline timeline = new Timeline(new KeyFrame(Duration.millis(SNAKE_MOVE_DELAY_MS), e -> {
                board.setHeadDirection(evaluateSnakeResult(client, board));
                if (board.move() == Result.DIED)
                    board.reset();
                boardDrawer.draw();
            }));
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();

            HBox newBox = new HBox(boardDrawer, genomeDrawer);
            newBox.setSpacing(10);
            Stage extraStage = new Stage();
            extraStage.setTitle("Best genome of generation " + generation);
            extraStage.setScene(new Scene(new ScrollPane(newBox)));
            extraStage.show();
        });

        ObservableList<XYChart.Data<Number, Number>> scores = FXCollections.observableArrayList();
        topClients.addListener((InvalidationListener) change -> {
            Platform.runLater(() -> {
                Client client = topClients.get(topClients.size() - 1);
                scores.add(new XYChart.Data<>(generation, client.getScore()));
                stepButton.setText("Show gen " + generation);
            });
        });

        XYChart.Series<Number, Number> scoreSeries = new XYChart.Series<>(scores);
        scoreSeries.setName("Best score");
        LineChart<Number, Number> scoreChart = new LineChart<>(new NumberAxis("Generation", 0, 600, 30), new NumberAxis("Score", 0, 30, 2));
        scoreChart.setData(FXCollections.observableArrayList(scoreSeries));
        scoreChart.setCreateSymbols(false);
        scoreChart.setPrefWidth(600 * 5);
        scoreChart.setAnimated(false);

        mainBox.setSpacing(30);
        mainBox.setPadding(new Insets(20));
        mainBox.getChildren().addAll(stepButton, new ScrollPane(scoreChart));
        primaryStage.setScene(new Scene(mainBox));
        primaryStage.setTitle("NeatSnake");
        primaryStage.setOnCloseRequest(e -> System.exit(0));
        primaryStage.setMaximized(true);
        primaryStage.show();

        shouldEvolve = true;
    }

    private ObservableList<Client> trainNeat(int clients) {
        final ObservableList<Client> topClients = FXCollections.observableArrayList();

        ScoreFunction scoreFunction = client -> {
            List<Integer> scores = new ArrayList<>();
            for (int i = 0; i < TRIES_FOR_SCORE; i++) {
                Board board = new Board(BOARD_SIZE, SNAKE_PLACEMENT_OFFSET);
                int stepsWithoutFood = 0;
                int score = 0;
                while(true) {
                    board.setHeadDirection(evaluateSnakeResult(client, board));
                    Result result = board.move();
                    if(result == Result.OK) {
                        stepsWithoutFood++;
                    } else if(result == Result.EATEN) {
                        stepsWithoutFood = 0;
                        score++;
                    } else {
                        scores.add(score);
                        break;
                    }
                    if(stepsWithoutFood > MAX_MOVES_WITHOUT_FOOD) {
                        scores.add(score);
                        break;
                    }
                }
            }
            return scores.stream().mapToInt(i -> i).average().getAsDouble();
        };

        final Neat neat = new Neat(7, 4, scoreFunction, clients, true);

        new Thread(() -> {

            while(true) {
                if (shouldEvolve) {
                    neat.evolve();
                    topClients.add(neat.getBestClient());
                    generation++;
                } else {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        return topClients;
    }

    private double positionToNumber(State state) {
        if (state == State.EMPTY || state == State.FOOD)
            return 0;
        else
            return 1;
    }

    private Direction evaluateSnakeResult(Organism client, Board board) {
        Point head = board.getHead();

        List<Double> inputs = new ArrayList<>();
        /*
        for (int x = -VIEW_RADIUS; x < VIEW_RADIUS + 1; x++) {
            for (int y = -VIEW_RADIUS; y < VIEW_RADIUS + 1; y++) {
                if (! (x == VIEW_RADIUS && y == VIEW_RADIUS))   // Ignore snake's head
                    inputs.add(positionToNumber(board.getStateAt(head.add(x, y))));
            }
        }
        */
        inputs.add(positionToNumber(board.getStateAt(head.add(-1, 0))));
        inputs.add(positionToNumber(board.getStateAt(head.add(0, 1))));
        inputs.add(positionToNumber(board.getStateAt(head.add(1, 0))));
        inputs.add(positionToNumber(board.getStateAt(head.add(0, -1))));
        inputs.add((double) board.getFood().getX() - head.getX());
        inputs.add((double) board.getFood().getY() - head.getY());
        inputs.add(1.0);    // Bias

        List<Double> output = client.think(inputs);

        double maxValue = 0;
        int maxIndex = 0;
        for (int i = 0; i < 4; i++) {
            if (output.get(i) > maxValue) {
                maxValue = output.get(i);
                maxIndex = i;
            }
        }
        switch(maxIndex) {
            case 0:
                return Direction.NORTH;
            case 1:
                return Direction.EAST;
            case 2:
                return Direction.SOUTH;
            case 3:
                return Direction.WEST;
        }
        throw new IllegalStateException();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
