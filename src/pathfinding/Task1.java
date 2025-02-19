package pathfinding;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;
import java.util.ArrayList;
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
        List<Obstacle> obstacles = new ArrayList<>();
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
            Obstacle obstacle = new Obstacle(obstacleJson.getInt("x"), obstacleJson.getInt("y"), invertObs, obstacleJson.getInt("id"));
            obstacles.add(obstacle);
        }


        OccupancyMap map = new OccupancyMap(obstacles);
        Hamiltonian tsp = new Hamiltonian(map, obstacles, 15, 10,Math.PI /2 , -Math.PI / 2, "euclidean", minR); 
        double[] currentPos = tsp.getStart(); //Hardcoded this
        List<Obstacle> obstaclePath = tsp.findNearestNeighborPath();

        List<Node> fullPath = new ArrayList<>();

        for (Obstacle obstacle : obstaclePath) {
            List<double[]> validCheckpoints = Hamiltonian.obstacleToCheckpointAll(map, obstacle, -Math.PI / 2);
            List<Node> path = null;
            while (path == null && !validCheckpoints.isEmpty()) {
                double[] checkpoint = validCheckpoints.remove(0);
                System.out.println("Routing to obstacle (x_g: " + obstacle.getX() + ", y_g: " + obstacle.getY() + "), x: " + checkpoint[0] + ", y: " + checkpoint[1] + " theta: " + Math.toDegrees(checkpoint[2]) + "...");
                Hybrid_astar algo = new Hybrid_astar(map,
                currentPos[0], currentPos[1], currentPos[2],
                checkpoint[0], checkpoint[1], checkpoint[2],
                0,10, 10, L, minR, "euclidean", false, 24);


                path = algo.findPath();
                if (path == null) {
                    System.out.println("Path failed to converge, trying another final position...");
                }
            }


            if (path != null) {
                fullPath.addAll(path);
                currentPos = new double[]{path.get(path.size() - 1).getX(), path.get(path.size() - 1).getY(), path.get(path.size() - 1).getTheta()};
                List<String> commandsList = PathCommand.constructPath2(path, L, minR);
                this.commands.add(commandsList);
                //this.android.add(PathCommand.constructPath2(path, L, minR));
                this.obstacleID.add(obstacle.getObstacleId());
                PathCommand.printPath(path);
            } else {
                System.out.println("Path could not be found, routing to next obstacle...");
            }
        }
        // Save the full path to a file
        PathCommand.printFullPath(fullPath);
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

    public static void main(String[] args) {
        // Create a sample JSON message
        JSONObject message = new JSONObject();
        message.put("type", "START_TASK");
        message.put("data", new JSONObject());
        message.getJSONObject("data").put("task", "EXPLORATION");
        message.getJSONObject("data").put("robot", new JSONObject());
        message.getJSONObject("data").getJSONObject("robot").put("id", "R");
        message.getJSONObject("data").getJSONObject("robot").put("x", 1);
        message.getJSONObject("data").getJSONObject("robot").put("y", 1);
        message.getJSONObject("data").getJSONObject("robot").put("dir", "N");

        // Add obstacles to the message
        JSONArray obstaclesArray = new JSONArray();
        obstaclesArray.put(new JSONObject().put("id", "00").put("x", 4).put("y", 5).put("dir", "S"));
        obstaclesArray.put(new JSONObject().put("id", "01").put("x", 4).put("y", 16).put("dir", "S"));
        obstaclesArray.put(new JSONObject().put("id", "02").put("x", 15).put("y", 16).put("dir", "W"));
        obstaclesArray.put(new JSONObject().put("id", "03").put("x", 15).put("y", 5).put("dir", "W"));
        obstaclesArray.put(new JSONObject().put("id", "04").put("x", 9).put("y", 11).put("dir", "S"));


        message.getJSONObject("data").put("obstacles", obstaclesArray);

        // Create an instance of Task1 and generate the path
        Task1 task = new Task1();
        task.generatePath(message);

        // Output the commands to the next obstacle
        while (!task.hasTaskEnded()) {
            String command = task.getCommandToNextObstacle();
            System.out.println("Command to next obstacle: " + command);
            int obstacleId = task.getObstacleId();
            System.out.println("Obstacle ID: " + obstacleId);
        }

        // Output the image IDs
        System.out.println("Image IDs: " + task.getImageId());
    }
}
