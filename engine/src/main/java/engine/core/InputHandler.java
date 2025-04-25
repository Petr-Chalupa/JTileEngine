package engine.core;

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
			if (!keyPressedCallbacks.containsKey(code)) return;
			for (Consumer<KeyEvent> callback : keyPressedCallbacks.get(code)) {
				callback.accept(event);
			}
		});
		scene.setOnKeyReleased(event -> {
			KeyCode code = event.getCode();
			pressedKeys.remove(code);
			if (!keyReleasedCallbacks.containsKey(code)) return;
			for (Consumer<KeyEvent> callback : keyReleasedCallbacks.get(code)) {
				callback.accept(event);
			}
		});
		scene.setOnMousePressed(event -> {
			pressedButtons.add(event.getButton());
			if (!mousePressedCallbacks.containsKey(event.getButton())) return;
			for (Consumer<MouseEvent> callback : mousePressedCallbacks.get(event.getButton())) {
				callback.accept(event);
			}
		});
		scene.setOnMouseReleased(event -> {
			pressedButtons.remove(event.getButton());
			if (!mouseReleasedCallbacks.containsKey(event.getButton())) return;
			for (Consumer<MouseEvent> callback : mouseReleasedCallbacks.get(event.getButton())) {
				callback.accept(event);
			}
		});
		scene.setOnScroll(event -> {
			for (Consumer<ScrollEvent> callback : mouseScrollCallbacks) {
				callback.accept(event);
			}
		});
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