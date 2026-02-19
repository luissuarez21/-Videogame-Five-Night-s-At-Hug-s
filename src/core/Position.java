package core;

public class Position {
    private final int x;
    private final int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean same(Position other) {
        return other != null && this.getX() == other.getX() && this.getY() == other.getY();
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public double distance(Position other) {
        int diffX = this.x - other.x;
        int diffY = this.y - other.y;
        return Math.sqrt(Math.pow(diffX, 2) + Math.pow(diffY, 2));
    }

    public String toString() {
        return "(" + this.x + ", " + this.y + ")";
    }
}
