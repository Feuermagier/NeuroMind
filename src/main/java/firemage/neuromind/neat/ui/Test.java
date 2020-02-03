package firemage.neuromind.neat.ui;

import firemage.neuromind.neat.ConnectionWrapper;
import firemage.neuromind.neat.Genome;
import firemage.neuromind.neat.Neat;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class Test extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Neat neat = new Neat(100, 3, 2);
        Genome genome = neat.createEmptyGenome();
        genome.addConnection(new ConnectionWrapper(neat.getGenePool().requestConnection(neat.getGenePool().getNode(0), neat.getGenePool().getNode(4)), 1.0));

        GenomeDrawer drawer = new GenomeDrawer(genome);

        HBox buttonBox = new HBox();
        Button mutateRandomWeight = new Button("Random weight");
        Button mutateWeightShift = new Button("Weight shift");
        Button mutateLink = new Button("Mutate link");
        Button mutateNode = new Button("Mutate node");
        Button mutateDisable = new Button("On / Off");
        Button mutate = new Button("Mutate");
        Button calculate = new Button("Calculate");
        buttonBox.getChildren().addAll(mutateRandomWeight, mutateWeightShift, mutateLink, mutateNode, mutateDisable, mutate, calculate);
        buttonBox.setSpacing(10);
        buttonBox.setPadding(new Insets(20));

        BorderPane pane = new BorderPane(drawer);
        drawer.widthProperty().bind(pane.widthProperty());
        drawer.heightProperty().bind(pane.heightProperty());
        pane.setTop(buttonBox);

        primaryStage.setScene(new Scene(pane));
        primaryStage.setWidth(400);
        primaryStage.setHeight(400);

        primaryStage.setTitle("NeroMind - Neat");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
