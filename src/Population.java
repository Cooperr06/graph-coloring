import java.util.*;

public record Population(List<Chromosome> chromosomes) {

    /**
     * Randomly generates a new population
     *
     * @param graph            the graph the chromosomes are based on
     * @param colors           available colors
     * @param chromosomeAmount amount of chromosomes for this population
     */
    public Population(Graph graph, List<Color> colors, int chromosomeAmount) {
        this(new ArrayList<>());
        for (int i = 0; i < chromosomeAmount; i++) {
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

        var selectionSize = (int) Math.floor(chromosomes.size() * selectionPercentage); // floor is here necessary to prevent the selection from stagnating
        var tournamentSize = (int) Math.round(chromosomes.size() * tournamentSizePercentage);

        var random = new Random();
        var selectedChromosomes = new ArrayList<Chromosome>(); // represents the selected subset



        for (int i = 0; i < selectionSize; i++) {
            var tournamentList = new ArrayList<Chromosome>(); // represents the chromosomes for the current tournament
            // randomly selects chromosomes of this population for the tournament
            for (int j = 0; j < tournamentSize; j++) {
                var chromosome = chromosomes.get(random.nextInt(chromosomes.size()));
                tournamentList.add(chromosome);
            }

            var winner = tournamentList.stream()
                    .max(Comparator.comparingInt(Chromosome::fitness)) // finds the chromosome with the highest fitness in this tournament
                    .orElseThrow();
            selectedChromosomes.add(winner); // adds the winner of this tournament to the subset
        }
        return selectedChromosomes;
    }

    /**
     * Calculates the fitness of all chromosomes
     */
    public void calculateFitness() {
        chromosomes.forEach(Chromosome::calculateFitness);
    }

    public void printInformation() {
        chromosomes.forEach(Chromosome::printInformation);
    }
}
