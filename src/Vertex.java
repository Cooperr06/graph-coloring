import java.util.ArrayList;
import java.util.List;

public class Vertex {

    private final int id;

    private Color color;

    private List<Vertex> adjacencies = new ArrayList<>();
    private boolean coloredAdjacencies = false; // represents whether from this vertex all adjacencies have been already colored

    public Vertex(int id) {
        this.id = id;
    }

    private Vertex(Builder builder) {
        this.id = builder.id;
        this.color = builder.color;
        this.adjacencies = builder.adjacencies;
        this.coloredAdjacencies = builder.coloredAdjacencies;
    }

    public static Vertex of(int id, Color color) {
        return new Builder(id).color(color).build();
    }

    /**
     * Colors this vertex with the first valid color
     *
     * @param colors available colors
     * @return whether this vertex can be colored
     */
    public boolean colorVertex(List<Color> colors) {
        var color = determineFirstValidColor(colors);
        if (color == null) {
            return false; // if there is no valid color for this vertex, the algorithm stops
        }

        color(color); // colors this vertex with the first valid color
        return true;
    }

    /**
     * Colors all adjacent vertices, except the ones which have already been colored, and all their adjacent vertices and stops the algorithm
     * if there is no valid color for a vertex
     *
     * @param colors available colors
     * @return whether the adjacencies or their adjacencies can be colored
     */
    public boolean colorAdjacencies(List<Color> colors) {
        coloredAdjacencies = true; // marks this vertex as "visited"

        for (var adjacency : adjacencies) {
            // go to the next adjacent vertex if from this adjacency the adjacencies have already been colored
            if (adjacency.coloredAdjacencies()) {
                continue;
            }

            if (adjacency.color() == null) {
                // color the adjacent vertex if it has not been already colored
                if (!adjacency.colorVertex(colors)) {
                    return false; // if this adjacent vertex cannot be validly colored with the available colors, the algorithm stops
                }
            }
            // color all the adjacent vertex's other adjacencies
            if (!adjacency.colorAdjacencies(colors)) {
                return false; // same here: if the adjacent vertex's adjacencies cannot be validly colored with the available colors, the algorithm stops
            }
        }
        return true;
    }

    /**
     * Determines all valid colors for this vertex which are not occupied by an adjacent vertex
     *
     * @param colors available colors
     * @return list of valid colors
     */
    public List<Color> determineValidColors(List<Color> colors) {
        var validColors = new ArrayList<Color>();
        // collects all valid colors for this vertex
        for (var color : colors) {
            if (canBeColoredWith(color)) {
                validColors.add(color);
            }
        }
        return validColors;
    }

    /**
     * Determines the first valid color which is not occupied by an adjacent vertex
     *
     * @param colors available colors
     * @return first valid color
     * @see Vertex#determineValidColors(List)
     */
    public Color determineFirstValidColor(List<Color> colors) {
        var validColors = determineValidColors(colors);
        return validColors.size() != 0 ? validColors.get(0) : null; // if there are no valid colors, return null
    }

    /**
     * Checks every adjacent vertex whether this color is already occupied
     *
     * @param color color to check
     * @return whether this color can be used to color this vertex
     */
    public boolean canBeColoredWith(Color color) {
        return adjacencies.stream().noneMatch(adjacency -> adjacency.color() == color);
    }

    public void resetVertex() {
        color = null;
        coloredAdjacencies = false;
    }

    /**
     * Used in order to control a result
     */
    public void printInformation() {
        System.out.printf("Vertex %d: %s%n", id(), color() == null ? "null" : color().displayName());
    }

    public int id() {
        return id;
    }

    public Color color() {
        return color;
    }

    public void color(Color color) {
        this.color = color;
    }

    public List<Vertex> adjacencies() {
        return adjacencies;
    }

    public void adjacencies(List<Vertex> adjacencies) {
        this.adjacencies = adjacencies;
    }

    public void adjacencies(Vertex... adjacencies) {
        this.adjacencies.addAll(List.of(adjacencies));
    }

    public boolean coloredAdjacencies() {
        return coloredAdjacencies;
    }

    public void coloredAdjacencies(boolean coloredAdjacencies) {
        this.coloredAdjacencies = coloredAdjacencies;
    }

    /**
     * Builder class for {@link Vertex}
     */
    public static class Builder {

        private final int id;

        private Color color;
        private List<Vertex> adjacencies = new ArrayList<>();
        private boolean coloredAdjacencies = false;

        public Builder(int id) {
            this.id = id;
        }

        public Vertex build() {
            return new Vertex(this);
        }

        public Builder color(Color color) {
            this.color = color;
            return this;
        }

        public Builder adjacencies(List<Vertex> adjacencies) {
            this.adjacencies = adjacencies;
            return this;
        }

        public Builder coloredAdjacencies(boolean coloredAdjacencies) {
            this.coloredAdjacencies = coloredAdjacencies;
            return this;
        }
    }
}
