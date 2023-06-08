public enum Color {
    GREEN("\u001B[32mgreen"),
    BLUE("\u001B[34mblue"),
    RED("\u001B[31mred"),
    YELLOW("\u001B[33myellow");

    private final String displayName; // display name with ANSI color code

    Color(String displayName) {
        this.displayName = displayName;
    }

    /**
     * @return display name with additionally the ANSI color code which resets the color after the displayname to prevent further outputs to be colored as well
     */
    public String displayName() {
        return displayName + "\u001B[0m";
    }
}
