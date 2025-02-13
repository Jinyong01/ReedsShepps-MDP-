package enums;

public enum RobotState {
    END(0),
    START(1),
    DRIVE(2),
    IMAGEREC(3),
    SELFDRIVE(4);

    private final int value;

    RobotState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

