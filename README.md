# Algorithms to solve the Graph Coloring Theorem
These algorithms are explained in detail in my seminar paper which deals with the Graph Coloring Theorem and tries to solve it
using these algorithms, primarily focussing on the genetic algorithm.
## Types of Algorithms
### Genetic Algorithm
This algorithm tries to solve the theorem by using a phenomena of evolution: Inheritance, Mutation and Selection (Survival of the Fittest).
To simulate this procedure, Chromosomes (represents a possible solution) and Populations (reprensents a generation) are used.
#### Tournament Selection
Tournament Selection is one of many ways to select the best chromosomes and meanwhile reducing the amount of possible solutions.
It collects random chromosomes and selects the ones with the highest fitness.
The amount of chromosomes in the tournament and as well the amount of selected chromosomes are dependent on the configuration.
#### Fitness
The selection is performed on the basis of the fitness of each chromosome, which represents the quality of it.
In this algorithm, the fitness states how many vertices do not cause conflicts. A conflict occurs if two adjacent vertices have the same color.
#### Mutation
A mutation in general changes the state of a solution, or in biology, an animal. In this algorithm, a chromosome does not grow a third leg,
but gets some genes' (vertices') states changed.
In this case, a specific percentage of vertices gets each with a specific probabilty a new color, which is valid to not cause any more conflicts.
#### Crossover
In the crossover, two parent chromosomes get merged into one child chromosome. The first half of the child's genes equals the
first half of the first parent's genes and the same with the second half and the second parent. In this algorithm, it is not always the
perfect half, there is a specific range of the crossover point to achieve more variation.
### Greedy Algorithm
This algorithm uses the first-fit strategy. It colors each vertex with the first fit color, which is valid for this vertex.
Starting with the first vertex, it continues to color from this vertex its adjacent vertices and from these adjacent vertices,
the adjacent vertices' adjacent vertices.
