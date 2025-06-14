package engine.core;

import engine.gameobjects.GameObject;
import engine.utils.LevelLoader;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;

public class Camera {
	private static Camera instance;
	private GameObject target;
	private double offsetX;
	private double offsetY;
	private Bounds viewBounds;

	private Camera() {
	}

	public static Camera getInstance() {
		if (instance == null) instance = new Camera();
		return instance;
	}

	public double getOffsetX() {
		return offsetX;
	}

	public double getOffsetY() {
		return offsetY;
	}

	public GameObject getTarget() {
		return target;
	}

	public void setTarget(GameObject target) {
		this.target = target;
	}

	public Bounds getViewBounds() {
		return viewBounds;
	}

	public double worldToScreenX(double posX) {
		double tileSize = GameSettings.getInstance().getTileSize();
		return posX * tileSize + offsetX;
	}

	public double worldToScreenY(double posY) {
		double tileSize = GameSettings.getInstance().getTileSize();
		return posY * tileSize + offsetY;
	}

	public void update(Canvas canvas) {
		if (target == null) return;

		double tileSize = GameSettings.getInstance().getTileSize();

		LevelLoader levelLoader = LevelLoader.getInstance();
		double mapWidth = levelLoader.getCols() * tileSize;
		double mapHeight = levelLoader.getRows() * tileSize;

		double viewWorldCenterX = target.getPosX() * tileSize + (target.getSize() * tileSize / 2.0);
		double viewWorldCenterY = target.getPosY() * tileSize + (target.getSize() * tileSize / 2.0);

		double viewLeft = viewWorldCenterX - canvas.getWidth() / 2.0;
		double viewRight = viewWorldCenterX + canvas.getWidth() / 2.0;
		double viewTop = viewWorldCenterY - canvas.getHeight() / 2.0;
		double viewBottom = viewWorldCenterY + canvas.getHeight() / 2.0;
		double viewOffsetX = -viewLeft;
		double viewOffsetY = -viewTop;

		if (viewLeft < 0) {
			viewRight -= viewLeft;
			viewOffsetX += viewLeft;
			viewLeft = 0;
		}
		if (viewRight > mapWidth) {
			viewLeft -= viewRight - mapWidth;
			viewOffsetX += viewRight - mapWidth;
			viewRight = mapWidth;
		}
		if (viewTop < 0) {
			viewBottom -= viewTop;
			viewOffsetY += viewTop;
			viewTop = 0;
		}
		if (viewBottom > mapHeight) {
			viewTop -= viewBottom - mapHeight;
			viewOffsetY += viewBottom - mapHeight;
			viewBottom = mapHeight;
		}

		offsetX = viewOffsetX;
		offsetY = viewOffsetY;
		viewBounds = new BoundingBox(viewLeft, viewTop, viewRight - viewLeft, viewBottom - viewTop);
	}

	/**
	 * Calculates intersection visible on a screen of the supplied bounds
	 *
	 * @param gameObjectBounds The bounds to calculate intersection for
	 * @return Bounds - the calculated intersection
	 */
	public Bounds getVisibleIntersection(Bounds gameObjectBounds) {
		if (viewBounds == null || gameObjectBounds == null) return null;

		double minX = Math.max(gameObjectBounds.getMinX(), viewBounds.getMinX());
		double minY = Math.max(gameObjectBounds.getMinY(), viewBounds.getMinY());
		double maxX = Math.min(gameObjectBounds.getMaxX(), viewBounds.getMaxX());
		double maxY = Math.min(gameObjectBounds.getMaxY(), viewBounds.getMaxY());
		return (minX < maxX && minY < maxY) ? new BoundingBox(minX, minY, maxX - minX, maxY - minY) : null;
	}

}