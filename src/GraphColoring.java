import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class GraphColoring {

    /**
     * @param args for genetic algorithm use the following JVM arguments:<br>
     *             Index 0: Amount of Generations (Integer)<br>
     *             Index 1: Initial Population Size (Integer)<br>
     *             Index 2: Tournament Selection Percentage (Double)<br>
     *             Index 3: Tournament Size Percentage (Double)<br>
     *             Index 4: Mutation Percentage (Double)<br>
     *             Index 5: Mutation Probability (Double)<br>
     *             Index 6: Amount of times the algorithm is executed (Integer)
     *             <br><br>
     *             for greedy algorithm use no JVM arguments
     */
    public static void main(String[] args) {
        var graph = setupGraph();
        if (graph.vertices().stream()
                .collect(Collectors.groupingBy(Vertex::id, Collectors.counting()))
                .values().stream()
                .anyMatch(count -> count > 1)) {
            System.out.println("The Graph is invalid since there are multiple vertices with the same ID.");
            return;
        }
        if (args.length == 7) {
            var arguments = new HashMap<String, Object>();
            arguments.put("maxGenerationAmount", Integer.parseInt(args[0]));
            arguments.put("initialPopulationSize", Integer.parseInt(args[1]));
            arguments.put("tournamentSelectionPercentage", Double.parseDouble(args[2]));
            arguments.put("tournamentSizePercentage", Double.parseDouble(args[3]));
            arguments.put("mutationPercentage", Double.parseDouble(args[4]));
            arguments.put("mutationProbability", Double.parseDouble(args[5]));
            arguments.put("algorithmAttempts", Integer.parseInt(args[6]));

            var argsInfo = "Setup:\n" +
                    "- Amount of Generations = " + arguments.get("maxGenerationAmount") + " (possible: " + getAmountOfGenerations(
                    (int) arguments.get("initialPopulationSize"),
                    (double) arguments.get("tournamentSelectionPercentage")) + ")\n" +
                    "- Initial Population Size = " + arguments.get("initialPopulationSize") + "\n" +
                    "- Tournament Selection Percentage = " + arguments.get("tournamentSelectionPercentage") + "\n" +
                    "- Tournament Size Percentage = " + arguments.get("tournamentSizePercentage") + "\n" +
                    "- Mutation Percentage = " + arguments.get("mutationPercentage") + "\n" +
                    "- Mutation Probability = " + arguments.get("mutationProbability") + "\n" +
                    "- Algorithm Attempts = " + arguments.get("algorithmAttempts") + "\n";
            System.out.println(argsInfo);

            var minimumColors = new HashMap<Integer, Integer>();
            var succeeded = 0;
            var times = 0;
            try (var progressBar = new ProgressBarBuilder()
                    .setTaskName("Genetic Algorithm Attempts")
                    .setInitialMax((int) arguments.get("algorithmAttempts"))
                    .setUpdateIntervalMillis(100)
                    .setStyle(ProgressBarStyle.UNICODE_BLOCK)
                    .build()) {
                for (var i = 0; i < (int) arguments.get("algorithmAttempts"); i++) {
                    var minimumColorAmount = colorGraphGeneticWithMinimumColors(graph, arguments, (int) arguments.get("algorithmAttempts") == 1);
                    minimumColors.put(minimumColorAmount, minimumColors.getOrDefault(minimumColorAmount, 0) + 1);
                    if (minimumColorAmount != 0) {
                        succeeded++;
                    }
                    times++;
                    progressBar.step();
                }
            }

            System.out.println("\n" + succeeded + " out of " + times + " time(s) the algorithm succeeded! Results:\n" +
                    "0 Colors: " + minimumColors.getOrDefault(0, 0) + "\n" +
                    "1 Color: " + minimumColors.getOrDefault(1, 0) + "\n" +
                    "2 Colors: " + minimumColors.getOrDefault(2, 0) + "\n" +
                    "3 Colors: " + minimumColors.getOrDefault(3, 0) + "\n" +
                    "4 Colors: " + minimumColors.getOrDefault(4, 0));
        } else if (args.length == 0) {
            var minimumColorAmount = colorGraphGreedyWithMinimumColors(graph);
            if (minimumColorAmount == 0) {
                System.out.println("With the greedy algorithm the given graph cannot be colored with at least four colors.");
            } else {
                System.out.printf("\n%d color(s) are necessary to color the graph.", minimumColorAmount);
            }
        }
    }

    /**
     * Performs the greedy algorithm with a different amount of colors on the given graph
     *
     * @param graph graph the algorithm should use
     * @return minimum amount of colors needed to color this graph
     */
    public static int colorGraphGreedyWithMinimumColors(Graph graph) {
        var minimumColorAmount = 0;
        if (greedyAlgorithm(graph, Color.GREEN)) { // check if graph can be colored with one color
            minimumColorAmount = 1;
            graph.printInformation();
        } else if (greedyAlgorithm(graph, Color.GREEN, Color.BLUE)) { // check if graph can be colored with two colors
            minimumColorAmount = 2;
            graph.printInformation();
        } else if (greedyAlgorithm(graph, Color.GREEN, Color.BLUE, Color.RED)) { // check if graph can be colored with three colors
            minimumColorAmount = 3;
            graph.printInformation();
        } else if (greedyAlgorithm(graph, Color.GREEN, Color.BLUE, Color.RED, Color.YELLOW)) { // check if graph can be colored with four colors
            minimumColorAmount = 4;
            graph.printInformation();
        }
        return minimumColorAmount;
    }

    /**
     * Performs a genetic algorithm with a different amount of colors on the given graph
     *
     * @param graph            graph the algorithm should use
     * @param args             system args ({@link GraphColoring#main(String[])})
     * @param printInformation whether to print the information of the result
     * @return minimum amount of colors needed to color this graph
     */
    public static int colorGraphGeneticWithMinimumColors(Graph graph, Map<String, Object> args, boolean printInformation) {
        var result = geneticAlgorithm(graph, args, Color.GREEN);
        if (result != null && result.valid()) {
            if (printInformation) {
                System.out.println("\n");
                result.printInformation();
            }
            return 1;
        }

        result = geneticAlgorithm(graph, args, Color.GREEN, Color.BLUE);
        if (result != null && result.valid()) {
            if (printInformation) {
                System.out.println("\n");
                result.printInformation();
            }
            return 2;
        }

        result = geneticAlgorithm(graph, args, Color.GREEN, Color.BLUE, Color.RED);
        if (result != null && result.valid()) {
            if (printInformation) {
                System.out.println("\n");
                result.printInformation();
            }
            return 3;
        }

        result = geneticAlgorithm(graph, args, Color.GREEN, Color.BLUE, Color.RED, Color.YELLOW);
        if (result != null && result.valid()) {
            if (printInformation) {
                System.out.println("\n");
                result.printInformation();
            }
            return 4;
        }
        return 0;
    }

    public static boolean greedyAlgorithm(Graph graph, Color... colors) {
        if (graph.greedyAlgorithm(colors) && graph.valid()) {
            return true; // returns true if the graph can be colored with the given colors
        } else {
            graph.resetGraph(); // resets the graph if the algorithm failed
            return false;
        }
    }

    public static Chromosome geneticAlgorithm(Graph graph, Map<String, Object> args, Color... colors) {
        return graph.geneticAlgorithm(args, colors);
    }

    /**
     * Calculates how many generations there will be with these arguments unless there is a limit
     *
     * @param initialPopulationSize         amount of chromosomes in first population
     * @param tournamentSelectionPercentage percentage of the size of the population, which will be selected for the next generation
     * @return amount of possible generations without limitations
     */
    public static int getAmountOfGenerations(int initialPopulationSize, double tournamentSelectionPercentage) {
        return (int) (Math.log((double) 1 / initialPopulationSize) / Math.log(tournamentSelectionPercentage));
    }

    /**
     * Setup graph with all vertices and adjacencies
     *
     * @return graph
     */
    public static Graph setupGraph() {
        var vertex0 = new Vertex(0);
        var vertex1 = new Vertex(1);
        var vertex2 = new Vertex(2);
        var vertex3 = new Vertex(3);
        var vertex4 = new Vertex(4);
        var vertex5 = new Vertex(5);
        var vertex6 = new Vertex(6);
        var vertex7 = new Vertex(7);
        var vertex8 = new Vertex(8);
        var vertex9 = new Vertex(9);

        vertex0.adjacencies(vertex1, vertex2, vertex3);
        vertex1.adjacencies(vertex0, vertex3, vertex8, vertex9);
        vertex2.adjacencies(vertex0, vertex3, vertex4);
        vertex3.adjacencies(vertex0, vertex1, vertex2, vertex4);
        vertex4.adjacencies(vertex2, vertex3, vertex7);
        vertex5.adjacencies(vertex8, vertex9);
        vertex6.adjacencies(vertex7, vertex8);
        vertex7.adjacencies(vertex4, vertex6);
        vertex8.adjacencies(vertex1, vertex5, vertex6);
        vertex9.adjacencies(vertex1, vertex5);

        return new Graph(vertex0, vertex1, vertex2, vertex3, vertex4, vertex5, vertex6, vertex7, vertex8, vertex9);
    }
}
