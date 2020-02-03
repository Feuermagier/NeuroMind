package firemage.neuromind.neat.ui;

import firemage.neuromind.neat.ConnectionWrapper;
import firemage.neuromind.neat.Genome;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;


public class GenomeDrawer extends Canvas {

    private static final Color BACKGROUND = Color.SANDYBROWN;
    private static final int NODE_RADIUS = 10;
    private static final Color NODE_COLOR = Color.BLACK;
    private static final int CONNECTION_THICKNESS = 3;
    private static final Color CONNECTION_COLOR = Color.BROWN;


    private final Genome genome;

    public GenomeDrawer(Genome genome) {
        this.genome = genome;

        draw();
        this.widthProperty().addListener(width -> draw());
        this.heightProperty().addListener(height -> draw());
    }

    public void draw() {
        GraphicsContext gc = this.getGraphicsContext2D();

        double width = getWidth();
        double height = getHeight();

        gc.setFill(BACKGROUND);
        gc.fillRect(0, 0, width, height);

        gc.setStroke(CONNECTION_COLOR);
        genome.getConnections().stream().map(ConnectionWrapper::getConnection).forEach(c -> {
            gc.beginPath();
            gc.setLineWidth(CONNECTION_THICKNESS);
            gc.moveTo(c.getFrom().getX() * width, c.getFrom().getY() * height);
            gc.lineTo(c.getTo().getX() * width, c.getTo().getY() * height);
            gc.stroke();
        });

        gc.setFill(NODE_COLOR);
        genome.getNodes().forEach(n ->
                gc.fillOval(n.getX() * width - NODE_RADIUS, n.getY() * height - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2)
        );
    }
}
