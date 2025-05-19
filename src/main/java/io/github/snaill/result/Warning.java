package io.github.snaill.result;

public class Warning implements Result {

    private static boolean isCritical = false;
    private static final String NEW_LINE = System.lineSeparator();
    private static final String SEPARATION = "~".repeat(32) + NEW_LINE;
    
    private final String place;
    private final WarningType type;

    public Warning(WarningType type, String place) {
        this.place = place;
        this.type = type;
    }

    public static void setCritical(boolean value) {
        isCritical = value;
    }

    @Override
    public boolean isCritical() {
        return isCritical;
    }

    @Override
    public String toString() {
        return String.format(
            "Warning: %s%s%s%s%s%s",
            type,
            NEW_LINE,
            SEPARATION,
            place,
            NEW_LINE,
            SEPARATION
        );
    }
    
}
