package pathfinding;

import entities.Obstacle;
import entities.OccupancyMap;
import enums.Direction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import simulation.*;
 
public class Hamiltonian {
    private OccupancyMap map;
    private List<Obstacle> obstacles;
    private double[] start;
    private double thetaOffset;
    private String metric;
    private double minR;


    public Hamiltonian(OccupancyMap map, List<Obstacle> obstacles, double x_start, double y_start, double theta_start, double theta_offset, String metric, double minR) {
        assert -Math.PI < theta_start && theta_start <= Math.PI;
        assert -Math.PI < theta_offset && theta_offset <= Math.PI;
        this.map = map;
        this.obstacles = obstacles;
        this.start = new double[]{x_start, y_start, theta_start};
        this.thetaOffset = theta_offset;
        this.metric = metric;
        this.minR = minR;
    }


    public double[] getStart() {
        // Return the starting position as a double array
        return new double[]{10, 10, 0}; // Example values, replace with actual logic
    }


    public List<Obstacle> findBruteForcePath() {
        List<List<Obstacle>> obstaclePermutations = generatePermutations(obstacles);
        double shortestDistance = Double.MAX_VALUE;
        List<Obstacle> shortestPath = null;


        for (List<Obstacle> obstaclePath : obstaclePermutations) {
            double[] currentPos = start;
            double totalDistance = 0;
            for (Obstacle obstacle : obstaclePath) {
                double[] checkpoint = obstacleToCheckpoint(map, obstacle, thetaOffset);
                double distance;
                if (metric.equals("euclidean")) {
                    distance = Utils.l2(currentPos[0], currentPos[1], checkpoint[0], checkpoint[1]);
                } else if (metric.equals("reeds-shepp")) {
                    distance = Reeds_shepp.getOptimalPathLength(currentPos, checkpoint, minR);
                } else {
                    throw new IllegalArgumentException("Unknown metric: " + metric);
                }
                totalDistance += distance;
                currentPos = checkpoint;
            }
            if (totalDistance < shortestDistance) {
                shortestDistance = totalDistance;
                shortestPath = obstaclePath;
            }
        }
        return shortestPath;
    }


    public List<Obstacle> findNearestNeighborPath() {
        double[] currentPos = start;
        List<Obstacle> path = new ArrayList<>();
        List<Obstacle> remainingObstacles = new ArrayList<>(obstacles);


        while (!remainingObstacles.isEmpty()) {
            Obstacle nearestNeighbor = null;
            double minDist = Double.MAX_VALUE;
            for (Obstacle obstacle : remainingObstacles) {
                double[] checkpoint = obstacleToCheckpoint(map, obstacle, thetaOffset);
                if (checkpoint == null) {
                    remainingObstacles.remove(obstacle);
                    continue;
                }
                double dist;
                if (metric.equals("euclidean")) {
                    dist = Utils.l2(currentPos[0], currentPos[1], checkpoint[0], checkpoint[1]);
                } else if (metric.equals("reeds-shepp")) {
                    dist = Reeds_shepp.getOptimalPathLength(currentPos, checkpoint, minR);
                } else {
                    throw new IllegalArgumentException("Unknown metric: " + metric);
                }
                if (dist < minDist) {
                    minDist = dist;
                    nearestNeighbor = obstacle;
                }
            }
            if (nearestNeighbor != null) {
                path.add(nearestNeighbor);
                remainingObstacles.remove(nearestNeighbor);
                currentPos = obstacleToCheckpoint(map, nearestNeighbor, thetaOffset);
            }
        }
        return path;
    }


    private List<List<Obstacle>> generatePermutations(List<Obstacle> obstacles) {
        List<List<Obstacle>> permutations = new ArrayList<>();
        generatePermutations(obstacles, 0, permutations);
        return permutations;
    }


    private void generatePermutations(List<Obstacle> obstacles, int start, List<List<Obstacle>> permutations) {
        if (start == obstacles.size() - 1) {
            permutations.add(new ArrayList<>(obstacles));
        } else {
            for (int i = start; i < obstacles.size(); i++) {
                Collections.swap(obstacles, start, i);
                generatePermutations(obstacles, start + 1, permutations);
                Collections.swap(obstacles, start, i);
            }
        }
    }


