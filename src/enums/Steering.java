package enums;

public enum Steering {
    LEFT(-1),
    STRAIGHT(0),
    RIGHT(1);

    private final int value;

    Steering(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Steering fromValue(int value) {
        for (Steering steering : values()) {
            if (steering.value == value) {
                return steering;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }
}