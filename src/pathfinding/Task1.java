package pathfinding;

// import org.json.JSONArray;
// import org.json.JSONObject;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import entities.*;
import enums.Direction;
import simulation.Action;

public class Task1 {
    private final List<Node> paths = new ArrayList<>();
    private final List<List<String>> commands = new ArrayList<>();
    private final List<List<Integer>> android = new ArrayList<>();
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

        Hamiltonian tsp = new Hamiltonian(map, new ArrayList<>(obstaclesMap.values()), 10, 10, 0, -Math.PI / 2, "euclidean", minR);
        Node currentPos = new Node(10, 10, 0, null); // Initialize the starting position directly
        List<Obstacle> obstaclePath = tsp.findNearestNeighborPath();

        for (Obstacle obstacle : obstaclePath) {
            List<double[]> validCheckpoints = Hamiltonian.obstacleToCheckpointAll(map, obstacle, -Math.PI / 2);
            PathCommand.PathResult pathResult = null;
            while (pathResult == null && !validCheckpoints.isEmpty()) {
                double[] checkpoint = validCheckpoints.remove(0);
                System.out.println("Routing to obstacle...");
                Hybrid_astar algo = new Hybrid_astar(map, currentPos.x, currentPos.y, currentPos.theta, checkpoint[0], checkpoint[1], checkpoint[2], 10, L, minR, "euclidean", false, 24);
                List<Node> path = algo.findPath();
                if (path == null) {
                    System.out.println("Path failed to converge, trying another final position...");
                } else {
                    pathResult = PathCommand.constructPathResult(path, L, minR);
                }
            }

            if (pathResult != null) {
                List<Node> nodePath = new ArrayList<>();
                for (int[] coords : pathResult.path) {
                    nodePath.add(new Node(coords[0], coords[1], coords[2], null));
                }
                this.paths.addAll(nodePath);
                currentPos = nodePath.get(nodePath.size() - 1);
                this.commands.add(new ArrayList<>(pathResult.commands));
                List<List<Integer>> convertedPath = new ArrayList<>();
                for (int[] coords : pathResult.path) {
                    List<Integer> coordList = new ArrayList<>();
                    for (int coord : coords) {
                        coordList.add(coord);
                    }
                    convertedPath.add(coordList);
                }
                this.android.addAll(convertedPath);
                this.obstacleID.add(obstacle.getObstacleId());
                PathCommand.printPath(nodePath);
            } else {
                System.out.println("Path could not be found, routing to next obstacle...");
            }
        }
    }

    public String getCommandToNextObstacle() {
        List<String> nextCommand = null;
        List<Integer> nextPath = null;
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
