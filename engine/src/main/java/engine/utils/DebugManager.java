package engine.utils;

import engine.core.Camera;
import engine.core.Collider;
import engine.core.GameSettings;
import engine.gameobjects.GameObject;
import engine.gameobjects.entities.Enemy;
import engine.gameobjects.entities.Entity;
import engine.gameobjects.entities.Player;
import engine.gameobjects.items.Item;
import engine.gameobjects.items.Sword;
import engine.ui.Inventory;
import engine.ui.UIRegion;
import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.EnumSet;
import java.util.List;

public class DebugManager {
	public enum Features {
		GAMEOBJECT_BOUNDS,
		GAMEOBJECT_COLLIDERS,
		ENTITY_FACING_DIRECTION_X,
		PLAYER_MAX_INTERACT_RANGE,
		ITEM_SWORD_RANGE,
		ENEMY_SEARCH_ATTACK_RANGE,
		UI_REGIONS
	}

	private static DebugManager instance;
	private final Camera camera;
	private final EnumSet<Features> enabledFeatures = EnumSet.noneOf(Features.class);

	private final Color COLOR_GAMEOBJECT_BOUNDS = Color.RED;
	private final Color COLOR_GAMEOBJECT_COLLIDERS = Color.GREEN;
	private final Color COLOR_ENTITY_DIRECTION = Color.PURPLE;
	private final Color COLOR_PLAYER_INTERACT_RANGE = Color.BLUE;
	private final Color COLOR_ITEM_SWORD_RANGE = Color.AQUAMARINE;
	private final Color COLOR_ENEMY_SEARCH_RANGE = Color.LIGHTBLUE;
	private final Color COLOR_ENEMY_ATTACK_RANGE = Color.DARKBLUE;
	private final Color COLOR_UI_REGIONS = Color.YELLOW;

	private DebugManager() {
		this.camera = Camera.getInstance();
	}

	public static DebugManager getInstance() {
		if (instance == null) instance = new DebugManager();
		return instance;
	}

	public Features[] getFeatures() {
		return Features.values();
	}

