package entities;

import simulation.Consts;

public class Robot extends GridObject{
    // robots occupy 3 x 3 space on grid
    public static final int SIZE_X = 3;
    public static final int SIZE_Y = 3;

    public Robot(int x, int y, double rotation) {
        super(x, y, SIZE_X, SIZE_Y, rotation);
    }

    @Override
    public boolean collidesWith(GridObject other) {
        // since there would only be 1 robot in the arena
        // do not perform collision detection with another robot
        if (other instanceof Robot) return false;
        return super.collidesWith(other);
    }

    public boolean collidesWithPoint(double x, double y) {
        int x_g = (int) (x / (200.0 / Consts.GRID_SIZE));
        int y_g = (int) (y / (200.0 / Consts.GRID_SIZE));
        return x_g >= getMinX() && x_g <= getMaxX() && y_g >= getMinY() && y_g <= getMaxY();
    }
}
