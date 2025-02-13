package enums;

public enum Gear{
    FORWARD(1), 
    PARK(0), 
    REVERSE(-1);

    private final int value;

    Gear(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Gear fromValue(int value) {
        for (Gear gear : values()) {
            if (gear.value == value) {
                return gear;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }
    
}