    public static double[] obstacleToCheckpoint(OccupancyMap map, Obstacle obstacle, double thetaOffset) {
        double starting_x = Utils.gridToCoordsX(obstacle.getX());
        starting_x += offsetX(obstacle.getDirection());
        double starting_y = Utils.gridToCoordsY(obstacle.getY());
        starting_y += offsetY(obstacle.getDirection());
        double starting_image_to_pos_theta = offsetTheta(obstacle.getDirection(), Math.PI);


        double[] theta_scan_list = {0, Math.PI / 36, -Math.PI / 36, Math.PI / 18, -Math.PI / 18, Math.PI / 12, -Math.PI / 12,
                Math.PI / 9, -Math.PI / 9, Math.PI / 7.2, -Math.PI / 7.2, Math.PI / 6, -Math.PI / 6,
                Math.PI * 180 / 35, -Math.PI * 180 / 35, Math.PI / 4.5, -Math.PI / 4.5, Math.PI / 4, -Math.PI / 4};
        double[] r_scan_list = {20, 19, 21, 18, 22, 17, 23, 16, 24, 15, 25, 26, 27, 28, 29, 30};


        for (double r_scan : r_scan_list) {
            for (double theta_scan : theta_scan_list) {
                double cur_image_to_pos_theta = Utils.M(starting_image_to_pos_theta + theta_scan);
                double cur_x = starting_x + r_scan * Math.cos(cur_image_to_pos_theta);
                double cur_y = starting_y + r_scan * Math.sin(cur_image_to_pos_theta);
                double theta = Utils.M(cur_image_to_pos_theta - thetaOffset);


                if (!map.isColliding((int)cur_x,(int) cur_y) &&
                        !map.isColliding((int) (cur_x + 0.5 * Consts.REAR_AXLE_TO_CENTER * Math.cos(theta)), (int) (cur_y + 0.5 * Consts.REAR_AXLE_TO_CENTER * Math.sin(theta))) &&
                        !map.isColliding((int) (cur_x - 0.5 * Consts.REAR_AXLE_TO_CENTER * Math.cos(theta)), (int) (cur_y - 0.5 * Consts.REAR_AXLE_TO_CENTER * Math.sin(theta)))) {


                    cur_x -= Consts.REAR_AXLE_TO_CENTER * Math.cos(theta);
                    cur_y -= Consts.REAR_AXLE_TO_CENTER * Math.sin(theta);
                    return new double[]{cur_x, cur_y, theta, obstacle.getObstacleId()};
                }
            }
        }
        return null;
    }


    public static List<double[]> obstacleToCheckpointAll(OccupancyMap map, Obstacle obstacle, double thetaOffset) {
        double starting_x = Utils.gridToCoordsX(obstacle.getX());
        starting_x += offsetX(obstacle.getDirection());
        double starting_y = Utils.gridToCoordsY(obstacle.getY());
        starting_y += offsetY(obstacle.getDirection());
        double starting_image_to_pos_theta = offsetTheta(obstacle.getDirection(), Math.PI);


        List<double[]> valid_checkpoints = new ArrayList<>();


        double[] theta_scan_list = {0, Math.PI / 36, -Math.PI / 36, Math.PI / 18, -Math.PI / 18, Math.PI / 12, -Math.PI / 12,
                Math.PI / 9, -Math.PI / 9, Math.PI / 7.2, -Math.PI / 7.2, Math.PI / 6, -Math.PI / 6};
        double[] r_scan_list = {20, 19, 21, 18, 22, 17, 23, 16, 24, 15, 25, 26, 27, 28, 29, 30};


        for (double r_scan : r_scan_list) {
            for (double theta_scan : theta_scan_list) {
                double cur_image_to_pos_theta = Utils.M(starting_image_to_pos_theta + theta_scan);
                double cur_x = starting_x + r_scan * Math.cos(cur_image_to_pos_theta);
                double cur_y = starting_y + r_scan * Math.sin(cur_image_to_pos_theta);
                double theta = Utils.M(cur_image_to_pos_theta - thetaOffset);


                if (!map.isColliding((int)cur_x,(int) cur_y) &&
                        !map.isColliding((int) (cur_x + 0.5 * Consts.REAR_AXLE_TO_CENTER * Math.cos(theta)), (int) (cur_y + 0.5 * Consts.REAR_AXLE_TO_CENTER * Math.sin(theta))) &&
                        !map.isColliding((int) (cur_x - 0.5 * Consts.REAR_AXLE_TO_CENTER * Math.cos(theta)), (int) (cur_y - 0.5 * Consts.REAR_AXLE_TO_CENTER * Math.sin(theta)))) {


                    cur_x -= Consts.REAR_AXLE_TO_CENTER * Math.cos(theta);
                    cur_y -= Consts.REAR_AXLE_TO_CENTER * Math.sin(theta);
                    valid_checkpoints.add(new double[]{cur_x, cur_y, theta, obstacle.getObstacleId()});
                }
            }
        }
        return valid_checkpoints;
    }

