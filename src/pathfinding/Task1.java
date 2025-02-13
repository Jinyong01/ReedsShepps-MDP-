package pathfinding;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import entities.*;
import enums.Direction;

public class Task1 {
    private final List<Node> paths = new ArrayList<>();
    private final List<List<String>> commands = new ArrayList<>();
    private final List<List<int[]>> android = new ArrayList<>();
    private final List<Integer> obstacleID = new ArrayList<>();
    private final List<String> imageID = new ArrayList<>();

    public Task1() {
    }

    public void generatePath(JSONObject message) {
        Map<Integer, Obstacle> obstaclesMap = new HashMap<>();
        double L = 26.5 * Math.PI / 4 / 5;
        double minR = 26.5;

        JSONArray obstaclesArray = message.getJSONObject("data").getJSONArray("obstacles");
        for (int i = 0; i < obstaclesArray.length(); i++) {
            JSONObject obstacleJson = obstaclesArray.getJSONObject(i);
            String obsDIR = obstacleJson.getString("dir");
            Direction invertObs = switch (obsDIR) {
                case "N" -> Direction.SOUTH;
                case "S" -> Direction.NORTH;
                case "W" -> Direction.EAST;
                case "E" -> Direction.WEST;
                default -> throw new IllegalArgumentException("Invalid direction: " + obsDIR);
            }
            Obstacle obstacle = new Obstacle(obstacleJson.getInt("x") * 2, obstacleJson.getInt("y") * 2, invertObs, obstacleJson.getInt("id"));
            obstaclesMap.put(obstacle.getObstacleId(), obstacle);
        }

        Grid map = new Grid(40, 40);
        map.setObstacles(obstaclesMap);

        Hamiltonian tsp = new Hamiltonian(map, obstaclesMap.values().stream().toList(), 10, 10, 0, -Math.PI / 2, "euclidean", minR);
        Node currentPos = new Node(10, 10, 0, null); // Initialize the starting position directly
        List<Obstacle> obstaclePath = tsp.findNearestNeighborPath();

        for (Obstacle obstacle : obstaclePath) {
            List<Node> validCheckpoints = Obstacle.obstacleToCheckpointAll(map, obstacle, -Math.PI / 2);
            List<Node> path = null;
            while (path == null && !validCheckpoints.isEmpty()) {
                Node checkpoint = validCheckpoints.remove(0);
                System.out.println("Routing to obstacle...");
                Hybrid_astar algo = new Hybrid_astar(map, currentPos.x, currentPos.y, currentPos.theta, checkpoint.x, checkpoint.y, checkpoint.theta, 10, 10, L, minR, "euclidean", false, 24);
                path = algo.findPath();
                if (path == null) {
                    System.out.println("Path failed to converge, trying another final position...");
                }
            }

            if (path != null) {
                this.paths.addAll(path);
                currentPos = path.get(path.size() - 1);
                List<String> commandsList = PathCommand.constructPath2(path, L, minR);
                this.commands.addAll(commandsList);
                this.android.addAll(PathCommand.constructPath2(path, L, minR));
                this.obstacleID.add(checkpoint.getId());
                PathCommand.printPath(path);
            } else {
                System.out.println("Path could not be found, routing to next obstacle...");
            }
        }
    }

    public String getCommandToNextObstacle() {
        List<String> nextCommand = null;
        List<int[]> nextPath = null;
        if (!this.commands.isEmpty()) {
            nextCommand = this.commands.remove(0);
        }
        if (!this.android.isEmpty()) {
            nextPath = this.android.remove(0);
        }
        return PathCommand.constructJson(nextCommand, nextPath);
    }

    public int getObstacleId() {
        return this.obstacleID.remove(0);
    }

    public boolean hasTaskEnded() {
        return this.commands.isEmpty();
    }

    public void updateImageId(String imageID) {
        this.imageID.add(imageID);
    }

    public List<String> getImageId() {
        return this.imageID;
    }
}
