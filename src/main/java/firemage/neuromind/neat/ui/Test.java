package firemage.neuromind.neat.ui;

import firemage.neuromind.neat.Client;
import firemage.neuromind.neat.Neat;
import firemage.neuromind.neat.ScoreFunction;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class Test extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        ScoreFunction scoreFunction = c -> {
            double score = 4;
            score -= Math.pow(0 - c.think(Arrays.asList(1d, 0d, 0d)).get(0), 2);
            score -= Math.pow(1 - c.think(Arrays.asList(1d, 0d, 1d)).get(0), 2);
            score -= Math.pow(1 - c.think(Arrays.asList(1d, 1d, 0d)).get(0), 2);
            score -= Math.pow(0 - c.think(Arrays.asList(1d, 1d, 1d)).get(0), 2);
            return score;
        };
        Neat neat = new Neat(3, 1, scoreFunction,150, true);

        ObservableList<XYChart.Data<Number, Number>> scores = FXCollections.observableArrayList();
        IntStream.range(0, 200).forEach(i -> {
            neat.evolve();
            System.out.println("Generation " + i + ", " + neat.getSpecies().size() + " species");
            scores.add(new XYChart.Data<>(i, neat.getBestClient().getScore()));
        });

        Client best = neat.getBestClient();
        System.out.println(best.think(Arrays.asList(1d, 0d, 0d)).get(0));
        System.out.println(best.think(Arrays.asList(1d, 0d, 1d)).get(0));
        System.out.println(best.think(Arrays.asList(1d, 1d, 0d)).get(0));
        System.out.println(best.think(Arrays.asList(1d, 1d, 1d)).get(0));

        List<GenomeDrawer> genomeDrawers = new ArrayList<>();
        genomeDrawers.add(new GenomeDrawer(neat.getBestClient().getGenome(), neat.getBestClient().getScore()));
        //neat.getBestClients().stream().sorted(Comparator.comparingDouble(Client::getScore)).forEach(c -> genomeDrawers.add(new GenomeDrawer(c.getGenome(), c.getScore())));
        /*
        HBox buttonBox = new HBox();
        Button mutateRandomWeight = new Button("Random weight");
        mutateRandomWeight.setOnAction(event -> {genome.mutateWeightRandom(); drawer.draw();});
        Button mutateWeightShift = new Button("Weight shift");
        mutateWeightShift.setOnAction(event -> {genome.mutateWeightShift(); drawer.draw();});
        Button mutateLink = new Button("Mutate link");
        mutateLink.setOnAction(event -> {genome.mutateLink(); drawer.draw();});
        Button mutateNode = new Button("Mutate node");
        mutateNode.setOnAction(event -> {genome.mutateNode(); drawer.draw();});
        Button mutateDisable = new Button("On / Off");
        mutateDisable.setOnAction(event -> {genome.mutateLinkToggle(); drawer.draw();});
        Button mutate = new Button("Mutate");
        mutate.setOnAction(event -> {genome.mutate(); drawer.draw();});
        Button calculate = new Button("Calculate");
        calculate.setOnAction(event -> {
            List<Double> outputs = new Calculator().calculate(genome, input);
            System.out.println(outputs);
        });
        buttonBox.getChildren().addAll(mutateRandomWeight, mutateWeightShift, mutateLink, mutateNode, mutateDisable, mutate, calculate);

        buttonBox.setSpacing(10);
        buttonBox.setPadding(new Insets(20));
        */

        LineChart<Number, Number> scoreChart = new LineChart<>(new NumberAxis("Generation", 0, scores.size(), 10), new NumberAxis("Score", 0, 4.0, 0.1));
        XYChart.Series<Number, Number> series = new XYChart.Series<>(scores);
        series.setName("Best score");
        scoreChart.setData(FXCollections.observableArrayList(series));
        scoreChart.setTitle("Score diagram");
        scoreChart.setCreateSymbols(false);

        HBox mainContent = new HBox();
        mainContent.getChildren().addAll(genomeDrawers);
        BorderPane pane = new BorderPane(new ScrollPane(mainContent));
        genomeDrawers.forEach(drawer -> {
            drawer.widthProperty().bind(pane.widthProperty());
            drawer.heightProperty().bind(pane.heightProperty());
        });
        //pane.setTop(buttonBox);
        pane.setBottom(new ScrollPane(scoreChart));

        primaryStage.setScene(new Scene(pane));
        primaryStage.setWidth(1000);
        primaryStage.setHeight(800);

        primaryStage.setTitle("NeuroMind - Neat");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
