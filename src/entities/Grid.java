package entities;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class Grid {
    private final int sizeX;
    private final int sizeY;
    private Map<Integer, Obstacle> obstacles = new HashMap<>();
    private Optional<Robot> robot = Optional.empty();
    //list of obstacles yet to be decided

    public Grid(int sizeX, int sizeY) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
    }   

    public void setObstacles(Map<Integer, Obstacle> obstacles) {
        this.obstacles = obstacles;
    }

    public void addObstacle(Obstacle obstacle) {
        // reject: out of bounds
        if (!isInBounds(obstacle)) {
            throw new IllegalArgumentException("Cannot place obstacle outside bounds of grid.");
        }
        // reject: obstacle collides with an object
        if (collide(obstacle)) {
            throw new IllegalArgumentException("Obstacle collides with another object on grid.");
        }
        getObstacles().put(obstacle.getObstacleId(), obstacle);
        //addObstacleToGrid(obstacle);
    }

    public void setRobot(Optional<Robot> robot) {
        if (robot.isPresent()) {
            // reject: out of bounds
            if (!isInBounds(robot.get())) {
                throw new IllegalArgumentException("Cannot place robot outside bounds of grid.");
            }
            // reject: collision with another object
            if (collide(robot.get())) {
                throw new IllegalArgumentException("Robot collides with another object on grid.");
            }
        }
        this.robot = robot;
    }

    public void resetObstacles() {
        this.obstacles.clear();
        // clearOccupancyGrid();
    }

    public boolean isValidPosition(int x, int y) {
        return x >= 0 && x < this.sizeX && y >= 0 && y < this.sizeY;
    }

    public boolean isInBounds(GridObject object) {
        return object.getMinX() >= 0
            && object.getMinY() >= 0
            && object.getMaxX() < getSizeX()
            && object.getMaxX() < getSizeY();
    }

    /** Whether any object in the grid collides with the given object */
    public boolean collide(GridObject object) {
        // object collides with obstacle
        if (obstacles.values().stream().anyMatch(o -> o.collidesWith(object))) return true;
        // object collides with robot
        return robot.isPresent() && robot.get().collidesWith(object);
    }

    public boolean collideWithPoint(double x, double y) {
        for (Obstacle obstacle : obstacles.values()) {
            if (obstacle.collidesWithPoint(x, y)) {
                return true;
            }
        }
        return robot.isPresent() && robot.get().collidesWithPoint(x, y);
    }

    // private void addObstacleToGrid(Obstacle obstacle) {
    //     int x_start = Math.max((int) obstacle.getX() - 3, 0);
    //     int x_end = Math.min((int) obstacle.getX() + 4, sizeX - 1);
    //     int y_start = Math.max((int) obstacle.getY() - 3, 0);
    //     int y_end = Math.min((int) obstacle.getY() + 4, sizeY - 1);
    //     for (int i = x_start; i <= x_end; i++) {
    //         for (int j = y_start; j <= y_end; j++) {
    //             occupancyGrid[i][j] = 1;
    //         }
    //     }
    // }

    // private void clearOccupancyGrid() {
    //     for (int i = 0; i < sizeX; i++) {
    //         for (int j = 0; j < sizeY; j++) {
    //             occupancyGrid[i][j] = 0;
    //         }
    //     }
    // }

    public int getSizeX(){
        return this.sizeX;
    }
    public int getSizeY(){
        return this.sizeY;
    }
    public Map<Integer, Obstacle> getObstacles(){
        return this.obstacles;
    }

    
}
