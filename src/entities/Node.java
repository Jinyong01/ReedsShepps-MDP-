package entities;

import java.util.Objects;
import simulation.Action;
import simulation.Utils;

public class Node implements Comparable<Node> {
    public double x;
    public double y;
    public double theta;
    public int x_g;
    public int y_g;
    public int theta_g;
    public Node parent;
    public Action prevAction;
    public double g;
    public double h;
    public double f;

    public static Node startNode;
    public Node(double x, double y, double theta, Action prevAction, Node parent) {
        this.x = x;
        this.y = y;
        this.theta = theta;
        this.parent = parent;
        this.prevAction = prevAction;
        this.g = 0;
        this.h = 0;
        this.f = 0;
        this.x_g = discretizePosition(x);
        this.y_g = discretizePosition(y);
        this.theta_g = discretizeTheta(theta);
    }

    public Node(double x, double y, double theta, Action prevAction) {
        this(x, y, theta, prevAction, null);
    }

    //Convert a real-word position to a grid position (Grid = 200cm, 20x20 grid)
    // private int discretizePosition(double position) {
    //     return (int) (position / (200.0 / 20));
    // }

    private int discretizePosition(double position) {
        int discretizedPosition = (int) (position / (200.0 / 20));
        if (discretizedPosition < 0 || discretizedPosition >= 20) {
            throw new IllegalArgumentException("Position out of bounds: " + position);
        }
        return discretizedPosition;
    }

    //Convert rad to angle, shift the range from [-180,180) to [0,360) and discretize into 24 bins
    // private int discretizeTheta(double theta) {
    //     return (int) (((theta * 180 / Math.PI + 180) / (360 / 24)));
    // }

    private int discretizeTheta(double theta) {
        theta = normaliseTheta(theta);

        int discretizedTheta = (int) (((theta * 180 / Math.PI + 180) / (360 / 24)));
        if (discretizedTheta < 0 || discretizedTheta >= 24) {
            throw new IllegalArgumentException(String.format("Theta out of bounds: %d" + theta, discretizedTheta));
        }
        return discretizedTheta;
    }

    private double normaliseTheta(double theta) {
        theta = theta % (2 * Math.PI);
        if (theta < -Math.PI) {
            return theta + 2 * Math.PI;
        } else if (theta >= Math.PI) {
            return theta - 2 * Math.PI;
        } else {
            return theta;
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Node node = (Node) obj;
        return Math.abs(x - node.x) <= 3.5 && Math.abs(y - node.y) <= 3.5 && (Math.abs(theta - node.theta) <= Math.PI / 24 || Math.abs(Math.abs(theta - node.theta) - 2 * Math.PI) <= Math.PI / 24);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, theta);
    }

    @Override
    public int compareTo(Node other) {
        return Double.compare(this.f, other.f);
    }

    @Override
    public String toString() {
        //return String.format("Node(x=%.2f, y=%.2f, theta=%.2f, action=%s)", x, y, theta * 180 / Math.PI, prevAction);
        return String.format("Node(x=%.2f, y=%.2f, theta=%.2f)", x, y, theta * 180 / Math.PI, prevAction);

    }

    public double getX() {
        return x;
    }  

    public double getY() {
        return y;
    }

    public double getTheta() {
        return theta;
    }

}
