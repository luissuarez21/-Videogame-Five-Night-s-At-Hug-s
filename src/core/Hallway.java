package core;

import java.util.ArrayList;
import java.util.List;

public class Hallway {

    private final List<Position> path;

    public Hallway() {
        this.path = new ArrayList<>();
    }

    public void add(Position p) {
        path.add(p);
    }

    public List<Position> tiles() {
        return path;
    }

}
