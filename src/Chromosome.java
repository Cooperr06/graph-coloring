import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Chromosome {

    private final List<Vertex> genes = new ArrayList<>();
    private int fitness = 0;

    public Chromosome() {
    }

    /**
     * Clones the given genes and assigns each of them a random color of the available colors
     *
     * @param genes  given genes
     * @param colors available colors
     */
    public Chromosome(List<Vertex> genes, List<Color> colors) {
        // the "cloning" procedure is necessary to ensure the correct reflection of the adjacency relationships and also to have "own" objects rather
        // than reference objects, because otherwise each chromosome would reference to the same genes which would disallow changing the genes' state
        var clones = new HashMap<Vertex, Vertex>(); // represents original (key) and cloned (value) vertices
        var random = new Random();
        // clones all vertices and assigns them random colors
        for (var gene : genes) {
            var color = colors.get(random.nextInt(colors.size())); // determines a random color for the cloned vertex
            var clonedVertex = Vertex.of(gene.id(), color);
            clones.put(gene, clonedVertex); // puts the original vertex and a clone of the current vertex with the random color into the map
            this.genes.add(clonedVertex);
        }
        // goes through every (original) vertex and determines its adjacencies so that the adjacencies of the original vertex's clone can be
        // assigned to the clones of the original vertex's adjacencies
        for (var originalVertex : genes) {
            var cloneAdjacencies = new ArrayList<Vertex>(); // stores the clones of the original vertex's adjacencies
            for (var originalAdjacency : originalVertex.adjacencies()) {
                cloneAdjacencies.add(clones.get(originalAdjacency)); // adds the clone of the original adjacency to the list
            }
            clones.get(originalVertex).adjacencies(cloneAdjacencies); // adds all cloned adjacencies to the original vertex's clone
        }
    }

    /**
     * Merges this chromosome with another chromosome to one child chromosome.<br>
     * From the first gene to a specific crossover point, this chromosome's genes are copied and from the crossover point to the last gene,
     * the second parent's genes are copied
     *
     * @param otherParent second parent
     * @return child of the parents
     */
    public Chromosome crossover(Chromosome otherParent) {
        var random = new Random();
        var child = new Chromosome();
        var crossoverPoint = genes.size() / 2 + (random.nextBoolean() ? 1 : -1) *
                random.nextInt(genes.size() / 5 + 1);

        for (var i = 0; i < genes.size(); i++) {
            if (i <= crossoverPoint) {
                child.genes().add(genes.get(i));
            } else {
                child.genes().add(otherParent.genes().get(i));
            }
        }
        return child;
    }

    /**
     * Changes randomly a color of the vertices
     *
     * @param colors             available colors
     * @param mutationPercentage amount of genes to change
     * @param probability        probability to change a gene
     */
    public void mutate(List<Color> colors, double mutationPercentage, double probability) {
        var random = new Random();
        var amountOfChanges = Math.round(genes.size() * mutationPercentage);

        for (var i = 0; i < amountOfChanges; i++) {
            // if a random double between 0 and 1 is smaller than the probability, a gene is being changed
            if (random.nextDouble() < probability) {
                var vertex = genes.get(random.nextInt(genes.size())); // picks a random vertex
                var newColor = vertex.color();
                // if there is more than one valid color for this vertex, change it
                if (vertex.determineValidColors(colors).size() > 1) {
                    while (newColor == vertex.color() || !vertex.canBeColoredWith(newColor)) {
                        newColor = colors.get(random.nextInt(colors.size()));
                    }
                }
                //newColor = colors.get(random.nextInt(colors.size()));
                vertex.color(newColor); // colors the vertex with its current color if there are no other valid colors, or otherwise with a different valid color
            }
        }
    }

    /**
     * Calculates the fitness of this chromosome.<br>
     * Fitness equals the amount of connections which connect two different colored vertices
     */
    public void calculateFitness() {
        var calculatedFitness = 0;
        for (var gene : genes) {
            for (var adjacency : gene.adjacencies()) {
                if (gene.color() != adjacency.color()) {
                    calculatedFitness++; // if a vertex has not the same color as one of its adjacent vertices, there is no conflict
                }
            }
        }
        this.fitness = calculatedFitness / 2; // the actual fitness equals the determined fitness divided by two, because each connection is checked twice
    }

    public boolean valid() {
        return Graph.validate(genes);
    }

    /**
     * @see Vertex#printInformation()
     */
    public void printInformation() {
        new Graph(genes).printInformation();
    }

    public List<Vertex> genes() {
        return genes;
    }

    public int fitness() {
        return fitness;
    }
}
