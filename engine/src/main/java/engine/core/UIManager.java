package engine.core;

import engine.ui.UIComponent;
import engine.ui.UIRegion;
import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class UIManager {
	List<UIComponent> components = new ArrayList<>();
	private static UIManager instance;

	private UIManager() {
		GameStateManager.getInstance().addListener((GameStateManager.GameState newState) -> {
			if (newState == GameStateManager.GameState.GAME_OVER) clearComponents();
		});
	}

	public static UIManager getInstance() {
		if (instance == null) instance = new UIManager();
		return instance;
	}

	public List<UIComponent> getComponents() {
		return components;
	}

	public void clearComponents() {
		components.clear();
	}

	public void addComponent(UIComponent component) {
		components.add(component);
	}

	public void removeComponent(UIComponent component) {
		components.remove(component);
	}

	private HashMap<UIRegion, Bounds> calculateRegionsBounds(Canvas canvas) {
		HashMap<UIRegion, Bounds> bounds = new HashMap<>();
		for (UIRegion region : UIRegion.values()) {
			Bounds regionBounds = region.getBounds(canvas.getWidth(), canvas.getHeight());
			bounds.put(region, regionBounds);
		}
		return bounds;
	}

	public void render(GraphicsContext context, Canvas canvas) {
		HashMap<UIRegion, Bounds> regionsBounds = calculateRegionsBounds(canvas);
		drawRegionBounds(context, canvas, true);

		// Sort components based on the layer
		List<UIComponent> sortedComponents = components
				.stream()
				.sorted(Comparator.comparingInt(UIComponent::getLayer))
				.toList();

		for (UIComponent component : sortedComponents) {
			if (!component.getParent().isRendered()) continue;

			Bounds componentBounds = component.calculateBounds(regionsBounds.get(component.getRegion()));

			context.save();
			context.translate(componentBounds.getMinX(), componentBounds.getMinY());
			component.render(context, componentBounds.getWidth(), componentBounds.getHeight());
			context.restore(); // Reset
		}
	}

	public void drawRegionBounds(GraphicsContext context, Canvas canvas, boolean drawLabels) {
		// Nastavení stylu
		context.save();
		context.setStroke(javafx.scene.paint.Color.LIGHTGRAY);
		context.setLineWidth(1);
		context.setLineDashes(5, 5); // Přerušovaná čára

		// Nastavení fontu pro popisky
		if (drawLabels) {
			context.setFont(new javafx.scene.text.Font(10));
			context.setFill(javafx.scene.paint.Color.LIGHTGRAY);
		}

		double canvasWidth = canvas.getWidth();
		double canvasHeight = canvas.getHeight();

		// Procházení všech regionů (kromě FLOAT)
		for (UIRegion region : UIRegion.values()) {
			if (region == UIRegion.FLOAT) continue;

			// Získání hranic regionu
			Bounds bounds = region.getBounds(canvasWidth, canvasHeight);

			// Vykreslení obdélníku
			context.strokeRect(
					bounds.getMinX(),
					bounds.getMinY(),
					bounds.getWidth(),
					bounds.getHeight()
			);

			// Vykreslení názvu regionu
			if (drawLabels) {
				context.fillText(
						region.name(),
						bounds.getMinX() + 5,
						bounds.getMinY() + 15
				);
			}
		}

		context.restore();
	}

}
