package pathfinding;

import enums.Gear;
import enums.Steering;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import simulation.*;

public class Reeds_shepp {
    public static double getOptimalPathLength(double[] start, double[] end, double radius) {
        double x_0 = start[0] / radius;
        double y_0 = start[1] / radius;
        double x_f = end[0] / radius;
        double y_f = end[1] / radius;

        List<PathElement> optPath = getOptimalPath(new double[]{x_0, y_0, start[2]}, new double[]{x_f, y_f, end[2]});
        return radius * pathLength(optPath);
    }

    private static List<PathElement> getOptimalPath(double[] start, double[] end) {
        List<List<PathElement>> paths = getAllPaths(start, end);
        return paths.stream().min(Comparator.comparingDouble(Reeds_shepp::pathLength)).orElse(new ArrayList<>());
    }

    private static List<List<PathElement>> getAllPaths(double[] start, double[] end) {
        List<List<PathElement>> paths = new ArrayList<>();
        List<List<PathElement>> pathFunctions = List.of(
                path1(start, end),
                path2(start, end),
                path3(start, end),
                path4(start, end),
                path5(start, end),
                path6(start, end),
                path7(start, end),
                path8(start, end),
                path9(start, end),
                path10(start, end),
                path11(start, end),
                path12(start, end)
        );

        for (List<PathElement> pathFn : pathFunctions) {
            paths.add(pathFn);
            paths.add(timeflip(pathFn));
            paths.add(reflect(pathFn));
            paths.add(reflect(timeflip(pathFn)));
        }

        paths = paths.stream()
                .map(path -> path.stream().filter(e -> e.getParam() != 0).collect(Collectors.toList()))
                .filter(path -> !path.isEmpty())
                .collect(Collectors.toList());

        return paths;
    }

    private static List<PathElement> timeflip(List<PathElement> path) {
        return path.stream().map(PathElement::reverseGear).collect(Collectors.toList());
    }

    private static List<PathElement> reflect(List<PathElement> path) {
        return path.stream().map(PathElement::reverseSteering).collect(Collectors.toList());
    }

    private static double pathLength(List<PathElement> path) {
        return path.stream().mapToDouble(PathElement::getParam).sum();
    }

    private static List<PathElement> path1(double[] start, double[] end) {
        List<PathElement> path = new ArrayList<>();
        double phi = Utils.degToRad(end[2]);
        double[] u_t = Utils.R(start[0] - Math.sin(phi), end[1] - 1 + Math.cos(phi));
        double v = Utils.M(phi - u_t[1]);

        path.add(PathElement.create(u_t[1], Steering.LEFT, Gear.FORWARD));
        path.add(PathElement.create(u_t[0], Steering.STRAIGHT, Gear.FORWARD));
        path.add(PathElement.create(v, Steering.LEFT, Gear.FORWARD));

        return path;
    }

    private static List<PathElement> path2(double[] start, double[] end) {
        List<PathElement> path = new ArrayList<>();
        double phi = Utils.M(Utils.degToRad(end[2]));
        double[] rho_t1 = Utils.R(start[0] + Math.sin(phi), end[1] - 1 - Math.cos(phi));

        if (rho_t1[0] * rho_t1[0] >= 4) {
            double u = Math.sqrt(rho_t1[0] * rho_t1[0] - 4);
            double t = Utils.M(rho_t1[1] + Math.atan2(2, u));
            double v = Utils.M(t - phi);

            path.add(PathElement.create(t, Steering.LEFT, Gear.FORWARD));
            path.add(PathElement.create(u, Steering.STRAIGHT, Gear.FORWARD));
            path.add(PathElement.create(v, Steering.RIGHT, Gear.FORWARD));
        }

        return path;
    }

    private static List<PathElement> path3(double[] start, double[] end) {
        List<PathElement> path = new ArrayList<>();
        double phi = Utils.degToRad(end[2]);
        double xi = start[0] - Math.sin(phi);
        double eta = end[1] - 1 + Math.cos(phi);
        double[] rho_theta = Utils.R(xi, eta);

        if (rho_theta[0] <= 4) {
            double A = Math.acos(rho_theta[0] / 4);
            double t = Utils.M(rho_theta[1] + Math.PI / 2 + A);
            double u = Utils.M(Math.PI - 2 * A);
            double v = Utils.M(phi - t - u);

            path.add(PathElement.create(t, Steering.LEFT, Gear.FORWARD));
            path.add(PathElement.create(u, Steering.RIGHT, Gear.REVERSE));
            path.add(PathElement.create(v, Steering.LEFT, Gear.FORWARD));
        }

        return path;
    }

