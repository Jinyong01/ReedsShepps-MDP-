package entities;

import enums.Direction;

public class GridObject {
    /** Center of the object on the x axis */
    private int x;

    /** Center of the object on the y axis */
    private int y;

    /** Size of the object on the x axis */
    private final int sizeX;

    /** Size of the object on the y axis */
    private final int sizeY;
    //Set to Double to allow for null value
    private Double rotation;

    public GridObject(int x, int y, int sizeX, int sizeY, double rotation) {
        this.x = x;
        this.y = y;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        setRotation(rotation);
    }

    // Overloaded Constructor w/o rotation, facing North
    public GridObject(int x, int y, int sizeX, int sizeY) {
        this(x, y, sizeX, sizeY, Math.PI / 2);
    }

    // Collision check
    public boolean collidesWith(GridObject other) {
        // no collision if our min x > their max x (right of them)
        return this.getMinX() <= other.getMaxX()
            &&
            // no collision if our max x < their min x (left of them)
            this.getMaxX() >= other.getMinX()
            &&
            // no collision if our min y > their max y (above them)
            this.getMinY() <= other.getMaxY()
            &&
            // no collision if our max y < min y (below them)
            this.getMaxY() >= other.getMinY();
    }

    // Getters and Setters
    public int getX(){
        return this.x;
    }

    public int getY(){
        return this.y;
    }

    // Convert Angle to the range of [0, 2π)
    public Double normalizeRotation(double angle) {
        angle = angle % (2 * Math.PI); // Bring within -2π to 2π
        if (angle < 0) angle += 2 * Math.PI; // Convert negative to positive range
        return angle;
    }

    public Direction getDirection() {
        switch ((int) Math.round(this.getRotation() / Math.PI * 2)) {
        case 0:
            return Direction.EAST; // 0
        case 1:
            return Direction.NORTH; // π/2
        case 2:
            return Direction.WEST; // π
        case 3:
            return Direction.SOUTH; // 3π/2
        }
        throw new IllegalStateException("Rotation should be between 0 and 2π but is: " + getRotation());
    }

    public double getRotation(){
        return this.rotation;
    }

    public void setRotation(double rotation) {
        this.rotation = normalizeRotation(rotation);
    }

    /** Get rotation of the gridObject in degrees. */
    public double getDegrees() {
        return getRotation() * 180.0 / Math.PI;
    }

    // Get bounding box coordinates
    public int getMinX() {
        return x - sizeX / 2;
    }

    public int getMaxX() {
        return x + sizeX / 2;
    }

    public int getMinY() {
        return y - sizeY / 2;
    }

    public int getMaxY() {
        return y + sizeY / 2;
    }
}
