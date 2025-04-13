package engine.core;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class InputHandler {
    private final Set<KeyCode> pressedKeys = new HashSet<>();
    private List<Consumer<KeyEvent>> pressedCallbacks = new ArrayList<>();
    private List<Consumer<KeyEvent>> releasedCallbacks = new ArrayList<>();

    public InputHandler(Scene scene) {
        scene.setOnKeyPressed(event -> {
            pressedKeys.add(event.getCode());
            if (pressedCallbacks.size() > 0) pressedCallbacks.forEach(cb -> cb.accept(event));
        });
        scene.setOnKeyReleased(event -> {
            pressedKeys.remove(event.getCode());
            if (releasedCallbacks.size() > 0) releasedCallbacks.forEach(cb -> cb.accept(event));
        });
    }

    public boolean isKeyPressed(KeyCode key) {
        return pressedKeys.contains(key);
    }

    public void clearKeys() {
        pressedKeys.clear();
    }

    public void addPressedCallback(Consumer<KeyEvent> pressedCallback) {
        this.pressedCallbacks.add(pressedCallback);
    }

    public void addReleasedCallback(Consumer<KeyEvent> releasedCallback) {
        this.releasedCallbacks.add(releasedCallback);
    }
}