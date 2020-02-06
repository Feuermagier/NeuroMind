package firemage.neuromind.util.structures;

import firemage.neuromind.neat.Species;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class RandomSet<T> {

    private final Set<T> set = new HashSet<T>();
    private final List<T> list = new ArrayList<T>();

    public boolean contains(T element) {
        return set.contains(element);
    }

    public T getRandomElement() {
        if (!list.isEmpty()) {
            return list.get(ThreadLocalRandom.current().nextInt(0, list.size()));
        } else {
            throw new IllegalStateException("There is no element to choose randomly");
        }
    }

    public int size() {
        return list.size();
    }

    public synchronized void clear() {
        list.clear();
        set.clear();
    }

    public T get(int index) {
        return list.get(index);
    }

    public synchronized void add(T element) {
        if (set.add(element)) {
            list.add(element);
        }
    }

    public synchronized void addAll(Collection<T> elements) {
        elements.forEach(this::add);
    }

    public synchronized void remove(int index) {
        set.remove(list.get(index));
        list.remove(index);
    }

    public synchronized void remove(T element) {
        set.remove(element);
        list.remove(element);
    }

    public Stream<T> stream() {
        return set.stream();
    }

    public void forEach(Consumer<? super T> action) {
        set.forEach(action);
    }

    public List<T> asList() {
        return list;
    }

    public Set<T> asSet() {
        return set;
    }

    public synchronized void removeAll(Collection<T> elements) {
        elements.forEach(this::remove);
    }
}
