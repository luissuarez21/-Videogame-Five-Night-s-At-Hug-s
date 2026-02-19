package core;

public class Player {

    private Position playerPosition;

    public Player(Position startPoint) {
        playerPosition = startPoint;
    }

    public Position getPlayerPosition() {
        return playerPosition;
    }

    public void setPlayerPosition(Position playerPosition) {
        this.playerPosition = playerPosition;
    }
    public  Position movePosition(int x, int y) {
        return new Position(playerPosition.getX() + x, playerPosition.getY() + y);
    }
}
