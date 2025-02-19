package pathfinding;

import entities.*;
import enums.Gear;
import enums.Steering;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import simulation.Action;
import org.json.JSONArray;
import org.json.JSONObject;

public class PathCommand {

    public static void printPath(List<Node> path) {
        for (Node node : path) {
            System.out.println(
                String.format("Current Node (x:%.2f, y:%.2f, theta:%.2f), Action: %s",
                        node.x, node.y, node.theta * 180 / Math.PI, node.prevAction));
        }
    }

    public static void printFullPath(List<Node> fullPath) {
        File fileObj = new File("full_path.json");
        System.out.println("File will be written to: " + fileObj.getAbsolutePath());

        try (FileWriter file = new FileWriter(fileObj)) {
            file.write("[\n");

            boolean first = true;
            for (Node node : fullPath) {
                if (!first) {
                    file.write(",\n");
                }
                first = false;

                file.write("  {\n");
                file.write("    \"x\": " + node.getX() + ",\n");
                file.write("    \"y\": " + node.getY() + ",\n");
                file.write("    \"theta\": " + node.getTheta() + "\n");
                file.write("  }");
            }

            file.write("\n]");
            System.out.println("Data has been written to " + fileObj.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        

        // for (Node node : path) {
        //     System.out.println(
        //         String.format("Current Node (x:%.2f, y:%.2f, theta:%.2f), Action: %s",
        //                 node.x, node.y, node.theta * 180 / Math.PI, node.prevAction));          //convert rad to degree
        // }
    }
    //Basically calculate euclidean distance 
    public static double distance(Node prev, Node node) {
        return Math.sqrt(Math.pow(node.x - prev.x, 2) + Math.pow(node.y - prev.y, 2));
    }

    public static List<String> constructPath2(List<Node> path, double L, double Radius) {
        printPath(path);
        List<String> commands = new ArrayList<>();
        List<int[]> gridPath = new ArrayList<>();
        double unitDist = L;
        double unitAngle = (L / (2 * Math.PI * Radius)) * 360;

        Node prev = path.get(0);
        Gear prevGear = prev.prevAction.gear;
        Steering prevSteering = prev.prevAction.steering;
        int sameCommandCount = 1;
        gridPath.add(new int[]{(int) (prev.x / 10), (int) (prev.y / 10)});

        for (Node node : path.subList(1, path.size())) {
            int curX = (int) (node.x / 10);
            int curY = (int) (node.y / 10);

            if (curX != gridPath.get(gridPath.size() - 1)[0] || curY != gridPath.get(gridPath.size() - 1)[1]) {
                gridPath.add(new int[]{curX, curY});
            }

            Gear gear = node.prevAction.gear;
            Steering steering = node.prevAction.steering;

            if (gear == prevGear && steering == prevSteering) {
                sameCommandCount += 1;
                continue;
            } else {
                if (prevSteering == Steering.STRAIGHT) {
                    commands.add("S" + (prevGear == Gear.FORWARD ? "F" : "B") + String.format("%03d", (int) (sameCommandCount * unitDist)));
                } else {
                    commands.add((prevSteering == Steering.LEFT ? "L" : "R") +
                            (prevGear == Gear.FORWARD ? "F" : "B") +
                            String.format("%03d", (int) (sameCommandCount * unitAngle)));
                }

                sameCommandCount = 1;
                prevGear = gear;
                prevSteering = steering;
            }

            prev = node;
        }

        if (prevSteering == Steering.STRAIGHT) {
            commands.add("S" + (prevGear == Gear.FORWARD ? "F" : "B") + String.format("%03d", (int) (sameCommandCount * unitDist)));
        } else {
            commands.add((prevSteering == Steering.LEFT ? "L" : "R") +
                    (prevGear == Gear.FORWARD ? "F" : "B") +
                    String.format("%03d", (int) (sameCommandCount * unitAngle)));
        }

        return commands;
        //return new PathResult(commands, gridPath);
    }

    //Used to serialize the path data for external use
    public static String constructJson(List<String> commands, List<int[]> path) {
        JSONObject json = new JSONObject();
        json.put("type", "NAVIGATION");
        JSONObject data = new JSONObject();
        data.put("commands", new JSONArray(commands));
        data.put("path", new JSONArray(path));
        json.put("data", data);
        return json.toString();
    }

    //A simple data structure to hold the list of commands and the grid path.
    //Useful for returning the results of path construction methods.
    public static class PathResult {
        public List<String> commands;
        public List<int[]> path;

        public PathResult(List<String> commands, List<int[]> path) {
            this.commands = commands;
            this.path = path;
        }
    }

    public static PathResult constructPathResult(List<Node> path, double L, double Radius) {
        List<String> commands = constructPath2(path, L, Radius);
        List<int[]> gridPath = new ArrayList<>();
        int approx = 10;
        for (Node node : path) {
            gridPath.add(new int[]{(int) (node.x / approx) - 1, (int) (node.y / approx) - 1});
        }
        return new PathResult(commands, gridPath);
    }
}