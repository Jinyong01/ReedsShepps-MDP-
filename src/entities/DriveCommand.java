package entities;
import enums.*;

public class DriveCommand {
    private Gear gear;
    private Steering steering;
    private double distance;

    public DriveCommand(Gear gear, Steering steering, double distance) {
        this.gear = gear;
        this.steering = steering;
        this.distance = distance;
    }

    public DriveCommand() {
        this(Gear.PARK, Steering.STRAIGHT, 0);
    }

    public Gear getGear() {
        return gear;
    }

    public void setGear(Gear gear) {
        this.gear = gear;
    }

    public Steering getSteering() {
        return steering;
    }

    public void setSteering(Steering steering) {
        this.steering = steering;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "DriveCommand{" +
                "gear=" + gear +
                ", steering=" + steering +
                ", distance=" + distance +
                '}';
    }
}
