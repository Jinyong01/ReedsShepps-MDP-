package pathfinding;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

// import org.json.JSONArray;
// import org.json.JSONObject;

import java.util.HashMap;
import entities.*;
import enums.Direction;
//import simulation.Action;

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
            };
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
        List<int[]> nextPath = null;
        if (!this.commands.isEmpty()) {
            nextCommand = this.commands.remove(0);
        }
        if (!this.android.isEmpty()) {
            List<Integer> firstPathList = this.android.get(0); //Get the first list of Integers

            if(!firstPathList.isEmpty()) {
                nextPath = new ArrayList<>();
                //Convert List<Integer> to int[]
                int [] firstElementArray = firstPathList.stream().mapToInt(i -> i).toArray();
                nextPath.add(firstElementArray);
                firstPathList.clear(); //Remove all elements
            }

            if (firstPathList.isEmpty()) {
                this.android.remove(0); //Remove the empty list from android
            }
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

    public static JSONObject callAlgo(JSONObject message, double L, double minR) {
        List<Obstacle> obstacles = new ArrayList<>();
        List<String> full_commands = new ArrayList<>();
        List<int[]> full_path = new ArrayList<>();
        JSONArray data_obstacles = message.getJSONObject("data").getJSONArray("obstacles");
        for (int i = 0; i < data_obstacles.length(); i++) {
            JSONObject obstacleJson = data_obstacles.getJSONObject(i);
            String obsDIR = obstacleJson.getString("dir");
            Direction invertObs = switch (obsDIR) {
                case "N" -> Direction.SOUTH;
                case "S" -> Direction.NORTH;
                case "W" -> Direction.EAST;
                case "E" -> Direction.WEST;
                default -> throw new IllegalArgumentException("Invalid direction: " + obsDIR);
            };
            Obstacle obstacle = new Obstacle(obstacleJson.getInt("x") * 2, obstacleJson.getInt("y") * 2, invertObs, obstacleJson.getInt("id"));
            obstacles.add(obstacle);
        }

        Grid map = new Grid(40, 40);
        Map<Integer, Obstacle> obstaclesMap = new HashMap<>();
        for (Obstacle obstacle : obstacles) {
            obstaclesMap.put(obstacle.getObstacleId(), obstacle);
        }
        map.setObstacles(obstaclesMap);

        Hamiltonian tsp = new Hamiltonian(map, obstacles, 15, 15, Math.PI / 2, -Math.PI / 2, "euclidean", minR);
        Node currentPos = new Node(15, 15, Math.PI / 2, null);
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
                full_commands.addAll(pathResult.commands);
                full_path.addAll(pathResult.path);
                currentPos = new Node(pathResult.path.get(pathResult.path.size() - 1)[0], pathResult.path.get(pathResult.path.size() - 1)[1], pathResult.path.get(pathResult.path.size() - 1)[2], null);
            } else {
                System.out.println("Path could not be found, routing to next obstacle...");
            }
        }

        String jsonString = PathCommand.constructJson(full_commands, full_path);
        JSONObject jsonFile = new JSONObject(jsonString);
        return jsonFile;
    }

    public static void main(String[] args) {
        JSONObject message = new JSONObject();
        message.put("type", "START_TASK");
        message.put("data", new JSONObject());
        message.getJSONObject("data").put("task", "EXPLORATION");
        message.getJSONObject("data").put("robot", new JSONObject());
        message.getJSONObject("data").getJSONObject("robot").put("id", "R");
        message.getJSONObject("data").getJSONObject("robot").put("x", 1);
        message.getJSONObject("data").getJSONObject("robot").put("y", 1);
        message.getJSONObject("data").getJSONObject("robot").put("dir", "N");
        JSONArray obstaclesArray = new JSONArray();
        obstaclesArray.put(new JSONObject().put("id", "00").put("x", 8).put("y", 5).put("dir", "S"));
        obstaclesArray.put(new JSONObject().put("id", "01").put("x", 10).put("y", 17).put("dir", "W"));
        message.getJSONObject("data").put("obstacles", obstaclesArray);

        System.out.println(callAlgo(message, 25 * Math.PI / 4 / 5, 25));
    }

}
