package firemage.neuromind.neat;

import java.util.stream.IntStream;

public class Neat {

    private static final double INPUT_X = 0.1;
    private static final double OUTPUT_X = 0.9;

    private int maxClients;
    private int inputSize, outputSize;

    private GenePool genePool;

    public Neat(int maxClients, int inputSize, int outputSize) {
        this.maxClients = maxClients;
        this.inputSize = inputSize;
        this.outputSize = outputSize;

        genePool = new GenePool();

        IntStream.range(0, inputSize).forEach(i -> genePool.addNode(INPUT_X, (i + 1d) / (inputSize + 1)));
        IntStream.range(0, outputSize).forEach(i -> genePool.addNode(OUTPUT_X, (i + 1d) / (outputSize + 1)));
    }

    public Genome createEmptyGenome() {
        Genome genome = new Genome(genePool);
        IntStream.range(0, inputSize + outputSize).forEach(i -> genome.addNode(genePool.getNode(i)));
        return genome;
    }

    public GenePool getGenePool() {
        return genePool;
    }
}
