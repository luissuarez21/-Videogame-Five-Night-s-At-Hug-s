package core;

import tileengine.TETile;
import tileengine.Tileset;

public class Hug {
    private Position hugPosition;
    public Hug(Position hugStart) {
        this.hugPosition = hugStart;
    }

    public Position getHugPosition() {
        return hugPosition;
    }

    public void setHugPosition(Position nextStep) {
        this.hugPosition = nextStep;
    }

}

