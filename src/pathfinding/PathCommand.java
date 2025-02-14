package pathfinding;

import entities.*;
import enums.Gear;
import enums.Steering;
import java.util.ArrayList;
import java.util.List;
import simulation.Action;
import org.json.JSONArray;
import org.json.JSONObject;

public class PathCommand {

    public static void printPath(List<Node> path) {
        for (Node node : path) {
            System.out.println(
                String.format("Current Node (x:%.2f, y:%.2f, theta:%.2f), Action: %s",
                        node.x, node.y, node.theta * 180 / Math.PI, node.prevAction));
        }
    }

    public static double distance(Node prev, Node node) {
        return Math.sqrt(Math.pow(node.x - prev.x, 2) + Math.pow(node.y - prev.y, 2));
    }

    public static List<String> constructPath(List<Node> path, double L, double Radius) {
        double LF = 0, SF = 0, RF = 0, LB = 0, SB = 0, RB = 0;
        int approx = 10;
        List<String> command = new ArrayList<>();
        List<int[]> droid = new ArrayList<>();
        Node prev = path.get(0);
        double dis = 0;

        for (Node node : path) {
            droid.add(new int[]{(int) (node.x / approx) - 1, (int) (node.y / approx) - 1});
            dis += distance(prev, node);
            Action action = node.prevAction;
            if (action.steering == Steering.LEFT && action.gear == Gear.FORWARD) {
                LF += 1;
            } else {
                if (LF >= 1) {
                    LF *= (L / (2 * Math.PI * Radius)) * 360;
                    command.add(String.format("LF%03d", (int) LF));
                    command.add(String.format("SF%03d", (int) dis));
                    LF = 0;
                    dis = 0;
                }
            }

            if (action.steering == Steering.STRAIGHT && action.gear == Gear.FORWARD) {
                SF += 1;
            } else {
                if (SF >= 1) {
                    SF *= L;
                    command.add(String.format("SF%03d", (int) SF));
                    SF = 0;
                    dis = 0;
                }
            }

            if (action.steering == Steering.RIGHT && action.gear == Gear.FORWARD) {
                RF += 1;
            } else {
                if (RF >= 1) {
                    RF *= (L / (2 * Math.PI * Radius)) * 360;
                    command.add(String.format("RF%03d", (int) RF));
                    command.add(String.format("SF%03d", (int) dis));
                    RF = 0;
                    dis = 0;
                }
            }

            if (action.steering == Steering.LEFT && action.gear == Gear.REVERSE) {
                LB += 1;
            } else {
                if (LB >= 1) {
                    LB *= (L / (2 * Math.PI * Radius)) * 360;
                    command.add(String.format("LB%03d", (int) LB));
                    command.add(String.format("SB%03d", (int) dis));
                    LB = 0;
                    dis = 0;
                }
            }

            if (action.steering == Steering.STRAIGHT && action.gear == Gear.REVERSE) {
                SB += 1;
            } else {
                if (SB >= 1) {
                    SB *= L;
                    command.add(String.format("SB%03d", (int) SB));
                    SB = 0;
                    dis = 0;
                }
            }

            if (action.steering == Steering.RIGHT && action.gear == Gear.REVERSE) {
                RB += 1;
            } else {
                if (RB >= 1) {
                    RB *= (L / (2 * Math.PI * Radius)) * 360;
                    command.add(String.format("RB%03d", (int) RB));
                    command.add(String.format("SB%03d", (int) dis));
                    RB = 0;
                    dis = 0;
                }
            }

            prev = node;
        }

        if (LF >= 1) {
            LF *= (L / (2 * Math.PI * Radius)) * 360;
            command.add(String.format("LF%03d", (int) LF));
            command.add(String.format("SF%03d", (int) dis));
        }

        if (SF >= 1) {
            SF *= L;
            command.add(String.format("SF%03d", (int) SF));
        }

        if (RF >= 1) {
            RF *= (L / (2 * Math.PI * Radius)) * 360;
            command.add(String.format("RF%03d", (int) RF));
            command.add(String.format("SF%03d", (int) dis));
        }

        if (LB >= 1) {
            LB *= (L / (2 * Math.PI * Radius)) * 360;
            command.add(String.format("LB%03d", (int) LB));
            command.add(String.format("SB%03d", (int) dis));
        }

        if (SB >= 1) {
            SB *= L;
            command.add(String.format("SB%03d", (int) SB));
        }

        if (RB >= 1) {
            RB *= (L / (2 * Math.PI * Radius)) * 360;
            command.add(String.format("RB%03d", (int) RB));
            command.add(String.format("SB%03d", (int) dis));
        }

        System.out.println(command);
        System.out.println(droid);
        return command;
    }

