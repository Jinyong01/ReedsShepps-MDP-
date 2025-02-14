package pathfinding;

import java.util.List;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import entities.*;

public class PathCommand {

    //rmb to add a constructPath here (alot of stuffs not included here)

    public static List<String> constructPath2(List<Node> path, double L, double minR) {
        List<String> commands = new ArrayList<>();
        List<Integer> gridPath = new ArrayList<>();
        for (Node node : path) {
            // Construct commands based on the path
            commands.add("Move to (" + node.x + ", " + node.y + ") with orientation " + node.theta);
        }
        return commands , gridPath;
    }

    public static String constructJson(List<String> commands, List<int[]> android) {
        JSONObject json = new JSONObject();
        json.put("commands", new JSONArray(commands));
        json.put("android", new JSONArray(android));
        return json.toString();
    }

    public static void printPath(List<Node> path) {
        for (Node node : path) {
            System.out.println("Node: (" + node.x + ", " + node.y + ", " + node.theta + ")");
        }
    }
}

