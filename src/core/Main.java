package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;
import utils.FileUtils;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static final int WIDTH = 80;
    private static final int HEIGHT = 40;
    private static final int HUD_HEIGHT = 3;
    private static final String SAVE_FILE = "save.txt";

    private TERenderer ter;
    private World world;
    private long seed;
    private boolean lampOn = true;
    private int lampRadius = 6;
    private boolean showPath = false;
    private boolean useCustomAvatar = false;

    public static void main(String[] args) {
        Main m = new Main();
        m.interactWithKeyboard();
    }

    public Main() {
        ter = new TERenderer();
    }

    public static void showWinScreen() {
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setPenColor(StdDraw.GREEN);

        StdDraw.text(WIDTH / 2.0, HEIGHT / 2.0 + 2, "YOU ESCAPED!");
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text(WIDTH / 2.0, HEIGHT / 2.0 - 1, "Press M to Return to Menu");
        StdDraw.text(WIDTH / 2.0, HEIGHT / 2.0 - 3, "Press Q to Quit");

        StdDraw.show();

        while (StdDraw.hasNextKeyTyped()) {
            StdDraw.nextKeyTyped();
        }

        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                if (c == 'q' || c == 'Q') {
                    System.exit(0);
                }

                if (c == 'm' || c == 'M') {
                    while (StdDraw.hasNextKeyTyped()) {
                        StdDraw.nextKeyTyped();
                    }
                    return;
                }
            }
        }
    }

    private void interactWithKeyboard() {
        StdDraw.enableDoubleBuffering();
        ter.initialize(WIDTH, HEIGHT + HUD_HEIGHT);

        showMenuLoop();
    }

    private void showMenuLoop() {

        while (true) {

            while (StdDraw.hasNextKeyTyped()) {
                StdDraw.nextKeyTyped();
            }
            world = null;
            lampOn = true;

            drawMainMenu();
            char c = waitForKey();

            if (c == 'n' || c == 'N') {
                seed = promptForSeed();
                startNewGame(seed);
                gameLoop();
                continue;
            }


            if (c == 'l' || c == 'L') {
                if (loadGame()) {
                    gameLoop();
                    continue;
                }
            }

            if (c == 'c' || c == 'C') {
                useCustomAvatar = !useCustomAvatar;

                while (StdDraw.hasNextKeyTyped()) {
                    StdDraw.nextKeyTyped();
                }
                continue;
            }

            if (c == 'q' || c == 'Q') {
                System.exit(0);
            }
        }
    }

    private void drawMainMenu() {
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setPenColor(StdDraw.WHITE);

        StdDraw.text(WIDTH / 2.0, HEIGHT / 2.0 + 5, "Five Night's At Hug's");
        StdDraw.text(WIDTH / 2.0, HEIGHT / 2.0, "New Game (N)");
        StdDraw.text(WIDTH / 2.0, HEIGHT / 2.0 - 2, "Load Game (L)");
        StdDraw.text(WIDTH / 2.0, HEIGHT / 2.0 - 4, "Quit (Q)");
        StdDraw.text(WIDTH / 2.0, HEIGHT / 2.0 - 6, "Custom Avatar (C)");

        StdDraw.show();
    }

    private char waitForKey() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                return StdDraw.nextKeyTyped();
            }
        }
    }

    private long promptForSeed() {
        StringBuilder digits = new StringBuilder();

        while (true) {
            drawSeedScreen(digits.toString());
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                if (Character.isDigit(c)) {
                    digits.append(c);
                } else if (c == 's' || c == 'S') {
                    if (digits.isEmpty()) {
                    } else {
                        return Long.parseLong(digits.toString());
                    }
                }
            }
        }

    }

    private void drawSeedScreen(String seedSoFar) {

        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setPenColor(StdDraw.WHITE);

        StdDraw.text(WIDTH / 2.0, HEIGHT / 2.0 + 3, "Enter Seed (numbers only)");
        StdDraw.text(WIDTH / 2.0, HEIGHT / 2.0, seedSoFar);
        StdDraw.text(WIDTH / 2.0, HEIGHT / 2.0 - 3, "Press S to start");

        StdDraw.show();
    }

    private void renderFrame() {

        StdDraw.clear(StdDraw.BLACK);
        TETile[][] base = world.getTiles();
        TETile[][] frame;

        if (lampOn) {
            frame = applyLamp(base, world.getPlayerPosition(), lampRadius);
        } else {
            frame = world.getTiles();
        }

        if (showPath) {
            for (Position position : world.getLastPath()) {
                if (world.inBounds(position.getX(), position.getY())) {
                    frame[position.getX()][position.getY()] = Tileset.PATH;
                }
            }
        }

        ter.renderFrame(frame);
        drawHUD(frame);
        StdDraw.show();

    }

    private void startNewGame(long seed) {
        this.seed = seed;
        world = null;
        lampOn = true;

        WorldGenerator worldGenerator = new WorldGenerator(WIDTH, HEIGHT, seed);
        TETile[][] grid = worldGenerator.generateWorld();
        world = new World(grid, seed);

        world.setUseCustomAvatar(useCustomAvatar);
        world.addHug(new Hug(world.randomFloorTile()));

        Position exitPosition = world.randomFloorTile();
        grid[exitPosition.getX()][exitPosition.getY()] = Tileset.EXIT;

    }

    private void gameLoop() {

        long lastMoveTime = System.currentTimeMillis();
        final long HUG_MOVE_DELAY = 300;

        while (true) {
            renderFrame();

            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();

                if (c == 'p' || c == 'P') {
                    showPath = !showPath;
                }
                if (c == 'w' || c == 'W') {
                    world.movePlayer(0, 1);
                } else if (c == 'a' || c == 'A') {
                    world.movePlayer(-1, 0);
                } else if (c == 's' || c == 'S') {
                    world.movePlayer(0, -1);
                } else if (c == 'd' || c == 'D') {
                    world.movePlayer(1, 0);
                } else if (c == 'l' || c == 'L') {
                    lampOn = !lampOn;
                } else if (c == ':') {
                    while (!StdDraw.hasNextKeyTyped()) {
                    }
                    char next = StdDraw.nextKeyTyped();
                    if (next == 'q' || next == 'Q') {
                        saveGame();
                        System.exit(0);
                    }
                }
            }

            long now = System.currentTimeMillis();
            if (now - lastMoveTime >= HUG_MOVE_DELAY) {
                if (world.moveHugs()) {
                    showGameOver();
                    return;
                }
                lastMoveTime = now;
            }

            StdDraw.pause(10);
        }
    }


    private void drawHUD(TETile[][] frame) {
        StdDraw.setPenColor(StdDraw.WHITE);

        String tileDescription = "nothing";

        double X = StdDraw.mouseX();
        double Y = StdDraw.mouseY();

        int mouseX = Double.valueOf(X).intValue();
        int mouseY = Double.valueOf(Y).intValue();

        if (mouseX >= 0 && mouseX < frame.length && mouseY >= 0 && mouseY < frame[0].length) {
            TETile tile = frame[mouseX][mouseY];
            if (tile != null) {
                tileDescription = tile.description();
            }
        }

        String lampStatus;
        if (lampOn) {
            lampStatus = "ON";
        } else {
            lampStatus = "OFF";
        }

        StdDraw.textLeft(1, HEIGHT + HUD_HEIGHT - 2, "Tile: " + tileDescription);
        StdDraw.textLeft(1, HEIGHT + HUD_HEIGHT - 1, "Lamp: " + lampStatus);
        StdDraw.textLeft(20, HEIGHT + HUD_HEIGHT - 1, "P = Toggle Path");

    }

    private TETile[][] applyLamp(TETile[][]grid, Position center, int radius) {

        int w = grid.length;
        int h = grid[0].length;

        TETile[][] lit = new TETile[w][h];

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int tileX = x - center.getX();
                int tileY = y - center.getY();
                int dist = tileX * tileX + tileY * tileY;

                if (dist <= radius * radius) {
                    lit[x][y] = grid[x][y];
                } else {
                    lit[x][y] = Tileset.NOTHING;
                }
            }
        }
        return lit;

    }

    private void saveGame() {
        String data = seed + ";" + world.save();
        FileUtils.writeFile(SAVE_FILE, data);
    }

    private boolean loadGame() {
        String data = FileUtils.readFile(SAVE_FILE);
        lampOn = true;
        if (data == null) {
            return false;
        }

        int index = data.indexOf(';');
        seed = Long.parseLong(data.substring(0, index));

        String worldData = data.substring(index + 1);
        world = World.load(worldData, seed);

        world.setUseCustomAvatar(useCustomAvatar);
        return true;
    }

    private void showGameOver() {
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setPenColor(StdDraw.RED);

        StdDraw.text(WIDTH / 2.0, HEIGHT / 2.0 + 2, "GAME OVER");
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text(WIDTH / 2.0, HEIGHT / 2.0 - 1, "Press Q to Quit");
        StdDraw.text(WIDTH / 2.0, HEIGHT / 2.0 - 3, "Press M for Main Menu");

        StdDraw.show();
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                if (c == 'q' || c == 'Q') {
                    System.exit(0);
                }
                if (c == 'm' || c == 'M') {
                    StdDraw.clear();
                    StdDraw.show();

                    while (StdDraw.hasNextKeyTyped()) {
                        StdDraw.nextKeyTyped();
                    }

                    return;
                }
            }
        }

    }




}
