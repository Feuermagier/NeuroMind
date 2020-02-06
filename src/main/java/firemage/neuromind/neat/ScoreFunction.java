package firemage.neuromind.neat;

import firemage.neuromind.neat.ui.Organism;

@FunctionalInterface
public interface ScoreFunction {
    double score(Organism organism);
}
