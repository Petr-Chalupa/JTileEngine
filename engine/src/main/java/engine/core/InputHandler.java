package engine.core;

import javafx.scene.Scene;
import javafx.scene.input.*;

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

    public int getDigit(KeyCode code) {
        int digit;
        switch (code) {
            case KeyCode.DIGIT0 -> digit = 0;
            case KeyCode.DIGIT1 -> digit = 1;
            case KeyCode.DIGIT2 -> digit = 2;
            case KeyCode.DIGIT3 -> digit = 3;
            case KeyCode.DIGIT4 -> digit = 4;
            case KeyCode.DIGIT5 -> digit = 5;
            case KeyCode.DIGIT6 -> digit = 6;
            case KeyCode.DIGIT7 -> digit = 7;
            case KeyCode.DIGIT8 -> digit = 8;
            case KeyCode.DIGIT9 -> digit = 9;
            default -> digit = -1; // Not a valid digit key
        }
        return digit;
    }

}