import java.util.*;

public class Graph {

    private final List<Vertex> vertices;

    public Graph(List<Vertex> vertices) {
        this.vertices = vertices;
    }

    /**
     * This constructor only exists in order to make it easier to set a graph up (see {@link GraphColoring#setupGraph()})
     *
     * @param vertices vertices of the graph
     */
    public Graph(Vertex... vertices) {
        this.vertices = List.of(vertices);
    }

    /**
     * This algorithm is only working with connected graphs since it starts at the first vertex and goes from it to all other genes
     *
     * @param givenColors available colors
     * @return whether the algorithm succeeded or not
     */
    public boolean greedyAlgorithm(Color... givenColors) {
        var colors = Arrays.asList(givenColors);

        // colors the first vertex and from it all its adjacencies
        // returns true if all operations succeed
        return vertices.get(0).colorVertex(colors) && vertices.get(0).colorAdjacencies(colors);
    }

    /**
     * Checks for every given vertex if its color is the same as one of its adjacencies
     *
     * @param vertices given vertices
     * @return whether the graph is valid or not
     */
    public static boolean validate(List<Vertex> vertices) {
        for (var vertex : vertices) {
            if (vertex.color() == null) {
                return false;
            }
            for (var adjacency : vertex.adjacencies()) {
                if (vertex.color() == adjacency.color()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * @param args        the JVM arguments
     * @param givenColors available colors
     * @return solution of this algorithm
     */
    public Chromosome geneticAlgorithm(Map<String, Object> args, Color... givenColors) {
        var colors = Arrays.asList(givenColors);
        var population = new Population(this, (int) args.get("initialPopulationSize"), colors); // creates the initial population
        population.calculateFitness();
        // generates new generations until maxGenerationAmount is reached or until the current generation only consists of one chromosome
        for (var i = 1; i < (int) args.get("maxGenerationAmount"); i++) {
            var nextGeneration = generateGeneration(colors, population, args);
            // no crossover possible, break loop
            if (nextGeneration.size() == 1) {
                return nextGeneration.get(0);
            }
            // updates the current generation to the new generated one and calculates and determines the
            // fitness for each chromosome in the initial population
            population = new Population(nextGeneration);
            population.calculateFitness();
        }
        // finds the chromosome with the highest fitness and returns it
        return population.chromosomes().stream()
                .max(Comparator.comparingInt(Chromosome::fitness))
                .orElseThrow();
    }

    /**
     * Generates a successor generation by selecting parents and performing crossovers and mutations of the
     * crossovers' resulting children with the given colors and arguments
     *
     * @param colors     available colors
     * @param population current population
     * @param args       the JVM arguments
     * @return successor generation
     */
    private List<Chromosome> generateGeneration(List<Color> colors, Population population, Map<String, Object> args) {
        // selects the parents for the next generation
        var selectedParents = population.tournamentSelection((double) args.get("tournamentSelectionPercentage"), (double) args.get("tournamentSizePercentage"));
        // if a generation only consists of one chromosome, the algorithm stops
        if (selectedParents.size() == 1) {
            return selectedParents;
        }

        var nextGeneration = new ArrayList<Chromosome>(); // represents the next generation resulting of the crossovers and mutations
        for (var i = 0; i < selectedParents.size(); i++) {
            var selectedParent = selectedParents.get(i);
            var otherParent = selectedParents.get(new Random().nextInt(selectedParents.size())); // randomly determines the other parent for the crossover
            var child = selectedParent.crossover(otherParent); // performs the crossover
            // mutates the child with the mutation percentage and the probability for a mutation of a gene and adds it to the next generation
            child.mutate(colors, (double) args.get("mutationPercentage"), (double) args.get("mutationProbability"));
            nextGeneration.add(child);
        }
        return nextGeneration;
    }

    public boolean valid() {
        return validate(vertices);
    }

    public void resetGraph() {
        vertices.forEach(Vertex::resetVertex);
    }

    /**
     * @see Vertex#printInformation()
     */
    public void printInformation() {
        vertices.stream().sorted(Comparator.comparing(Vertex::id)).forEach(Vertex::printInformation);
    }

    public List<Vertex> vertices() {
        return vertices;
    }
}
