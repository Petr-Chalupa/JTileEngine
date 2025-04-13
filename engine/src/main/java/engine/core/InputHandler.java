package engine.core;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class InputHandler {
    private final Set<KeyCode> pressedKeys = new HashSet<>();
    private final Set<MouseButton> pressedButtons = new HashSet<>();
    private List<Consumer<KeyEvent>> pressedCallbacks = new ArrayList<>();
    private List<Consumer<KeyEvent>> releasedCallbacks = new ArrayList<>();
    private List<Consumer<MouseEvent>> mousePressedCallbacks = new ArrayList<>();
    private List<Consumer<MouseEvent>> mouseReleasedCallbacks = new ArrayList<>();
    private List<Consumer<ScrollEvent>> mouseScrollCallbacks = new ArrayList<>();

    public InputHandler(Scene scene) {
        // Keyboard
        scene.setOnKeyPressed(event -> {
            pressedKeys.add(event.getCode());
            if (pressedCallbacks.size() > 0) pressedCallbacks.forEach(cb -> cb.accept(event));
        });
        scene.setOnKeyReleased(event -> {
            pressedKeys.remove(event.getCode());
            if (releasedCallbacks.size() > 0) releasedCallbacks.forEach(cb -> cb.accept(event));
        });

        // Mouse
        scene.setOnMousePressed(event -> {
            pressedButtons.add(event.getButton());
            if (mousePressedCallbacks.size() > 0) mousePressedCallbacks.forEach(cb -> cb.accept(event));
        });
        scene.setOnMouseReleased(event -> {
            pressedButtons.remove(event.getButton());
            if (mouseReleasedCallbacks.size() > 0) mouseReleasedCallbacks.forEach(cb -> cb.accept(event));
        });
        scene.setOnScroll(event -> {
            if (mouseScrollCallbacks.size() > 0) mouseScrollCallbacks.forEach(cb -> cb.accept(event));
        });
    }

    public boolean isKeyPressed(KeyCode key) {
        return pressedKeys.contains(key);
    }

    public boolean isMouseButtonPressed(MouseButton button) {
        return pressedButtons.contains(button);
    }

    public void clear() {
        pressedKeys.clear();
        pressedButtons.clear();
    }

    public void addPressedCallback(Consumer<KeyEvent> pressedCallback) {
        this.pressedCallbacks.add(pressedCallback);
    }

    public void addReleasedCallback(Consumer<KeyEvent> releasedCallback) {
        this.releasedCallbacks.add(releasedCallback);
    }

    public void addMousePressedCallback(Consumer<MouseEvent> callback) {
        this.mousePressedCallbacks.add(callback);
    }

    public void addMouseReleasedCallback(Consumer<MouseEvent> callback) {
        this.mouseReleasedCallbacks.add(callback);
    }

    public void addMouseScrollCallback(Consumer<ScrollEvent> callback) {
        this.mouseScrollCallbacks.add(callback);
    }

}