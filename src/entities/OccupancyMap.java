package entities;

import java.util.Arrays;
import java.util.List;
import simulation.Consts;


public class OccupancyMap {


    private int[][] occupancyGrid; // 2D array to store obstacle information


    public OccupancyMap() {
        this.occupancyGrid = new int[Consts.GRID_SIZE][Consts.GRID_SIZE]; // Initialize grid with 0s
    }
    public OccupancyMap(List<Obstacle> obstacles) {
        this.occupancyGrid = new int[Consts.GRID_SIZE][Consts.GRID_SIZE]; // Initialize grid with 0s
    }


    public void addObstacle(int x_coord, int y_coord) {  //Not GRID coordinates
        int gridX = (int) (x_coord / Consts.CELL_SIZE_CM);
        int gridY = (int) (y_coord / Consts.CELL_SIZE_CM);
       
        if (isValid(gridX, gridY)) {
            occupancyGrid[gridX][gridY] = 1; // Mark as occupied
        }
    }


    public boolean isColliding(double x_cm, double y_cm) {
        int gridX = (int) (x_cm / Consts.CELL_SIZE_CM);
        int gridY = (int) (y_cm / Consts.CELL_SIZE_CM);
       
        return isValid(gridX, gridY) && occupancyGrid[gridX][gridY] == 1;
    }


    private boolean isValid(int x, int y) {
        return x >= 0 && x < Consts.GRID_SIZE && y >= 0 && y < Consts.GRID_SIZE;
    }


    public void printGrid() {
        for (int[] row : occupancyGrid) {
            System.out.println(Arrays.toString(row));
        }
    }


    public static void main(String[] args) {
        OccupancyMap map = new OccupancyMap();
       
        map.addObstacle(50, 50);  // Add an obstacle at (50cm, 50cm)
        map.addObstacle(100, 100); // Add an obstacle at (100cm, 100cm)


        System.out.println("Collision at (50,50): " + map.isColliding(50, 50));
        System.out.println("Collision at (20,20): " + map.isColliding(20, 20));


        map.printGrid();
    }
}
