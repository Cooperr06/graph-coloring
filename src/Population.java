import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class Population {

    private final List<Chromosome> chromosomes;

    public Population(List<Chromosome> chromosomes) {
        this.chromosomes = chromosomes;
    }

    /**
     * Randomly generates a new population
     *
     * @param graph            the graph the chromosomes are based on
     * @param chromosomeAmount amount of chromosomes for this population
     */
    public Population(Graph graph, int chromosomeAmount, List<Color> colors) {
        this(new ArrayList<>());
        for (var i = 0; i < chromosomeAmount; i++) {
            chromosomes.add(new Chromosome(graph.vertices(), colors));
        }
    }

    /**
     * Determines a subset of chromosomes using the tournament selection.<br>
     * For the amount of tournaments, this algorithm randomly picks chromosomes of this population (= tournament) and determines the chromosome with
     * the highest fitness (= tournament winner).<br>
     * The selection percentage determines how many chromosomes will be picked and returned dependent on the amount of chromosomes in this population.
     * Consequently, the selection percentage also influences the amount of tournaments, because each tournament only determines one "winning" chromosome
     * which is being selected for the final subset.<br>
     * The size of each tournament is the tournament size percentage of this population's size.<br>
     * The amount of chromosomes in each tournament is dependent on the tournament size percentage.
     *
     * @param selectionPercentage percentage of the amount of chromosomes in this population, which determines the size of the final subset
     * @param tournamentSizePercentage percentage of the amount of chromosomes in this population, which determines the amount of chromosomes in each tournament
     * @return selected subset
     */
    public List<Chromosome> tournamentSelection(double selectionPercentage, double tournamentSizePercentage) {
        // calculating the absolute values of the given percentages
        var selectionSize = (int) Math.round(chromosomes.size() * selectionPercentage);
        var tournamentSize = (int) Math.round(chromosomes.size() * tournamentSizePercentage);

        var random = new Random();
        var selectedChromosomes = new ArrayList<Chromosome>(); // represents the selected chromosomes which will form the successor generation

        for (var i = 0; i < selectionSize; i++) {
            var tournamentList = new ArrayList<Chromosome>(); // represents the chromosomes for the current tournament
            // randomly selects chromosomes of this population for the tournament
            for (var j = 0; j < tournamentSize; j++) {
                var chromosome = chromosomes.get(random.nextInt(chromosomes.size()));
                tournamentList.add(chromosome);
            }

            var winner = tournamentList.stream()
                    .max(Comparator.comparingInt(Chromosome::fitness)) // finds the chromosome with the highest fitness in this tournament
                    .orElseThrow();
            // adds the winner of this tournament to the subset, so to the list of chromosomes which will form the new generation
            selectedChromosomes.add(winner);
        }
        return selectedChromosomes;
    }

    /**
     * Calculates the fitness of all chromosomes
     */
    public void calculateFitness() {
        chromosomes.forEach(Chromosome::calculateFitness);
    }

    public List<Chromosome> chromosomes() {
        return chromosomes;
    }
}