    private static List<PathElement> path4(double[] start, double[] end) {
        List<PathElement> path = new ArrayList<>();
        double phi = Utils.degToRad(end[2]);
        double xi = start[0] - Math.sin(phi);
        double eta = end[1] - 1 + Math.cos(phi);
        double[] rho_theta = Utils.R(xi, eta);

        if (rho_theta[0] <= 4) {
            double A = Math.acos(rho_theta[0] / 4);
            double t = Utils.M(rho_theta[1] + Math.PI / 2 + A);
            double u = Utils.M(Math.PI - 2 * A);
            double v = Utils.M(t + u - phi);

            path.add(PathElement.create(t, Steering.LEFT, Gear.FORWARD));
            path.add(PathElement.create(u, Steering.RIGHT, Gear.REVERSE));
            path.add(PathElement.create(v, Steering.LEFT, Gear.REVERSE));
        }

        return path;
    }

    private static List<PathElement> path5(double[] start, double[] end) {
        List<PathElement> path = new ArrayList<>();
        double phi = Utils.degToRad(end[2]);
        double xi = start[0] - Math.sin(phi);
        double eta = end[1] - 1 + Math.cos(phi);
        double[] rho_theta = Utils.R(xi, eta);

        if (rho_theta[0] <= 4) {
            double u = Math.acos(1 - rho_theta[0] * rho_theta[0] / 8);
            double A = Math.asin(2 * Math.sin(u) / rho_theta[0]);
            double t = Utils.M(rho_theta[1] + Math.PI / 2 - A);
            double v = Utils.M(t - u - phi);

            path.add(PathElement.create(t, Steering.LEFT, Gear.FORWARD));
            path.add(PathElement.create(u, Steering.RIGHT, Gear.FORWARD));
            path.add(PathElement.create(v, Steering.LEFT, Gear.REVERSE));
        }

        return path;
    }

    private static List<PathElement> path6(double[] start, double[] end) {
        List<PathElement> path = new ArrayList<>();
        double phi = Utils.degToRad(end[2]);
        double xi = start[0] + Math.sin(phi);
        double eta = end[1] - 1 - Math.cos(phi);
        double[] rho_theta = Utils.R(xi, eta);

        if (rho_theta[0] <= 4) {
            if (rho_theta[0] <= 2) {
                double A = Math.acos((rho_theta[0] + 2) / 4);
                double t = Utils.M(rho_theta[1] + Math.PI / 2 + A);
                double u = Utils.M(A);
                double v = Utils.M(phi - t + 2 * u);
                path.add(PathElement.create(t, Steering.LEFT, Gear.FORWARD));
                path.add(PathElement.create(u, Steering.RIGHT, Gear.FORWARD));
                path.add(PathElement.create(u, Steering.LEFT, Gear.REVERSE));
                path.add(PathElement.create(v, Steering.RIGHT, Gear.REVERSE));
            } else {
                double A = Math.acos((rho_theta[0] - 2) / 4);
                double t = Utils.M(rho_theta[1] + Math.PI / 2 - A);
                double u = Utils.M(Math.PI - A);
                double v = Utils.M(phi - t + 2 * u);
                path.add(PathElement.create(t, Steering.LEFT, Gear.FORWARD));
                path.add(PathElement.create(u, Steering.RIGHT, Gear.FORWARD));
                path.add(PathElement.create(u, Steering.LEFT, Gear.REVERSE));
                path.add(PathElement.create(v, Steering.RIGHT, Gear.REVERSE));
            }
        }

        return path;
    }

    private static List<PathElement> path7(double[] start, double[] end) {
        List<PathElement> path = new ArrayList<>();
        double phi = Utils.degToRad(end[2]);
        double xi = start[0] + Math.sin(phi);
        double eta = end[1] - 1 - Math.cos(phi);
        double[] rho_theta = Utils.R(xi, eta);
        double u1 = (20 - rho_theta[0] * rho_theta[0]) / 16;

        if (rho_theta[0] <= 6 && u1 >= 0 && u1 <= 1) {
            double u = Math.acos(u1);
            double A = Math.asin(2 * Math.sin(u) / rho_theta[0]);
            double t = Utils.M(rho_theta[1] + Math.PI / 2 + A);
            double v = Utils.M(t - phi);

            path.add(PathElement.create(t, Steering.LEFT, Gear.FORWARD));
            path.add(PathElement.create(u, Steering.RIGHT, Gear.REVERSE));
            path.add(PathElement.create(u, Steering.LEFT, Gear.REVERSE));
            path.add(PathElement.create(v, Steering.RIGHT, Gear.FORWARD));
        }

        return path;
    }

