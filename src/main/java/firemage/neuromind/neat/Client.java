package firemage.neuromind.neat;

import firemage.neuromind.neat.ui.Organism;

import java.util.List;

public class Client implements Organism {

    private Genome genome;
    private double score;
    private boolean scoreCalculated = false;
    private ScoreFunction scoreFunction;

    public Client(Genome genome, ScoreFunction scoreFunction) {
        this.genome = genome;
        this.scoreFunction = scoreFunction;
    }

    public double distance(Client other) {
        return genome.distance(other.getGenome());
    }

    @Override
    public List<Double> think(List<Double> input) {
        return new Calculator().calculate(genome, input);
    }

    public Genome getGenome() {
        return genome;
    }

    public void setGenome(Genome genome) {
        this.genome = genome;
    }

    public double getScore() {
        if (!scoreCalculated) {
            score = scoreFunction.score(this);
            scoreCalculated = true;
        }
        return score;
    }
}