	public EnumSet<Features> getEnabledFeatures() {
		return enabledFeatures;
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

	public void setFeature(Features feature, boolean enabled) {
		if (enabled) enableFeature(feature);
		else disableFeature(feature);
	}

	public void clearFeatures() {
		enabledFeatures.clear();
	}

	public void renderForGameObject(GraphicsContext context, GameObject gameObject) {
		if (enabledFeatures.contains(Features.GAMEOBJECT_BOUNDS)) renderGameObjectBounds(context, gameObject);
		if (enabledFeatures.contains(Features.GAMEOBJECT_COLLIDERS)) renderGameObjectCollider(context, gameObject);
		if (enabledFeatures.contains(Features.ENTITY_FACING_DIRECTION_X) && gameObject instanceof Entity entity) renderEntityFacingDirectionX(context, entity);
		if (enabledFeatures.contains(Features.PLAYER_MAX_INTERACT_RANGE) && gameObject instanceof Player player) renderPlayerMaxInteractRange(context, player);
		if (enabledFeatures.contains(Features.ITEM_SWORD_RANGE) && gameObject instanceof Player player) renderSwordRange(context, player);
		if (enabledFeatures.contains(Features.ENEMY_SEARCH_ATTACK_RANGE) && gameObject instanceof Enemy enemy) renderEnemySearchAttackRange(context, enemy);
	}

	private void renderGameObjectBounds(GraphicsContext context, GameObject gameObject) {
		Bounds bounds = gameObject.getBounds();
		double boundsMinX = camera.worldToScreenX(bounds.getMinX());
		double boundsMinY = camera.worldToScreenY(bounds.getMinY());
		double boundsCenterX = camera.worldToScreenX(bounds.getCenterX());
		double boundsCenterY = camera.worldToScreenY(bounds.getCenterY());
		double tileSize = GameSettings.getInstance().getTileSize();

		context.save();
		context.setStroke(COLOR_GAMEOBJECT_BOUNDS);
		context.strokeRect(boundsMinX, boundsMinY, bounds.getWidth() * tileSize, bounds.getHeight() * tileSize);
		context.strokeLine(boundsCenterX - 3, boundsCenterY, boundsCenterX + 3, boundsCenterY);
		context.strokeLine(boundsCenterX, boundsCenterY - 3, boundsCenterX, boundsCenterY + 3);
		context.restore();
	}

	private void renderGameObjectCollider(GraphicsContext context, GameObject gameObject) {
		Collider collider = gameObject.getCollider();
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

	private void renderEntityFacingDirectionX(GraphicsContext context, Entity entity) {
		Collider collider = entity.getCollider();
		double objectCenterX = camera.worldToScreenX(collider.getCenterX());
		double objectCenterY = camera.worldToScreenY(collider.getCenterY());
		int direction = entity.getFacingDirectionX();
		double arrowSize = 10.0;
		double endX = objectCenterX + collider.getWidth() * GameSettings.getInstance().getTileSize() * direction;

		context.save();
		context.setStroke(COLOR_ENTITY_DIRECTION);
		context.setLineWidth(2);
		context.strokeLine(objectCenterX, objectCenterY, endX, objectCenterY);
		double angle = (direction > 0) ? 0 : Math.PI;
		double x1 = endX - arrowSize * Math.cos(angle - Math.PI / 6);
		double y1 = objectCenterY - arrowSize * Math.sin(angle - Math.PI / 6);
		double x2 = endX - arrowSize * Math.cos(angle + Math.PI / 6);
		double y2 = objectCenterY - arrowSize * Math.sin(angle + Math.PI / 6);
		context.strokeLine(endX, objectCenterY, x1, y1);
		context.strokeLine(endX, objectCenterY, x2, y2);
		context.restore();
	}

	private void renderPlayerMaxInteractRange(GraphicsContext context, Player player) {
		Collider collider = player.getCollider();
		double objectCenterX = camera.worldToScreenX(collider.getCenterX());
		double objectCenterY = camera.worldToScreenY(collider.getCenterY());
		double interactRange = player.getInteractRange() * GameSettings.getInstance().getTileSize();

		context.save();
		context.setStroke(COLOR_PLAYER_INTERACT_RANGE);
		context.setLineWidth(1);
		context.setLineDashes(3, 3);
		context.strokeOval(objectCenterX - interactRange, objectCenterY - interactRange, 2 * interactRange, 2 * interactRange);
		context.restore();
	}

	private void renderEnemySearchAttackRange(GraphicsContext context, Enemy enemy) {
		Collider collider = enemy.getCollider();
		double objectCenterX = camera.worldToScreenX(collider.getCenterX());
		double objectCenterY = camera.worldToScreenY(collider.getCenterY());
		double searchRange = enemy.getSearchRange() * GameSettings.getInstance().getTileSize();
		double attackRange = enemy.getAttackRange() * GameSettings.getInstance().getTileSize();

		context.save();
		context.setStroke(COLOR_ENEMY_SEARCH_RANGE);
		context.setLineWidth(1);
		context.setLineDashes(3, 3);
		context.strokeOval(objectCenterX - searchRange, objectCenterY - searchRange, 2 * searchRange, 2 * searchRange);
		context.setStroke(COLOR_ENEMY_ATTACK_RANGE);
		context.strokeOval(objectCenterX - attackRange, objectCenterY - attackRange, 2 * attackRange, 2 * attackRange);
		context.restore();
	}

	private void renderSwordRange(GraphicsContext context, Player player) {
		Inventory inventory = player.getInventory();
		Item selectedItem = inventory.getSelectedItem();
		if (!(selectedItem instanceof Sword sword)) return;

		Collider collider = player.getCollider();
		double objectCenterX = camera.worldToScreenX(collider.getCenterX());
		double objectCenterY = camera.worldToScreenY(collider.getCenterY());
		double range = sword.getRange() * GameSettings.getInstance().getTileSize();

		context.save();
		context.setLineWidth(1);
		context.setLineDashes(3, 3);
		context.setStroke(COLOR_ITEM_SWORD_RANGE);
		context.strokeOval(objectCenterX - range, objectCenterY - range, 2 * range, 2 * range);
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
