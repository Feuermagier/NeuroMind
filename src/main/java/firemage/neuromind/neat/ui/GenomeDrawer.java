package firemage.neuromind.neat.ui;

import firemage.neuromind.neat.Genome;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;


public class GenomeDrawer extends Canvas {

    private static final Color BACKGROUND = Color.SANDYBROWN;
    private static final Color TEXT_COLOR = Color.BLACK;
    private static final int NODE_RADIUS = 10;
    private static final Color NODE_COLOR = Color.BLACK;
    private static final Color NODE_TEXT_COLOR = Color.RED;

    private static final int CONNECTION_DISABLED_THICKNESS = 1;
    private static final int CONNECTION_ENABLED_THICKNESS_FACTOR = 3;
    private static final int CONNECTION_ENABLED_MAX_THICKNESS = 30;
    private static final Color CONNECTION_ENABLED_POSITIVE_COLOR = Color.RED;
    private static final Color CONNECTION_ENABLED_NEGATIVE_COLOR = Color.BLUE;
    private static final Color CONNECTION_DISABLED_COLOR = Color.GREY;
    private static final int CONNECTION_TEXT_X_OFFSET = 30;
    private static final int CONNECTION_TEXT_Y_OFFSET = 10;

    private Genome genome = null;
    private double score;

    public GenomeDrawer(Genome genome, double score) {
        this();
        setGenome(genome, score);
    }

    public GenomeDrawer() {
        this.widthProperty().addListener(width -> draw());
        this.heightProperty().addListener(height -> draw());
        draw();
    }

    public void setGenome(Genome genome, double score) {
        this.genome = genome;
        this.score = score;
        draw();
    }

    public void draw() {
        GraphicsContext gc = this.getGraphicsContext2D();

        double width = getWidth();
        double height = getHeight();

        gc.setFill(BACKGROUND);
        gc.fillRect(0, 0, width, height);

        if (genome == null)
            return;

        gc.setFill(TEXT_COLOR);
        gc.fillText("Nodes: " + genome.getNodes().size(), 10, 20);
        gc.fillText("Connections: " + genome.getConnections().size(), 10, 40);
        gc.fillText("Score: " + score, 10, 60);

        genome.getConnections().stream().forEach(c -> {
            if (!c.isEnabled()) {
                gc.setStroke(CONNECTION_DISABLED_COLOR);
                gc.setLineWidth(CONNECTION_DISABLED_THICKNESS);
            } else {
                gc.setLineWidth(Math.min(Math.abs(c.getWeight()) * CONNECTION_ENABLED_THICKNESS_FACTOR, CONNECTION_ENABLED_MAX_THICKNESS));
                if (c.getWeight() >= 0) {
                    gc.setStroke(CONNECTION_ENABLED_POSITIVE_COLOR);
                } else {
                    gc.setStroke(CONNECTION_ENABLED_NEGATIVE_COLOR);
                }
            }
            gc.beginPath();
            gc.moveTo(c.getConnection().getFrom().getX() * width, c.getConnection().getFrom().getY() * height);
            gc.lineTo(c.getConnection().getTo().getX() * width, c.getConnection().getTo().getY() * height);
            gc.stroke();

            /*
            gc.setFill(TEXT_COLOR);
            double m = height / width * (c.getConnection().getTo().getY() - c.getConnection().getFrom().getY()) / (c.getConnection().getTo().getX() - c.getConnection().getFrom().getX());
            double textX = c.getConnection().getFrom().getX() * width + CONNECTION_TEXT_X_OFFSET;
            double textY = c.getConnection().getFrom().getY() * height + m * (CONNECTION_TEXT_X_OFFSET + c.getConnection().getFrom().getX());
            if (m < 0)
                textY += CONNECTION_TEXT_Y_OFFSET;
            else
                textY -=CONNECTION_TEXT_Y_OFFSET;

            gc.fillText(String.format("%.2f", c.getWeight()), textX, textY);
            */
        });

        genome.getNodes().forEach(n -> {
                gc.setFill(NODE_COLOR);
                gc.fillOval(n.getX() * width - NODE_RADIUS, n.getY() * height - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);
                gc.setFill(NODE_TEXT_COLOR);
                gc.fillText(n.getInnovation() + "", n.getX() * width - NODE_RADIUS + 2, n.getY() * height - NODE_RADIUS, NODE_RADIUS * 2 + 2);
        });
    }
}