    private static double offsetX(Direction facing) {
        return switch (facing) {
            case NORTH -> 2.5; //5
            case SOUTH -> 2.5; //5
            case EAST -> 0.0;
            case WEST -> 5.0;  //10
            default -> throw new IllegalArgumentException("Invalid facing direction: " + facing);
        };
    }

    private static double offsetY(Direction facing) {
        return switch (facing) {
            case NORTH -> 0.0;
            case SOUTH -> 5.0; //10
            case EAST -> 2.5;   //5
            case WEST -> 2.5;   //5
            default -> throw new IllegalArgumentException("Invalid facing direction: " + facing);
        };
    }

    private static double offsetTheta(Direction facing, double theta_offset) {
        return Utils.M(Utils.facingToRad(facing) + theta_offset);
    }

    public static List<Obstacle> generateRandomObstacles(int grid_size, int obstacle_count) {
        int offset = grid_size < 100 ? 2 : 10;  //5 : 50
        List<Obstacle> obstacles = new ArrayList<>();
        Direction[] directions = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
        int i = 0;

        while (obstacles.size() < obstacle_count) {
            int x = (int) (Math.random() * (grid_size - 2 * offset) + offset);
            int y = (int) (Math.random() * (grid_size - 2 * offset) + offset);
            Direction direction = directions[(int) (Math.random() * directions.length)];
            obstacles.add(new Obstacle(x, y, direction, i++));
        }

        return obstacles;
    }

    public static void printGrid(int grid_size, List<Obstacle> obstacles) {
        List<int[]> path = new ArrayList<>();
        for (int y = grid_size - 1; y >= 0; y--) {
            for (int x = 0; x < grid_size; x++) {
                final int finalX = x;
                final int finalY = y;
                boolean isStart = (0 <= finalX && finalX <= 2) && (0 <= finalY && finalY <= 2);
                boolean isObstacle = obstacles.stream().anyMatch(obstacle -> obstacle.getX() == finalX && obstacle.getY() == finalY);
                boolean isPath = path.stream().anyMatch(point -> point[0] == finalX && point[1] == finalY);

                if (isStart) {
                    System.out.print("C ");
                } else if (isObstacle) {
                    final int finX = x;
                    final int finY = y;
                    Direction direction = obstacles.stream()
                            .filter(obstacle -> obstacle.getX() == finX && obstacle.getY() == finY)
                            .map(Obstacle::getDirection)
                            .findFirst()
                            .orElse(null);
                    System.out.print(direction != null ? direction : ".");
                } else if (isPath) {
                    System.out.print("* ");
                } else {
                    System.out.print(". ");
                }
            }
            System.out.println();
        }
    }
   

    public static void main(String[] args) {
        List<Obstacle> obstacles = List.of(
            new Obstacle(10, 10, Direction.NORTH, 0),
            new Obstacle(10, 5, Direction.SOUTH, 1),
            new Obstacle(5, 10, Direction.EAST, 2),
            new Obstacle(8, 6, Direction.WEST, 3),
            new Obstacle(15, 9, Direction.NORTH, 4)
            // new Obstacle(10, 10, Direction.NORTH, 0),
                // new Obstacle(20, 10, Direction.SOUTH, 1),  
                // new Obstacle(10, 20, Direction.EAST, 2),        
                // new Obstacle(20, 20, Direction.WEST, 3),  
                // new Obstacle(38, 38, Direction.NORTH, 4)
        );
        OccupancyMap map = new OccupancyMap(obstacles);
        // Hamiltonian tsp = new Hamiltonian(map, obstacles, 5, 15, 0, -Math.PI / 2, "euclidean", 25);
        Hamiltonian tsp = new Hamiltonian(map, obstacles, 1, 1, 0, -Math.PI / 2, "euclidean", 25);
        List<Obstacle> path = tsp.findNearestNeighborPath();
        System.out.println("\nShortest Path:");
        System.out.println(path);
    }
}
