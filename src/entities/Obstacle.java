package entities;

import enums.Direction;
import java.util.Objects;
import java.util.Optional;
import simulation.Consts;


public class Obstacle extends GridObject{
    // obstacles occupy 1 x 1 space on grid
    public static final int SIZE_X = 1;
    public static final int SIZE_Y = 1;

    /** Obstacles of the same id are considered to be identical */
    private final int obstacleId;

    /**Direction of Obstacle */
    private final Direction direction;

    /** Image ID of the symbol on the obstacle */
    private Optional<Integer> imageId = Optional.empty();

    /** Whether the obstacle was found */
    private boolean foundFlag;

    public Obstacle(int x, int y, Direction direction, int obstacleId) {
        super(x, y, SIZE_X, SIZE_Y);
        this.direction = direction;
        this.obstacleId = obstacleId;
    }
    
    public int getObstacleId(){
        return this.obstacleId;
    }

    public boolean isFoundFlag() {
        return foundFlag;
    }

    @Override
    public Direction getDirection(){
        return this.direction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Obstacle)) return false;
        Obstacle obstacle = (Obstacle) o;
        return getObstacleId() == obstacle.getObstacleId();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getObstacleId());
    }

    @Override
    public boolean collidesWith(GridObject other) {
        // do not collide obstacle with same obstacle id
        if (equals(other)) return false;
        return super.collidesWith(other);
    }

    public boolean collidesWithPoint(double x, double y) {
        int x_g = (int) (x / (200.0 / Consts.GRID_SIZE));
        int y_g = (int) (y / (200.0 / Consts.GRID_SIZE));
        return x_g >= getMinX() && x_g <= getMaxX() && y_g >= getMinY() && y_g <= getMaxY();
    }
}
