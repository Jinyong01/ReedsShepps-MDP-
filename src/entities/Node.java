package entities;

import java.util.Objects;
import simulation.Action;

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

    private int discretizePosition(double position) {
        return (int) (position / (200.0 / 40));
    }

    private int discretizeTheta(double theta) {
        return (int) (((theta * 180 / Math.PI + 180) / (360 / 24)));
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
}
