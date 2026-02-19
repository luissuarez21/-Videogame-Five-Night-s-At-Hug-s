package core;

public class Room {

    private final int x;
    private final int y;
    private final int width;
    private final int height;

    public Room(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public boolean intersects(Room other) {
        return !(this.x + width <= other.x || other.x + other.width <= x || y + height <= other.y
                || other.y + other.height <= y);
    }

    public Position center() {
        int centerX = this.x + this.width / 2;
        int centerY = this.y + this.height / 2;
        return new Position(centerX, centerY);
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}
