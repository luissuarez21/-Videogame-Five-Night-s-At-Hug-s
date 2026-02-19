package core;

import tileengine.TETile;
import tileengine.Tileset;
import utils.RandomUtils;

import java.util.*;

public class WorldGenerator {

    private static final int MIN_ROOMS = 16;
    private static final int MAX_ROOMS = 23;
    private static final int MIN_ROOM_WIDTH = 4;
    private static final int MAX_ROOM_WIDTH = 10;
    private static final int MIN_ROOM_HEIGHT = 3;
    private static final int MAX_ROOM_HEIGHT = 8;
    private final int width;
    private final int height;

    private final long seed;
    private final Random random;

    private final HashSet<Room> rooms;
    private final ArrayList<Hallway> hallways;
    private final TETile[][] world;

    public WorldGenerator(int width, int height, long seed) {
        this.width = width;
        this.height = height;
        this.seed = seed;
        this.random = new Random(seed);

        this.rooms = new HashSet<>();
        this.hallways = new ArrayList<>();
        this.world = new TETile[width][height];
    }

    public TETile[][] generateWorld() {
        initializeTiles();
        generateRooms();
        connectRooms();
        drawRooms();
        drawHallways();
        addWalls();
        return this.world;
    }

    private void initializeTiles() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                this.world[x][y] = Tileset.NOTHING;
            }
        }
    }

    private void generateRooms() {
        int numRooms = RandomUtils.uniform(random, MIN_ROOMS, MAX_ROOMS);
        int attempts = 0;

        while (rooms.size() < numRooms && attempts < numRooms * MAX_ROOM_WIDTH) {
            attempts = attempts + 1;

            int w = RandomUtils.uniform(random, MIN_ROOM_WIDTH, MAX_ROOM_WIDTH);
            int h = RandomUtils.uniform(random, MIN_ROOM_HEIGHT, MAX_ROOM_HEIGHT);

            int x = RandomUtils.uniform(random, 1, this.width - w - 1);
            int y = RandomUtils.uniform(random, 1, this.height - h - 1);

            Room possibleRoom = new Room(x, y, w, h);

            if (!this.overlaps(possibleRoom)) {
                rooms.add(possibleRoom);
            }
        }
    }

    private void connectRooms() {
        if (rooms.size() < 2) {
            return;
        }

        List<Room> sortedRooms = new ArrayList<>(rooms);
        roomSorter(sortedRooms);

        for (int i = 0; i < sortedRooms.size() - 1; i++) {
            Room a = sortedRooms.get(i);
            Room b = sortedRooms.get(i + 1);

            Position centerA = a.center();
            Position centerB = b.center();

            createHallway(centerA, centerB);

        }

    }

    private void roomSorter(List<Room> list) {
        for (int i = 0; i < list.size(); i++) {

            int minimumIndex = i;
            int minimumX = list.get(i).center().getX();

            for (int j = i + 1; j < list.size(); j++) {
                Room room = list.get(j);
                int centerX = room.center().getX();

                if (centerX < minimumX) {
                    minimumX = centerX;
                    minimumIndex = j;
                }
            }

            if (minimumIndex != i) {
                Room temp = list.get(i);
                list.set(i, list.get(minimumIndex));
                list.set(minimumIndex, temp);
            }
        }
    }

    private void drawRooms() {
        for (Room room : rooms) {
            for (int x = room.getX(); x < room.getX() + room.getWidth(); x++) {
                for (int y = room.getY(); y < room.getY() + room.getHeight(); y++) {
                    this.world[x][y] = Tileset.FLOOR;
                }
            }
        }
    }

    private void drawHallways() {
        for (Hallway hallway: hallways) {
            for (Position position : hallway.tiles()) {
                if (position.getX() >= 0 && position.getX() < this.width && position.getY() >= 0
                        && position.getY() < this.height) {
                    if (this.world[position.getX()][position.getY()] == Tileset.NOTHING) {
                        this.world[position.getX()][position.getY()] = Tileset.FLOOR;
                    }
                }
            }
        }
    }

    private void addWalls() {

        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {

                if (this.world[x][y] == Tileset.NOTHING) {

                    boolean isWall = false;

                    for (int movedX = -1; movedX <= 1; movedX++) {
                        for (int movedY = -1; movedY <= 1; movedY++) {

                            int newX = x + movedX;
                            int newY = y + movedY;

                            if (newX >= 0 && newX < this.width && newY >= 0 && newY < this.height) {

                                if (this.world[newX][newY] == Tileset.FLOOR) {
                                    isWall = true;
                                }
                            }
                        }
                    }
                    if (isWall) {
                        this.world[x][y] = Tileset.WALL;
                    }
                }
            }

        }

    }

    private void createHallway(Position a, Position b) {
        Hallway hallway = new Hallway();

        int xStart = Math.min(a.getX(), b.getX());
        int xEnd = Math.max(a.getX(), b.getX());
        for (int x = xStart; x <= xEnd; x++) {
            hallway.add(new Position(x, a.getY()));
        }

        int yStart = Math.min(a.getY(), b.getY());
        int yEnd = Math.max(a.getY(), b.getY());
        for (int y = yStart; y <= yEnd; y++) {
            hallway.add(new Position(b.getX(), y));
        }

        this.hallways.add(hallway);
    }

    public TETile[][] getWorld() {
        return this.world;
    }

    private boolean overlaps(Room a) {
        for (Room b: rooms) {
            if (a.intersects(b)) {
                return true;
            }
        }
        return false;
    }
}
