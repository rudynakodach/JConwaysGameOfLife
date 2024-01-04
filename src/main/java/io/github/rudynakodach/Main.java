package io.github.rudynakodach;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static final int SIZE = 50;
    public static long liveCells = 0;
    public static long deadCells = SIZE * SIZE - liveCells;
    public static long DELAY = 100;
    public static boolean gameStarted = false;
    private static GUI gui;
    public static Boolean[][] map;
    public static void printMap(Boolean[][] map) {
        String[][] array = new String[map.length][map.length];

        for (int i = 0; i < array.length; i++) {
            array[i] = Arrays.stream(map[i]).map(o -> o ? "T" : "F").toList().toArray(new String[0]);
        }

        int rows = array.length;
        int cols = array[0].length;

        // Print column headers
        System.out.print("   ");
        for (int i = 0; i < cols; i++) {
            System.out.print(i + " | ");
        }
        System.out.println();


        // Print array elements with row headers
        for (int i = 0; i < rows; i++) {
            System.out.print(i + ". ");
            for (int j = 0; j < cols; j++) {
                System.out.print(array[i][j] + " | ");
            }
            System.out.println();

//            // Print separator line after each row
//            for (int j = 0; j < cols; j++) {
//                System.out.print("----");
//            }
        }
    }

    public static List<Point> getNeighbouringCellsNotEqualTo(Boolean[][] map, Point p, Boolean value, boolean includeCurrent) {
        List<Point> points = new ArrayList<>();

        for (int y = Math.max(0, (int)p.getY() - 1); y <= Math.min(map.length - 1, p.getY() + 1); y++) {
            for (int x = Math.max(0, (int)p.getX() - 1); x <= Math.min(map[y].length - 1, p.getX() + 1); x++) {
                if(!includeCurrent && x == p.getX() && y == p.getY()) {
                    continue;
                }
                else if(map[y][x] == value) {
                    continue;
                }

                points.add(new Point(x, y));
            }
        }

        return points;
    }

    public static void drawMap(Boolean[][] map, Graphics g) {
        g.clearRect(0, 0, 500, 500);

        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                int cellWidth = 500 / map.length;
                int cellHeight = 500 / map.length;

                // Use fillRect to draw filled rectangles
                if(map[y][x]) {
                    g.fillRect(cellWidth * x, cellHeight * y, cellWidth, cellHeight);
                } else {
                    g.drawRect(cellWidth * x, cellHeight * y, cellWidth, cellHeight);
                }
            }
        }

    }

    public static void main(String[] args) throws InterruptedException {
        gui = new GUI();

        map = new Boolean[SIZE][SIZE];
        for(Boolean[] barr : map) {
            Arrays.fill(barr, false);
        }

        for(Point p : getNeighbouringCellsNotEqualTo(map, new Point(3, 3), null, true)) {
            map[(int)p.getY()][(int)p.getX()] = true;
        }
        for(Point p : getNeighbouringCellsNotEqualTo(map, new Point(3, 5), null, false)) {
            map[(int)p.getY()][(int)p.getX()] = true;
        }

    }

    public static long generation = 0;
    public static boolean scheduledThreadStop = false;
    public static void gameLoop() throws InterruptedException {
        while(true) {
            Boolean[][] newMap = new Boolean[map.length][map[0].length];
            for (int i = 0; i < map.length; i++) {
                newMap[i] = Arrays.copyOf(map[i], map[i].length);
            }
            for (int y = 0; y < map.length; y++) {
                for (int x = 0; x < map[y].length; x++) {
                    List<Point> liveNeighbors = getNeighbouringCellsNotEqualTo(map, new Point(x, y), false, false);

                    if (map[y][x]) { //live cell
                        if (liveNeighbors.size() < 2 || liveNeighbors.size() > 3) {
                            //System.out.printf("Killing cell @ %d:%d due to over/underpopulation (%d alive neighbors)%n", y,x, liveNeighbors.size());
                            if(newMap[y][x]) {
                                liveCells--;
                            }
                            newMap[y][x] = false;
                        }
                    } else {
                        if (liveNeighbors.size() == 3) {
                            if(!newMap[y][x]) {
                                liveCells++;
                            }
                            newMap[y][x] = true;
                        }
                    }

                }
            }

            deadCells = SIZE * SIZE - liveCells;

            map = newMap;
            Thread.sleep(DELAY);
            generation++;
            gui.drawMap(map);
            gui.generationLabel.setText("Generation: %d".formatted(generation));
            gui.liveCellsLabel.setText("Live cells: %d".formatted(liveCells));
            gui.deadCellsLabel.setText("Dead cells: %d".formatted(deadCells));
            if(scheduledThreadStop) {
                scheduledThreadStop = false;
                break;
            }
        }
    }
}
