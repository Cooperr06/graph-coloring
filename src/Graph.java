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
     * This algorithm performs a brute force algorithm on this graph with the given colors
     *
     * @param givenColors available colors
     * @return validly colored graph or null if there is no solution
     */
    public Map<Integer, Color> bruteForceAlgorithm(Color... givenColors) {
        var colors = Arrays.asList(givenColors);
        // this list contains all solutions with a valid coloring, the integer represents
        // the id of the vertex of this solution and the color its color
        var solutions = new ArrayList<Map<Integer, Color>>();
        colorVerticesRecursively(0, colors, solutions); // starting the coloring at the first vertex
        // this complex looking like operation simply finds the solution with minimal color amount used
        return solutions.stream().min(Comparator.comparing(map -> {
            var colorAmount = 0;
            var usedColors = new ArrayList<Color>();
            for (var color : map.values()) {
                if (!usedColors.contains(color)) {
                    colorAmount++;
                    usedColors.add(color);
                }
            }
            return colorAmount;
        })).orElse(null);
    }

    /**
     * Colors the vertex with the vertex index with all colors and then colors the next vertex with all colors by making a recursive call
     *
     * @param vertexIndex index of the current vertex
     * @param colors      available colors
     * @param solutions   all valid solutions
     */
    public void colorVerticesRecursively(int vertexIndex, List<Color> colors, List<Map<Integer, Color>> solutions) {
        if (vertexIndex == vertices.size()) { // if every combination has been validated, the algorithm is finished
            if (valid()) {
                solutions.add(toMap());
            }
            return;
        }

        for (var color : colors) {
            vertices.get(vertexIndex).color(color); // colors the current vertex with the current color
            colorVerticesRecursively(vertexIndex + 1, colors, solutions); // colors the next vertex recursively
        }
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
     * @param args        the JVM arguments
     * @param givenColors available colors
     * @return solution of this algorithm
     */
    public Chromosome geneticAlgorithm(Map<String, Object> args, Color... givenColors) {
        var random = new Random();
        var colors = Arrays.asList(givenColors);

        var population = new Population(this, colors, (int) args.get("initialPopulationSize")); // creates the initial population
        population.calculateFitness(); // calculates and determines the fitness for each chromosome in the initial population

        var generationAmount = 1;
        while (generationAmount < (int) args.get("maxGenerationAmount")) {
            // selects the parents for the next generation
            var selectedParents = population.tournamentSelection((double) args.get("tournamentSelectionPercentage"), (double) args.get("tournamentSizePercentage"));
            // if a generation only persists of one chromosome, the algorithm stops
            if (selectedParents.size() == 1) {
                break;
            }

            var nextGeneration = new ArrayList<Chromosome>(); // represents the next generation resulting of the crossover
            for (int i = 0; i < selectedParents.size(); i++) {
                var selectedParent = selectedParents.get(i);
                var otherParent = selectedParents.get(random.nextInt(selectedParents.size())); // randomly determines the other parent for the crossover
                var child = selectedParent.crossover(otherParent); // performs the crossover
                // mutates the child with the mutation percentage and the probability for a mutation of a gene and adds it to the next generation
                child.mutate(colors, (double) args.get("mutationPercentage"), (double) args.get("mutationProbability"));
                nextGeneration.add(child);
            }
            // updates the population to the next generation and calculates its fitness
            population = new Population(nextGeneration);
            population.calculateFitness();

            generationAmount++;
        }
        return population.chromosomes().stream().max(Comparator.comparingInt(Chromosome::fitness)).orElseThrow(); // determines the chromosome with the highest fitness
    }

    /**
     * Checks for every given vertex if its color is the same as one of its adjacencies
     *
     * @param vertices given vertices
     * @return whether the graph is valid or not
     */
    public static boolean validate(List<Vertex> vertices) {
        for (var vertex : vertices) {
            for (var adjacency : vertex.adjacencies()) {
                if (vertex.color() == adjacency.color()) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean valid() {
        return validate(vertices);
    }

    public Map<Integer, Color> toMap() {
        var result = new HashMap<Integer, Color>();
        for (var vertex : vertices) {
            result.put(vertex.id(), vertex.color());
        }
        return result;
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