    private static List<PathElement> path8(double[] start, double[] end) {
        List<PathElement> path = new ArrayList<>();
        double phi = Utils.degToRad(end[2]);
        double xi = start[0] - Math.sin(phi);
        double eta = end[1] - 1 + Math.cos(phi);
        double[] rho_theta = Utils.R(xi, eta);

        if (rho_theta[0] >= 2) {
            double u = Math.sqrt(rho_theta[0] * rho_theta[0] - 4) - 2;
            double A = Math.atan2(2, u + 2);
            double t = Utils.M(rho_theta[1] + Math.PI / 2 + A);
            double v = Utils.M(t - phi + Math.PI / 2);

            path.add(PathElement.create(t, Steering.LEFT, Gear.FORWARD));
            path.add(PathElement.create(Math.PI / 2, Steering.RIGHT, Gear.REVERSE));
            path.add(PathElement.create(u, Steering.STRAIGHT, Gear.REVERSE));
            path.add(PathElement.create(v, Steering.LEFT, Gear.REVERSE));
        }

        return path;
    }

    private static List<PathElement> path9(double[] start, double[] end) {
        List<PathElement> path = new ArrayList<>();
        double phi = Utils.degToRad(end[2]);
        double xi = start[0] - Math.sin(phi);
        double eta = end[1] - 1 + Math.cos(phi);
        double[] rho_theta = Utils.R(xi, eta);

        if (rho_theta[0] >= 2) {
            double u = Math.sqrt(rho_theta[0] * rho_theta[0] - 4) - 2;
            double A = Math.atan2(u + 2, 2);
            double t = Utils.M(rho_theta[1] + Math.PI / 2 - A);
            double v = Utils.M(t - phi - Math.PI / 2);

            path.add(PathElement.create(t, Steering.LEFT, Gear.FORWARD));
            path.add(PathElement.create(u, Steering.STRAIGHT, Gear.FORWARD));
            path.add(PathElement.create(Math.PI / 2, Steering.RIGHT, Gear.FORWARD));
            path.add(PathElement.create(v, Steering.LEFT, Gear.REVERSE));
        }

        return path;
    }

    private static List<PathElement> path10(double[] start, double[] end) {
        List<PathElement> path = new ArrayList<>();
        double phi = Utils.degToRad(end[2]);
        double xi = start[0] + Math.sin(phi);
        double eta = end[1] - 1 - Math.cos(phi);
        double[] rho_theta = Utils.R(xi, eta);

        if (rho_theta[0] >= 2) {
            double t = Utils.M(rho_theta[1] + Math.PI / 2);
            double u = rho_theta[0] - 2;
            double v = Utils.M(phi - t - Math.PI / 2);

            path.add(PathElement.create(t, Steering.LEFT, Gear.FORWARD));
            path.add(PathElement.create(Math.PI / 2, Steering.RIGHT, Gear.REVERSE));
            path.add(PathElement.create(u, Steering.STRAIGHT, Gear.REVERSE));
            path.add(PathElement.create(v, Steering.RIGHT, Gear.REVERSE));
        }

        return path;
    }

    private static List<PathElement> path11(double[] start, double[] end) {
        List<PathElement> path = new ArrayList<>();
        double phi = Utils.degToRad(end[2]);
        double xi = start[0] + Math.sin(phi);
        double eta = end[1] - 1 - Math.cos(phi);
        double[] rho_theta = Utils.R(xi, eta);

        if (rho_theta[0] >= 2) {
            double t = Utils.M(rho_theta[1]);
            double u = rho_theta[0] - 2;
            double v = Utils.M(phi - t - Math.PI / 2);

            path.add(PathElement.create(t, Steering.LEFT, Gear.FORWARD));
            path.add(PathElement.create(u, Steering.STRAIGHT, Gear.FORWARD));
            path.add(PathElement.create(Math.PI / 2, Steering.LEFT, Gear.FORWARD));
            path.add(PathElement.create(v, Steering.RIGHT, Gear.REVERSE));
        }

        return path;
    }

    private static List<PathElement> path12(double[] start, double[] end) {
        List<PathElement> path = new ArrayList<>();
        double phi = Utils.degToRad(end[2]);
        double xi = start[0] + Math.sin(phi);
        double eta = end[1] - 1 - Math.cos(phi);
        double[] rho_theta = Utils.R(xi, eta);

        if (rho_theta[0] >= 4) {
            double u = Math.sqrt(rho_theta[0] * rho_theta[0] - 4) - 4;
            double A = Math.atan2(2, u + 4);
            double t = Utils.M(rho_theta[1] + Math.PI / 2 + A);
            double v = Utils.M(t - phi);

            path.add(PathElement.create(t, Steering.LEFT, Gear.FORWARD));
            path.add(PathElement.create(Math.PI / 2, Steering.RIGHT, Gear.REVERSE));
            path.add(PathElement.create(u, Steering.STRAIGHT, Gear.REVERSE));
            path.add(PathElement.create(Math.PI / 2, Steering.LEFT, Gear.REVERSE));
            path.add(PathElement.create(v, Steering.RIGHT, Gear.FORWARD));
        }

        return path;
    }

    public static void main(String[] args) {
        double optimalPathLength = getOptimalPathLength(new double[]{15, 15, Math.PI / 2}, new double[]{150, 150, 0}, 25);
        System.out.println(optimalPathLength);
    }
}
