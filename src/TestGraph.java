import java.util.HashMap;
import java.util.Map;

public class TestGraph {

    /**
     * @param args none for greedy algorithm, for genetic use the following:
     *             <br>Index 0: Amount of Generations (Integer)<br>Index 1: Initial Population Size (Integer)<br>Index 2: Tournament Selection Percentage (Double)
     *             <br>Index 3: Tournament Size Percentage (Double)<br>Index 4: Mutation Percentage (Double)<br>Index 5: Mutation Probability (Double)
     *             <br>Index 6: Amount of times the algorithm is executed (Integer)
     */
    public static void main(String[] args) {
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
                    "- Algorithm Attempts = " + arguments.get("algorithmAttempts");
            System.out.println(argsInfo);

            var fails = 0;
            var times = 0;

            for (var i = 0; i < (int) arguments.get("algorithmAttempts"); i++) {
                var minimumColorAmount = colorGraphGeneticWithMinimumColors(arguments);
                if (minimumColorAmount == 0) {
                    fails++;
                }
                times++;
            }
            System.out.println(fails + " out of " + times + " times the algorithm failed");
        } else {
            var minimumColorAmount = colorGraphGreedyWithMinimumColors();
            if (minimumColorAmount == 0) {
                System.out.println("With this greedy algorithm the given graph cannot be colored with at least four colors.");
            } else {
                System.out.printf("%n%d colors are necessary to color the graph.%n", colorGraphGreedyWithMinimumColors());
            }
        }
    }

    /**
     * Performs the greedy algorithm with a different amount of colors on the given graph
     *
     * @return minimum amount of colors needed to color this graph
     * @see TestGraph#setupGraph()
     */
    public static int colorGraphGreedyWithMinimumColors() {
        var graph = setupGraph();

        var minimumColorAmount = 0;
        if (greedyAlgorithm(graph, Color.GREEN)) { // check if graph can be colored with one color
            minimumColorAmount = 1;
        } else if (greedyAlgorithm(graph, Color.GREEN, Color.BLUE)) { // check if graph can be colored with two colors
            minimumColorAmount = 2;
        } else if (greedyAlgorithm(graph, Color.GREEN, Color.BLUE, Color.RED)) { // check if graph can be colored with three colors
            minimumColorAmount = 3;
        } else if (greedyAlgorithm(graph, Color.GREEN, Color.BLUE, Color.RED, Color.YELLOW)) { // check if graph can be colored with four colors
            minimumColorAmount = 4;
        }
        graph.printInformation();
        return minimumColorAmount;
    }

    /**
     * Performs a genetic algorithm with a different amount of colors on the given graph
     *
     * @param args system args ({@link TestGraph#main(String[])})
     * @return minimum amount of colors needed to color this graph
     * @see TestGraph#setupGraph()
     */
    public static int colorGraphGeneticWithMinimumColors(Map<String, Object> args) {
        var graph = setupGraph();

        var result1 = geneticAlgorithm(graph, args, Color.GREEN);
        if (result1 != null && result1.valid()) {
            result1.printInformation();
            return 1;
        }

        var result2 = geneticAlgorithm(graph, args, Color.GREEN, Color.BLUE);
        if (result2 != null && result2.valid()) {
            //result2.printInformation();
            return 2;
        }

        var result3 = geneticAlgorithm(graph, args, Color.GREEN, Color.BLUE, Color.RED);
        if (result3 != null && result3.valid()) {
            //result3.printInformation();
            return 3;
        }

        var result4 = geneticAlgorithm(graph, args, Color.GREEN, Color.BLUE, Color.RED, Color.YELLOW);
        if (result4 != null && result4.valid()) {
            //result4.printInformation();
            return 4;
        }
        return 0;
    }

    public static boolean greedyAlgorithm(Graph graph, Color... colors) {
        if (graph.greedyAlgorithm(colors) && graph.valid()) {
            return true; // returns true if the graph can be colored with the given colors
        } else {
            graph.resetGraph(); // resets the graph if the algorithm failed to make it possible to use the same graph again with more colors
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
        var amountOfGenerations = 0;
        while (initialPopulationSize > 2) {
            initialPopulationSize *= tournamentSelectionPercentage;
            amountOfGenerations++;
        }
        return amountOfGenerations;
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

        /*vertex0.adjacencies(vertex1, vertex2);
        vertex1.adjacencies(vertex0, vertex2, vertex3);
        vertex2.adjacencies(vertex0, vertex1, vertex3);
        vertex3.adjacencies(vertex1, vertex2, vertex4);
        vertex4.adjacencies(vertex3);*/

        /*vertex0.adjacencies(vertex1, vertex2);
        vertex1.adjacencies(vertex0, vertex2, vertex4);
        vertex2.adjacencies(vertex0, vertex1, vertex4);
        vertex3.adjacencies(vertex4);
        vertex4.adjacencies(vertex1, vertex2, vertex3);*/

        /*vertex0.adjacencies(vertex1);
        vertex1.adjacencies(vertex0);*/

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

        /*vertex0.adjacencies(vertex1, vertex2);
        vertex1.adjacencies(vertex0, vertex2, vertex3);
        vertex2.adjacencies(vertex0, vertex1, vertex3);
        vertex3.adjacencies(vertex1, vertex2);*/

        return new Graph(vertex0, vertex1, vertex2, vertex3, vertex4, vertex5, vertex6, vertex7, vertex8, vertex9);
    }
}
