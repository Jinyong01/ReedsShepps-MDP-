package entities;

import simulation.Utils;

public class Checkpoint {
    private double x_g;
    private double y_g;
    private double theta;
    private boolean completed;

    public Checkpoint(double x_g, double y_g, double theta) {
        this.x_g = x_g;
        this.y_g = y_g;
        this.theta = theta;
        this.completed = false;
    }

    public double getX() {
        return Utils.gridToCoordsX(x_g);
    }

    public double getY() {
        return Utils.gridToCoordsY(y_g);
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
