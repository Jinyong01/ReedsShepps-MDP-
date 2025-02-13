package pathfinding;

import enums.Steering;
import enums.Gear;

public class PathElement {
    private double param;
    private Steering steering;
    private Gear gear;

    public PathElement(double param, Steering steering, Gear gear) {
        this.param = param;
        this.steering = steering;
        this.gear = gear;
    }

    public static PathElement create(double param, Steering steering, Gear gear) {
        if (param >= 0) {
            return new PathElement(param, steering, gear);
        } else {
            return new PathElement(-param, steering, gear).reverseGear();
        }
    }

    @Override
    public String toString() {
        return "{ Steering: " + steering.name() + "\tGear: " + gear.name() + "\tdistance: " + String.format("%.2f", param) + " }";
    }

    public PathElement reverseSteering() {
        Steering newSteering = Steering.fromValue(-this.steering.getValue());
        return new PathElement(this.param, newSteering, this.gear);
    }

    public PathElement reverseGear() {
        Gear newGear = Gear.fromValue(-this.gear.getValue());
        return new PathElement(this.param, this.steering, newGear);
    }

    public double getParam() {
        return param;
    }

    public Steering getSteering() {
        return steering;
    }

    public Gear getGear() {
        return gear;
    }
}
