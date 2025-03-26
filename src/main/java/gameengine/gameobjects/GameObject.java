package gameengine.gameobjects;

import javafx.scene.Node;

public class GameObject {
    protected Node self;
    public boolean rendered = false;
    public boolean solid;

    public Node getSelf() {
        return self;
    }

    public double getPosX() {
        return self.getLayoutX();
    }

    public double getPosY() {
        return self.getLayoutY();
    }

    public void moveX(double dist) {
        self.setTranslateX(dist);
    }

    public void moveY(double dist) {
        self.setTranslateY(dist);
    }
}
