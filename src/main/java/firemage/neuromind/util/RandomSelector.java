package firemage.neuromind.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomSelector<T> {
    private List<T> elements = new ArrayList<>();
    private List<Double> scores = new ArrayList<>();
    private double totalScore = 0;

    public synchronized void add(T element, double score) {
        elements.add(element);
        scores.add(score);
        totalScore += score;
    }

    public T randomElement() {
        double threshold = ThreadLocalRandom.current().nextDouble(0, totalScore);
        double current = 0;

        for (int i = 0; i < scores.size(); i++) {
            current += scores.get(i);
            if (current > threshold)
                return elements.get(i);
        }
        throw new IllegalStateException("Found no element");
    }

    public synchronized void reset() {
        elements.clear();
        scores.clear();
        totalScore = 0;
    }
}
