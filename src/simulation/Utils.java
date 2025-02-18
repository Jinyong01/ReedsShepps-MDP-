package simulation;

import enums.Direction;
import java.util.Objects;

public class Utils {

    public static double gridToCoordsX(double x_g) {
        return x_g * 200.0 / Consts.GRID_SIZE;
    }

    public static double gridToCoordsY(double y_g) {
        return y_g * 200.0 / Consts.GRID_SIZE;
    }

    public static int coordsToGridX(double x) {
        return (int) (x / (200.0 / Consts.GRID_SIZE));
    }

    public static int coordsToGridY( double y) {
        return (int) (y / (200.0 / Consts.GRID_SIZE));
    }

    public static double facingToRad(Direction facing) {
        Objects.requireNonNull(facing, "Facing direction cannot be null");
        return switch (facing) {
            case EAST -> 0;
            case NORTH -> Math.PI / 2;
            case WEST -> Math.PI;
            case SOUTH -> -Math.PI / 2;
            default -> throw new IllegalArgumentException("Invalid facing direction: " + facing);
        };
    }

    public static String radToFacing(double rad) {
        if (Math.abs(rad) > Math.PI) {
            throw new IllegalArgumentException("Radians out of range: " + rad);
        }
        if (Math.PI / 4 < rad && rad <= 3 * Math.PI / 4) {
            return "N";
        } else if (-Math.PI / 4 < rad && rad <= Math.PI / 4) {
            return "E";
        } else if (-3 * Math.PI / 4 < rad && rad <= -Math.PI / 4) {
            return "S";
        } else {
            return "W";
        }
    }

    public static int[] coordsToPixelcoords(int x_g, int y_g, Double x, Double y, int map_x0, int map_y0, int map_width, int map_height) {
        if (x == null || y == null) {
            x = gridToCoordsX(x_g);
            y = gridToCoordsY(y_g);
        }
        int new_x = map_x0 + (int) (x * map_width / 200);
        int new_y = map_y0 + map_height - (int) (y * map_height / 200);
        return new int[]{new_x, new_y};
    }

    public static double l1(double x1, double y1, double x2, double y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    public static double l2(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    public static double diagDist(double x1, double y1, double x2, double y2) {
        double dx = Math.abs(x1 - x2);
        double dy = Math.abs(y1 - y2);
        return Math.sqrt(2 * Math.min(dx, dy) * Math.min(dx, dy)) + Math.abs(dx - dy);
    }

    public static double normaliseTheta(double theta) {
        int revolutions = (int) ((theta + Math.signum(theta) * Math.PI) / (2 * Math.PI));
        double p1 = truncatedRemainder(theta + Math.signum(theta) * Math.PI, 2 * Math.PI);
        double p2 = (Math.signum(Math.signum(theta) + 2 * (Math.signum(Math.abs(truncatedRemainder(theta + Math.PI, 2 * Math.PI) / (2 * Math.PI))) - 1))) * Math.PI;
        return p1 - p2;
    }

    public static double truncatedRemainder(double dividend, double divisor) {
        double dividedNumber = dividend / divisor;
        dividedNumber = dividedNumber >= 0 ? Math.floor(dividedNumber) : Math.ceil(dividedNumber);
        return dividend - divisor * dividedNumber;
    }

    public static double M(double theta) {
        theta = theta % (2 * Math.PI);
        if (theta < -Math.PI) {
            return theta + 2 * Math.PI;
        } else if (theta >= Math.PI) {
            return theta - 2 * Math.PI;
        } else {
            return theta;
        }
    }

    public static double[] R(double x, double y) {
        double r = Math.sqrt(x * x + y * y);
        double theta = Math.atan2(y, x);
        return new double[]{r, theta};
    }

    public static double[] changeOfBasis(double[] p1, double[] p2) {
        double theta1 = degToRad(p1[2]);
        double dx = p2[0] - p1[0];
        double dy = p2[1] - p1[1];
        double new_x = dx * Math.cos(theta1) + dy * Math.sin(theta1);
        double new_y = -dx * Math.sin(theta1) + dy * Math.cos(theta1);
        double new_theta = p2[2] - p1[2];
        return new double[]{new_x, new_y, new_theta};
    }

    public static double degToRad(double deg) {
        return Math.PI * deg / 180;
    }
}
