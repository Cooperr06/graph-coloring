import java.util.*;

public class Graph {

    private final List<Vertex> vertices;

    public Graph(List<Vertex> vertices) {
        this.vertices = vertices;
    }

    public Graph(Vertex... vertices) {
        this.vertices = List.of(vertices);
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
     * This algorithm performs a backtracking algorithm on this graph with the given colors
     *
     * @param givenColors available colors
     * @return whether this graph can be colored with the available colors or not
     */
    public boolean backtrackingAlgorithm(Color... givenColors) {
        var colors = Arrays.asList(givenColors);

        return colorVerticesRecursivelyBacktracking(0, colors);
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
     * Tries to color the vertex with the current vertex index with every color and checks for every color if it is valid, if so, the method recursively calls itself
     *
     * @param vertexIndex index of the current vertex
     * @param colors      available colors
     * @return whether all vertices could be colored recursively
     */
    public boolean colorVerticesRecursivelyBacktracking(int vertexIndex, List<Color> colors) {
        // if all vertices have been colored, the algorithm succeeds
        if (vertexIndex == vertices.size()) {
            return true;
        }

        var vertex = vertices.get(vertexIndex); // the current vertex acquired based on the current vertex index

        // goes through every color and checks if the vertex can be colored with it, if true, the algorithm tries to color the next vertex with the
        // same procedure, but if this fails, the next color for this vertex is picked (because of the loop)
        for (var color : colors) {
            if (vertex.canBeColoredWith(color)) {
                vertex.color(color);
                // if all vertices can be colored validly, the algorithm succeeded
                if (colorVerticesRecursivelyBacktracking(vertexIndex + 1, colors)) {
                    return true;
                }
            }
            vertex.color(null); // reset the color of the current vertex if it cannot be colored with the current color
        }
        return false; // returns false if all colors have been tried for this vertex and with each color assigned to the vertex, no solution was found
    }

    /**
     * @param args        the JVM arguments
     * @param givenColors available colors
     * @return solution of this algorithm
     */
    public Chromosome geneticAlgorithm(Map<String, Object> args,
                                       Color... givenColors) {
        var colors = Arrays.asList(givenColors);

        var population = new Population(this,
                (int) args.get("initialPopulationSize"), colors);
        population.calculateFitness();


        for (int i = 1; i < (int) args.get("maxGenerationAmount"); i++) {
            var nextGeneration = generateGeneration(colors, population, args);
            if (nextGeneration.size() == 1) {
                break;
            }

            population = new Population(nextGeneration);
            population.calculateFitness();
        }
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

        var nextGeneration = new ArrayList<Chromosome>(); // represents the next generation resulting of the crossover
        for (int i = 0; i < selectedParents.size(); i++) {
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

    public void printInformation() {
        vertices.stream().sorted(Comparator.comparing(Vertex::id)).forEach(Vertex::printInformation);
    }

    public List<Vertex> vertices() {
        return vertices;
    }
}
