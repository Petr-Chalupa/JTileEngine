package engine.core;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class InputHandler {
    private final Set<KeyCode> pressedKeys = new HashSet<>();
    private Consumer<KeyEvent> pressedCallback;
    private Consumer<KeyEvent> releasedCallback;

    public InputHandler(Scene scene) {
        scene.setOnKeyPressed(event -> {
            pressedKeys.add(event.getCode());
            if (pressedCallback != null) pressedCallback.accept(event);
        });
        scene.setOnKeyReleased(event -> {
            pressedKeys.remove(event.getCode());
            if (releasedCallback != null) releasedCallback.accept(event);
        });
    }

    public boolean isKeyPressed(KeyCode key) {
        return pressedKeys.contains(key);
    }

    public void clearKeys() {
        pressedKeys.clear();
    }

    public void setPressedCallback(Consumer<KeyEvent> pressedCallback) {
        this.pressedCallback = pressedCallback;
    }

    public void setReleasedCallback(Consumer<KeyEvent> releasedCallback) {
        this.releasedCallback = releasedCallback;
    }
}