    public static List<String> constructPath2(List<Node> path, double L, double Radius) {
        printPath(path);
        List<String> commands = new ArrayList<>();
        List<int[]> gridPath = new ArrayList<>();
        double unitDist = L;
        double unitAngle = (L / (2 * Math.PI * Radius)) * 360;

        Node prev = path.get(0);
        Gear prevGear = prev.prevAction.gear;
        Steering prevSteering = prev.prevAction.steering;
        int sameCommandCount = 1;
        gridPath.add(new int[]{(int) (prev.x / 10), (int) (prev.y / 10)});

        for (Node node : path.subList(1, path.size())) {
            int curX = (int) (node.x / 10);
            int curY = (int) (node.y / 10);

            if (curX != gridPath.get(gridPath.size() - 1)[0] || curY != gridPath.get(gridPath.size() - 1)[1]) {
                gridPath.add(new int[]{curX, curY});
            }

            Gear gear = node.prevAction.gear;
            Steering steering = node.prevAction.steering;

            if (gear == prevGear && steering == prevSteering) {
                sameCommandCount += 1;
                continue;
            } else {
                if (prevSteering == Steering.STRAIGHT) {
                    commands.add("S" + (prevGear == Gear.FORWARD ? "F" : "B") + String.format("%03d", (int) (sameCommandCount * unitDist)));
                } else {
                    commands.add((prevSteering == Steering.LEFT ? "L" : "R") +
                            (prevGear == Gear.FORWARD ? "F" : "B") +
                            String.format("%03d", (int) (sameCommandCount * unitAngle)));
                }

                sameCommandCount = 1;
                prevGear = gear;
                prevSteering = steering;
            }

            prev = node;
        }

        if (prevSteering == Steering.STRAIGHT) {
            commands.add("S" + (prevGear == Gear.FORWARD ? "F" : "B") + String.format("%03d", (int) (sameCommandCount * unitDist)));
        } else {
            commands.add((prevSteering == Steering.LEFT ? "L" : "R") +
                    (prevGear == Gear.FORWARD ? "F" : "B") +
                    String.format("%03d", (int) (sameCommandCount * unitAngle)));
        }

        return commands;
        //return new PathResult(commands, gridPath);
    }

    public static String constructJson(List<String> commands, List<int[]> path) {
        JSONObject json = new JSONObject();
        json.put("type", "NAVIGATION");
        JSONObject data = new JSONObject();
        data.put("commands", new JSONArray(commands));
        data.put("path", new JSONArray(path));
        json.put("data", data);
        return json.toString();
    }

    public static class PathResult {
        public List<String> commands;
        public List<int[]> path;

        public PathResult(List<String> commands, List<int[]> path) {
            this.commands = commands;
            this.path = path;
        }
    }

    public static PathResult constructPathResult(List<Node> path, double L, double Radius) {
        List<String> commands = constructPath2(path, L, Radius);
        List<int[]> gridPath = new ArrayList<>();
        int approx = 10;
        for (Node node : path) {
            gridPath.add(new int[]{(int) (node.x / approx) - 1, (int) (node.y / approx) - 1});
        }
        return new PathResult(commands, gridPath);
    }

    // public static class PathResult {
    //     public List<String> commands;
    //     public List<int[]> path;

    //     public PathResult(List<String> commands, List<int[]> path) {
    //         this.commands = commands;
    //         this.path = path;
    //     }
    // }
    
}

