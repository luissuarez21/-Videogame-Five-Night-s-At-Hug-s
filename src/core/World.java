package core;

import tileengine.TETile;
import tileengine.Tileset;
import utils.RandomUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class World {

    private final TETile[][] tiles;
    private Player player;
    private List<Hug> hugs;
    private Random random;
    private List<Position> lastPath = new ArrayList<>();
    private boolean useCustomAvatar = false;

    public World(TETile[][] tiles, long seed) {
        this.tiles = tiles;
        this.hugs = new ArrayList<>();
        this.random = new Random(seed);

        Position start = findStartPosition();
        this.player = new Player(start);

        if (inBounds(start.getX(), start.getY())) {
            updateAvatarTile();
        }
    }

    public TETile[][] getTiles() {
        return tiles;
    }

    public Position getPlayerPosition() {
        return player.getPlayerPosition();
    }

    public void setUseCustomAvatar(boolean b) {
        useCustomAvatar = b;
        updateAvatarTile();
    }

    public boolean getUseCustomAvatar() {
        return useCustomAvatar;
    }

    public void setPlayer(Player p) {
        this.player = p;
    }

    public Player getPlayer() {
        return this.player;
    }


    public List<Position> getLastPath() {
        return lastPath;
    }

    public void movePlayer(int destX, int destY) {
        if (player == null) {
            return;
        }

        Position current = player.getPlayerPosition();
        int newX = current.getX() + destX;
        int newY = current.getY() + destY;

        if (!inBounds(newX, newY)) {
            return;
        }

        TETile dest = tiles[newX][newY];

        if(dest == Tileset.WALL || dest == Tileset.NOTHING) {
            return;
        }

        if (dest == Tileset.EXIT) {
            Main.showWinScreen();
            return;
        }

        tiles[current.getX()][current.getY()] = Tileset.FLOOR;

        if (useCustomAvatar) {
            tiles[newX][newY] = Tileset.AVATAR2;
        } else {
            tiles[newX][newY] = Tileset.AVATAR;
        }
        player.setPlayerPosition(new Position(newX, newY));

    }

    public void forcePlayerPosition(Position p) {
        if (player != null) {
            Position current = player.getPlayerPosition();
            if (inBounds(current.getX(), current.getY())) {
                tiles[current.getX()][current.getY()] = Tileset.FLOOR;
            }
        }

        if (inBounds(p.getX(), p.getY())) {
            this.player = new Player(p);
            updateAvatarTile();
        }
    }

    public TETile getTile(int x, int y) {
        return tiles[x][y];
    }

    public List<Hug> getHugs() {
        return this.hugs;
    }


    public void setTile(int x, int y, TETile t) {
        this.tiles[x][y] = t;
    }

    public void addHug(Hug hug) {
        this.hugs.add(hug);
        Position p = hug.getHugPosition();
        if (inBounds(p.getX(), p.getY())) {
            tiles[p.getX()][p.getY()] = Tileset.CELL;
        }
    }

    public boolean inBounds(int x, int y) {
        return x >= 0 && x < tiles.length && y >= 0 && y < tiles[0].length;
    }

    private Position findStartPosition() {
        int width = tiles.length;
        int height = tiles[0].length;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (tiles[x][y] == Tileset.FLOOR) {
                    return new Position(x, y);
                }
            }
        }
        return new Position(0, 0);
    }

    public String save() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(player.getPlayerPosition().getX()).append(",");
        stringBuilder.append(player.getPlayerPosition().getY()).append(";");

        stringBuilder.append(hugs.size()).append(";");
        for (Hug hug : hugs) {
            stringBuilder.append(hug.getHugPosition().getX()).append(",");
            stringBuilder.append(hug.getHugPosition().getY()).append(";");
        }

        int width = tiles.length;
        int height = tiles[0].length;
        stringBuilder.append(width).append(",").append(height).append(";");

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                stringBuilder.append(tiles[x][y].id());
                if (!(x == width - 1 && y == height - 1)) {
                    stringBuilder.append(",");
                }
            }
        }

        return stringBuilder.toString();
    }

    public static World load(String data, long seed) {
        String[] parts = data.split(";");

        String[] players = parts[0].split(",");
        int playerX = Integer.parseInt(players[0]);
        int playerY = Integer.parseInt(players[1]);

        int hugCount = Integer.parseInt(parts[1]);
        int index = 2;

        List<Hug> hugList = new ArrayList<>();

        for (int i = 0; i < hugCount; i++) {
            String[] hugs = parts[index++].split(",");
            int hugX = Integer.parseInt(hugs[0]);
            int hugY = Integer.parseInt(hugs[1]);
            hugList.add(new Hug(new Position(hugX, hugY)));
        }

        String[] widthHeight = parts[index++].split(",");
        int width = Integer.parseInt(widthHeight[0]);
        int height = Integer.parseInt(widthHeight[1]);

        TETile[][] grid = new TETile[width][height];
        String[] tileIDs = parts[index].split(",");

        int k = 0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int id = Integer.parseInt(tileIDs[k++]);
                grid[x][y] = tileFromID(id);
            }
        }
        World world = new World(grid, seed);
        world.forcePlayerPosition(new Position(playerX, playerY));

        for (Hug hug : hugList) {
            world.addHug(hug);
        }

        return world;
    }

    private static TETile tileFromID(int id) {
        if (id == Tileset.AVATAR.id()) {
            return Tileset.AVATAR;
        } else if (id == Tileset.FLOOR.id()) {
            return Tileset.FLOOR;
        } else if (id == Tileset.WALL.id()) {
            return Tileset.WALL;
        } else if (id == Tileset.NOTHING.id()) {
            return Tileset.NOTHING;
        } else if (id == Tileset.EXIT.id()) {
            return Tileset.EXIT;
        } else if (id == Tileset.PATH.id()) {
            return Tileset.PATH;
        } else if (id == Tileset.AVATAR2.id()) {
            return Tileset.AVATAR2;
        }
        return Tileset.NOTHING;
    }

    public boolean moveHugs() {
        if (player == null || hugs == null || hugs.isEmpty()) {
            return false;
        }

        Hug hug = hugs.get(0);
        Position start = hug.getHugPosition();
        Position goal = player.getPlayerPosition();
        int w = tiles.length;
        int h = tiles[0].length;

        boolean[][] visited = new boolean[w][h];
        Position[][] prev = new Position[w][h];

        ArrayList<Position> queue = new ArrayList<>();
        queue.add(start);
        visited[start.getX()][start.getY()] = true;

        int index = 0;
        boolean found = false;

        int[] diffx = {1, -1, 0, 0};
        int[] diffy = {0, 0, 1, -1};

        while (index < queue.size()) {
            Position currently = queue.get(index++);
            if (currently.same(goal)) {
                found = true;
                break;
            }

            for (int i = 0; i < 4; i++) {
                int newx = currently.getX() + diffx[i];
                int newy = currently.getY() + diffy[i];

                if (!inBounds(newx, newy)) {
                    continue;
                }

                if (visited[newx][newy]) {
                    continue;
                }

                if (tiles[newx][newy] == Tileset.WALL) {
                    continue;
                }

                visited[newx][newy] = true;
                prev[newx][newy] = currently;
                queue.add(new Position(newx, newy));
            }
        }

        if (!found) {
            lastPath = new ArrayList<>();
            return false;
        }

        ArrayList<Position> path = new ArrayList<>();
        Position step = goal;

        while (!step.same(start)) {
            path.add(step);
            step = prev[step.getX()][step.getY()];
            if (step == null) {
                break;
            }
        }

        lastPath = path;
        if (!path.isEmpty()) {
            Position nextStep = path.get(path.size() - 1);
            if (nextStep.same(goal)) {
                return true;
            }

            tiles[start.getX()][start.getY()] = Tileset.FLOOR;

            hug.setHugPosition(nextStep);
            tiles[nextStep.getX()][nextStep.getY()] = Tileset.CELL;
        }
        return false;
    }

    public Position randomFloorTile() {
        int width = tiles.length;
        int height = tiles[0].length;

        while (true) {
            int x = RandomUtils.uniform(random, width);
            int y = RandomUtils.uniform(random, height);

            if (tiles[x][y] == Tileset.FLOOR && !player.getPlayerPosition().same(new Position(x, y))) {
                return new Position(x, y);
            }
        }

    }

    public void updateAvatarTile() {
        if (player == null) {
            return;
        }

        Position p = player.getPlayerPosition();
        if (!inBounds(p.getX(), p.getY())) {
            return;
        }

        if (useCustomAvatar) {
            tiles[p.getX()][p.getY()] = Tileset.AVATAR2;
        } else {
            tiles[p.getX()][p.getY()] = Tileset.AVATAR;
        }
    }

    public boolean isOnExit() {
        Position p = player.getPlayerPosition();
        return tiles[p.getX()][p.getY()] == Tileset.EXIT;
    }

    private List<Position> reconstructPath(Position[][] prev, int pathx, int pathy) {
        List<Position> path = new ArrayList<>();
        Position currently = prev[pathx][pathy];

        while (currently != null) {
            path.add(currently);
            currently = prev[currently.getX()][currently.getY()];
        }
        return path;
    }

}
