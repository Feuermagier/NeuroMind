package firemage.neuromind.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomSelector<T> {
    private List<T> elements = new ArrayList<>();
    private List<Double> scores = new ArrayList<>();
    private double totalScore = 0;

    public synchronized void add(T element, double score) {
        if (score < 0)
            throw new IllegalArgumentException("Score must be greater than zero");
        elements.add(element);
        scores.add(score);
        totalScore += score;
    }

    public synchronized void addAll(List<T> elements, List<Double> scores) {
        this.elements.addAll(elements);
        this.scores.addAll(scores);
        totalScore += scores.stream().mapToDouble(s -> s).sum();
    }

    public T randomElement() {
        double threshold = ThreadLocalRandom.current().nextDouble() * totalScore;
        double current = 0;

        for (int i = 0; i < scores.size(); i++) {
            current += scores.get(i);
            if (current >= threshold)
                return elements.get(i);
        }
        throw new IllegalStateException("Found no element");
    }
}
