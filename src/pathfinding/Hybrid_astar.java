package pathfinding;

import entities.Grid;
import entities.Node;
import enums.Gear;
import enums.Steering;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import simulation.*;

public class Hybrid_astar {
    private Grid map;
    private double x_0;
    private double y_0;
    private double theta_0;
    private double x_f;
    private double y_f;
    private double theta_f;
    private double thetaOffset;
    //private int steeringChangeCost;
    //private int gearChangeCost;
    private double L;
    private double minR;
    private String heuristic;
    //private boolean simulate;
    private int thetaBins;


    public Hybrid_astar(Grid map, double x_0, double y_0, double theta_0, double x_f, double y_f, double theta_f, double thetaOffset, double L, double minR, String heuristic, boolean simulate, int thetaBins) {
        this.map = map;
        this.x_0 = x_0;
        this.y_0 = y_0;
        this.theta_0 = theta_0;
        this.x_f = x_f;
        this.y_f = y_f;
        this.theta_f = theta_f;
        this.thetaOffset = thetaOffset;
        //this.steeringChangeCost = steeringChangeCost;
        //this.gearChangeCost = gearChangeCost;
        this.L = L;
        this.minR = minR;
        this.heuristic = heuristic;
        //this.simulate = simulate;
        this.thetaBins = thetaBins;
    }

    public List<Node> findPath() {
        PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparingDouble(node -> node.f));
        PriorityQueue<Node> close = new PriorityQueue<>(Comparator.comparingDouble(node -> node.f));
        double[][][] openList = new double[40][40][thetaBins + 1];
        double[][][] closedList = new double[40][40][thetaBins + 1];

        for (int i = 0; i < 40; i++) {
            for (int j = 0; j < 40; j++) {
                for (int k = 0; k <= thetaBins; k++) {
                    openList[i][j][k] = Double.MAX_VALUE;
                    closedList[i][j][k] = Double.MAX_VALUE;
                }
            }
        }

        Node startNode = new Node(x_0, y_0, theta_0, new Action(Gear.FORWARD, Steering.STRAIGHT));
        Node endNode = new Node(x_f, y_f, theta_f, new Action(Gear.FORWARD, Steering.STRAIGHT));

        open.add(startNode);
        openList[(int) startNode.x_g][(int) startNode.y_g][(int) startNode.theta_g] = startNode.f;

        while (!open.isEmpty()) {
            Node currentNode = open.poll();
            openList[(int) currentNode.x_g][(int) currentNode.y_g][(int) currentNode.theta_g] = Double.MAX_VALUE;

            if (checkPathFound(currentNode)) {
                return constructPath(currentNode);
            }

            for (Action choice : generateChoices()) {
                Node childNode = generateChildNode(currentNode, choice);
                if (map.collideWithPoint(childNode.x, childNode.y)) continue;

                childNode.g = currentNode.g + L;
                childNode.h = calculateHeuristic(childNode, endNode);
                childNode.f = childNode.g + childNode.h;

                if (childNode.f < openList[(int) childNode.x_g][(int) childNode.y_g][(int) childNode.theta_g]) {
                    open.add(childNode);
                    openList[(int) childNode.x_g][(int) childNode.y_g][(int) childNode.theta_g] = childNode.f;
                }
            }

            close.add(currentNode);
            closedList[(int) currentNode.x_g][(int) currentNode.y_g][(int) currentNode.theta_g] = currentNode.f;
        }

