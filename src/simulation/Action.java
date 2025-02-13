package simulation;

import enums.Gear;
import enums.Steering;

public class Action {
    public Gear gear;
    public Steering steering;

    public Action(Gear gear, Steering steering) {
        this.gear = gear;
        this.steering = steering;
    }
}
