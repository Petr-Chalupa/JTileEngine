package engine.utils;

import engine.core.Camera;
import engine.core.Collider;
import engine.gameobjects.GameObject;
import engine.ui.UIRegion;
import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;

public class DebugManager {
	public enum Features {
		GAMEOBJECT_BOUNDS,
		GAMEOBJECT_COLLIDERS,
		GAMEOBJECT_MAX_INTERACT_DIST,
		UI_REGIONS
	}

	private static DebugManager instance;
	private final Camera camera;
	private final List<Features> enabledFeatures = new ArrayList<>();

	private final Color COLOR_GAMEOBJECT_BOUNDS = Color.RED;
	private final Color COLOR_GAMEOBJECT_COLLIDERS = Color.GREEN;
	private final Color COLOR_GAMEOBJECT_INTERACTS = Color.BLUE;
	private final Color COLOR_UI_REGIONS = Color.YELLOW;

	private DebugManager() {
		this.camera = Camera.getInstance();
	}

	public static DebugManager getInstance() {
		if (instance == null) instance = new DebugManager();
		return instance;
	}

	public boolean isFeatureEnabled(Features feature) {
		return enabledFeatures.contains(feature);
	}

	public void enableFeature(Features feature) {
		enabledFeatures.add(feature);
	}

	public void enableAllFeatures() {
		enabledFeatures.addAll(List.of(Features.values()));
	}

	public void disableFeature(Features feature) {
		enabledFeatures.remove(feature);
	}

	public void clearFeatures() {
		enabledFeatures.clear();
	}

	public void renderForGameObject(GraphicsContext context, GameObject gameObject) {
		if (enabledFeatures.contains(Features.GAMEOBJECT_BOUNDS)) renderGameObjectBounds(context, gameObject);
		if (enabledFeatures.contains(Features.GAMEOBJECT_COLLIDERS)) renderGameObjectCollider(context, gameObject);
		if (enabledFeatures.contains(Features.GAMEOBJECT_MAX_INTERACT_DIST)) renderGameObjectMaxInteractDist(context, gameObject);
	}

	private void renderGameObjectBounds(GraphicsContext context, GameObject gameObject) {
		Bounds bounds = gameObject.getBounds();
		double boundsMinX = camera.worldToScreenX(bounds.getMinX());
		double boundsMinY = camera.worldToScreenY(bounds.getMinY());
		double boundsCenterX = camera.worldToScreenX(bounds.getCenterX());
		double boundsCenterY = camera.worldToScreenY(bounds.getCenterY());

		context.save();
		context.setStroke(COLOR_GAMEOBJECT_BOUNDS);
		context.strokeRect(boundsMinX, boundsMinY, bounds.getWidth(), bounds.getHeight());
		context.strokeLine(boundsCenterX - 3, boundsCenterY, boundsCenterX + 3, boundsCenterY);
		context.strokeLine(boundsCenterX, boundsCenterY - 3, boundsCenterX, boundsCenterY + 3);
		context.restore();
	}

	private void renderGameObjectCollider(GraphicsContext context, GameObject gameObject) {
		Collider collider = gameObject.getCollider();
		if (collider == null) return;

		double colliderMinX = camera.worldToScreenX(collider.getMinX());
		double colliderMinY = camera.worldToScreenY(collider.getMinY());
		double colliderCenterX = camera.worldToScreenX(collider.getCenterX());
		double colliderCenterY = camera.worldToScreenY(collider.getCenterY());

		context.save();
		context.setStroke(COLOR_GAMEOBJECT_COLLIDERS);
		context.strokeRect(colliderMinX, colliderMinY, collider.getWidth(), collider.getHeight());
		context.strokeLine(colliderCenterX - 3, colliderCenterY, colliderCenterX + 3, colliderCenterY);
		context.strokeLine(colliderCenterX, colliderCenterY - 3, colliderCenterX, colliderCenterY + 3);
		context.restore();
	}

	private void renderGameObjectMaxInteractDist(GraphicsContext context, GameObject gameObject) {
		double maxInteractDist = gameObject.getMaxInteractDist();
		if (maxInteractDist <= 0) return;
		Collider collider = gameObject.getCollider();
		if (collider == null) return;

		double objectCenterX = camera.worldToScreenX(collider.getCenterX());
		double objectCenterY = camera.worldToScreenY(collider.getCenterY());

		context.save();
		context.setStroke(COLOR_GAMEOBJECT_INTERACTS);
		context.setLineWidth(1);
		context.setLineDashes(3, 3); // Make it dashed for better visibility
		context.strokeOval(objectCenterX - maxInteractDist, objectCenterY - maxInteractDist, 2 * maxInteractDist, 2 * maxInteractDist);
		context.restore();
	}

	public void renderUIRegionsBounds(GraphicsContext context, Canvas canvas) {
		double canvasWidth = canvas.getWidth();
		double canvasHeight = canvas.getHeight();

		context.save();
		context.setStroke(COLOR_UI_REGIONS);
		context.setLineWidth(1);
		context.setFill(COLOR_UI_REGIONS);
		context.setFont(new Font(10));
		for (UIRegion region : UIRegion.values()) {
			if (region == UIRegion.FLOAT) continue;

			Bounds bounds = region.getBounds(canvasWidth, canvasHeight);
			context.strokeRect(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight());
			context.fillText(region.name(), bounds.getMinX() + 5, bounds.getMinY() + 15);
		}
		context.restore();
	}
}
