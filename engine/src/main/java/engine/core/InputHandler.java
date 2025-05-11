package engine.core;

import engine.Engine;
import javafx.scene.Scene;
import javafx.scene.input.*;

import java.util.*;
import java.util.function.Consumer;

public class InputHandler {
	private final Set<KeyCode> pressedKeys = new HashSet<>();
	private final Set<MouseButton> pressedButtons = new HashSet<>();
	private final Map<KeyCode, List<Consumer<KeyEvent>>> keyPressedCallbacks = new HashMap<>();
	private final Map<KeyCode, List<Consumer<KeyEvent>>> keyReleasedCallbacks = new HashMap<>();
	private final Map<MouseButton, List<Consumer<MouseEvent>>> mousePressedCallbacks = new HashMap<>();
	private final Map<MouseButton, List<Consumer<MouseEvent>>> mouseReleasedCallbacks = new HashMap<>();
	private final List<Consumer<ScrollEvent>> mouseScrollCallbacks = new ArrayList<>();

	public InputHandler(Scene scene) {
		scene.setOnKeyPressed(event -> {
			KeyCode code = event.getCode();
			pressedKeys.add(code);
			executeCallbacks(keyPressedCallbacks.get(code), event);
		});
		scene.setOnKeyReleased(event -> {
			KeyCode code = event.getCode();
			pressedKeys.remove(code);
			executeCallbacks(keyReleasedCallbacks.get(code), event);
		});
		scene.setOnMousePressed(event -> {
			pressedButtons.add(event.getButton());
			executeCallbacks(mousePressedCallbacks.get(event.getButton()), event);
		});
		scene.setOnMouseReleased(event -> {
			pressedButtons.remove(event.getButton());
			executeCallbacks(mouseReleasedCallbacks.get(event.getButton()), event);
		});
		scene.setOnScroll(event -> {
			executeCallbacks(mouseScrollCallbacks, event);
		});
	}

	private <T> void executeCallbacks(List<Consumer<T>> callbacks, T event) {
		if (callbacks == null || Engine.getInstance().isPaused()) return;
		for (Consumer<T> callback : callbacks) {
			callback.accept(event);
		}
	}

	public void bindKeyPressed(KeyCode code, Consumer<KeyEvent> callback) {
		keyPressedCallbacks.computeIfAbsent(code, k -> new ArrayList<>()).add(callback);
	}

	public void bindKeyReleased(KeyCode code, Consumer<KeyEvent> callback) {
		keyReleasedCallbacks.computeIfAbsent(code, k -> new ArrayList<>()).add(callback);
	}

	public void bindMousePressed(MouseButton button, Consumer<MouseEvent> callback) {
		mousePressedCallbacks.computeIfAbsent(button, k -> new ArrayList<>()).add(callback);
	}

	public void bindMouseReleased(MouseButton button, Consumer<MouseEvent> callback) {
		mouseReleasedCallbacks.computeIfAbsent(button, k -> new ArrayList<>()).add(callback);
	}

	public void addMouseScrollCallback(Consumer<ScrollEvent> callback) {
		mouseScrollCallbacks.add(callback);
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
}