        return null;
    }

    //Used in findPath()
    private List<Node> constructPath(Node currentNode) {
        List<Node> path = new ArrayList<>();
        while (currentNode != null) {
            path.add(currentNode);
            currentNode = currentNode.parent;
        }
        Collections.reverse(path);
        return path;
    }

    //Used in findPath()
    private double calculateHeuristic(Node node, Node endNode) {
        return switch (heuristic) {
            case "euclidean" -> Utils.l2(node.x, node.y, endNode.x, endNode.y);
            case "manhattan" -> Utils.l1(node.x, node.y, endNode.x, endNode.y);
            case "diag" -> Utils.diagDist(node.x, node.y, endNode.x, endNode.y);
            case "reeds-shepp" -> Reeds_shepp.getOptimalPathLength(new double[]{node.x, node.y, node.theta}, new double[]{endNode.x, endNode.y, endNode.theta}, minR);
            case "hybridl2" -> Math.max(Utils.l2(node.x, node.y, endNode.x, endNode.y), Reeds_shepp.getOptimalPathLength(new double[]{node.x, node.y, node.theta}, new double[]{endNode.x, endNode.y, endNode.theta}, minR));
            case "hybridl1" -> Math.min(Utils.l1(node.x, node.y, endNode.x, endNode.y), Reeds_shepp.getOptimalPathLength(new double[]{node.x, node.y, node.theta}, new double[]{endNode.x, endNode.y, endNode.theta}, minR));
            case "hybriddiag" -> Math.min(Utils.diagDist(node.x, node.y, endNode.x, endNode.y), Reeds_shepp.getOptimalPathLength(new double[]{node.x, node.y, node.theta}, new double[]{endNode.x, endNode.y, endNode.theta}, minR));
            case "greedy" -> 0;
            default -> 0;
        };
    }

    //Used in findPath()
    private List<Action> generateChoices() {
        List<Action> choices = new ArrayList<>();
        for (Gear gear : Gear.values()) {
            for (Steering steering : Steering.values()) {
                choices.add(new Action(gear, steering));
            }
        }
        return choices;
    }

    //Calculte_next_node 
    private Node generateChildNode(Node currentNode, Action choice) {
        double x_child, y_child, theta_child;
        if (choice.steering == Steering.STRAIGHT) {
            x_child = currentNode.x + choice.gear.getValue() * L * Math.cos(currentNode.theta);
            y_child = currentNode.y + choice.gear.getValue() * L * Math.sin(currentNode.theta);
            theta_child = currentNode.theta;
        } else {
            double x_c = currentNode.x + choice.steering.getValue() * minR * Math.sin(currentNode.theta);
            double y_c = currentNode.y - choice.steering.getValue() * minR * Math.cos(currentNode.theta);
            double theta_t = -choice.steering.getValue() * L / minR;
            double theta_b = Utils.normaliseTheta(currentNode.theta + choice.gear.getValue() * theta_t);
            double x_ca = currentNode.x - x_c;
            double y_ca = currentNode.y - y_c;
            x_child = x_c + (x_ca * Math.cos(choice.gear.getValue() * theta_t) - y_ca * Math.sin(choice.gear.getValue() * theta_t));
            y_child = y_c + (x_ca * Math.sin(choice.gear.getValue() * theta_t) + y_ca * Math.cos(choice.gear.getValue() * theta_t));
            theta_child = theta_b;
        }
        return new Node(x_child, y_child, theta_child, choice, currentNode);
    }

    //Used in findPath()
    private boolean checkPathFound(Node curNode) {
        double thetaMargin = Math.PI / 12;
        double targetDistance = 21;
        double distanceMargin = 7.5;
        double maxPerpDistance = 0.5;

        if (Math.abs(curNode.theta - theta_f) > thetaMargin) {
            return false;
        }

        double target_x = x_f + Consts.REAR_AXLE_TO_CENTER * Math.cos(theta_f);
        double target_y = y_f + Consts.REAR_AXLE_TO_CENTER * Math.sin(theta_f);

        if (map.collideWithPoint(target_x, target_y)) {
            return false;
        }

        target_x += targetDistance * Math.cos(theta_f - thetaOffset);
        target_y += targetDistance * Math.sin(theta_f - thetaOffset);

        double cur_x = curNode.x + Consts.REAR_AXLE_TO_CENTER * Math.cos(curNode.theta);
        double cur_y = curNode.y + Consts.REAR_AXLE_TO_CENTER * Math.sin(curNode.theta);

        double dist = Utils.l2(target_x, target_y, cur_x, cur_y);

        if (dist > targetDistance + distanceMargin || dist < targetDistance - distanceMargin) {
            return false;
        }

        double projection_x = cur_x + dist * Math.cos(curNode.theta - thetaOffset);
        double projection_y = cur_y + dist * Math.sin(curNode.theta - thetaOffset);
        double perpDistance = Utils.l2(projection_x, projection_y, target_x, target_y);

        if (perpDistance > maxPerpDistance * targetDistance / dist) {
            return false;
        }
        
        return true;
    }
}
